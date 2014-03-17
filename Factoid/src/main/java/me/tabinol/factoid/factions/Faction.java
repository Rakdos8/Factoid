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
import java.util.UUID;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;

public class Faction {

    private String name;
    private final UUID uuid;
    private TreeSet<PlayerContainerPlayer> players;
    private boolean autoSave = true;

    public Faction(String name, UUID uuid) {

        this.name = name.toLowerCase();
        this.uuid = uuid;
        this.players = new TreeSet<PlayerContainerPlayer>();
        doSave();
    }

    public String getName() {

        return name;
    }
    
    public UUID getUUID() {
        
        return uuid;
    }

    public void addPlayer(PlayerContainerPlayer player) {

        players.add(player);
        doSave();
        Factoid.getLog().write(player.toString() + " is added in faction " + name);
    }

    public boolean removePlayer(PlayerContainerPlayer player) {

        if (players.remove(player)) {
            doSave();
            Factoid.getLog().write(player.toString() + " is removed in faction " + name);
            return true;
        }

        return false;
    }

    public boolean isPlayerInList(PlayerContainerPlayer player) {

        return players.contains(player);
    }

    public Collection<PlayerContainerPlayer> getPlayers() {

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
