package me.tabinol.factoid.factions;

import java.util.Collection;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;

public class Faction {

    private String name;
    private TreeSet<String> players;

    public Faction(String name) {

        this.name = name;
        this.players = new TreeSet<>();
        Factoid.getStorage().saveFaction(this);
    }

    public String getName() {

        return name;
    }

    public void addPlayer(String playerName) {

        players.add(playerName.toLowerCase());
        Factoid.getStorage().saveFaction(this);
    }

    public boolean removePlayer(String playerName) {

        if (players.remove(playerName.toLowerCase())) {
            Factoid.getStorage().saveFaction(this);
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
}
