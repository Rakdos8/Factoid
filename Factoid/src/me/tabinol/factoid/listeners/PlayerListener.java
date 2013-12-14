package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
    private static HashMap<Player, Land> lastLand;
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

    public static TreeSet<String> getPlayersInLand(Land land) {

        TreeSet<String> listp = new TreeSet<>();

        for (Map.Entry<Player, Land> entry : lastLand.entrySet()) {
            if (entry.getValue() == land) {
                listp.add(entry.getKey().getDisplayName());
            }
        }

        return listp;
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
        tpCancel.remove(player);
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
            Material ml = event.getClickedBlock().getType();
            Player player = event.getPlayer();
            Action action = event.getAction();

            Factoid.getLog().write("PlayerInteract player name: " + event.getPlayer().getName() + ", Action: " + event.getAction());

            // For infoItem
            if (player.getItemInHand() != null
                    && action == Action.LEFT_CLICK_BLOCK
                    && player.getItemInHand().getTypeId() == conf.InfoItem) {
                OnCommand.landInfo(land, player);
                event.setCancelled(true);

            } else if (!OnCommand.isAdminMod(player.getName())) {
                if ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                        || (((action == Action.RIGHT_CLICK_BLOCK // BEGIN of USE
                        && (ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR
                        || ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON
                        || ml == Material.LEVER || ml == Material.TRAPPED_CHEST))
                        || (action == Action.PHYSICAL
                        && (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE
                        || ml == Material.STRING)))
                        && !checkPermission(land, player, PermissionType.USE)) // End of "USE"
                        || (action == Action.RIGHT_CLICK_BLOCK
                        && (ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR)
                        && !checkPermission(land, player, PermissionType.USE_DOOR))
                        || (action == Action.RIGHT_CLICK_BLOCK
                        && (ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON)
                        && !checkPermission(land, player, PermissionType.USE_BUTTON))
                        || (action == Action.RIGHT_CLICK_BLOCK
                        && ml == Material.LEVER
                        && !checkPermission(land, player, PermissionType.USE_LEVER))
                        || (action == Action.PHYSICAL
                        && (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE)
                        && !checkPermission(land, player, PermissionType.USE_PRESSUREPLATE))
                        || (action == Action.RIGHT_CLICK_BLOCK
                        && ml == Material.TRAPPED_CHEST
                        && !checkPermission(land, player, PermissionType.USE_TRAPPEDCHEST))
                        || (action == Action.PHYSICAL
                        && ml == Material.STRING
                        && !checkPermission(land, player, PermissionType.USE_STRING))) {

                    if (action != Action.PHYSICAL) {
                        MessagePermission(player);
                    }
                    event.setCancelled(true);
                } else if (action == Action.RIGHT_CLICK_BLOCK
                        && (((ml == Material.CHEST || ml == Material.ENDER_CHEST // Begin of OPEN
                        || ml == Material.WORKBENCH || ml == Material.BREWING_STAND
                        || ml == Material.FURNACE || ml == Material.BEACON
                        || ml == Material.DROPPER || ml == Material.HOPPER)
                        && !checkPermission(land, player, PermissionType.OPEN)) // End of OPEN
                        || (ml == Material.CHEST
                        && !checkPermission(land, player, PermissionType.OPEN_CHEST))
                        || (ml == Material.ENDER_CHEST
                        && !checkPermission(land, player, PermissionType.OPEN_ENDERCHEST))
                        || (ml == Material.WORKBENCH
                        && !checkPermission(land, player, PermissionType.OPEN_CRAFT))
                        || (ml == Material.BREWING_STAND
                        && !checkPermission(land, player, PermissionType.OPEN_BREW))
                        || (ml == Material.FURNACE
                        && !checkPermission(land, player, PermissionType.OPEN_FURNACE))
                        || (ml == Material.BEACON
                        && !checkPermission(land, player, PermissionType.OPEN_BEACON))
                        || (ml == Material.DROPPER
                        && !checkPermission(land, player, PermissionType.OPEN_DROPPER))
                        || (ml == Material.HOPPER
                        && !checkPermission(land, player, PermissionType.OPEN_HOPPER)))) {
                    MessagePermission(player);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer().getName()))
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD)
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD_PLACE)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
            Player player = event.getPlayer();

            if ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_PLACE)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())
                && event.getRightClicked() instanceof ItemFrame) {

            Player player = (Player) event.getPlayer();
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getRightClicked().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_PLACE)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer().getName()))
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD)
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD_DESTROY)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {

        Player player;

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())
                && event.getRemover() instanceof Player
                && !OnCommand.isAdminMod((player = (Player) event.getRemover()).getName())) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_DESTROY)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())
                && event.getDamager() instanceof Player
                && !OnCommand.isAdminMod(((Player) event.getDamager()).getName())
                && event.getEntity() instanceof ItemFrame) {

            Player player = (Player) event.getDamager();
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_DESTROY)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());

            if (!checkPermission(land, event.getPlayer(), PermissionType.DROP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());

            if (!checkPermission(land, event.getPlayer(), PermissionType.PICKETUP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBed().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer().getName()))
                    || (!checkPermission(land, event.getPlayer(), PermissionType.SLEEP))) {
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
                Entity entity = event.getEntity();
                EntityType et = entity.getType();

                // kill en entity (none player)
                if (!OnCommand.isAdminMod(player.getName())
                        && ((land instanceof Land && ((Land) land).isBanned(player.getName()))
                        || (entity instanceof Animals
                        && !checkPermission(land, player, PermissionType.ANIMAL_KILL))
                        || (entity instanceof Monster
                        && !checkPermission(land, player, PermissionType.MOB_KILL))
                        || (et == EntityType.VILLAGER
                        && !checkPermission(land, player, PermissionType.VILLAGER_KILL))
                        || (et == EntityType.IRON_GOLEM
                        && !checkPermission(land, player, PermissionType.VILLAGER_GOLEM_KILL))
                        || (et == EntityType.HORSE
                        && !checkPermission(land, player, PermissionType.HORSE_KILL))
                        || (entity instanceof Tameable && ((Tameable) entity).isTamed() == true
                        && ((Tameable) entity).getOwner() != player
                        && !checkPermission(land, player, PermissionType.TAMED_KILL)))) {

                    MessagePermission(player);
                    event.setCancelled(true);

                    // For PVP
                } else if (entity instanceof Player) {

                    LandFlag flag;
                    Faction faction = Factoid.getFactions().getPlayerFaction(player.getName());
                    Faction factionVictime = Factoid.getFactions().getPlayerFaction(((Player) entity).getName());

                    if (faction != null && faction == factionVictime
                            && (flag = land.getFlagAndInherit(FlagType.FACTION_PVP)) != null && flag.getValueBoolean() == false) {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOFACTIONPVP"));
                        event.setCancelled(true);
                    } else if ((flag = land.getFlagAndInherit(FlagType.FULL_PVP)) != null && flag.getValueBoolean() == false) {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOPVP"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        if (conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !OnCommand.isAdminMod(event.getPlayer().getName())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlockClicked().getLocation());
            Material mt = event.getBucket();

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer().getName()))
                    || (mt == Material.LAVA_BUCKET
                    && !checkPermission(land, event.getPlayer(), PermissionType.BUCKET_LAVA))
                    || (mt == Material.WATER_BUCKET
                    && !checkPermission(land, event.getPlayer(), PermissionType.BUCKET_WATER))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (event.getPlayer() != null && conf.Worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            if (event.getPlayer() != null && !OnCommand.isAdminMod(event.getPlayer().getName())) {

                DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

                if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer().getName()))
                        || (!checkPermission(land, event.getPlayer(), PermissionType.FIRE))) {
                    MessagePermission(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {

        if (conf.Worlds.contains(event.getPotion().getLocation().getWorld().getName().toLowerCase())) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPotion().getLocation());

            if (event.getEntity() != null && event.getEntity().getShooter() instanceof Player) {

                Player player = (Player) event.getEntity().getShooter();

                if (!checkPermission(land, player, PermissionType.POTION_SPLASH)) {
                    if (player.isOnline()) {
                        MessagePermission(player);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean checkPermission(DummyLand land, Player player, PermissionType pt) {

        return land.checkPermissionAndInherit(player.getName(), pt) == pt.baseValue();
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
            lastLand.put(player, landOld = land);
        } else {
            landOld = lastLand.get(player);
        }
        if (land != landOld || newPlayer) {
            isTp = event instanceof PlayerTeleportEvent;
            landEvent = new PlayerLandChangeEvent(landOld, land, player, lastLoc.get(player), loc, isTp);
            pm.callEvent(landEvent);
            if (landEvent.isCancelled()) {
                if (isTp) {
                    ((PlayerTeleportEvent) event).setCancelled(true);
                    return;
                }
                if (land == landOld || newPlayer) {
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    player.teleport(lastLoc.get(player));
                }
                tpCancel.add(player);
                return;
            }
            lastLand.put(player, land);
        }
        lastLoc.put(player, loc);
    }
}
