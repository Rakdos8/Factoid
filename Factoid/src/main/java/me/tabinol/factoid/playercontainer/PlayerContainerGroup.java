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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerGroup;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerGroup.
 */
public class PlayerContainerGroup extends PlayerContainer
	implements IPlayerContainerGroup {

	/**
	 * Instantiates a new player container group.
	 *
	 * @param groupName the group name
	 */
	public PlayerContainerGroup(final String groupName) {

		super(groupName, EPlayerContainerType.GROUP, true);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
	 */
	@Override
	public boolean equals(final IPlayerContainer container2) {

		return container2 instanceof PlayerContainerGroup &&
				name.equalsIgnoreCase(container2.getName());
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
	 */
	@Override
	public PlayerContainer copyOf() {

		return new PlayerContainerGroup(name);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
	 */
	@Override
	public boolean hasAccess(final Player player) {

		if(player != null) {
			return Factoid.getThisPlugin().iDependPlugin().getPermission().playerInGroup(player, name);
		} else {
			return false;
		}
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

		return ChatColor.BLUE + "G:" + ChatColor.WHITE + name;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
	 */
	@Override
	public void setLand(final ILand land) {

	}
}
