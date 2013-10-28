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
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {

    private Config conf;

    public WorldListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {

        if (event.getEntity() == null) {
            return;
        }

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {

            Location loc = event.getEntity().getLocation();
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);
            LandFlag flag;
            EntityType entityType = event.getEntityType();

            // Check for Explosion cancel 
            if ((entityType == EntityType.CREEPER
                    && (flag = land.getFlagAndInherit(FlagType.CREEPER_EXPLOSION)) != null
                    && flag.getValueBoolean() == false)
                    || (entityType == EntityType.PRIMED_TNT
                    && (flag = land.getFlagAndInherit(FlagType.TNT_EXPLOSION)) != null
                    && flag.getValueBoolean() == false)
                    || ((flag = land.getFlagAndInherit(FlagType.EXPLOSION)) != null
                    && flag.getValueBoolean() == false)) {
                event.setCancelled(true);
                if(entityType == EntityType.CREEPER) {
                    event.getEntity().remove();
                }
            }
            
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {

        if (event.getEntity() == null) {
            return;
        }

        if (conf.OverrideExplosions) {

            float power;

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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {

        if (conf.OverrideExplosions && conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
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
                    block.getLocation()).getFlagAndInherit(ft)) == null
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
                if (block.getType() == Material.TNT) {
                    block.setType(Material.AIR);
                    loc.getWorld().spawn(block.getLocation().add(.5, .5, .5), TNTPrimed.class);
                } else {
                    if (Calculate.getRandomYield(yield)) {
                        loc.getWorld().dropItem(loc, new ItemStack(block.getType()));
                    }
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            LandFlag flag;

            // Enderman removeblock
            if (event.getEntityType() == EntityType.ENDERMAN
                    && (flag = land.getFlagAndInherit(FlagType.ENDERMAN_DAMAGE)) != null
                    && flag.getValueBoolean() == false) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        if (conf.Worlds.contains(event.getBlock().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
            LandFlag flag;

            if ((event.getCause() == IgniteCause.SPREAD
                    && (flag = land.getFlagAndInherit(FlagType.FIRESPREAD)) != null
                    && flag.getValueBoolean() == false)
                    || ((flag = land.getFlagAndInherit(FlagType.FIRE)) != null
                    && flag.getValueBoolean() == false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (conf.Worlds.contains(event.getEntity().getWorld().getName().toLowerCase())) {
            DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getEntity().getLocation());
            LandFlag flag;

            if ((event.getEntity() instanceof Animals
                    && (flag = land.getFlagAndInherit(FlagType.ANIMAL_SPAWN)) != null
                    && flag.getValueBoolean() == false)
                    || (event.getEntity() instanceof Monster
                    && (flag = land.getFlagAndInherit(FlagType.MOB_SPAWN)) != null
                    && flag.getValueBoolean() == false)) {
                event.setCancelled(true);
            }
        }
    }
}
