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
package me.tabinol.factoid.lands.collisions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;

public class Collisions {

    public enum LandAction {

        LAND_ADD,
        LAND_RENAME,
        LAND_REMOVE,
        AREA_ADD,
        AREA_REMOVE,
        AREA_MODIFY;
    }

    public enum LandError {

        COLLISION(true),
        NAME_IN_USE(false),
        HAS_CHILDREN(false),
        CHILD_OUT_OF_BORDER(true),
        OUT_OF_PARENT(true),
        IN_APPROVE_LIST(false),
        NOT_ENOUGH_MONEY(false);

        public final boolean canBeApproved; // False = No approve is possible

        private LandError(boolean canBeApproved) {
            this.canBeApproved = canBeApproved;
        }
    }

    private final List<CollisionsEntry> coll;
    private final Lands lands;
    private final String landName;
    private final Land land;
    private final LandAction action;
    private final int removedAreaId;
    private final CuboidArea newArea;
    private final Land parent;
    private final double price;
    private final PlayerContainer owner;
    private boolean allowApprove;

    public Collisions(String landName, Land land, LandAction action, int removedAreaId, CuboidArea newArea, Land parent,
            PlayerContainer owner, double price, boolean checkApproveList) {

        coll = new ArrayList<CollisionsEntry>();
        lands = Factoid.getLands();
        this.landName = landName;
        this.land = land;
        this.action = action;
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.parent = parent;
        this.owner = owner;
        this.price = price;

        // Pass 1 check if there is a collision
        if (action == LandAction.LAND_ADD || action == LandAction.AREA_ADD || action == LandAction.AREA_MODIFY) {
            checkCollisions();

            // Pass 2 check if the the cuboid is inside the parent
            if (parent != null) {
                checkIfInsideParent();
            }
        }

        // Pass 3 check if children are not out of land
        if ((action == LandAction.AREA_MODIFY || action == LandAction.AREA_REMOVE)
                && !land.getChildren().isEmpty()) {
            checkIfChildrenOutside();
        }

        // Pass 4 check if the deleted land has children
        if (action == LandAction.LAND_REMOVE) {
            checkIfLandHasChildren();
        }

        // Pass 5 check if the name is allready existing
        if (action == LandAction.LAND_ADD || action == LandAction.LAND_RENAME) {
            checkIfNameExist();
        }

        // Pass 6 check if the name is allready in Approve List
        if (!checkApproveList && lands.getApproveList().isInApprove(landName)) {
            coll.add(new CollisionsEntry(LandError.IN_APPROVE_LIST, null, 0));
        }
        
        // Pass 7 check if the player has enough money
        if(price > 0 && owner.getContainerType() == PlayerContainerType.PLAYER && newArea != null) {
            double playerBalance = Factoid.getPlayerMoney().getPlayerBalance(
                    ((PlayerContainerPlayer)owner).getPlayerName(), newArea.getWorldName());
            if(playerBalance < price) {
                coll.add(new CollisionsEntry(LandError.NOT_ENOUGH_MONEY, null, 0));
            }
        }

        // End check if the action can be done or approve
        allowApprove = true;
        for (CollisionsEntry entry : coll) {
            if (!entry.getError().canBeApproved) {
                allowApprove = false;
                return;
            }
        }
    }

    private void checkCollisions() {

        for (Land land2 : lands.getLands()) {
            if (land != land2 && !isDescendants(land, land2) && !isDescendants(land2, parent)) {
                for (int areaId2 : land2.getAreasKey()) {
                    if (newArea.isCollision(land2.getArea(areaId2))) {
                        coll.add(new CollisionsEntry(LandError.COLLISION, land2, areaId2));
                    }
                }
            }
        }
    }

    private boolean isDescendants(Land land1, Land land2) {
        
        if(land1 == null || land2 == null) {
            return false;
        }
        if (land1.isDescendants(land2)) {
            return true;
        }

        return false;
    }
    
    private void checkIfInsideParent() {

        if (checkIfAreaOutsideParent(newArea, parent.getAreas())) {
            coll.add(new CollisionsEntry(LandError.OUT_OF_PARENT, parent, 0));
        }

    }

    private void checkIfChildrenOutside() {

        HashSet<CuboidArea> areaList = new HashSet<CuboidArea>();

        // If this is a Land remove, the list must be empty
        if (action != LandAction.LAND_REMOVE) {
            areaList.addAll(land.getAreas());
            areaList.remove(land.getArea(removedAreaId));
        }
        if (newArea != null) {
            areaList.add(newArea);
        }

        for (Land child : land.getChildren()) {
            for (CuboidArea childArea : child.getAreas()) {
                if (checkIfAreaOutsideParent(childArea, areaList)) {
                    coll.add(new CollisionsEntry(LandError.CHILD_OUT_OF_BORDER, child, 0));
                }
            }
        }
    }

    private void checkIfLandHasChildren() {

        for (Land child : land.getChildren()) {
            coll.add(new CollisionsEntry(LandError.HAS_CHILDREN, child, 0));
        }
    }

    private void checkIfNameExist() {

        if (lands.isNameExist(landName)) {
            coll.add(new CollisionsEntry(LandError.NAME_IN_USE, null, 0));
        }
    }

    // Called from checkIfInsideParent and checkIfChildrenOutside
    private boolean checkIfAreaOutsideParent(CuboidArea childArea, Collection parentAreas) {

        // area = this new area, areas2 = areas of parents
        Collection<CuboidArea> childAreas = new HashSet<CuboidArea>();
        childAreas.add(childArea);
        Iterator<CuboidArea> iterator = parentAreas.iterator();

        while (iterator.hasNext()) {
            CuboidArea parentArea = iterator.next();
            Collection<CuboidArea> childAreasNew = new HashSet<CuboidArea>();
            for (CuboidArea areaC : childAreas) {
                childAreasNew.addAll(parentArea.getOutside(areaC));
            }

            // Exit if no areas is returned (the child area is inside)
            if (childAreasNew.isEmpty()) {
                return false;
            }

            childAreas = childAreasNew;
        }

        return true;
    }

    public String getPrints() {

        StringBuilder str = new StringBuilder();

        for (CollisionsEntry ce : coll) {
            str.append(ce.getPrint()).append(Config.NEWLINE);
        }

        return str.toString();
    }

    public boolean hasCollisions() {

        return coll.size() > 0;
    }

    public Collection<CollisionsEntry> getEntries() {

        return coll;
    }

    public boolean getAllowApprove() {

        return allowApprove;
    }

}
