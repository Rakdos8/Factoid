package me.tabinol.factoid.lands.expansion;

import java.util.HashMap;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.block.BlockFace;


public class LandExpandSquare extends Thread{
    
    private Player player;
    private World world;
    private byte by = 0;
    private Location FrontCornerLeft;
    private Location BackCornerLeft;
    private Location FrontCornerRigth;
    private Location BackCornerRigth;
    private boolean IsCollisionFront = false;
    private boolean IsCollisionBack = false;
    private boolean IsCollisionLeft = false;
    private boolean IsCollisionRigth = false;
    private boolean SELECTING = false;
    private String direction = null;
    
    
    public LandExpandSquare(Player player, String direction,Location loc, int x1, int x2, int y1, int y2, int z1, int z2, boolean isSelecting){
        this.player = player;
        this.world = player.getWorld();
        this.direction = direction;
        
        if(direction.equalsIgnoreCase("TopLeft")){
            if (loc != null) {
                FrontCornerLeft = new Location(this.world, loc.getX(), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), -6), Calculate.AdditionInt(loc.getBlockZ(), 6)) - 1, loc.getZ());
                BackCornerLeft = new Location(this.world, Calculate.AdditionDouble(loc.getX(), -6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), -6), Calculate.AdditionInt(loc.getBlockZ(), -6)) - 1, loc.getZ());
                FrontCornerRigth = new Location(this.world, loc.getX(), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), 6), Calculate.AdditionInt(loc.getBlockZ(), 6)) - 1, Calculate.AdditionDouble(loc.getZ(), 6.0));
                BackCornerRigth = new Location(this.world, Calculate.AdditionDouble(loc.getX(), 6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), 6), Calculate.AdditionInt(loc.getBlockZ(), -6)) - 1, Calculate.AdditionDouble(loc.getZ(), -6.0));
            } 
        }else if(direction.equalsIgnoreCase("TopRight")){
            
        }else if(direction.equalsIgnoreCase("BottomLeft")){
            
        }else if(direction.equalsIgnoreCase("BottomRight")){
            
        }
        
        
        if(isSelecting) {
            SELECTING = true;
        }
    }
    
    public Map<Location,Material> expandSquare(){
        Map<Location, Material> BlockList = new HashMap<Location, Material>();
        Double DiffFrontX = Calculate.getDifference(this.FrontCornerLeft.getX(), this.FrontCornerRigth.getX());
        Double DiffBackX = Calculate.getDifference(this.BackCornerLeft.getX(), this.BackCornerRigth.getX());
        Double DiffLeftZ = Calculate.getDifference(this.FrontCornerLeft.getZ(), this.BackCornerLeft.getZ());
        Double DiffRigthZ = Calculate.getDifference(this.FrontCornerRigth.getZ(), this.BackCornerRigth.getZ());
         //Corner
        //FrontLeft
        BlockList.put(FrontCornerLeft, FrontCornerLeft.getBlock().getType());
        Location FronCornerLeftLoc = new Location(this.world, this.FrontCornerLeft.getX(), this.getYNearPlayer(this.FrontCornerLeft.getBlockX(), this.FrontCornerLeft.getBlockZ()) - 1, this.FrontCornerLeft.getZ());
        Land testCuboidareafcl = Factoid.getLands().getLand(FronCornerLeftLoc);
        if (testCuboidareafcl == null) {
            player.sendBlockChange(FronCornerLeftLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(FronCornerLeftLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionFront = true;
            } else {
                player.sendBlockChange(FronCornerLeftLoc, Material.BEACON, this.by);

                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX(), FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() - 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() + 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX(), FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX(), FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() - 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() + 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() - 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FronCornerLeftLoc.getX() + 1, FronCornerLeftLoc.getY() - 1, FronCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                }
            }
        }
        //FrontRigth
        BlockList.put(FrontCornerRigth, FrontCornerRigth.getBlock().getType());
        Location FrontCornerRigthLoc = new Location(this.world, this.FrontCornerRigth.getX(), this.getYNearPlayer(this.FrontCornerRigth.getBlockX(), this.FrontCornerRigth.getBlockZ()) - 1, this.FrontCornerRigth.getZ());
        Land testCuboidareafcr = Factoid.getLands().getLand(FrontCornerRigthLoc);
        if (testCuboidareafcr == null) {
            player.sendBlockChange(FrontCornerRigthLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(FrontCornerRigthLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionFront = true;
            } else {
                player.sendBlockChange(FrontCornerRigthLoc, Material.BEACON, this.by);
                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX(), FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() - 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() + 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX(), FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX(), FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() - 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() + 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() - 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRigthLoc.getX() + 1, FrontCornerRigthLoc.getY() - 1, FrontCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                }
            }
        }
        //BackLeft
        BlockList.put(this.BackCornerLeft, this.BackCornerLeft.getBlock().getType());
        Location BackCornerLeftLoc = new Location(world, BackCornerLeft.getX(), this.getYNearPlayer(this.BackCornerLeft.getBlockX(), this.BackCornerLeft.getBlockZ()) - 1, this.BackCornerLeft.getZ());
        Land testCuboidareabcl = Factoid.getLands().getLand(BackCornerLeftLoc);
        if (testCuboidareabcl == null) {
            player.sendBlockChange(BackCornerLeftLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(BackCornerLeftLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionBack = true;
            } else {
                player.sendBlockChange(BackCornerLeftLoc, Material.BEACON, this.by);
                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX(), BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() - 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() + 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX(), BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX(), BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() - 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() + 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() - 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerLeftLoc.getX() + 1, BackCornerLeftLoc.getY() - 1, BackCornerLeftLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                }
            }
        }
        //BackRigth
        BlockList.put(this.BackCornerRigth, this.BackCornerRigth.getBlock().getType());
        Location BackCornerRigthLoc = new Location(this.world, this.BackCornerRigth.getX(), this.getYNearPlayer(this.BackCornerRigth.getBlockX(), this.BackCornerRigth.getBlockZ()) - 1, this.BackCornerRigth.getZ());
        Land testCuboidareabcr = Factoid.getLands().getLand(BackCornerRigthLoc);
        if (testCuboidareabcr == null) {
            player.sendBlockChange(BackCornerRigthLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(BackCornerRigthLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionBack = true;
            } else {
                player.sendBlockChange(BackCornerRigthLoc, Material.BEACON, this.by);
                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX(), BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() - 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() + 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX(), BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX(), BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() - 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() + 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() - 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRigthLoc.getX() + 1, BackCornerRigthLoc.getY() - 1, BackCornerRigthLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                }
            }
        }
        return BlockList;
    }
    
    public Map<String, Location> getCorner() {
        Map<String, Location> CornerList = new HashMap<String, Location>();
        CornerList.put("FrontCornerLeft", this.FrontCornerLeft);
        CornerList.put("BackCornerLeft", this.BackCornerLeft);
        CornerList.put("FrontCornerRigth", this.FrontCornerRigth);
        CornerList.put("BackCornerRigth", this.BackCornerRigth);
        return CornerList;
    }

    public boolean getCollision() {

        if (IsCollisionFront || IsCollisionBack || IsCollisionLeft || IsCollisionRigth) {
            return true;
        }

        return false;
    }

    // Get the nearest block from player before air
    private int getYNearPlayer(int x, int z) {

        Location loc = new Location(world, x, player.getLocation().getY() - 1, z);

        if (loc.getBlock().getType() == Material.AIR) {
            while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
                    && loc.getBlockY() != 1) {
                loc.subtract(0, 1, 0);
            }
        } else {
            while (loc.getBlock().getType() != Material.AIR && loc.getBlockY() != world.getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }
}
