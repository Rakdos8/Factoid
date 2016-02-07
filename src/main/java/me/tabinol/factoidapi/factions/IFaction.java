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
import java.util.UUID;

import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

/**
 * The Interface IFaction. Represent a faction.
 */
public interface IFaction {

    /**
     * Gets the name of the faction.
     *
     * @return the name
     */
    public String getName();
    
    /**
     * Gets the uuid of the faction.
     *
     * @return the uuid
     */
    public UUID getUUID();

    /**
     * Adds a player.
     *
     * @param player the player
     */
    public void addPlayer(IPlayerContainerPlayer player);
    
    /**
     * Removes a player.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayer(IPlayerContainerPlayer player);
    /**
     * Checks if player is in this faction.
     *
     * @param player the player
     * @return true, if is player in list
     */
    public boolean isPlayerInList(IPlayerContainerPlayer player);
    
    /**
     * Gets all players from this faction.
     *
     * @return the players
     */
    public Collection<IPlayerContainerPlayer> getPlayers();
    
    /**
     * Enable or disable auto save. PLEASE USE WITH CAUTION!
     *
     * @param autoSave if auto save or not
     */
    public void setAutoSave(boolean autoSave);
    
    /**
     * Force save.
     */
    public void forceSave();
    
}
