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
package me.tabinol.factoid.config.players;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.chat.Chat;
import me.tabinol.factoid.config.chat.ChatEssentials;
import me.tabinol.factoid.config.chat.ChatFactoid;
import me.tabinol.factoid.config.vanish.DummyVanish;
import me.tabinol.factoid.config.vanish.Vanish;
import me.tabinol.factoid.config.vanish.VanishEssentials;
import me.tabinol.factoid.config.vanish.VanishNoPacket;
import me.tabinol.factoidapi.config.players.IPlayerStaticConfig;


// Contain lists for player (selection, ect, ...)
/**
 * The Class PlayerStaticConfig.
 */
public class PlayerStaticConfig implements IPlayerStaticConfig {

	/** The player conf list. */
	private final Map<CommandSender, PlayerConfEntry> playerConfList = new HashMap<>();

	/** The vanish. */
	private final Vanish vanish;

	/** The chat. */
	private final Chat chat;

	/**
	 * Instantiates a new player static config.
	 */
	public PlayerStaticConfig() {
		// Check for VanishNoPacket plugin
		if (Factoid.getThisPlugin().iDependPlugin().getVanishNoPacket() != null) {
			vanish = new VanishNoPacket();
			// Check for Essentials plugin
		} else if (Factoid.getThisPlugin().iDependPlugin().getEssentials() != null) {
			vanish = new VanishEssentials();
			// Dummy Vanish if no plugins
		} else {
			vanish = new DummyVanish();
		}

		// Check for Chat plugin
		if (Factoid.getThisPlugin().iDependPlugin().getEssentials() != null) {
			chat = new ChatEssentials();
		} else {
			chat = new ChatFactoid();
		}
	}

	// Methods for geting a player static config
	/**
	 * Adds the.
	 *
	 * @param sender the sender
	 * @return the player conf entry
	 */
	public PlayerConfEntry add(final CommandSender sender) {
		final PlayerConfEntry entry = new PlayerConfEntry(sender);
		playerConfList.put(sender, entry);

		return entry;
	}

	/**
	 * Removes the.
	 *
	 * @param sender the sender
	 */
	public void remove(final CommandSender sender) {
		final PlayerConfEntry entry = playerConfList.get(sender);

		// First, remove AutoCancelSelect
		entry.setAutoCancelSelect(false);

		playerConfList.remove(sender);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerStaticConfig#get(org.bukkit.command.CommandSender)
	 */
	@Override
	public PlayerConfEntry get(final CommandSender sender) {
		return playerConfList.get(sender);
	}

	/**
	 * Adds the all.
	 */
	public void addAll() {
		// Add the consle in the list
		add(Factoid.getThisPlugin().getServer().getConsoleSender());

		// Add online players
		for (final CommandSender sender : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
			add(sender);
		}
	}

	/**
	 * Removes the all.
	 */
	public void removeAll() {
		for (final PlayerConfEntry entry : playerConfList.values()) {
			// First, remove AutoCancelSelect
			entry.setAutoCancelSelect(false);

		}
		playerConfList.clear();
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerStaticConfig#isVanished(org.bukkit.entity.Player)
	 */
	@Override
	public boolean isVanished(final Player player) {
		return vanish.isVanished(player);
	}

	public Chat getChat() {
		return chat;
	}
}
