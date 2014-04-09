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
import java.util.HashSet;
import java.util.Iterator;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
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
        if (sel != null) {
            return sel.getLand();
        } else {
            return null;
        }
    }

    public CuboidArea getCuboidArea() {

        AreaSelection sel = (AreaSelection) selectionList.get(SelectionType.AREA);
        if (sel != null) {
            return sel.getCuboidArea();
        } else {
            return null;
        }
    }

    public double getLandCreatePrice() {

        if (Factoid.getPlayerMoney() == null) {
            return 0;
        }

        Land land = getLand();
        CuboidArea area = getCuboidArea();
        LandFlag priceFlag;

        // Get land price
        if (land == null) {
            priceFlag = Factoid.getLands().getOutsideArea(area.getWorldName()).getFlagAndInherit(FlagType.ECO_BLOCK_PRICE);
        } else {
            priceFlag = land.getFlagAndInherit(FlagType.ECO_BLOCK_PRICE);
        }

        // Not set, return 0
        if (priceFlag == null) {
            return 0;
        }

        return priceFlag.getValueDouble() * area.getTotalBlock();
    }

    public double getAreaAddPrice() {

        if (Factoid.getPlayerMoney() == null) {
            return 0;
        }

        Land land = getLand();
        CuboidArea area = getCuboidArea();
        LandFlag priceFlag;

        // The area is from parent ask parent
        if (land.getParent() == null) {
            priceFlag = Factoid.getLands().getOutsideArea(area.getWorldName()).getFlagAndInherit(FlagType.ECO_BLOCK_PRICE);
        } else {
            priceFlag = land.getParent().getFlagAndInherit(FlagType.ECO_BLOCK_PRICE);
        }

        // Not set, return 0
        if (priceFlag == null) {
            return 0;
        }

        // Remove already here area
        Collection<CuboidArea> areas = new HashSet<CuboidArea>();
        areas.add(area);
        Iterator<CuboidArea> iterator = land.getAreas().iterator();

        while (iterator.hasNext()) {
            CuboidArea parentArea = iterator.next();
            Collection<CuboidArea> areasNew = new HashSet<CuboidArea>();
            for (CuboidArea areaC : areas) {
                areasNew.addAll(parentArea.getOutside(areaC));
            }

            // Exit if no areas is returned (the child area is inside)
            if (areasNew.isEmpty()) {
                return 0;
            }

            areas = areasNew;
        }

        // get total areas cube
        long nbCube = 0;
        for(CuboidArea areaRes : areas) {
            nbCube += areaRes.getTotalBlock();
        }
        
        return priceFlag.getValueDouble() * nbCube;
    }

    public double getAreaReplacePrice(int areaId) {

        // Check only with Area add. No refound for reduced area.
        return getAreaAddPrice();
    }
}
