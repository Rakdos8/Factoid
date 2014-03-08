package me.tabinol.factoid.factions;

import java.util.TreeMap;
import me.tabinol.factoid.Factoid;

public class Factions {
    
    private TreeMap<String, Faction> factionList;
    
    public Factions() {
        
        factionList = new TreeMap<String, Faction>();
    }
    
    public boolean createFaction(Faction faction) {
        
        if (factionList.containsKey(faction.getName())) {
            return false;
        }
        factionList.put(faction.getName(), faction);
        Factoid.getLog().write("add faction: " + faction.getName());
        return true;
    }
    
    public boolean removeFaction(Faction faction) {
        
        if (!factionList.containsKey(faction.getName())) {
            return false;
        }
        Factoid.getStorage().removeFaction(faction);
        factionList.remove(faction.getName());
        Factoid.getLog().write("remove faction: " + faction.getName());
        return true;
    }
    
    public boolean removeFaction(String factionName) {
        
        String factionLower;

        if (factionName == null || !factionList.containsKey(factionLower = factionName.toLowerCase())) {
            return false;
        }
        return removeFaction(factionList.get(factionLower));
    }
    
    public Faction getFaction(String factionName) {
        
        return factionList.get(factionName.toLowerCase());
    }
    
    public Faction getPlayerFaction(String playerName) {
        
        String playerLower = playerName.toLowerCase();
        
        for(Faction faction : factionList.values()) {
            if(faction.isPlayerInList(playerLower)) {
                return faction;
            }
        }
        
        return null;
    }
}
