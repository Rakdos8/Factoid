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

import java.util.Map;
import java.util.HashMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.areas.CuboidArea;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class AreaSelection extends Thread implements Listener {

    private Player player;
    private World world;
    private byte by = 0;
    private boolean isSelected = false;
    private Map<Location, Material> BlockList = new HashMap<Location, Material>();
    private Map<String, Location> CornerList = new HashMap<String, Location>();
    private Location AreaPos;
    private boolean IsCollision;
    private int trueY1;
    private int trueY2;

    public AreaSelection(Player player) {
        LandSelection(player, player.getLocation(), 0, 0, 
        Factoid.getConf().getMaxLandHigh(), Factoid.getConf().getMaxLandHigh(), 0, 0, false);
    }

    // public LandSelection(Player player, Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {

    //    LandSelection(player, loc, x1, x2, y1, y2, z1, z2, loc == null);
    // }

    // For WorldEdit
    public AreaSelection(Player player, int x1, int x2, int y1, int y2, int z1, int z2) {

        LandSelection(player, null, x1, x2, y1, y2, z1, z2, false);
    }
    private void LandSelection(Player player, Location loc, 
            int x1, int x2, int y1, int y2, int z1, int z2, boolean isSelection) {
        this.trueY1 = y1;
        this.trueY2 = y2;
        Factoid.getThisPlugin().getServer().getPluginManager().registerEvents(this, Factoid.getThisPlugin());
        this.player = player;
        this.world = player.getWorld();
        AreaMakeSquare Areamake = new AreaMakeSquare(player, loc, x1, x2, y1, y2, z1, z2, isSelection);
        this.BlockList = Areamake.makeSquare();
        this.CornerList = Areamake.getCorner();
        this.AreaPos = player.getLocation();
        this.IsCollision = Areamake.getCollision();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!this.isSelected) {
            if (event.getFrom() != event.getTo()) {
                if (event.getPlayer().getName().equals(this.player.getName())) {
                    if (!this.BlockList.isEmpty() && !this.CornerList.isEmpty()) {
                        boolean done = new AreaResetSelection(this.BlockList, this.CornerList, this.player).Reset();
                        if (done) {
                            this.BlockList.clear();
                            AreaMakeSquare landmake = new AreaMakeSquare(this.player, event.getTo(), 0, 0, 0, 0, 0, 0, false);
                            this.BlockList = landmake.makeSquare();
                            this.CornerList = landmake.getCorner();
                            this.AreaPos = event.getTo();
                            this.IsCollision = landmake.getCollision();
                        }
                    }
                }
            }
        }
    }

    public Map<Location, Material> getSquare() {
        return this.BlockList;
    }

    public Player getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public void setSelected() {
        this.isSelected = true;
    }

    public Location getSelection() {
        return this.AreaPos;
    }

    public Map<String, Location> getCorner() {
        return this.CornerList;
    }

    public CuboidArea toCuboidArea() {

        int x1 = CornerList.get("FrontCornerLeft").getBlockX();
        int x2 = CornerList.get("BackCornerRight").getBlockX();
        int y1 = trueY1;
        int y2 = trueY2;
        int z1 = CornerList.get("FrontCornerLeft").getBlockZ();
        int z2 = CornerList.get("BackCornerRight").getBlockZ();
        
        return new CuboidArea(player.getWorld().getName(), x1, y1, z1, x2, y2, z2);
    }

    public void resetSelection() {
        if (!this.BlockList.isEmpty() && !this.CornerList.isEmpty()) {
            boolean done = new AreaResetSelection(this.BlockList, this.CornerList, this.player).Reset();
            if (done) {
                this.BlockList.clear();
            }
        }
    }

    public boolean getCollision() {
        return IsCollision;
    }
}