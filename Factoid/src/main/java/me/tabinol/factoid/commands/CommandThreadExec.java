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
package me.tabinol.factoid.commands;

import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;


/**
 * The Class CommandThreadExec.
 */
public abstract class CommandThreadExec extends CommandExec {

	protected IPlayerContainer pc;

	/**
	 * Instantiates a new command thread exec.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandThreadExec(final CommandEntities entity) throws FactoidCommandException {

	super(entity);
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
	 * Convert only if the PlayerContainer is a PlayerContainerPlayerName
	 * It takes the result from the UUID request.
	 *
	 * @param playerCacheEntry the player cache entry
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected void convertPcIfNeeded(final PlayerCacheEntry[] playerCacheEntry)
			throws FactoidCommandException {

		if (pc instanceof PlayerContainerPlayerName) {
			if (playerCacheEntry.length == 1 && playerCacheEntry[0] != null) {
				pc = new PlayerContainerPlayer(playerCacheEntry[0].getUUID());
			} else {
				throw new FactoidCommandException("Player not exist Error", entity.player, "COMMAND.CONTAINER.PLAYERNOTEXIST");
			}
		}
	}
}
