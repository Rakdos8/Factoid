package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerVisitor extends PlayerContainer implements PlayerContainerInterface {
    
    private Land land;
    
    public PlayerContainerVisitor(Land land) {
        
        super("", PlayerContainerType.VISITOR);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerVisitor
                && land == ((PlayerContainerVisitor) container2).land;
    }
    
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerVisitor(land);
    }
    
    @Override
    public boolean hasAccess(String playerName) {
        
        return !land.getOwner().hasAccess(playerName)
                && !land.isResident(playerName);
    }
    
    public Land getLand() {
        
        return land;
    }

    @Override
    public void setLand(Land land) {
    
        this.land = land;
    }
}
