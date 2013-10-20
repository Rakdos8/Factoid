package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;

public class PlayerListener implements Listener {

    private Config conf;
    public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds
    private int timeCheck;
    private HashMap<Player, Long> lastUpdate;
    private HashMap<Player, Land> lastLand;
    private HashMap<Player, Location> lastLoc;
    private List<Player> tpCancel;
    private PluginManager pm;

    public PlayerListener() {

        super();
        conf = Factoid.getConf();
        timeCheck = DEFAULT_TIME_LAPS;
        lastUpdate = new HashMap<>();
        pm = Factoid.getThisPlugin().getServer().getPluginManager();
        lastLand = new HashMap<>();
        lastLoc = new HashMap<>();
        tpCancel = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        lastUpdate.put(player, 0L);
        handleNewLocation(event, player, player.getLocation(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        lastUpdate.remove(player);
        lastLand.remove(player);
        lastLoc.remove(player);
        if (tpCancel.contains(player)) {
            tpCancel.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        Location loc = event.getTo();
        Player player = event.getPlayer();

        if (!tpCancel.contains(player)) {
            handleNewLocation(event, player, loc, false);
        } else {
            tpCancel.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        long last = lastUpdate.get(player);
        long now = System.currentTimeMillis();
        if (now - last < timeCheck) {
            return;
        }
        lastUpdate.put(player, now);
        if (event.getFrom().getWorld() == event.getTo().getWorld()) {
            if (event.getFrom().distance(event.getTo()) == 0) {
                return;
            }
        }
        handleNewLocation(event, player, event.getTo(), false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getClickedBlock().getLocation());
            String worldName = event.getClickedBlock().getLocation().getWorld().getName();
            Material ml = event.getClickedBlock().getType();
            Player player = event.getPlayer();
            Action action = event.getAction();

            Factoid.getLog().write("PlayerInteract player name: " + event.getPlayer().getName() + ", Action: " + event.getAction());

            // For infoItem
            if (player.getItemInHand() != null
                    && action == Action.LEFT_CLICK_BLOCK
                    && player.getItemInHand().getTypeId() == conf.InfoItem) {
                if (land instanceof Land) {

                    Land trueLand = (Land) land;
                    player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", trueLand.getName()));
                    player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", trueLand.getOwner().getContainerType().name(), trueLand.getOwner().getName()));
                    player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA"));
                    for (CuboidArea area : trueLand.getAreas()) {
                        player.sendMessage(ChatColor.GRAY + area.toString());
                    }
                } else {
                    player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
                }
                event.setCancelled(true);
            } else if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(player.getName())))
                    || ((action == Action.RIGHT_CLICK_BLOCK // BEGIN of USE
                    && (ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR
                    || ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON
                    || ml == Material.LEVER || ml == Material.TRAPPED_CHEST))
                    || (action == Action.PHYSICAL
                    && (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE
                    || ml == Material.STRING)))
                    && (!checkPermission(worldName, land, player, PermissionType.USE)) // End of "USE"
                    || (action == Action.RIGHT_CLICK_BLOCK
                    && (ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR)
                    && !checkPermission(worldName, land, player, PermissionType.USE_DOOR))
                    || (action == Action.RIGHT_CLICK_BLOCK
                    && (ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON)
                    && !checkPermission(worldName, land, player, PermissionType.USE_BUTTON))
                    || (action == Action.RIGHT_CLICK_BLOCK
                    && ml == Material.LEVER
                    && !checkPermission(worldName, land, player, PermissionType.USE_LEVER))
                    || (action == Action.PHYSICAL
                    && (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE)
                    && !checkPermission(worldName, land, player, PermissionType.USE_PRESSUREPLATE))
                    || (action == Action.RIGHT_CLICK_BLOCK
                    && ml == Material.TRAPPED_CHEST
                    && !checkPermission(worldName, land, player, PermissionType.USE_TRAPPEDCHEST))
                    || (action == Action.PHYSICAL
                    && ml == Material.STRING
                    && !checkPermission(worldName, land, player, PermissionType.USE_STRING))) {

                if (action != Action.PHYSICAL) {
                    MessagePermission(player);
                }
                event.setCancelled(true);
            } else if (action == Action.RIGHT_CLICK_BLOCK
                    && (((ml == Material.CHEST || ml == Material.ENDER_CHEST // Begin of OPEN
                    || ml == Material.WORKBENCH || ml == Material.BREWING_STAND
                    || ml == Material.FURNACE || ml == Material.BEACON
                    || ml == Material.DROPPER || ml == Material.HOPPER)
                    && !checkPermission(worldName, land, player, PermissionType.OPEN)) // End of OPEN
                    || (ml == Material.CHEST
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_CHEST))
                    || (ml == Material.ENDER_CHEST
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_ENDERCHEST))
                    || (ml == Material.WORKBENCH
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_CRAFT))
                    || (ml == Material.BREWING_STAND
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_BREW))
                    || (ml == Material.FURNACE
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_FURNACE))
                    || (ml == Material.BEACON
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_BEACON))
                    || (ml == Material.DROPPER
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_DROPPER))
                    || (ml == Material.HOPPER
                    && !checkPermission(worldName, land, player, PermissionType.OPEN_HOPPER)))) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            String worldName = event.getBlock().getLocation().getWorld().getName();

            if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(event.getPlayer().getName())))
                    || (!checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD)
                    && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD_PLACE))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            String worldName = event.getBlock().getLocation().getWorld().getName();

            if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(event.getPlayer().getName())))
                    || (!checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD)
                    && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD_DESTROY))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());
            String worldName = event.getPlayer().getLocation().getWorld().getName();

            if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.DROP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());
            String worldName = event.getPlayer().getLocation().getWorld().getName();

            if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.PICKETUP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBed().getLocation());
            String worldName = event.getBed().getLocation().getWorld().getName();

            if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(event.getPlayer().getName())))
                    || (!checkPermission(worldName, land, event.getPlayer(), PermissionType.SLEEP))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            Player player = null;
            Arrow damagerArrow;

            // Check if the damager is a player
            if (event.getDamager() instanceof Player) {
                player = (Player) event.getDamager();
            } else if (event.getDamager().getType() == EntityType.ARROW) {
                damagerArrow = (Arrow) event.getDamager();
                if (damagerArrow.getShooter() instanceof Player) {
                    player = (Player) damagerArrow.getShooter();
                }
            }

            if (player != null) {
                DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
                String worldName = event.getEntity().getLocation().getWorld().getName();
                Entity entity = event.getEntity();
                EntityType et = entity.getType();

                // kill en entity (none player)
                if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(player.getName())))
                        || (entity instanceof Animals
                        && !checkPermission(worldName, land, player, PermissionType.ANIMAL_KILL))
                        || (entity instanceof Monster
                        && !checkPermission(worldName, land, player, PermissionType.MOB_KILL))
                        || (et == EntityType.VILLAGER
                        && !checkPermission(worldName, land, player, PermissionType.VILLAGER_KILL))
                        || (et == EntityType.IRON_GOLEM
                        && !checkPermission(worldName, land, player, PermissionType.VILLAGER_GOLEM_KILL))
                        || (et == EntityType.HORSE
                        && !checkPermission(worldName, land, player, PermissionType.HORSE_KILL))) {
                
                    MessagePermission(player);
                    event.setCancelled(true);

                    // For PVP
                } else if (entity instanceof Player) {
                    
                    LandFlag flag;
                    Faction faction = Factoid.getFactions().getPlayerFaction(player.getName());
                    Faction factionVictime = Factoid.getFactions().getPlayerFaction(((Player)entity).getName());
                    
                    if(faction != null && faction == factionVictime &&
                            (flag = land.getFlagAndInherit(worldName, FlagType.FACTION_PVP)) != null && flag.getValueBoolean() == false) {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOFACTIONPVP"));
                        event.setCancelled(true);
                    } else if((flag = land.getFlagAndInherit(worldName, FlagType.FULL_PVP)) != null && flag.getValueBoolean() == false) {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOPVP"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlockClicked().getLocation());
            String worldName = event.getPlayer().getLocation().getWorld().getName();
            Material mt = event.getBucket();

            if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(event.getPlayer().getName())))
                    || (mt == Material.LAVA_BUCKET
                    && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUCKET_LAVA))
                    || (mt == Material.WATER_BUCKET
                    && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUCKET_WATER))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (event.getPlayer() != null && conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            if (event.getPlayer() != null) {

                DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
                String worldName = event.getBlock().getLocation().getWorld().getName();

                if ((land instanceof Land && ((Land) land).isBanned(new PlayerContainerPlayer(event.getPlayer().getName())))
                        || (!checkPermission(worldName, land, event.getPlayer(), PermissionType.FIRE))) {
                    MessagePermission(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean checkPermission(String worldName, DummyLand land, Player player, PermissionType pt) {

        if (land.checkPermissionAndInherit(worldName, player.getName(), pt) != pt.baseValue()) {
            return false;
        }

        return true;
    }

    private void MessagePermission(Player player) {

        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.MISSINGPERMISSION"));
    }

    private void handleNewLocation(Event event, Player player, Location loc, boolean newPlayer) {

        int t;
        Land land;
        Land landOld;
        PlayerLandChangeEvent landEvent;
        Boolean isTp;

        land = Factoid.getLands().getLand(loc);

        if (newPlayer) {
            lastLand.put(player, land);
        } else {
            landOld = lastLand.get(player);
            if (land != landOld) {
                isTp = event instanceof PlayerTeleportEvent;
                landEvent = new PlayerLandChangeEvent(landOld, land, player, lastLoc.get(player), loc, isTp);
                pm.callEvent(landEvent);
                if (landEvent.isCancelled()) {
                    if (isTp) {
                        ((PlayerTeleportEvent) event).setCancelled(true);
                        return;
                    }
                    player.teleport(lastLoc.get(player));
                    tpCancel.add(player);
                    return;
                }
                lastLand.put(player, land);
            }
        }
        lastLoc.put(player, loc);
    }
}
