package me.tabinol.factoid.factions;

import java.util.TreeMap;

public class Factions {
    
    private TreeMap<String, Faction> factionList;
    
    public Factions() {
        
        factionList = new TreeMap<>();
    }
    
    public boolean createFaction(Faction faction) {
        
        if (factionList.containsKey(faction.getName())) {
            return false;
        }
        factionList.put(faction.getName(), faction);
        return true;
    }
    
    public boolean removeFaction(Faction faction) {
        
        if (!factionList.containsKey(faction.getName())) {
            return false;
        }
        factionList.remove(faction.getName());
        return true;
    }
    
    public boolean removeFaction(String factionName) {
        
        if (factionName == null || !factionList.containsKey(factionName)) {
            return false;
        }
        return removeFaction(factionList.get(factionName));
        
    }
    
    public Faction getFaction(String factionName) {
        
        return factionList.get(factionName);
    }
    
    public Faction getPlayerFaction(String playerName) {
        
        for(Faction faction : factionList.values()) {
            if(faction.isPlayerInList(playerName)) {
                return faction;
            }
        }
        
        return null;
    }
}
