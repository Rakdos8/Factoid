package me.tabinol.factoid.lands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LandResetSelection extends Thread{
    
    private Player player;
    private World world;
    private byte by = 0;
    private Map<Location,Material> BlockList = new HashMap<Location,Material>();
    
    public LandResetSelection(Map<Location,Material> BlockList,Player player){
        this.BlockList = BlockList;
        this.player = player;
    }
    
    public boolean Reset(){
        if(!BlockList.isEmpty()){
            for(Map.Entry<Location, Material> EntrySet : BlockList.entrySet()){
                        player.sendBlockChange(EntrySet.getKey(),EntrySet.getValue(),by);
            }
        }
        return true;
    }
}
