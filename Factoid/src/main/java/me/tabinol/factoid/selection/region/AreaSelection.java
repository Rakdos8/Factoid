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
package me.tabinol.factoid.selection.region;

import static java.lang.Math.abs;
import java.util.HashMap;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AreaSelection extends RegionSelection implements Listener {

    CuboidArea area;
    boolean isCollision = false;
    private final byte by = 0;
    private final Map<Location, Material> blockList = new HashMap<Location, Material>();
    private boolean isFromLand = false;

    public AreaSelection(Player player, CuboidArea area) {

        super(SelectionType.AREA, player);
        this.area = area;
        
        makeVisualSelection();
    }

    // Called from Land Selection list
    public AreaSelection(Player player, CuboidArea area, boolean isFromLand) {

        super(SelectionType.AREA, player);
        this.area = area;
        this.isFromLand = isFromLand;
        
        makeVisualSelection();
    }

    // Called from ActiveAreaSelection
    AreaSelection(Player player) {

        super(SelectionType.AREA, player);
    }

    final void makeVisualSelection() {

        // Get the size (x and z) no abs (already ajusted)
        int diffX = area.getX2() - area.getX1();
        int diffZ = area.getZ2() - area.getZ1();

        // Do not show a too big select to avoid crash or severe lag
        int maxSize = Factoid.getConf().getMaxVisualSelect();
        int maxDisPlayer = Factoid.getConf().getMaxVisualSelectFromPlayer();
        Location playerLoc = player.getLocation();
        if (diffX > maxSize || diffZ > maxSize
                || abs(area.getX1() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getX2() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getZ1() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(area.getZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
            Factoid.getLog().write("Selection disabled!");
            return;
        }

        //MakeSquare
        for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
                if (posX == area.getX1() || posX == area.getX2()
                        || posZ == area.getZ1() || posZ == area.getZ2()) {

                    Location newloc = new Location(area.getWord(), posX, this.getYNearPlayer(posX, posZ) - 1, posZ);
                    blockList.put(newloc, newloc.getBlock().getType());

                    if (!isFromLand) {

                        // Active Selection
                        Land testCuboidarea = Factoid.getLands().getLand(newloc);
                        if (testCuboidarea == null) {
                            this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
                        } else {
                            this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
                            isCollision = true;
                        }
                    } else {

                        // Passive Selection (created area)
                        if ((posX == area.getX1() && posZ == area.getZ1() + 1)
                                || (posX == area.getX1() && posZ == area.getZ2() - 1)
                                || (posX == area.getX2() && posZ == area.getZ1() + 1)
                                || (posX == area.getX2() && posZ == area.getZ2() - 1)
                                || (posX == area.getX1() + 1 && posZ == area.getZ1())
                                || (posX == area.getX2() - 1 && posZ == area.getZ1())
                                || (posX == area.getX1() + 1 && posZ == area.getZ2())
                                || (posX == area.getX2() - 1 && posZ == area.getZ2())) {

                            // Subcorner
                            this.player.sendBlockChange(newloc, Material.IRON_BLOCK, this.by);

                        } else if ((posX == area.getX1() && posZ == area.getZ1())
                                || (posX == area.getX2() && posZ == area.getZ1())
                                || (posX == area.getX1() && posZ == area.getZ2())
                                || (posX == area.getX2() && posZ == area.getZ2())) {

                            // Exact corner
                            this.player.sendBlockChange(newloc, Material.BEACON, this.by);
                        }
                    }

                } else {
                    // Square center, skip!
                    posZ = area.getZ2() - 1;
                }
            }
        }
    }

    @Override
    public void removeSelection() {

        for (Map.Entry<Location, Material> EntrySet : this.blockList.entrySet()) {
            this.player.sendBlockChange(EntrySet.getKey(), EntrySet.getValue(), this.by);
        }

        blockList.clear();
    }

    public CuboidArea getCuboidArea() {
        
        return area;
    }
    
    public boolean getCollision() {
        
        return isCollision;
    }
    
     // Get the nearest block from player before air
    private int getYNearPlayer(int x, int z) {

        Location loc = new Location(player.getWorld(), x, player.getLocation().getY() - 1, z);

        if (loc.getBlock().getType() == Material.AIR) {
            while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
                    && loc.getBlockY() > 1) {
                loc.subtract(0, 1, 0);
            }
        } else {
            while (loc.getBlock().getType() != Material.AIR && loc.getBlockY() < player.getWorld().getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }
}
