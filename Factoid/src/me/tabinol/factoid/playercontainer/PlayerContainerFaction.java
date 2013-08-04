package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.factions.Faction;

public class PlayerContainerFaction extends PlayerContainer implements PlayerContainerInterface {
    
    private Faction faction;
    
    public PlayerContainerFaction(Faction faction) {
        
        super(faction.getName());
        this.faction = faction;
    }
    
    public Faction getFaction() {
        
        return faction;
    }

    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerFaction &&
                name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerFaction(faction);
    }
}
