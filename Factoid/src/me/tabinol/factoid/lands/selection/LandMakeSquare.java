package me.tabinol.factoid.lands.selection;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class LandMakeSquare extends Thread{
    private Player player;
    private World world;
    private byte by = 0;
    private Location FrontCornerLeft;
    private Location BackCornerLeft;
    private Location FrontCornerRigth;
    private Location BackCornerRigth;
    
    
    public LandMakeSquare(Player player,Location loc){
        this.player = player;
        this.world = player.getWorld();
        FrontCornerLeft = new Location(this.world,getConvert(loc.getX(),-6.0),this.world.getHighestBlockYAt(getConvertInt(loc.getBlockX(),-6),getConvertInt(loc.getBlockZ(),6))-1,getConvert(loc.getZ(),6.0));
        BackCornerLeft = new Location(this.world,getConvert(loc.getX(),-6.0),this.world.getHighestBlockYAt(getConvertInt(loc.getBlockX(),-6),getConvertInt(loc.getBlockZ(),-6))-1,getConvert(loc.getZ(),-6.0));
        FrontCornerRigth = new Location(this.world,getConvert(loc.getX(),6.0),this.world.getHighestBlockYAt(getConvertInt(loc.getBlockX(),6),getConvertInt(loc.getBlockZ(),6))-1,getConvert(loc.getZ(),6.0));
        BackCornerRigth = new Location(this.world,getConvert(loc.getX(),6.0),this.world.getHighestBlockYAt(getConvertInt(loc.getBlockX(),6),getConvertInt(loc.getBlockZ(),-6))-1,getConvert(loc.getZ(),-6.0));
         
    }
    
    public Map<Location,Material> makeSquare(){
            Map<Location,Material> BlockList = new HashMap<Location,Material>();
            Double DiffFrontX = getDifference(this.FrontCornerLeft.getX(),this.FrontCornerRigth.getX());
            Double DiffBackX = getDifference(this.BackCornerLeft.getX(),this.BackCornerRigth.getX());
            Double DiffLeftZ = getDifference(this.FrontCornerLeft.getZ(),this.BackCornerLeft.getZ());
            Double DiffRigthZ = getDifference(this.FrontCornerRigth.getZ(),this.BackCornerRigth.getZ());
            //player.sendMessage(ChatColor.GRAY+"[Factoid] Diff:"+DiffFrontX);
            //Corner
            //FrontLeft
            BlockList.put(FrontCornerLeft, FrontCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),this.FrontCornerLeft.getBlockZ())-1,this.FrontCornerLeft.getZ()),Material.SPONGE,this.by);
            //FrontRigth
            BlockList.put(FrontCornerRigth, FrontCornerRigth.getBlock().getType());
            player.sendBlockChange(new Location(this.world,this.FrontCornerRigth.getX(),this.world.getHighestBlockYAt(this.FrontCornerRigth.getBlockX(),this.FrontCornerRigth.getBlockZ())-1,this.FrontCornerRigth.getZ()),Material.SPONGE,this.by);
            //BackLeft
            BlockList.put(this.BackCornerLeft, this.BackCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(world,BackCornerLeft.getX(),this.world.getHighestBlockYAt(this.BackCornerLeft.getBlockX(),this.BackCornerLeft.getBlockZ())-1,this.BackCornerLeft.getZ()),Material.SPONGE,this.by);
            //BackRigth
            BlockList.put(this.BackCornerRigth, this.BackCornerRigth.getBlock().getType());
            player.sendBlockChange(new Location(this.world,this.BackCornerRigth.getX(),this.world.getHighestBlockYAt(this.BackCornerRigth.getBlockX(),this.BackCornerRigth.getBlockZ())-1,this.BackCornerRigth.getZ()),Material.SPONGE,this.by);

            //Front
            for(Double i = 1.0;i<=DiffFrontX;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,getConvert(this.FrontCornerLeft.getX(),i),this.world.getHighestBlockYAt(getConvertInt(this.FrontCornerLeft.getBlockX(),ii),this.FrontCornerLeft.getBlockZ())-1,this.FrontCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
            }
            //Back
            for(Double i = 1.0;i<=DiffBackX;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,getConvert(this.BackCornerLeft.getX(),i),this.world.getHighestBlockYAt(getConvertInt(this.BackCornerLeft.getBlockX(),ii),this.BackCornerLeft.getBlockZ())-1,this.BackCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                this.player.sendBlockChange(newloc ,Material.SPONGE,this.by);
            }
            //Left
            for(Double i = 1.0;i<=DiffLeftZ;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),getConvertInt(this.FrontCornerLeft.getBlockZ(),-ii))-1,getConvert(this.FrontCornerLeft.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
            }
            //Rigth
            for(Double i = 1.0;i<=DiffRigthZ;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,this.FrontCornerRigth.getX(),this.world.getHighestBlockYAt(this.FrontCornerRigth.getBlockX(),getConvertInt(this.FrontCornerRigth.getBlockZ(),-ii))-1,getConvert(this.FrontCornerRigth.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
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
