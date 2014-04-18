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
package me.tabinol.factoid.lands.approve;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Approve {
    
    private final LandAction action;
    private final String landName;
    private final int removedAreaId;
    private final CuboidArea newArea;
    private final PlayerContainer owner;
    private final Land parent;
    private final double price;
    
    public Approve(String landName, LandAction action, int removedAreaId, 
            CuboidArea newArea, PlayerContainer owner, Land parent, double price) {
        
        this.action = action;
        this.landName = landName.toLowerCase();
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
        this.price = price;
    }

    public LandAction getAction() {
        
        return action;
    }
    
    public String getLandName() {
        
        return landName;
    }
    
    public int getRemovedAreaId() {
        
        return removedAreaId;
    }
    
    public CuboidArea getNewArea() {
        
        return newArea;
    }
    
    public PlayerContainer getOwner() {
        
        return owner;
    }
    
    public Land getParent() {
        
        return parent;
    }
    
    public double getPrice() {
        
        return price;
    }
    
    public void createAction() {
        
        if(action == LandAction.AREA_ADD) {
            Factoid.getLands().getLand(landName).addArea(newArea, price);
        } else if(action == LandAction.AREA_REMOVE) {
            Factoid.getLands().getLand(landName).removeArea(removedAreaId);
        } else if(action == LandAction.AREA_MODIFY) {
            Factoid.getLands().getLand(landName).replaceArea(removedAreaId, newArea, price);
        } else if(action == LandAction.LAND_ADD) {
            try {
                Factoid.getLands().createLand(landName, owner, newArea, parent, price);
            } catch (FactoidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land create", ex);
            }
        } else if(action == LandAction.LAND_REMOVE) {
            try {
                Factoid.getLands().removeLand(landName);
            } catch (FactoidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land remove", ex);
            }
        }
    }
}
