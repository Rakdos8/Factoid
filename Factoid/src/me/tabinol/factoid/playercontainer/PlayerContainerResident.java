package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerResident extends PlayerContainer implements PlayerContainerInterface {
    
    Land land;
    
    public PlayerContainerResident(Land land) {
        
        super(land.getName(), PlayerContainerType.RESIDENT);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerResident &&
                name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerResident(land);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        for(PlayerContainer pc : land.getResidents()) {
            if(pc.hasAccess(playerName)) {
                return true;
            }
        }
        
        return false;
    }
}
