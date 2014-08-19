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

// TODO: Auto-generated Javadoc
/**
 * The Class Collisions.
 */
public class Collisions {

    /**
     * The Enum LandAction.
     */
    public enum LandAction {

        /** The land add. */
        LAND_ADD,
        
        /** The land rename. */
        LAND_RENAME,
        
        /** The land remove. */
        LAND_REMOVE,
        
        /** The area add. */
        AREA_ADD,
        
        /** The area remove. */
        AREA_REMOVE,
        
        /** The area modify. */
        AREA_MODIFY;
    }

    /**
     * The Enum LandError.
     */
    public enum LandError {

        /** The collision. */
        COLLISION(true),
        
        /** The name in use. */
        NAME_IN_USE(false),
        
        /** The has children. */
        HAS_CHILDREN(false),
        
        /** The child out of border. */
        CHILD_OUT_OF_BORDER(true),
        
        /** The out of parent. */
        OUT_OF_PARENT(true),
        
        /** The in approve list. */
        IN_APPROVE_LIST(false),
        
        /** The not enough money. */
        NOT_ENOUGH_MONEY(false),
        
        /** The max area for land. */
        MAX_AREA_FOR_LAND(true),
        
        /** The max land for player. */
        MAX_LAND_FOR_PLAYER(true),
        
        /** A land must have one or more areas */
        MUST_HAVE_AT_LEAST_ONE_AREA(false);

        /** The can be approved. */
        public final boolean canBeApproved; // False = No approve is possible

        /**
         * Instantiates a new land error.
         *
         * @param canBeApproved the can be approved
         */
        private LandError(boolean canBeApproved) {
            this.canBeApproved = canBeApproved;
        }
    }

    /** The coll. */
    private final List<CollisionsEntry> coll;
    
    /** The lands. */
    private final Lands lands;
    
    /** The land name. */
    private final String landName;
    
    /** The land. */
    private final Land land;
    
    /** The action. */
    private final LandAction action;
    
    /** The removed area id. */
    private final int removedAreaId;
    
    /** The new area. */
    private final CuboidArea newArea;
    
    /** The parent. */
    private final Land parent;
    
    /** The price. */
    private final double price;
    
    /** The owner. */
    private final PlayerContainer owner;
    
    /** The allow approve. */
    private boolean allowApprove;

    /**
     * Instantiates a new collisions.
     *
     * @param landName the land name
     * @param land the land
     * @param action the action
     * @param removedAreaId the removed area id
     * @param newArea the new area
     * @param parent the parent
     * @param owner the owner
     * @param price the price
     * @param checkApproveList the check approve list
     */
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

        // Pass 5 check if the name is already existing
        if (action == LandAction.LAND_ADD || action == LandAction.LAND_RENAME) {
            checkIfNameExist();
        }

        // Pass 6 check if the name is already in Approve List
        if (!checkApproveList && lands.getApproveList().isInApprove(landName)) {
            coll.add(new CollisionsEntry(LandError.IN_APPROVE_LIST, null, 0));
        }
        
        if(owner.getContainerType() == PlayerContainerType.PLAYER) {
        	
        	// Pass 7 check if the player has enough money
        	if(price > 0 && newArea != null) {
        		double playerBalance = Factoid.getPlayerMoney().getPlayerBalance(
        				((PlayerContainerPlayer)owner).getOfflinePlayer(), newArea.getWorldName());
        		if(playerBalance < price) {
        			coll.add(new CollisionsEntry(LandError.NOT_ENOUGH_MONEY, null, 0));
        		}
        	}
        
        	// Pass 8 check if the land has more than the maximum number of areas
        	if(action == LandAction.AREA_ADD && land.getAreas().size() >= Factoid.getConf().getMaxAreaPerLand()) {
        		coll.add(new CollisionsEntry(LandError.MAX_AREA_FOR_LAND, land, 0));
        	}
        
        	// Pass 9 check if the player has more than the maximum number of land
        	if(action == LandAction.LAND_ADD && owner != null 
        			&& Factoid.getLands().getLands(owner).size() >= Factoid.getConf().getMaxLandPerPlayer()) {
        		coll.add(new CollisionsEntry(LandError.MAX_LAND_FOR_PLAYER, null, 0));
        	}
        }
        
        // Pass 10 check if the area to remove is the only one
        if(action == LandAction.AREA_REMOVE && land.getAreas().size() == 1) {
        	coll.add(new CollisionsEntry(LandError.MUST_HAVE_AT_LEAST_ONE_AREA, land, removedAreaId));
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

    /**
     * Check collisions.
     */
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

    /**
     * Checks if is descendants.
     *
     * @param land1 the land1
     * @param land2 the land2
     * @return true, if is descendants
     */
    private boolean isDescendants(Land land1, Land land2) {
        
        if(land1 == null || land2 == null) {
            return false;
        }
        if (land1.isDescendants(land2)) {
            return true;
        }

        return false;
    }
    
    /**
     * Check if inside parent.
     */
    private void checkIfInsideParent() {

        if (checkIfAreaOutsideParent(newArea, parent.getAreas())) {
            coll.add(new CollisionsEntry(LandError.OUT_OF_PARENT, parent, 0));
        }

    }

    /**
     * Check if children outside.
     */
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

    /**
     * Check if land has children.
     */
    private void checkIfLandHasChildren() {

        for (Land child : land.getChildren()) {
            coll.add(new CollisionsEntry(LandError.HAS_CHILDREN, child, 0));
        }
    }

    /**
     * Check if name exist.
     */
    private void checkIfNameExist() {

        if (lands.isNameExist(landName)) {
            coll.add(new CollisionsEntry(LandError.NAME_IN_USE, null, 0));
        }
    }

    // Called from checkIfInsideParent and checkIfChildrenOutside
    /**
     * Check if area outside parent.
     *
     * @param childArea the child area
     * @param parentAreas the parent areas
     * @return true, if successful
     */
    private boolean checkIfAreaOutsideParent(CuboidArea childArea, Collection<CuboidArea> parentAreas) {

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

    /**
     * Gets the prints.
     *
     * @return the prints
     */
    public String getPrints() {

        StringBuilder str = new StringBuilder();

        for (CollisionsEntry ce : coll) {
            str.append(ce.getPrint()).append(Config.NEWLINE);
        }

        return str.toString();
    }

    /**
     * Checks for collisions.
     *
     * @return true, if successful
     */
    public boolean hasCollisions() {

        return coll.size() > 0;
    }

    /**
     * Gets the entries.
     *
     * @return the entries
     */
    public Collection<CollisionsEntry> getEntries() {

        return coll;
    }

    /**
     * Gets the allow approve.
     *
     * @return the allow approve
     */
    public boolean getAllowApprove() {

        return allowApprove;
    }

}
