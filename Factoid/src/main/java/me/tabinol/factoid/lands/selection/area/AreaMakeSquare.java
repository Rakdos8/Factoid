/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.lands.selection.area;

import static java.lang.Math.abs;
import java.util.HashMap;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.*;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class AreaMakeSquare extends Thread {

    private Player player;
    private World world;
    private byte by = 0;
    private Location FrontCornerLeft;
    private Location BackCornerLeft;
    private Location FrontCornerRight;
    private Location BackCornerRight;
    private boolean IsCollisionFront = false;
    private boolean IsCollisionBack = false;
    private boolean IsCollisionLeft = false;
    private boolean IsCollisionRight = false;
    private boolean SELECTING = false;

    public AreaMakeSquare(Player player, Location loc, int x1, int x2, int y1, int y2, int z1, int z2, boolean isSelecting) {
        this.player = player;
        this.world = player.getWorld();
        if (loc != null) {
            FrontCornerLeft = new Location(this.world, Calculate.AdditionDouble(loc.getX(), -6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), -6), Calculate.AdditionInt(loc.getBlockZ(), 6)) - 1, Calculate.AdditionDouble(loc.getZ(), 6.0));
            BackCornerLeft = new Location(this.world, Calculate.AdditionDouble(loc.getX(), -6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), -6), Calculate.AdditionInt(loc.getBlockZ(), -6)) - 1, Calculate.AdditionDouble(loc.getZ(), -6.0));
            FrontCornerRight = new Location(this.world, Calculate.AdditionDouble(loc.getX(), 6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), 6), Calculate.AdditionInt(loc.getBlockZ(), 6)) - 1, Calculate.AdditionDouble(loc.getZ(), 6.0));
            BackCornerRight = new Location(this.world, Calculate.AdditionDouble(loc.getX(), 6.0), this.getYNearPlayer(Calculate.AdditionInt(loc.getBlockX(), 6), Calculate.AdditionInt(loc.getBlockZ(), -6)) - 1, Calculate.AdditionDouble(loc.getZ(), -6.0));
        } else {
            FrontCornerLeft = new Location(this.world, x1, this.getYNearPlayer(x1, z1) - 1, z1);
            BackCornerLeft = new Location(this.world, x1, this.getYNearPlayer(x2, z2) - 1, z2);
            FrontCornerRight = new Location(this.world, x2, this.getYNearPlayer(x1, z1) - 1, z1);
            BackCornerRight = new Location(this.world, x2, this.getYNearPlayer(x2, z2) - 1, z2);
        } 
        if(isSelecting) {
            SELECTING = true;
        }
    }

    public Map<Location, Material> makeSquare() {
        Map<Location, Material> BlockList = new HashMap<Location, Material>();
        Double DiffFrontX = Calculate.getDifference(this.FrontCornerLeft.getX(), this.FrontCornerRight.getX());
        Double DiffBackX = Calculate.getDifference(this.BackCornerLeft.getX(), this.BackCornerRight.getX());
        Double DiffLeftZ = Calculate.getDifference(this.FrontCornerLeft.getZ(), this.BackCornerLeft.getZ());
        Double DiffRightZ = Calculate.getDifference(this.FrontCornerRight.getZ(), this.BackCornerRight.getZ());
        
        // Do not show a too big select to avoid crash or severe lag
        int maxSize = Factoid.getConf().getMaxVisualSelect();
        int maxDisPlayer = Factoid.getConf().getMaxVisualSelectFromPlayer();
        Location playerLoc = player.getLocation();
        if(DiffFrontX > maxSize || DiffBackX > maxSize || DiffLeftZ > maxSize || DiffRightZ > maxSize
                || abs(FrontCornerLeft.getBlockX() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(FrontCornerRight.getBlockX() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(BackCornerLeft.getBlockX() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(BackCornerRight.getBlockX() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(FrontCornerLeft.getBlockZ() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(FrontCornerRight.getBlockZ() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(BackCornerLeft.getBlockZ() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(BackCornerRight.getBlockZ() - playerLoc.getBlockZ()) > maxDisPlayer) {
            Factoid.getLog().write("Selection disabled!");
            return BlockList;
        }

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
        //FrontRight
        BlockList.put(FrontCornerRight, FrontCornerRight.getBlock().getType());
        Location FrontCornerRightLoc = new Location(this.world, this.FrontCornerRight.getX(), this.getYNearPlayer(this.FrontCornerRight.getBlockX(), this.FrontCornerRight.getBlockZ()) - 1, this.FrontCornerRight.getZ());
        Land testCuboidareafcr = Factoid.getLands().getLand(FrontCornerRightLoc);
        if (testCuboidareafcr == null) {
            player.sendBlockChange(FrontCornerRightLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(FrontCornerRightLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionFront = true;
            } else {
                player.sendBlockChange(FrontCornerRightLoc, Material.BEACON, this.by);
                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX(), FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() - 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() + 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX(), FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX(), FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() - 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() + 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() - 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, FrontCornerRightLoc.getX() + 1, FrontCornerRightLoc.getY() - 1, FrontCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
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
        //BackRight
        BlockList.put(this.BackCornerRight, this.BackCornerRight.getBlock().getType());
        Location BackCornerRightLoc = new Location(this.world, this.BackCornerRight.getX(), this.getYNearPlayer(this.BackCornerRight.getBlockX(), this.BackCornerRight.getBlockZ()) - 1, this.BackCornerRight.getZ());
        Land testCuboidareabcr = Factoid.getLands().getLand(BackCornerRightLoc);
        if (testCuboidareabcr == null) {
            player.sendBlockChange(BackCornerRightLoc, Material.SPONGE, this.by);
        } else {
            if (!SELECTING) {
                player.sendBlockChange(BackCornerRightLoc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionBack = true;
            } else {
                player.sendBlockChange(BackCornerRightLoc, Material.BEACON, this.by);
                if (Factoid.getConf().isBeaconLight()) {
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX(), BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() - 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() + 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ()), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX(), BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX(), BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() - 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() + 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() - 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() - 1), Material.IRON_BLOCK, this.by);
                    player.sendBlockChange(new Location(world, BackCornerRightLoc.getX() + 1, BackCornerRightLoc.getY() - 1, BackCornerRightLoc.getZ() + 1), Material.IRON_BLOCK, this.by);
                }
            }
        }
        //Front
        for (Double i = 1.0; i <= DiffFrontX; i++) {
            int ii = (int) Math.floor(i + 0.5d);
            //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
            Location newloc = new Location(this.world, Calculate.AdditionDouble(this.FrontCornerLeft.getX(), i), this.getYNearPlayer(Calculate.AdditionInt(this.FrontCornerLeft.getBlockX(), ii), this.FrontCornerLeft.getBlockZ()) - 1, this.FrontCornerLeft.getZ());
            BlockList.put(newloc, newloc.getBlock().getType());
            Land testCuboidarea = Factoid.getLands().getLand(newloc);
            if (testCuboidarea == null) {
                this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
            } else if (!SELECTING) {
                this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionFront = true;
            }
        }
        //Back
        for (Double i = 1.0; i <= DiffBackX; i++) {
            int ii = (int) Math.floor(i + 0.5d);
            //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
            Location newloc = new Location(this.world, Calculate.AdditionDouble(this.BackCornerLeft.getX(), i), this.getYNearPlayer(Calculate.AdditionInt(this.BackCornerLeft.getBlockX(), ii), this.BackCornerLeft.getBlockZ()) - 1, this.BackCornerLeft.getZ());
            BlockList.put(newloc, newloc.getBlock().getType());
            Land testCuboidarea = Factoid.getLands().getLand(newloc);
            if (testCuboidarea == null) {
                this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
            } else if (!SELECTING) {
                this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionBack = true;
            }
        }
        //Left
        for (Double i = 1.0; i <= DiffLeftZ; i++) {
            int ii = (int) Math.floor(i + 0.5d);
            //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
            Location newloc = new Location(this.world, this.FrontCornerLeft.getX(), this.getYNearPlayer(this.FrontCornerLeft.getBlockX(), Calculate.AdditionInt(this.FrontCornerLeft.getBlockZ(), -ii)) - 1, Calculate.AdditionDouble(this.FrontCornerLeft.getZ(), -i));
            BlockList.put(newloc, newloc.getBlock().getType());
            Land testCuboidarea = Factoid.getLands().getLand(newloc);
            if (testCuboidarea == null) {
                this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
            } else if (!SELECTING) {
                this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionLeft = true;
            }
        }
        //Right
        for (Double i = 1.0; i <= DiffRightZ; i++) {
            int ii = (int) Math.floor(i + 0.5d);
            //player.sendMessage(ChatColor.GRAY+"[Factoid] BlockChange:"+getConvert(FrontCornerLeft.getX(),i)+","+FrontCornerLeft.getY()+","+FrontCornerLeft.getZ());
            Location newloc = new Location(this.world, this.FrontCornerRight.getX(), this.getYNearPlayer(this.FrontCornerRight.getBlockX(), Calculate.AdditionInt(this.FrontCornerRight.getBlockZ(), -ii)) - 1, Calculate.AdditionDouble(this.FrontCornerRight.getZ(), -i));
            BlockList.put(newloc, newloc.getBlock().getType());
            Land testCuboidarea = Factoid.getLands().getLand(newloc);
            if (testCuboidarea == null) {
                this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
            } else if (!SELECTING) {
                this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
                IsCollisionRight = true;
            }
        }
        return BlockList;
    }

    public Map<String, Location> getCorner() {
        Map<String, Location> CornerList = new HashMap<String, Location>();
        CornerList.put("FrontCornerLeft", this.FrontCornerLeft);
        CornerList.put("BackCornerLeft", this.BackCornerLeft);
        CornerList.put("FrontCornerRight", this.FrontCornerRight);
        CornerList.put("BackCornerRight", this.BackCornerRight);
        return CornerList;
    }

    public boolean getCollision() {

        if (IsCollisionFront || IsCollisionBack || IsCollisionLeft || IsCollisionRight) {
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
