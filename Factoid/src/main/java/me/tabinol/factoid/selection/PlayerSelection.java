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
package me.tabinol.factoid.selection;

import java.util.Collection;
import java.util.EnumMap;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.LandSelection;
import me.tabinol.factoid.selection.region.RegionSelection;

public class PlayerSelection {

    /**
     * Selection Type
     */
    public enum SelectionType { // ACTIVE = move with the player, PASSIVE = fixed

        LAND,
        AREA;
    }

    private final EnumMap<SelectionType, RegionSelection> selectionList; // SelectionList for the player

    public PlayerSelection() {

        selectionList = new EnumMap<SelectionType, RegionSelection>(SelectionType.class);
    }

    public boolean hasSelection() {
        
        return !selectionList.isEmpty();
    }
    
    public Collection<RegionSelection> getSelections() {
        
        return selectionList.values();
    }
    
    public void addSelection(RegionSelection sel) {
        
        selectionList.put(sel.getSelectionType(), sel);
    }
    
    public RegionSelection getSelection(SelectionType type) {
        
        return selectionList.get(type);
    }
    
    public RegionSelection removeSelection(SelectionType type) {
        
        return selectionList.remove(type);
    }
    
    public Land getLand() {
        
        LandSelection sel = (LandSelection) selectionList.get(SelectionType.LAND);
        if(sel != null) {
            return sel.getLand();
        } else {
            return null;
        }
    }
    
    public CuboidArea getCuboidArea() {
        
        AreaSelection sel = (AreaSelection) selectionList.get(SelectionType.AREA);
        if(sel != null) {
            return sel.getCuboidArea();
        } else {
            return null;
        }
    }
}
