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
package me.tabinol.factoid.playercontainer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;


/**
 * The Class PlayerContainerPlayer.
 */
public class PlayerContainerPlayer extends PlayerContainer
	implements IPlayerContainerPlayer {

	/** The minecraft uuid. */
	private final UUID minecraftUUID;

	// Compare before create
	/**
	 * Instantiates a new player container player.
	 *
	 * @param minecraftUUID the minecraft uuid
	 */
	public PlayerContainerPlayer(final UUID minecraftUUID) {

		super("ID-" + minecraftUUID.toString(), EPlayerContainerType.PLAYER, false);
		this.minecraftUUID = minecraftUUID;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
	 */
	@Override
	public boolean equals(final IPlayerContainer container2) {

		return container2 instanceof PlayerContainerPlayer &&
				minecraftUUID.equals(((PlayerContainerPlayer) container2).minecraftUUID);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
	 */
	@Override
	public PlayerContainer copyOf() {

		return new PlayerContainerPlayer(minecraftUUID);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
	 */
	@Override
	public boolean hasAccess(final Player player) {

		if (player != null) {
			return minecraftUUID.equals(player.getUniqueId());
		}
		return false;
	}

	@Override
	public boolean hasAccess(final Player player, final ILand land) {

		return hasAccess(player);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainer#getPrint()
	 */
	@Override
	public String getPrint() {

		final StringBuilder sb = new StringBuilder();
		final String playerName = getPlayerName();

		sb.append(ChatColor.DARK_RED).append("P:");

		if (playerName != null) {
			sb.append(ChatColor.WHITE).append(playerName);
		} else {
			// Player never connected on the server, show UUID
			sb.append(ChatColor.DARK_GRAY).append("ID-" + minecraftUUID);
		}

		return sb.toString();
	}

	@Override
	public String getPlayerName() {

		String playerName;

		// Pass 1 get in Online players
		final Player player = Bukkit.getPlayer(minecraftUUID);
		if (player != null) {
			return player.getName();
		}

		// Pass 2 get from Factoid cache
		playerName = Factoid.getThisPlugin().iPlayersCache().getNameFromUUID(minecraftUUID);
		if (playerName != null) {
			return playerName;
		}

		// Pass 3 get from offline players
		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(minecraftUUID);
		if (offlinePlayer != null) {
			return offlinePlayer.getName();
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
	 */
	@Override
	public void setLand(final ILand land) {

	}

	/**
	 * Gets the minecraft uuid.
	 *
	 * @return the minecraft uuid
	 */
	@Override
	public UUID getMinecraftUUID() {

		return minecraftUUID;
	}

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	@Override
	public Player getPlayer() {

		return Bukkit.getPlayer(minecraftUUID);
	}

	/**
	 * Get the offline player.
	 *
	 * @return the offline player
	 */
	@Override
	public OfflinePlayer getOfflinePlayer() {

		return Bukkit.getOfflinePlayer(minecraftUUID);
	}
}
