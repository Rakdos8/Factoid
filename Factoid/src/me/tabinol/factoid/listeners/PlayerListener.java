package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerListener implements Listener {

    private Config conf;

    public PlayerListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getClickedBlock().getLocation());
        String worldName = event.getClickedBlock().getLocation().getWorld().getName();
        Material ml = event.getClickedBlock().getType();
        Player player = event.getPlayer();

        Factoid.getLog().write("PlayerInteract player name: " + event.getPlayer().getName() + ", Action: " + event.getAction());

        // For infoItem
        if (event.getPlayer().getItemInHand() != null
                && event.getAction() == Action.LEFT_CLICK_BLOCK
                && event.getPlayer().getItemInHand().getTypeId() == conf.InfoItem) {
            if (land instanceof Land) {

                Land trueLand = (Land) land;
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", trueLand.getName()));
                player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", trueLand.getOwner().getContainerType().name(), trueLand.getOwner().getName()));
                player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA"));
                for (CuboidArea area : trueLand.getAreas()) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + area.toString());
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
            }
            event.setCancelled(true);
        } else {
            if (!checkPermission(worldName, land, player, PermissionType.USE)
                    || ((ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR)
                    && !checkPermission(worldName, land, player, PermissionType.USE_DOOR))
                    || ((ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON)
                    && !checkPermission(worldName, land, player, PermissionType.USE_BUTTON))
                    || (ml == Material.LEVER
                    && !checkPermission(worldName, land, player, PermissionType.USE_LEVER))
                    || ((ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE)
                    && !checkPermission(worldName, land, player, PermissionType.USE_PRESSUREPLATE))
                    || (ml == Material.TRAPPED_CHEST
                    && !checkPermission(worldName, land, player, PermissionType.USE_TRAPPEDCHEST))
                    || (ml == Material.STRING
                    && !checkPermission(worldName, land, player, PermissionType.USE_STRING))) {
                MessagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
        String worldName = event.getBlock().getLocation().getWorld().getName();

        if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD)
                && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD_PLACE)) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
        String worldName = event.getBlock().getLocation().getWorld().getName();

        if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD)
                && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUILD_DESTROY)) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());
        String worldName = event.getPlayer().getLocation().getWorld().getName();

        if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.DROP)) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getLocation());
        String worldName = event.getPlayer().getLocation().getWorld().getName();

        if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.PICKETUP)) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onInventoryOpen(InventoryOpenEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getPlayer().getTargetBlock(null, 10).getLocation());
        String worldName = event.getPlayer().getLocation().getWorld().getName();
        InventoryType it = event.getInventory().getType();
        Player player = (Player) event.getPlayer();

        if (((it != InventoryType.CREATIVE && it != InventoryType.PLAYER
                && !checkPermission(worldName, land, player, PermissionType.OPEN)))
                || ((it == InventoryType.CHEST
                && !checkPermission(worldName, land, player, PermissionType.OPEN_CHEST)))
                || ((it == InventoryType.ENDER_CHEST
                && !checkPermission(worldName, land, player, PermissionType.OPEN_ENDERCHEST)))
                || ((it == InventoryType.CRAFTING
                && !checkPermission(worldName, land, player, PermissionType.OPEN_CRAFT)))
                || ((it == InventoryType.BREWING
                && !checkPermission(worldName, land, player, PermissionType.OPEN_BREW)))
                || ((it == InventoryType.FURNACE
                && !checkPermission(worldName, land, player, PermissionType.OPEN_FURNACE)))
                || ((it == InventoryType.BEACON
                && !checkPermission(worldName, land, player, PermissionType.OPEN_BEACON)))
                || ((it == InventoryType.DROPPER
                && !checkPermission(worldName, land, player, PermissionType.OPEN_DROPPER)))
                || ((it == InventoryType.HOPPER
                && !checkPermission(worldName, land, player, PermissionType.OPEN_HOPPER)))) {
            MessagePermission(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBed().getLocation());
        String worldName = event.getBed().getLocation().getWorld().getName();

        if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.SLEEP)) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

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

            if ((entity instanceof Animals
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
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlockClicked().getLocation());
        String worldName = event.getPlayer().getLocation().getWorld().getName();
        Material mt = event.getBucket();

        if ((mt == Material.LAVA_BUCKET
                && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUCKET_LAVA))
                || (mt == Material.WATER_BUCKET
                && !checkPermission(worldName, land, event.getPlayer(), PermissionType.BUCKET_WATER))) {
            MessagePermission(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (event.getPlayer() != null) {

            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            String worldName = event.getBlock().getLocation().getWorld().getName();

            if (!checkPermission(worldName, land, event.getPlayer(), PermissionType.FIRE)) {
                MessagePermission(event.getPlayer());
                event.setCancelled(true);
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
}
