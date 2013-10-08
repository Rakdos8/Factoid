package me.tabinol.factoid.lands.selection;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.tabinol.factoid.utilities.Calculate;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.*;

public class LandMakeSquare extends Thread{
    private Player player;
    private World world;
    private byte by = 0;
    private Location FrontCornerLeft;
    private Location BackCornerLeft;
    private Location FrontCornerRigth;
    private Location BackCornerRigth;
    private boolean IsCollision = false;
    
    
    public LandMakeSquare(Player player,Location loc){
        this.player = player;
        this.world = player.getWorld();
        FrontCornerLeft = new Location(this.world,Calculate.AdditionDouble(loc.getX(),-6.0),this.world.getHighestBlockYAt(Calculate.AdditionInt(loc.getBlockX(),-6),Calculate.AdditionInt(loc.getBlockZ(),6))-1,Calculate.AdditionDouble(loc.getZ(),6.0));
        BackCornerLeft = new Location(this.world,Calculate.AdditionDouble(loc.getX(),-6.0),this.world.getHighestBlockYAt(Calculate.AdditionInt(loc.getBlockX(),-6),Calculate.AdditionInt(loc.getBlockZ(),-6))-1,Calculate.AdditionDouble(loc.getZ(),-6.0));
        FrontCornerRigth = new Location(this.world,Calculate.AdditionDouble(loc.getX(),6.0),this.world.getHighestBlockYAt(Calculate.AdditionInt(loc.getBlockX(),6),Calculate.AdditionInt(loc.getBlockZ(),6))-1,Calculate.AdditionDouble(loc.getZ(),6.0));
        BackCornerRigth = new Location(this.world,Calculate.AdditionDouble(loc.getX(),6.0),this.world.getHighestBlockYAt(Calculate.AdditionInt(loc.getBlockX(),6),Calculate.AdditionInt(loc.getBlockZ(),-6))-1,Calculate.AdditionDouble(loc.getZ(),-6.0));
         
    }
    
