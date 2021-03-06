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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.parameters.ILandFlag;
import me.tabinol.factoidapi.utilities.StringChanges;


/**
 * The Class LandFlag.
 */
public class LandFlag implements ILandFlag {

	/** The flag type. */
	private final FlagType flagType;

	/** The value. */
	private FlagValue value = null;

	/** The inheritable. */
	private final boolean heritable;

	/**
	 * Instantiates a new land flag.
	 *
	 * @param flagType the flag type
	 * @param value the value
	 * @param heritable the inheritable
	 */
	public LandFlag(final FlagType flagType, final Object value, final boolean heritable) {

		this.flagType = flagType;
		if (value instanceof FlagValue) {
			this.value = (FlagValue) value;
		} else {
			this.value = new FlagValue(value);
		}
		this.heritable = heritable;

		if (!flagType.isRegistered()) {
			Factoid.getThisPlugin().iParameters().unRegisteredFlags.add(this);
		}
	}

	@Override
	public LandFlag copyOf() {

		return new LandFlag(flagType, value.copyOf(), heritable);
	}

	/**
	 * Equals.
	 *
	 * @param lf2 the lf2
	 * @return true, if successful
	 */
	@Override
	public boolean equals(final ILandFlag lf2) {

		return flagType == lf2.getFlagType();
	}

	/**
	 * Gets the flag type.
	 *
	 * @return the flag type
	 */
	@Override
	public final FlagType getFlagType() {

		return flagType;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Override
	public final FlagValue getValue() {

		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	protected void setValue(final FlagValue value) {

		this.value = value;
	}

	/**
	 * Checks if is heritable.
	 *
	 * @return true, if is heritable
	 */
	@Override
	public boolean isHeritable() {

		return heritable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		if (!flagType.isRegistered()) {
			return flagType.toString() + ":" + value.getValue() + ":" + heritable;
		}

		if (value.getValue() instanceof Boolean) {
			return flagType.toString() + ":" + value.getValueBoolean() + ":" + heritable;
		}

		if (value.getValue() instanceof Double) {
			return flagType.toString() + ":" + value.getValueDouble() + ":" + heritable;
		}

		if (value.getValue() instanceof String) {
			return flagType.toString() + ":" + StringChanges.toQuote(value.getValueString()) + ":" + heritable;
		}

		if (value.getValue() instanceof String[]) {
			final StringBuilder sb = new StringBuilder();
			for (final String st : value.getValueStringList()) {
				sb.append(StringChanges.toQuote(st)).append(";");
			}
			return flagType.toString() + ":" + sb.toString() + ":" + heritable;
		}

		return null;
	}

	/**
	 * Gets the from string.
	 *
	 * @param str the str
	 * @return the from string
	 */
	public static LandFlag getFromString(final String str) {

		final String[] multiStr = StringChanges.splitKeepQuote(str, ":");
		final FlagType ft = Factoid.getThisPlugin().iParameters().getFlagTypeNoValid(multiStr[0]);
		final Object value = FlagValue.getFromString(multiStr[1], ft);

		return new LandFlag(ft, value, Boolean.parseBoolean(multiStr[2]));
	}
}