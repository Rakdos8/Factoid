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
package me.tabinol.factoid.parameters;

/**
 * The Enum FlagList.
 *
 * @author Tabinol
 */
public enum FlagList {

	/** The undefined. */
	UNDEFINED(new String()),

	/** The firespread. */
	FIRESPREAD(Boolean.TRUE),

	/** The fire. */
	FIRE(Boolean.TRUE),

	/** The explosion. */
	EXPLOSION(Boolean.TRUE),

	/** The creeper explosion. */
	CREEPER_EXPLOSION(Boolean.TRUE),
	/** The creeper damage. */
	CREEPER_DAMAGE(Boolean.TRUE),

	/** The tnt explosion. */
	TNT_EXPLOSION(Boolean.TRUE),
	/** The tnt damage. */
	TNT_DAMAGE(Boolean.TRUE),

	/** The end crystal explosion. */
	END_CRYSTAL_EXPLOSION(Boolean.TRUE),
	/** The end crystal damage. */
	END_CRYSTAL_DAMAGE(Boolean.TRUE),

	/** The firework explosion. */
	FIREWORK_EXPLOSION(Boolean.TRUE),
	/** The firework damage. */
	FIREWORK_DAMAGE(Boolean.TRUE),

	/** The enderman damage. */
	ENDERMAN_DAMAGE(Boolean.TRUE),

	/** The chorus fruit random tp. */
	CHORUS_FRUIT_TP(Boolean.FALSE),

	/** The wither damage. */
	WITHER_DAMAGE(Boolean.TRUE),

	/** The ghast damage. */
	GHAST_DAMAGE(Boolean.TRUE),

	/** The enderdragon damage. */
	ENDERDRAGON_DAMAGE(Boolean.TRUE),

	/** The mob spawn. */
	MOB_SPAWN(Boolean.TRUE),

	/** The animal spawn. */
	ANIMAL_SPAWN(Boolean.TRUE),

	/** The leaves decay */
	LEAF_DECAY(Boolean.TRUE),

	/** The crop trample */
	CROP_TRAMPLE(Boolean.TRUE),

	/** The lava flow */
	LAVA_FLOW(Boolean.TRUE),

	/** The water flow */
	WATER_FLOW(Boolean.TRUE),

	/** The full pvp. */
	FULL_PVP(Boolean.TRUE),

	/** The faction pvp. */
	FACTION_PVP(Boolean.TRUE),

	/** The message join. */
	MESSAGE_JOIN(new String()),

	/** The message quit. */
	MESSAGE_QUIT(new String()),

	/** The eco block price. */
	ECO_BLOCK_PRICE(new Double(0)),

	/** The exclude commands. */
	EXCLUDE_COMMANDS(new String[] {}),

	/**  The spawn and teleport point. */
	SPAWN(new String("")),

	/** Inherit from parent owner */
	INHERIT_OWNER(Boolean.TRUE),

	/** Inherit from parent residents */
	INHERIT_RESIDENTS(Boolean.TRUE),

	/** Inherit from parent tenant */
	INHERIT_TENANT(Boolean.TRUE);

	/** The base value. */
	final FlagValue baseValue;

	/** The flag type. */
	private FlagType flagType;

	/**
	 * Instantiates a new flag list.
	 *
	 * @param baseValue the base value
	 */
	private FlagList(final Object baseValue) {

		this.baseValue = new FlagValue(baseValue);
	}

	/**
	 * Sets the flag type.
	 *
	 * @param flagType the new flag type
	 */
	void setFlagType(final FlagType flagType) {

		this.flagType = flagType;
	}

	/**
	 * Gets the flag type.
	 *
	 * @return the flag type
	 */
	public FlagType getFlagType() {

		return flagType;
	}
}
