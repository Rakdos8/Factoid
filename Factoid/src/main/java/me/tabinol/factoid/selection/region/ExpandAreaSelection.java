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

import me.tabinol.factoid.lands.areas.CuboidArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ExpandAreaSelection extends ActiveAreaSelection implements PlayerMoveListen {

    public ExpandAreaSelection(Player player) {

        super(player);
        
        // Take first active selection from parent
        setActiveSelection();
    }

    // When we know the area
    public ExpandAreaSelection(Player player, CuboidArea area) {

        super(player);
        
        // Take first active selection from parent
        this.area = area;
        makeVisualSelection();
    }

    // Called from player listenner
    @Override
    public void playerMove() {

        removeSelection();
        Location playerLoc = player.getLocation();
        
        // Check where the player is outside the land
        if(playerLoc.getBlockX() - 1 < area.getX1()) {
            area.setX1(playerLoc.getBlockX() - 1);
        }
        if(playerLoc.getBlockX() + 1 > area.getX2()) {
            area.setX2(playerLoc.getBlockX() + 1);
        }
        if(playerLoc.getBlockZ() - 1 < area.getZ1()) {
            area.setZ1(playerLoc.getBlockZ() - 1);
        }
        if(playerLoc.getBlockZ() + 1 > area.getZ2()) {
            area.setZ2(playerLoc.getBlockZ() + 1);
        }
        
        makeVisualSelection();
    }
}