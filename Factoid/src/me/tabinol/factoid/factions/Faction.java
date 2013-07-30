package me.tabinol.factoid.factions;

import java.util.HashSet;

public class Faction {
    
    private String name;
    private HashSet<String> players = new HashSet<>();
    
    public Faction(String name) {
        
        this.name = name;
    }
    
    public String getName() {
        
        return name;
    }
    
    public void addPlayer(String playerName) {
        
        players.add(playerName.toLowerCase());
    }
    
    public boolean removePlayer(String playerName) {
        
        return players.remove(playerName.toLowerCase());
    }
    
    public boolean isPlayerInList(String playerName) {
        
        return players.contains(playerName.toLowerCase());
    }
}
