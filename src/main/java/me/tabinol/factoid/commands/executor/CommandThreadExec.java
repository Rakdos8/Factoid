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
package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;


/**
 * The Class CommandThreadExec.
 */
public abstract class CommandThreadExec extends CommandExec {
	
	/**
	 * Instantiates a new command thread exec.
	 *
	 * @param entity the entity
	 * @param canFromConsole the can from console
	 * @param needsMoreParameter the needs more parameter
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandThreadExec(CommandEntities entity,
            boolean canFromConsole, boolean needsMoreParameter) throws FactoidCommandException {
	
	super(entity, canFromConsole, needsMoreParameter);
	}
	
	/**
	 * Command thread execute.
	 *
	 * @param playerCacheEntry the player cache entry
	 * @throws FactoidCommandException the factoid command exception
	 */
	public abstract void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry) 
			throws FactoidCommandException;
	
	/**
	 * Convert pc if needed.
	 *
	 * @param playerCacheEntry the player cache entry
	 * @param pc the pc
	 * @return the player container
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected PlayerContainer convertPcIfNeeded(PlayerCacheEntry[] playerCacheEntry, PlayerContainer pc)
			throws FactoidCommandException {
		
		PlayerContainer newPc;
		
		if(playerCacheEntry.length == 1) {
			if(playerCacheEntry[0] != null) {
				newPc = new PlayerContainerPlayer(playerCacheEntry[0].getUUID());
			} else {
				throw new FactoidCommandException("Player not exist Error", entity.player, "COMMAND.CONTAINER.PLAYERNOTEXIST");
			}
		} else {
			newPc = pc;
		}
		
		return newPc;
	}
}
