package me.tabinol.factoid.lands.selection;

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
import org.bukkit.entity.Player;
import org.bukkit.World;

public class LandSelection extends Thread implements Listener{
    
    private Player player;
    private World world;
    private byte by = 0;
    private boolean isSelected = false;
    private Map<Location,Material> BlockList = new HashMap<Location,Material>();
    private Map<String,Location> CornerList = new HashMap<String,Location>();
    private Location LandPos;
    
    public LandSelection(Player player,Server server,JavaPlugin plugin){
        server.getPluginManager().registerEvents(this, plugin);
        this.player = player;
        this.world = player.getWorld();
        LandMakeSquare landmake = new LandMakeSquare(player,player.getLocation());
        this.BlockList = landmake.makeSquare();
        this.CornerList = landmake.getCorner();
        this.LandPos = player.getLocation();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        if(!this.isSelected){
            if(event.getFrom()!=event.getTo()){
                if(event.getPlayer().getName().equals(this.player.getName())){
                    if(!this.BlockList.isEmpty() && this.CornerList.isEmpty()){
                       boolean done = new LandResetSelection(this.BlockList,this.CornerList,this.player).Reset();
                       if(done){
                           this.BlockList.clear();
                           LandMakeSquare landmake = new LandMakeSquare(this.player,event.getTo());
                           this.BlockList = landmake.makeSquare();
                           this.CornerList = landmake.getCorner();
                           this.LandPos = event.getTo();
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
    
    public void setSelected(){
        this.isSelected = true;
    }
    
    public Location getSelection(){
        return this.LandPos;
    }
    
    public Map<String,Location> getCorner(){
        return this.CornerList;
    }
}
