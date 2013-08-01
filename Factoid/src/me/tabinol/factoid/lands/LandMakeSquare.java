package me.tabinol.factoid.lands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class LandMakeSquare extends Thread{
    private Player player;
    private World world;
    private byte by = 0;
    
    public LandMakeSquare(Player player){
        this.player = player;
        this.world = player.getWorld();
    }
    
    public Map<Location,Material> getExpansion(Location base,Location loc){
        // TODO 
        // 2 Choice
        // Draw or Fix
        Map<Location,Material> BlockList = new HashMap<Location,Material>();
        Map<Location,Material> SelectedBlocList = getSquare(base);
        Location FrontCornerLeftBase = new Location(world,getConvert(base.getX(),-6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),6.0));
        Location BackCornerLeftBase = new Location(world,getConvert(base.getX(),-6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),-6.0));
        
        Location FrontCornerRigth = new Location(world,getConvert(loc.getX(),6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),6.0));
        
        Location BackCornerRigth = new Location(world,getConvert(loc.getX(),6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),-6.0));
        
        if(base.getX() != loc.getX()){
            Location FrontCornerLeft = new Location(world,getConvert(FrontCornerLeftBase.getX(),1.0),FrontCornerLeftBase.getY(),FrontCornerLeftBase.getZ());
            Location BackCornerLeft = new Location(world,getConvert(BackCornerLeftBase.getX(),1.0),BackCornerLeftBase.getY(),BackCornerLeftBase.getZ());
            player.sendBlockChange(FrontCornerLeft,Material.SPONGE,by);
            player.sendBlockChange(BackCornerLeft,Material.SPONGE,by);
        }
        
        return BlockList;
    }
    
    public Map<Location,Material> getSquare(Location loc){
            Map<Location,Material> BlockList = new HashMap<Location,Material>();
            Location FrontCornerLeft = new Location(world,getConvert(loc.getX(),-6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),6.0));
            Location FrontCornerRigth = new Location(world,getConvert(loc.getX(),6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),6.0));
            Location BackCornerLeft = new Location(world,getConvert(loc.getX(),-6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),-6.0));
            Location BackCornerRigth = new Location(world,getConvert(loc.getX(),6.0),getConvert(loc.getY(),-1.0),getConvert(loc.getZ(),-6.0));
            Double DiffFrontX = getDifference(FrontCornerLeft.getX(),FrontCornerRigth.getX());
            Double DiffBackX = getDifference(BackCornerLeft.getX(),BackCornerRigth.getX());
            Double DiffLeftZ = getDifference(FrontCornerLeft.getZ(),BackCornerLeft.getZ());
            Double DiffRigthZ = getDifference(FrontCornerRigth.getZ(),BackCornerRigth.getZ());
            player.sendMessage(ChatColor.GRAY+"[Factoid] Diff:"+DiffFrontX);
            //Corner
            //FrontLeft
            BlockList.put(FrontCornerLeft, FrontCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(world,FrontCornerLeft.getX(),FrontCornerLeft.getY(),FrontCornerLeft.getZ()),Material.SPONGE,by);
            //FrontRigth
            BlockList.put(FrontCornerRigth, FrontCornerRigth.getBlock().getType());
            player.sendBlockChange(new Location(world,FrontCornerRigth.getX(),FrontCornerRigth.getY(),FrontCornerRigth.getZ()),Material.SPONGE,by);
            //BackLeft
            BlockList.put(BackCornerLeft, BackCornerLeft.getBlock().getType());
            player.sendBlockChange(new Location(world,BackCornerLeft.getX(),BackCornerLeft.getY(),BackCornerLeft.getZ()),Material.SPONGE,by);
            //BackRigth
            BlockList.put(BackCornerRigth, BackCornerRigth.getBlock().getType());
            player.sendBlockChange(new Location(world,BackCornerRigth.getX(),BackCornerRigth.getY(),BackCornerRigth.getZ()),Material.SPONGE,by);

            //Front
            for(Double i = 1.0;i<=DiffFrontX;i++){
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(world,getConvert(FrontCornerLeft.getX(),i),FrontCornerLeft.getY(),FrontCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                player.sendBlockChange(newloc,Material.SPONGE,by);
            }
            //Back
            for(Double i = 1.0;i<=DiffBackX;i++){
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(world,getConvert(BackCornerLeft.getX(),i),BackCornerLeft.getY(),BackCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                player.sendBlockChange(newloc ,Material.SPONGE,by);
            }
            //Left
            for(Double i = 1.0;i<=DiffLeftZ;i++){
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(world,FrontCornerLeft.getX(),FrontCornerLeft.getY(),getConvert(FrontCornerLeft.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                player.sendBlockChange(newloc,Material.SPONGE,by);
            }
            //Rigth
            for(Double i = 1.0;i<=DiffRigthZ;i++){
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(world,FrontCornerRigth.getX(),FrontCornerRigth.getY(),getConvert(FrontCornerRigth.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                player.sendBlockChange(newloc,Material.SPONGE,by);
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
    
    private Double getDifference(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a-b;
        }
        return (t < 0 ? -t : t);
    }
}
