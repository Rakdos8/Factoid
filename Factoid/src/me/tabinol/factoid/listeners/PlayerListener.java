package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements Listener {
    
    private Config conf;

    public PlayerListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        
        // For infoItem
        if( event.getPlayer() != null && event.getPlayer().getItemInHand() != null &&
                event.getPlayer().getItemInHand().getTypeId() == conf.InfoItem) {
            event.getPlayer().sendMessage("The Land name is: " 
                    + Factoid.getLands().getLand(event.getPlayer().getLocation()).getName());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
    }
}
