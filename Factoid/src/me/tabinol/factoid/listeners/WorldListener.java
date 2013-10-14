package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {

    private Config conf;

    public WorldListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {

        float power;
        
        // Creeper Explosion
        if (event.getEntity() != null && event.getEntityType() == EntityType.CREEPER) {
            if(((Creeper) event.getEntity()).isPowered()) {
                power = 6L;
            } else {
                power = 3L;
            }
            event.setCancelled(true);
            ExplodeBlocks(event.blockList(), FlagType.CREEPER_DAMAGE, event.getLocation(), power, false, false);
        }
    }

    private void ExplodeBlocks(List<Block> blocks, FlagType ft, Location loc,
            float power, boolean setFire, boolean breakBlocks) {

        LandFlag flag;
        ArrayList<Block> listToRem = new ArrayList<>();

        // Check blocks to remove
        for (Block block : blocks) {
            if ((flag = Factoid.getLands().getFlag(block.getLocation(), ft)) == null
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
                loc.getWorld().dropItem(loc, new ItemStack(block.getType()));
            }
            block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        LandFlag flag;

        // Enderman removeblock
        if (event.getEntityType() == EntityType.ENDERMAN
                && (flag = Factoid.getLands().getFlag(event.getBlock().getLocation(), FlagType.ENDERMAN_DAMAGE)) != null
                && flag.getValueBoolean() == false) {
            event.setCancelled(true);
        }

    }
}