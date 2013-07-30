package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.factions.Faction;

public class PlayerContainerFaction extends PlayerContainer implements PlayerContainerInterface {
    
    private Faction faction;
    
    public PlayerContainerFaction(Faction faction) {
        
        super(faction.getName());
        this.faction = faction;
    }
}
