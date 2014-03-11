/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
        this.players = new TreeSet<String>();
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
