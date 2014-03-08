package me.tabinol.factoid.lands.approve;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
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
    
    public Approve(String landName, LandAction action, int removedAreaId, 
            CuboidArea newArea, PlayerContainer owner, Land parent) {
        
        this.action = action;
        this.landName = landName.toLowerCase();
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
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
    
    public void createAction() {
        
        if(action == LandAction.AREA_ADD) {
            Factoid.getLands().getLand(landName).addArea(newArea);
        } else if(action == LandAction.AREA_REMOVE) {
            Factoid.getLands().getLand(landName).removeArea(removedAreaId);
        } else if(action == LandAction.AREA_MODIFY) {
            Factoid.getLands().getLand(landName).replaceArea(removedAreaId, newArea);
        } else if(action == LandAction.LAND_ADD) {
            try {
                Factoid.getLands().createLand(landName, owner, newArea, parent);
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
