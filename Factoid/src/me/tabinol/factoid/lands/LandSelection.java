package me.tabinol.factoid.lands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class LandSelection extends Thread implements Listener{
    
    private Player player;
    private World world;
    private byte by = 0;
    private boolean isSelected = false;
    private boolean isExpanding = false;
    private Map<Location,Material> BlockList = new HashMap<Location,Material>();
    private Location LandPos;
    
    public LandSelection(Player player,Server server,Location LandPos,JavaPlugin plugin){
        server.getPluginManager().registerEvents(this, plugin);
        this.player = player;
        this.world = player.getWorld();
        if(LandPos == null){
            BlockList = new LandMakeSquare(player).getSquare(player.getLocation());
        }else{
            this.LandPos = LandPos;
            this.isExpanding = true;
            new LandMakeSquare(player).getExpansion(LandPos,player.getLocation());
        }
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        if(!isSelected){
            if(event.getFrom()!=event.getTo()){
                if(event.getPlayer().getName()==player.getName()){
                    player.sendMessage("move");
                    if(!BlockList.isEmpty()){
                       boolean done = new LandResetSelection(BlockList,player).Reset();
                       if(done){
                           BlockList.clear();
                            LandMakeSquare landmake = new LandMakeSquare(player);
                            if(isExpanding){
                                BlockList = landmake.getExpansion(event.getTo(),player.getLocation());
                            }else{
                                BlockList = landmake.getSquare(event.getTo());
                            }
                            LandPos = event.getTo();
                       }
                    }
                }
            }
        }
    }
    
    public Map<Location,Material> getSquare(){
        return this.BlockList;
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public World getWorld(){
        return this.world;
    }
    
    public void isSelected(){
        this.isSelected = true;
    }
    
    public Location getSelection(){
        return this.LandPos;
    }
}
