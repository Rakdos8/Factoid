/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.commands.executor.CommandInfo;
import me.tabinol.factoid.commands.executor.CommandSelect;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.selection.region.PlayerMoveListen;
import me.tabinol.factoid.selection.region.RegionSelection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
    private PlayerStaticConfig playerConf;
    public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds
    private int timeCheck;
    private PluginManager pm;

    public PlayerListener() {

        super();
        conf = Factoid.getConf();
        playerConf = Factoid.getPlayerConf();
        timeCheck = DEFAULT_TIME_LAPS;
        pm = Factoid.getThisPlugin().getServer().getPluginManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // Create a new static config
        PlayerConfEntry entry = playerConf.add(player);

        updatePosInfo(event, entry, player.getLocation(), true);

        // Note pour kaz00 : L'endroit correct pour mettre ceci serait
        // dans public void onPlayerLandChange(PlayerLandChangeEvent event) (LandListener.java)
        // J'ai réglé un bug de joueur «null» et je l'ai désactiver pour ne pas
        // le voit tout de suite en prod. Tu le l'activera si tu veux travailler
        // dessus.
        /*
         Land landScoreboard = Factoid.getLands().getLand(player.getLocation());
         if (landScoreboard != null) {
         for (Player playerInLand : landScoreboard.getPlayersInLand()) {
         Factoid.getScoreboard().sendScoreboard(landScoreboard.getPlayersInLand(), playerInLand, landScoreboard.getName());
         }
         } */
        // Check if AdminMod is auto
        if (player.hasPermission("factoid.adminmod.auto")) {
            playerConf.get(player).setAdminMod(true);
        }
    }

    // Must be running after LandListener
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        // Remove player from the land
        Land land = playerConf.get(player).getLastLand();
        if (land != null) {
            land.removePlayerInLand(player);
        }

        // Remove player from Static Config
        playerConf.remove(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        Location loc = event.getTo();
        Player player = event.getPlayer();
        PlayerConfEntry entry = playerConf.get(player);

        // BugFix Citizens plugin
        if (entry == null) {
            return;
        }

        if (!entry.hasTpCancel()) {
            updatePosInfo(event, entry, loc, false);
        } else {
            entry.setTpCancel(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        PlayerConfEntry entry = playerConf.get(player);

        if (player == null) {
            return;
        }
        long last = entry.getLastMoveUpdate();
        long now = System.currentTimeMillis();
        if (now - last < timeCheck) {
            return;
        }
        entry.setLastMoveUpdate(now);
        if (event.getFrom().getWorld() == event.getTo().getWorld()) {
            if (event.getFrom().distance(event.getTo()) == 0) {
                return;
            }
        }
        updatePosInfo(event, entry, event.getTo(), false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            DummyLand land;
            Material ml = event.getClickedBlock().getType();
            Player player = event.getPlayer();
            Action action = event.getAction();
            PlayerConfEntry entry;

            Factoid.getLog().write("PlayerInteract player name: " + event.getPlayer().getName() + ", Action: " + event.getAction());

            // For infoItem
            if (player.getItemInHand() != null
                    && action == Action.LEFT_CLICK_BLOCK
                    && player.getItemInHand().getTypeId() == conf.getInfoItem()) {
                try {
                    new CommandInfo(player, Factoid.getLands().getCuboidArea(event.getClickedBlock().getLocation())).commandExecute();
                } catch (FactoidCommandException ex) {
                    Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, "Error when trying to get area", ex);
                }
                event.setCancelled(true);

                // For Select
            } else if (player.getItemInHand() != null
                    && action == Action.LEFT_CLICK_BLOCK
                    && player.getItemInHand().getTypeId() == conf.getSelectItem()) {

                try {
                    new CommandSelect(player, new ArgList(new String[]{"here"}, player),
                            event.getClickedBlock().getLocation()).commandExecute();
                } catch (FactoidCommandException ex) {
                    // Empty, message is sent by the catch
                }

                event.setCancelled(true);

                // For Select Cancel
            } else if (player.getItemInHand() != null
                    && action == Action.RIGHT_CLICK_BLOCK
                    && player.getItemInHand().getTypeId() == conf.getSelectItem()
                    && (entry = playerConf.get(player)).getSelection().hasSelection()) {

                try {
                    new CommandCancel(entry, false).commandExecute();
                } catch (FactoidCommandException ex) {
                    // Empty, message is sent by the catch
                }

                event.setCancelled(true);

                // Citizen bug, check if entry exist before
            } else if ((entry = playerConf.get(player)) != null && !entry.isAdminMod()) {
                land = Factoid.getLands().getLandOrOutsideArea(event.getClickedBlock().getLocation());
                if ((land instanceof Land && ((Land) land).isBanned(player))
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

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD)
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD_PLACE)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {

        if (conf.getWorlds().contains(event.getEntity().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
            Player player = event.getPlayer();

            if ((land instanceof Land && ((Land) land).isBanned(player))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_PLACE)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()
                && event.getRightClicked() instanceof ItemFrame) {

            Player player = (Player) event.getPlayer();
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getRightClicked().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(player))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_PLACE)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD)
                    || !checkPermission(land, event.getPlayer(), PermissionType.BUILD_DESTROY)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        Player player;

        if (conf.getWorlds().contains(event.getEntity().getWorld().getName().toLowerCase())
                && event.getRemover() instanceof Player
                && !playerConf.get((player = (Player) event.getRemover())).isAdminMod()) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(player))
                    || !checkPermission(land, player, PermissionType.BUILD)
                    || !checkPermission(land, player, PermissionType.BUILD_DESTROY)) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());

            if (!checkPermission(land, event.getPlayer(), PermissionType.DROP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());

            if (!checkPermission(land, event.getPlayer(), PermissionType.PICKETUP)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBed().getLocation());

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
                    || (!checkPermission(land, event.getPlayer(), PermissionType.SLEEP))) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (conf.getWorlds().contains(event.getEntity().getWorld().getName().toLowerCase())) {

            PlayerConfEntry entry;
            PlayerConfEntry entryVictime;

            // Check if a player break a ItemFrame
            if (event.getDamager() instanceof Player
                    && (entry = playerConf.get((Player) event.getDamager())) != null // Citizens bugfix
                    && !entry.isAdminMod()
                    && event.getEntity() instanceof Hanging) {

                Player player = (Player) event.getDamager();
                DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());

                if ((land instanceof Land && ((Land) land).isBanned(player))
                        || !checkPermission(land, player, PermissionType.BUILD)
                        || !checkPermission(land, player, PermissionType.BUILD_DESTROY)) {
                    MessagePermission(player);
                    event.setCancelled(true);
                }
            } else {
                Player player = null;
                Projectile damagerProjectile;

                // Check if the damager is a player
                if (event.getDamager() instanceof Player) {
                    player = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile
                        && event.getDamager().getType() != EntityType.EGG
                        && event.getDamager().getType() != EntityType.SNOWBALL) {
                    damagerProjectile = (Projectile) event.getDamager();
                    if (damagerProjectile.getShooter() instanceof Player) {
                        player = (Player) damagerProjectile.getShooter();
                    }
                }

                if (player != null) {
                    DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
                    Entity entity = event.getEntity();
                    EntityType et = entity.getType();

                    // kill en entity (none player)
                    if (event.getDamager() instanceof Player
                            && (entry = playerConf.get(player)) != null // Citizens bugfix
                            && !entry.isAdminMod()
                            && ((land instanceof Land && ((Land) land).isBanned(player))
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
                    } else if (entity instanceof Player && (entryVictime = playerConf.get((Player) entity)) != null
                            && (entry = playerConf.get(player)) != null) { // Citizens bugfix

                        LandFlag flag;
                        Faction faction = Factoid.getFactions().getPlayerFaction(entry.getPlayerContainer());
                        Faction factionVictime = Factoid.getFactions().getPlayerFaction(entryVictime.getPlayerContainer());

                        if (faction != null && faction == factionVictime
                                && (flag = land.getFlagAndInherit(FlagType.FACTION_PVP)) != null && flag.getValueBoolean() == false) {
                            // player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOFACTIONPVP"));
                            event.setCancelled(true);
                        } else if ((flag = land.getFlagAndInherit(FlagType.FULL_PVP)) != null && flag.getValueBoolean() == false) {
                            // player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOPVP"));
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        if (conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())
                && !playerConf.get(event.getPlayer()).isAdminMod()) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlockClicked().getLocation());
            Material mt = event.getBucket();

            if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
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

        if (event.getPlayer() != null && conf.getWorlds().contains(event.getPlayer().getWorld().getName().toLowerCase())) {
            if (event.getPlayer() != null && !playerConf.get(event.getPlayer()).isAdminMod()) {

                DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());

                if ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
                        || (!checkPermission(land, event.getPlayer(), PermissionType.FIRE))) {
                    MessagePermission(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {

        if (conf.getWorlds().contains(event.getPotion().getLocation().getWorld().getName().toLowerCase())) {

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

        return land.checkPermissionAndInherit(player, pt) == pt.baseValue();
    }

    private void MessagePermission(Player player) {

        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
    }

    private void updatePosInfo(Event event, PlayerConfEntry entry, Location loc, boolean newPlayer) {

        Land land;
        Land landOld;
        PlayerLandChangeEvent landEvent;
        Boolean isTp;
        Player player = entry.getPlayer();

        land = Factoid.getLands().getLand(loc);

        if (newPlayer) {
            entry.setLastLand(landOld = land);
        } else {
            landOld = entry.getLastLand();
        }
        if (newPlayer || land != landOld) {
            isTp = event instanceof PlayerTeleportEvent;
            // First parameter : If it is a new player, it is null, if not new player, it is "landOld"
            landEvent = new PlayerLandChangeEvent(newPlayer ? null : landOld, land, player, entry.getLastLoc(), loc, isTp);
            pm.callEvent(landEvent);
            if (landEvent.isCancelled()) {
                if (isTp) {
                    ((PlayerTeleportEvent) event).setCancelled(true);
                    return;
                }
                if (land == landOld || newPlayer) {
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    Location retLoc = entry.getLastLoc();
                    player.teleport(new Location(retLoc.getWorld(),
                            retLoc.getX(), retLoc.getBlockY(), retLoc.getZ(),
                            loc.getYaw(), loc.getPitch()));
                }
                entry.setTpCancel(true);
                return;
            }
            entry.setLastLand(land);

            // Update player in the lands
            if (landOld != null && landOld != land) {
                landOld.removePlayerInLand(player);
            }
            if (land != null) {
                land.addPlayerInLand(player);
            }
        }
        entry.setLastLoc(loc);

        // Update visual selection
        if (entry.getSelection().hasSelection()) {
            for (RegionSelection sel : entry.getSelection().getSelections()) {
                if (sel instanceof PlayerMoveListen) {
                    ((PlayerMoveListen) sel).playerMove();
                }
            }
        }
    }
}
