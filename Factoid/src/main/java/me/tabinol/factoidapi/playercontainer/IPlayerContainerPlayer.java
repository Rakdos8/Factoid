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
package me.tabinol.factoidapi.playercontainer;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The Interface IPlayerContainerPlayer. Represent a player.
 */
public interface IPlayerContainerPlayer extends IPlayerContainer {

	public String getPlayerName();

	/**
	 * Gets the minecraft uuid.
	 *
	 * @return the minecraft uuid
	 */
	public UUID getMinecraftUUID();

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	public Player getPlayer();

	/**
	 * Get the offline player.
	 *
	 * @return the offline player
	 */
	public OfflinePlayer getOfflinePlayer();

}
