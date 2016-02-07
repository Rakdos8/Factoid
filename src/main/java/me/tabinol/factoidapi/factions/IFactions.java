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
package me.tabinol.factoidapi.factions;

import java.util.Collection;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

public interface IFactions {
	
    /**
     * Creates the faction.
     *
     * @param factionName the faction name
     * @return the faction
     */
    public IFaction createFaction(String factionName);
    

    /**
     * Removes the faction.
     *
     * @param faction the faction
     * @return true, if successful
     */
    public boolean removeFaction(IFaction faction);
    
    /**
     * Removes the faction.
     *
     * @param factionName the faction name
     * @return true, if successful
     */
    public boolean removeFaction(String factionName);
    
    /**
     * Gets the faction.
     *
     * @param factionName the faction name
     * @return the faction
     */
    public IFaction getFaction(String factionName);
    
    /**
     * Gets the player faction.
     *
     * @param player the player
     * @return the player faction
     */
    public IFaction getPlayerFaction(IPlayerContainerPlayer player);
    
    /**
     * Gets the factions.
     *
     * @return the factions
     */
    public Collection<IFaction> getFactions();
}
