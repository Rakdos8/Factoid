package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private Config conf;

    public PlayerListener() {

        super();
        conf = Factoid.getConf();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        // For infoItem
        if (event.getPlayer() != null && event.getPlayer().getItemInHand() != null
                && event.getAction() == Action.LEFT_CLICK_BLOCK
                && event.getPlayer().getItemInHand().getTypeId() == conf.InfoItem) {
            Land land = Factoid.getLands().getLand(event.getPlayer().getLocation());
            if (land != null) {
                event.getPlayer().sendMessage("The Land name is: "
                        + land.getName());
                event.getPlayer().sendMessage("Owner: " + land.getOwner().getContainerType()
                        + ":" + land.getOwner().getName());
                event.getPlayer().sendMessage("Area(s):");
                for(CuboidArea area : land.getAreas()) {
                    event.getPlayer().sendMessage(area.toString());
                }
            } else {
                event.getPlayer().sendMessage("There is no land here!");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
    }
}
