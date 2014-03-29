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
package me.tabinol.factoid.lands.expansion;

import java.util.Map;
import java.util.HashMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.areas.CuboidArea;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.event.player.PlayerInteractEvent;

public class LandExpansion extends Thread implements Listener{
    
    private Player player;
    private World world;
    private byte by = 0;
    private boolean isSelected = false;
    private Map<Location, Material> BlockList = new HashMap<Location, Material>();
    private Map<String, Location> CornerList = new HashMap<String, Location>();
    private Location LandPos;
    private boolean IsCollision;
    private int trueY1;
    private int trueY2;
    private boolean TopRight = false;
    private boolean TopLeft = false;
    private boolean BottomRight = false;
    private boolean BottomLeft = false;
    private boolean isDone = false;
    private String direction = null;
    
    public LandExpansion(Player player){
        LandExpansion(player, player.getLocation(), 0, 0, 
        Factoid.getConf().getMinLandHigh(), Factoid.getConf().getMaxLandHigh(), 0, 0, false);
    }
    
    // For WorldEdit
    public LandExpansion(Player player, int x1, int x2, int y1, int y2, int z1, int z2) {

        LandExpansion(player, null, x1, x2, y1, y2, z1, z2, false);
    }
    
    private void LandExpansion(Player player, Location loc, 
            int x1, int x2, int y1, int y2, int z1, int z2, boolean isSelection) {
        this.trueY1 = y1;
        this.trueY2 = y2;
        Factoid.getThisPlugin().getServer().getPluginManager().registerEvents(this, Factoid.getThisPlugin());
        this.player = player;
        this.world = player.getWorld();
        LandExpandSquare landmake = new LandExpandSquare(player, direction, loc, x1, x2, y1, y2, z1, z2, isSelection);
        this.BlockList = landmake.expandSquare();
        this.CornerList = landmake.getCorner();
        this.LandPos = player.getLocation();
        this.IsCollision = landmake.getCollision();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(isDone){
            if (!this.isSelected) {
                if (!this.BlockList.isEmpty() && !this.CornerList.isEmpty()) {
                    if (event.getClickedBlock() == player.getWorld().getBlockAt(CornerList.get("FrontCornerLeft"))){
                        TopLeft = true;
                        direction = "TopLeft";
                        CornerList.remove("FrontCornerLeft");
                    }else if(event.getClickedBlock() == player.getWorld().getBlockAt(CornerList.get("BackCornerLeft"))){
                        BottomLeft = true;
                        direction = "BottomLeft";
                        CornerList.remove("BackCornerLeft");
                    }else if(event.getClickedBlock() == player.getWorld().getBlockAt(CornerList.get("FrontCornerRight"))){ 
                        TopRight = true;
                        direction = "TopRight";
                        CornerList.remove("FrontCornerRight");
                    }else if(event.getClickedBlock() == player.getWorld().getBlockAt(CornerList.get("BackCornerRight"))) {
                        BottomRight = true;
                        direction = "BottomRight";
                        CornerList.remove("BackCornerRight");

                    }

                    if (TopRight || TopLeft || BottomRight || BottomLeft) {
                            isSelected = true;
                            System.out.print("Selected Coner");
                            player.sendBlockChange(event.getClickedBlock().getLocation(), event.getClickedBlock().getType(), this.by);
                            event.setCancelled(true);
                    }
                }
            }else{
                Location loc = event.getClickedBlock().getLocation();
                if(TopRight){
                    CornerList.put("FrontCornerRight", loc);
                }else if(TopLeft){
                    CornerList.put("FrontCornerLeft", loc);
                }else if(BottomLeft){
                    CornerList.put("BackCornerLeft", loc);
                }else if(BottomRight){
                    CornerList.put("BackCornerRight", loc);
                }
                boolean done = new LandResetExpansion(this.BlockList, this.CornerList, this.player).Reset();
                if (done) {
                    System.out.print("Created Coner");
                    this.BlockList.clear();
                    LandExpandSquare landmake = new LandExpandSquare(this.player, direction, event.getClickedBlock().getLocation(), 0, 0, 0, 0, 0, 0, false);
                    this.BlockList = landmake.expandSquare();
                    this.CornerList = landmake.getCorner();
                    this.LandPos = event.getClickedBlock().getLocation();
                    this.IsCollision = landmake.getCollision();
                }
                event.setCancelled(true);
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

    public void setDone() {
        this.isDone = true;
    }

    public Location getSelection() {
        return this.LandPos;
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
            boolean done = new LandResetExpansion(this.BlockList, this.CornerList, this.player).Reset();
            if (done) {
                this.BlockList.clear();
            }
        }
    }

    public boolean getCollision() {
        return IsCollision;
    }
}
