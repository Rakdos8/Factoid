package me.tabinol.factoid.factions;

import java.util.Collection;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;

public class Faction {

    private String name;
    private TreeSet<String> players;
    private boolean autoSave = true;

    public Faction(String name) {

        this.name = name.toLowerCase();
        this.players = new TreeSet<>();
        doSave();
    }

    public String getName() {

        return name;
    }

    public void addPlayer(String playerName) {

        players.add(playerName.toLowerCase());
        doSave();
        Factoid.getLog().write(playerName + " is added in faction " + name);
    }

    public boolean removePlayer(String playerName) {

        if (players.remove(playerName.toLowerCase())) {
            doSave();
            Factoid.getLog().write(playerName + " is removed in faction " + name);
            return true;
        }

        return false;
    }

    public boolean isPlayerInList(String playerName) {

        return players.contains(playerName.toLowerCase());
    }

    public Collection<String> getPlayers() {

        return players;
    }

    public void setAutoSave(boolean autoSave) {
        
        this.autoSave = autoSave;
    }
    
    public void forceSave() {
        
        Factoid.getStorage().saveFaction(this);
        Factoid.getLog().write("Faction " + name + " is saved.");
    }
    
    private void doSave() {
        
        if(autoSave) {
            forceSave();
        }
    }
}
