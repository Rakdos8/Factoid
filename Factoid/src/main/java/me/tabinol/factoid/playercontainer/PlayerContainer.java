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

import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.utilities.StringChanges;


/**
 * The Class PlayerContainer.
 */
public abstract class PlayerContainer implements IPlayerContainer, Comparable<PlayerContainer> {

	/** The name. */
	protected String name;

	/** The container type. */
	protected EPlayerContainerType containerType;

	/**
	 * Instantiates a new player container.
	 *
	 * @param name the name
	 * @param containerType the container type
	 * @param toLowerCase the to lower case
	 */
	protected PlayerContainer(final String name, final EPlayerContainerType containerType, final boolean toLowerCase) {

		if (toLowerCase) {
			this.name = name.toLowerCase();
		} else {
			this.name = name;
		}
		this.containerType = containerType;
	}

	/**
	 * Creates the.
	 *
	 * @param land the land
	 * @param pct the pct
	 * @param name the name
	 * @return the player container
	 */
	public static PlayerContainer create(final ILand land, final EPlayerContainerType pct, final String name) {

		if (pct == EPlayerContainerType.GROUP) {
			return new PlayerContainerGroup(name);
		} else if (pct == EPlayerContainerType.RESIDENT) {
			return new PlayerContainerResident(land);
		} else if (pct == EPlayerContainerType.VISITOR) {
			return new PlayerContainerVisitor(land);
		} else if (pct == EPlayerContainerType.OWNER) {
			return new PlayerContainerOwner(land);
		} else if (pct == EPlayerContainerType.EVERYBODY) {
			return new PlayerContainerEverybody();
		} else if (pct == EPlayerContainerType.NOBODY) {
			return new PlayerContainerNobody();
		} else if (pct == EPlayerContainerType.PLAYER || pct == EPlayerContainerType.PLAYERNAME) {
			UUID minecraftUUID;

			// First check if the ID is valid or was connected to the server
			try {
				minecraftUUID = UUID.fromString(name.replaceFirst("ID-", ""));
			} catch (final IllegalArgumentException ex) {

				// If there is an error, just return a temporary PlayerName
				return new PlayerContainerPlayerName(name);
			}

			// If not null, assign the value to a new PlayerContainer
			return new PlayerContainerPlayer(minecraftUUID);
		} else if (pct == EPlayerContainerType.PERMISSION) {
			return new PlayerContainerPermission(name);
		} else if (pct == EPlayerContainerType.TENANT) {
			return new PlayerContainerTenant(land);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#getName()
	 */
	@Override
	public String getName() {

		return name;
	}

	/**
	 * Gets the container type.
	 *
	 * @return the container type
	 */
	@Override
	public EPlayerContainerType getContainerType() {

		return containerType;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#compareTo(me.tabinol.factoid.playercontainer.PlayerContainer)
	 */
	@Override
	public int compareTo(final PlayerContainer t) {

		if (containerType.getValue() < t.containerType.getValue()) {
			return -1;
		}
		if (containerType.getValue() > t.containerType.getValue()) {
			return 1;
		}

		// No ignorecase (Already Lower, except UUID)
		return name.compareTo(t.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return containerType.toString() + ":" + name;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#getPrint()
	 */
	@Override
	public String getPrint() {

		return containerType.toString();
	}

	/**
	 * Gets the from string.
	 *
	 * @param string the string
	 * @return the from string
	 */
	public static PlayerContainer getFromString(final String string) {

		final String strs[] = StringChanges.splitAddVoid(string, ":");
		final EPlayerContainerType type = EPlayerContainerType.getFromString(strs[0]);
		return create(null, type, strs[1]);
	}

	/**
	 * Sets the land. Not in Common API for security.
	 *
	 * @param land the new land
	 */
	public abstract void setLand(ILand land);

}
