package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {

    private Config conf;

    public WorldListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            float power;

            if (event.getEntity() != null) {

                // Creeper Explosion
                if (event.getEntityType() == EntityType.CREEPER) {
                    if (((Creeper) event.getEntity()).isPowered()) {
                        power = 6L;
                    } else {
                        power = 3L;
                    }
                    event.setCancelled(true);
                    ExplodeBlocks(event.blockList(), FlagType.CREEPER_DAMAGE, event.getLocation(),
                            event.getYield(), power, false, false);

                    //  Wither
                } else if (event.getEntityType() == EntityType.WITHER_SKULL) {
                    event.setCancelled(true);
                    ExplodeBlocks(event.blockList(), FlagType.WHITER_DAMAGE, event.getLocation(),
                            event.getYield(), 1L, false, false);
                } else if (event.getEntityType() == EntityType.WITHER) {
                    event.setCancelled(true);
                    ExplodeBlocks(event.blockList(), FlagType.WHITER_DAMAGE, event.getLocation(),
                            event.getYield(), 7L, false, false);

                    // Ghast
                } else if (event.getEntityType() == EntityType.FIREBALL) {
                    event.setCancelled(true);
                    ExplodeBlocks(event.blockList(), FlagType.GHAST_DAMAGE, event.getLocation(),
                            event.getYield(), 1L, true, false);

                    // TNT
                } else if (event.getEntityType() == EntityType.MINECART_TNT
                        || event.getEntityType() == EntityType.PRIMED_TNT) {
                    event.setCancelled(true);
                    ExplodeBlocks(event.blockList(), FlagType.TNT_DAMAGE, event.getLocation(),
                            event.getYield(), 4L, false, false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onHangingBreak(HangingBreakEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            // Check for painting
            if (event.getCause() == RemoveCause.EXPLOSION || event.getCause() == RemoveCause.ENTITY) {
                Factoid.getLog().write("Cancel HangingBreak : " + event.getEntity() + ", Cause: " + event.getCause());
                event.setCancelled(true);
            }
        }
    }

    private void ExplodeBlocks(List<Block> blocks, FlagType ft, Location loc,
            float yield, float power, boolean setFire, boolean breakBlocks) {

        LandFlag flag;
        ArrayList<Block> listToRem = new ArrayList<>();

        Factoid.getLog().write("Explosion : " + ", Yield: " + yield + ", power: " + power);

        // Check blocks to remove
        for (Block block : blocks) {
            if ((flag = Factoid.getLands().getLandOrOutsideArea(
                    block.getLocation()).getFlagAndInherit(block.getLocation().getWorld().getName(), ft)) == null
                    || (flag != null && flag.getValueBoolean() == true)) {
                listToRem.add(block);
            }
        }

        // Do the explosion
        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(),
                power, setFire, breakBlocks);

        // Remove and drop exploded blocks
        for (Block block : listToRem) {
            if (block.getType() != Material.AIR) {
                if (Calculate.getRandomYield(yield)) {
                    loc.getWorld().dropItem(loc, new ItemStack(block.getType()));
                }
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            LandFlag flag;

            // Enderman removeblock
            if (event.getEntityType() == EntityType.ENDERMAN
                    && (flag = land.getFlagAndInherit(
                    event.getBlock().getLocation().getWorld().getName(), FlagType.ENDERMAN_DAMAGE)) != null
                    && flag.getValueBoolean() == false) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (conf.Worlds.contains(event.getBlock().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            LandFlag flag;

            if ((event.getCause() == IgniteCause.SPREAD
                    && (flag = land.getFlagAndInherit(event.getBlock().getLocation().getWorld().getName(), FlagType.FIRESPREAD)) != null
                    && flag.getValueBoolean() == false)
                    || ((flag = land.getFlagAndInherit(event.getBlock().getLocation().getWorld().getName(), FlagType.FIRE)) != null
                    && flag.getValueBoolean() == false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
            LandFlag flag;

            if ((event.getEntity() instanceof Animals
                    && (flag = land.getFlagAndInherit(
                    event.getEntity().getLocation().getWorld().getName(), FlagType.ANIMAL_SPAWN)) != null
                    && flag.getValueBoolean() == false)
                    || (event.getEntity() instanceof Monster
                    && (flag = land.getFlagAndInherit(
                    event.getEntity().getLocation().getWorld().getName(), FlagType.MOB_SPAWN)) != null
                    && flag.getValueBoolean() == false)) {
                event.setCancelled(true);
            }
        }
    }
}