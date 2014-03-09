package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerNobody extends PlayerContainer implements PlayerContainerInterface {

    public PlayerContainerNobody() {
        
        super("", PlayerContainerType.NOBODY);
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerNobody;
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerNobody();
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        return false;
    }

    @Override
    public void setLand(Land land) {
        
    }
}
