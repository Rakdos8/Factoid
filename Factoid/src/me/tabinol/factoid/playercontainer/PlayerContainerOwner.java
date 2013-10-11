package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerOwner extends PlayerContainer implements PlayerContainerInterface {

    private Land land;
    
    public PlayerContainerOwner(Land land) {
        
        super("", PlayerContainerType.OWNER);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerOwner &&
                land == ((PlayerContainerOwner)container2).land;
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerOwner(land);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        return land.getOwner().hasAccess(playerName);
    }
        
    public Land getLand() {
        
        return land;
    }
}
