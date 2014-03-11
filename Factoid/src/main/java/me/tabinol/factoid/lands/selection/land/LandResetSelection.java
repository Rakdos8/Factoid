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
package me.tabinol.factoid.lands.selection.land;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LandResetSelection extends Thread{
    
    private Player player;
    private World world;
    private byte by = 0;
    private Map<Location,Material> BlockList = new HashMap<Location,Material>();
    private Map<String,Location> CornerList = new HashMap<String,Location>();
    
    public LandResetSelection(Map<Location,Material> BlockList,Map<String,Location> CornerList,Player player){
        this.BlockList = BlockList;
        this.CornerList = CornerList;
        this.player = player;
    }
    
    public boolean Reset(){
        if(!this.BlockList.isEmpty()){
            for(Map.Entry<Location, Material> EntrySet : this.BlockList.entrySet()){
                        this.player.sendBlockChange(EntrySet.getKey(),EntrySet.getValue(),this.by);
            }
        }
        
        if(!this.CornerList.isEmpty()){
            this.CornerList.clear();
        }
        return true;
    }
}
