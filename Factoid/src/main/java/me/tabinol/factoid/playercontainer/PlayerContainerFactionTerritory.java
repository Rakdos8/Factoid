package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public class PlayerContainerFactionTerritory extends PlayerContainer implements PlayerContainerInterface {
    
    private Land land;
    
    public PlayerContainerFactionTerritory(Land land) {
        
        super("", PlayerContainerType.FACTION_TERRITORY);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerFactionTerritory &&
                land == ((PlayerContainerFactionTerritory)container2).land;
    }
    
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerFactionTerritory(land);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        if(land.getFactionTerritory() == null) {
            return false;
        }
        
        return land.getFactionTerritory().isPlayerInList(playerName);
    }

    public Land getLand() {
        
        return land;
    }

    @Override
    public void setLand(Land land) {
     
        this.land = land;
    }
}
