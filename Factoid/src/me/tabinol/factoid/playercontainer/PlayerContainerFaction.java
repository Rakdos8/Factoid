package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.factions.Faction;
import org.bukkit.ChatColor;

public class PlayerContainerFaction extends PlayerContainer implements PlayerContainerInterface {
    
    private Faction faction;
    
    public PlayerContainerFaction(Faction faction) {
        
        super(faction.getName(), PlayerContainerType.FACTION);
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

    @Override
    public boolean hasAccess(String playerName) {
        
        return faction.isPlayerInList(playerName);
    }
    
    @Override
    public String getPrint() {
        
        return ChatColor.GOLD + "F:" + ChatColor.WHITE + name;
    }

}
