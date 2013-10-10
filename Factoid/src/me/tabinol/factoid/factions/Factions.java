package me.tabinol.factoid.factions;

import java.util.TreeMap;
import me.tabinol.factoid.Factoid;

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
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.FACTION.ADD.FACTION",faction.getName()));
        return true;
    }
    
    public boolean removeFaction(Faction faction) {
        
        if (!factionList.containsKey(faction.getName())) {
            return false;
        }
        Factoid.getStorage().removeFaction(faction);
        factionList.remove(faction.getName());
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.FACTION.REMOVE.FACTION",faction.getName()));
        return true;
    }
    
    public boolean removeFaction(String factionName) {
        
        if (factionName == null || !factionList.containsKey(factionName)) {
            return false;
        }
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.FACTION.REMOVE.FACTION",factionName));
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
