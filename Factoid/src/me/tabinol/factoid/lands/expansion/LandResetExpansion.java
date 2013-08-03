package me.tabinol.factoid.lands.expansion;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LandResetExpansion extends Thread{
    
    private Player player;
    private World world;
    private byte by = 0;
    private Map<Location,Material> BlockList = new HashMap<Location,Material>();
    private Map<String,Location> CornerList = new HashMap<String,Location>();
    
    public LandResetExpansion(Map<Location,Material> BlockList,Map<String,Location> CornerList,Player player){
        this.BlockList = BlockList;
        this.CornerList = CornerList;
        this.player = player;
    }
    
    public boolean Reset(){
        if(!this.BlockList.isEmpty()){
            for(Map.Entry<Location, Material> EntrySet : this.BlockList.entrySet()){
                        this.player.sendBlockChange(EntrySet.getKey(),EntrySet.getValue(),this.by);
            }
        }
        
        if(!this.CornerList.isEmpty()){
            this.CornerList.clear();
        }
        return true;
    }
}
