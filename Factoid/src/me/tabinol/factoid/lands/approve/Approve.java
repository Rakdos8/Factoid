package me.tabinol.factoid.lands.approve;

import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Approve {
    
    private final String landName;
    private final CuboidArea area;
    private final PlayerContainer owner;
    private final Land parent;
    
    public Approve(String landName, CuboidArea area, PlayerContainer owner, Land parent) {
        
        this.landName = landName.toLowerCase();
        this.area = area;
        this.owner = owner;
        this.parent = parent;
    }

    public String getLandName() {
        
        return landName;
    }
    
    public CuboidArea getCuboidArea() {
        
        return area;
    }
    
    public PlayerContainer getOwner() {
        
        return owner;
    }
    
    public Land getParent() {
        
        return parent;
    }
}
