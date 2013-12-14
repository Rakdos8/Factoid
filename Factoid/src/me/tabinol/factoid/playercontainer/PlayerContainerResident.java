package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerResident extends PlayerContainer implements PlayerContainerInterface {
    
    private Land land;
    
    public PlayerContainerResident(Land land) {
        
        super("", PlayerContainerType.RESIDENT);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerResident &&
                land == ((PlayerContainerResident)container2).land;
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerResident(land);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        return land.isResident(playerName);
    }
    
    public Land getLand() {
        
        return land;
    }
}