    public Map<Location,Material> makeSquare(){
            Map<Location,Material> BlockList = new HashMap<Location,Material>();
            Double DiffFrontX = Calculate.getDifference(this.FrontCornerLeft.getX(),this.FrontCornerRigth.getX());
            Double DiffBackX = Calculate.getDifference(this.BackCornerLeft.getX(),this.BackCornerRigth.getX());
            Double DiffLeftZ = Calculate.getDifference(this.FrontCornerLeft.getZ(),this.BackCornerLeft.getZ());
            Double DiffRigthZ = Calculate.getDifference(this.FrontCornerRigth.getZ(),this.BackCornerRigth.getZ());
            //Corner
            //FrontLeft
            BlockList.put(FrontCornerLeft, FrontCornerLeft.getBlock().getType());
            Location FronCornerLeftLoc = new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),this.FrontCornerLeft.getBlockZ())-1,this.FrontCornerLeft.getZ());
            Land testCuboidareafcl = Factoid.getLands().getLand(FronCornerLeftLoc);
            if(testCuboidareafcl == null){
                player.sendBlockChange(FronCornerLeftLoc,Material.SPONGE,this.by);
            }else{
                player.sendBlockChange(FronCornerLeftLoc,Material.REDSTONE_BLOCK,this.by);
                IsCollision = true;
                System.out.print("COLLEFT");
            }
            //FrontRigth
            BlockList.put(FrontCornerRigth, FrontCornerRigth.getBlock().getType());
            Location FrontCornerRigthLoc = new Location(this.world,this.FrontCornerRigth.getX(),this.world.getHighestBlockYAt(this.FrontCornerRigth.getBlockX(),this.FrontCornerRigth.getBlockZ())-1,this.FrontCornerRigth.getZ());
            Land testCuboidareafcr = Factoid.getLands().getLand(FrontCornerRigthLoc);
            if(testCuboidareafcr == null){
                player.sendBlockChange(FrontCornerRigthLoc,Material.SPONGE,this.by);
            }else{
                player.sendBlockChange(FrontCornerRigthLoc,Material.REDSTONE_BLOCK,this.by);
                IsCollision = true;
                System.out.print("COLfrontrigth");
            }
            //BackLeft
            BlockList.put(this.BackCornerLeft, this.BackCornerLeft.getBlock().getType());
            Location BackCornerLeftLoc = new Location(world,BackCornerLeft.getX(),this.world.getHighestBlockYAt(this.BackCornerLeft.getBlockX(),this.BackCornerLeft.getBlockZ())-1,this.BackCornerLeft.getZ());
            Land testCuboidareabcl = Factoid.getLands().getLand(BackCornerLeftLoc);
            if(testCuboidareabcl == null){
                player.sendBlockChange(BackCornerLeftLoc,Material.SPONGE,this.by);
            }else{
                player.sendBlockChange(BackCornerLeftLoc,Material.REDSTONE_BLOCK,this.by);
                IsCollision = true;
                System.out.print("COLbackLEFT");
            }
            //BackRigth
            BlockList.put(this.BackCornerRigth, this.BackCornerRigth.getBlock().getType());
            Location BackCornerRigthLoc = new Location(this.world,this.BackCornerRigth.getX(),this.world.getHighestBlockYAt(this.BackCornerRigth.getBlockX(),this.BackCornerRigth.getBlockZ())-1,this.BackCornerRigth.getZ());
            Land testCuboidareabcr = Factoid.getLands().getLand(BackCornerRigthLoc);
            if(testCuboidareabcr == null){
                player.sendBlockChange(BackCornerRigthLoc,Material.SPONGE,this.by);
            }else{
                player.sendBlockChange(BackCornerRigthLoc,Material.REDSTONE_BLOCK,this.by);
                IsCollision = true;
                System.out.print("COLbackrigth");
            }
            //Front
            for(Double i = 1.0;i<=DiffFrontX;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,Calculate.AdditionDouble(this.FrontCornerLeft.getX(),i),this.world.getHighestBlockYAt(Calculate.AdditionInt(this.FrontCornerLeft.getBlockX(),ii),this.FrontCornerLeft.getBlockZ())-1,this.FrontCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                Land testCuboidarea = Factoid.getLands().getLand(newloc);
                if(testCuboidarea == null){
                    this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
                }else{
                    this.player.sendBlockChange(newloc,Material.REDSTONE_BLOCK,this.by);
                    IsCollision = true;
                    System.out.print("COLFront");
                }
            }
            //Back
            for(Double i = 1.0;i<=DiffBackX;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,Calculate.AdditionDouble(this.BackCornerLeft.getX(),i),this.world.getHighestBlockYAt(Calculate.AdditionInt(this.BackCornerLeft.getBlockX(),ii),this.BackCornerLeft.getBlockZ())-1,this.BackCornerLeft.getZ());
                BlockList.put(newloc, newloc.getBlock().getType());
                Land testCuboidarea = Factoid.getLands().getLand(newloc);
                if(testCuboidarea == null){
                    this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
                }else{
                    this.player.sendBlockChange(newloc,Material.REDSTONE_BLOCK,this.by);
                    IsCollision = true;
                    System.out.print("COLBack");
                }
            }
            //Left
            for(Double i = 1.0;i<=DiffLeftZ;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,this.FrontCornerLeft.getX(),this.world.getHighestBlockYAt(this.FrontCornerLeft.getBlockX(),Calculate.AdditionInt(this.FrontCornerLeft.getBlockZ(),-ii))-1,Calculate.AdditionDouble(this.FrontCornerLeft.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                Land testCuboidarea = Factoid.getLands().getLand(newloc);
                if(testCuboidarea == null){
                    this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
                }else{
                    this.player.sendBlockChange(newloc,Material.REDSTONE_BLOCK,this.by);
                    IsCollision = true;
                    System.out.print("COLLEFT");
                }
            }
            //Rigth
            for(Double i = 1.0;i<=DiffRigthZ;i++){
                int ii = (int)Math.floor(i + 0.5d);
                //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
                Location newloc = new Location(this.world,this.FrontCornerRigth.getX(),this.world.getHighestBlockYAt(this.FrontCornerRigth.getBlockX(),Calculate.AdditionInt(this.FrontCornerRigth.getBlockZ(),-ii))-1,Calculate.AdditionDouble(this.FrontCornerRigth.getZ(),-i));
                BlockList.put(newloc, newloc.getBlock().getType());
                Land testCuboidarea = Factoid.getLands().getLand(newloc);
                if(testCuboidarea == null){
                    this.player.sendBlockChange(newloc,Material.SPONGE,this.by);
                }else{
                    this.player.sendBlockChange(newloc,Material.REDSTONE_BLOCK,this.by);
                    IsCollision = true;
                    System.out.print("COLRiGTH");
                }
            }
            return BlockList;
    }
    
    public Map<String,Location> getCorner(){
        Map<String,Location> CornerList = new HashMap<String,Location>();
        CornerList.put("FrontCornerLeft", this.FrontCornerLeft);
        CornerList.put("BackCornerLeft", this.BackCornerLeft);
        CornerList.put("FrontCornerRigth", this.FrontCornerRigth);
        CornerList.put("BackCornerRigth", this.BackCornerRigth);
        return CornerList;
    }
    
    public boolean getCollision(){
        return IsCollision;
    }
    
}
