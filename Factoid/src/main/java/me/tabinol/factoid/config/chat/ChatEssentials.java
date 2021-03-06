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
package me.tabinol.factoid.config.chat;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import me.tabinol.factoid.Factoid;

/**
 * The Class ChatEssentials.
 */
public class ChatEssentials implements Chat {

	/** The essentials. */
	private final Essentials essentials;

	/**
	 * Instantiates a new chat essentials.
	 */
	public ChatEssentials() {

		essentials = (Essentials)Factoid.getThisPlugin().iDependPlugin().getEssentials();
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.chat.Chat#isSpy(org.bukkit.entity.Player)
	 */
	@Override
	public boolean isSpy(final Player player) {

		return essentials.getUser(player).isSocialSpyEnabled();
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.chat.Chat#isMuted(org.bukkit.entity.Player)
	 */
	@Override
	public boolean isMuted(final Player player) {

		return essentials.getUser(player).isMuted();
	}

}
