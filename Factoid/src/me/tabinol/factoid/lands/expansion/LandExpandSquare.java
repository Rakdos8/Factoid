package me.tabinol.factoid.lands.expansion;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class LandExpandSquare extends Thread{
    private Player player;
    private World world;
    private byte by = 0;
    private Location FrontCornerLeft;
    private Location BackCornerLeft;
    private Location FrontCornerRigth;
    private Location BackCornerRigth;
    
    
    public LandExpandSquare(Player player,Map<String,Location> Corner){
        this.player = player;
        this.world = player.getWorld();
        FrontCornerLeft = Corner.get("FrontCornerLeft");
        BackCornerLeft = Corner.get("BackCornerLeft");
        FrontCornerRigth = Corner.get("FrontCornerRigth");
        BackCornerRigth = Corner.get("BackCornerRigth");
         
    }
    
    public Map<Location,Material> expandSquare(){
        Map<Location,Material> BlockList = new HashMap<Location,Material>();
        if(this.player.getLocation().getX() >= FrontCornerLeft.getX()){
            Double DiffLeftZ = getDifference(this.FrontCornerLeft.getZ(),this.BackCornerLeft.getZ());
            //FrontLeft
            BlockList.put(FrontCornerLeft, FrontCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),this.FrontCornerLeft.getBlockZ())-1,this.FrontCornerLeft.getZ()),Material.SPONGE,this.by);
            //BackLeft
            BlockList.put(this.BackCornerLeft, this.BackCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(world,BackCornerLeft.getX(),this.world.getHighestBlockYAt(this.BackCornerLeft.getBlockX(),this.BackCornerLeft.getBlockZ())-1,this.BackCornerLeft.getZ()),Material.SPONGE,this.by);
            //Left
            for(Double i = 1.0;i<=DiffLeftZ;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),getConvertInt(this.FrontCornerLeft.getBlockZ(),-ii))-1,getConvert(this.FrontCornerLeft.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
            }
        }
            return BlockList;
    }
    
    private Double getConvert(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    private int getConvertInt(int a, int b){
        int t = 0;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    private Double getDifference(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a-b;
        }
        return (t < 0 ? -t : t);
    }
    
    public Map<String,Location> getCorner(){
        Map<String,Location> CornerList = new HashMap<String,Location>();
        CornerList.put("FrontCornerLeft", this.FrontCornerLeft);
        CornerList.put("BackCornerLeft", this.BackCornerLeft);
        CornerList.put("FrontCornerRigth", this.FrontCornerRigth);
        CornerList.put("BackCornerRigth", this.BackCornerRigth);
        return CornerList;
    }
}
