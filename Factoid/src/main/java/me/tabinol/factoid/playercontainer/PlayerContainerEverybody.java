package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerEverybody extends PlayerContainer implements PlayerContainerInterface {

    public PlayerContainerEverybody() {
        
        super("", PlayerContainerType.EVERYBODY);
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerEverybody;
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerEverybody();
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        return true;
    }
    
    @Override
    public void setLand(Land land) {
        
    }
}
