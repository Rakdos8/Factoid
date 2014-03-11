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
