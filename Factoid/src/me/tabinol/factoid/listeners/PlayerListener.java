package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
            if(land != null){
                event.getPlayer().sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME",land.getName()));
                event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER",land.getOwner().getContainerType().name(),land.getOwner().getName()));
                event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA"));
                for(CuboidArea area : land.getAreas()) {
                    event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY+area.toString());
                }
            }else{
                event.getPlayer().sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        
        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
        
        if(!checkPermission(event.getBlock().getLocation().getWorld().getName(), land, event.getPlayer(),
                PermissionType.BUILD_PLACE, true)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        
        DummyLand land = Factoid.getLands().getLandOrOutsideArea(event.getBlock().getLocation());
 
        if(!checkPermission(event.getBlock().getLocation().getWorld().getName(), land, event.getPlayer(),
                PermissionType.BUILD_DESTROY, true)) {
            event.setCancelled(true);
        }
    }

    private boolean checkPermission(String worldName, DummyLand land, Player player, PermissionType pt, boolean sendMessage) {
        
        if(land.checkPermissionAndInherit(worldName, player.getName(), pt) != pt.baseValue()) {
            if(sendMessage) {
                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("ACTION.MISSINGPERMISSION"));
            }
            return false;
        }
        return true;
    }
}
