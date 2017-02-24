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
package me.tabinol.factoid.event;

import org.bukkit.event.HandlerList;

import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainer;


/**
 * The Class PlayerContainerLandBanEvent.
 * @deprecated Use FactoidAPI
 */
@Deprecated
public class PlayerContainerLandBanEvent extends LandEvent {

	/** The Constant handlers. */
	private static final HandlerList handlers = new HandlerList();

	/** The player container. */
	PlayerContainer playerContainer;

	/**
	 * Instantiates a new player container land ban event.
	 *
	 * @param land the land
	 * @param playerContainer the player container
	 */
	public PlayerContainerLandBanEvent(final Land land, final PlayerContainer playerContainer) {

		super(land);
		this.playerContainer = playerContainer;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.event.LandEvent#getHandlers()
	 */
	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	/**
	 * Gets the handler list.
	 *
	 * @return the handler list
	 */
	public static HandlerList getHandlerList() {

		return handlers;
	}

	/**
	 * Gets the player container.
	 *
	 * @return the player container
	 */
	public PlayerContainer getPlayerContainer() {

		return playerContainer;
	}
}
