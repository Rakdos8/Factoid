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

import java.util.ArrayList;

import org.bukkit.ChatColor;

import me.tabinol.factoidapi.parameters.IFlagValue;
import me.tabinol.factoidapi.utilities.StringChanges;

/**
 * The Class FlagValue.
 * Represent a flag value : Boolean, Double, String and String[]
 */
public class FlagValue implements IFlagValue {

	/** The value. */
	private Object value;

	/**
	 * Instantiates a new flag value.
	 *
	 * @param value the value
	 */
	public FlagValue(final Object value) {

		this.value = value;
	}


	@Override
	public FlagValue copyOf() {

		if (value instanceof Boolean) {
			return new FlagValue(Boolean.valueOf((Boolean)value));
		} else if (value instanceof Double) {
			return new FlagValue(Double.valueOf((Double)value));
		} else if (value instanceof String) {
			return new FlagValue(String.valueOf(value));
		} else if (value instanceof String[]) {
			final String[] newStr = new String[((String[]) value).length];
			for (int t = 0; t < ((String[]) value).length; t ++) {
				newStr[t] = String.valueOf(((String[]) value)[t]);
			}
			return new FlagValue(newStr);
		}
		return new FlagValue(value);
	}
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	@Override
	public void setValue(final Object value) {

		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Override
	public final Object getValue() {

		return value;
	}

	/**
	 * Gets the value boolean.
	 *
	 * @return the value boolean
	 */
	@Override
	public final boolean getValueBoolean() {

		return (Boolean) value;
	}

	/**
	 * Gets the value double.
	 *
	 * @return the value double
	 */
	@Override
	public final double getValueDouble() {

		return (Double) value;
	}

	/**
	 * Gets the value string.
	 *
	 * @return the value string
	 */
	@Override
	public final String getValueString() {

		return (String) value;
	}

	/**
	 * Gets the value string list.
	 *
	 * @return the value string list
	 */
	@Override
	public final String[] getValueStringList() {

		return (String[]) value;
	}

	/**
	 * Gets the value print.
	 *
	 * @return the value print
	 */
	@Override
	public final String getValuePrint() {

		if (value instanceof Boolean) {
			if ((Boolean) value) {
				return "" + ChatColor.GREEN + true;
			}
			return "" + ChatColor.RED + false;
		}

		if (value instanceof Double) {
			return ((Double) value).toString();
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof String[]) {
			final StringBuilder sb = new StringBuilder();
			for (final String st : (String[]) value) {
				if (sb.length() != 0) {
					sb.append("; ");
				}
				sb.append(StringChanges.toQuote(st));
			}
			return sb.toString();
		}

		return null;
	}

	/**
	 * Gets the flag value from string.
	 *
	 * @param str the string
	 * @param ft the flag type
	 * @return the flag value
	 */
	public static FlagValue getFromString(final String str, final FlagType ft) {

		FlagValue value;

		if (ft.isRegistered()) {
			if (ft.getDefaultValue().getValue() instanceof Boolean) {
				final String[] strs = str.split(" ");
				value = new FlagValue(Boolean.parseBoolean(strs[0]));
			} else if (ft.getDefaultValue().getValue() instanceof Double) {
				final String[] strs = str.split(" ");
				value = new FlagValue(Double.parseDouble(strs[0]));
			} else if (ft.getDefaultValue().getValue() instanceof String) {
				value = new FlagValue(StringChanges.fromQuote(str));
			} else if (ft.getDefaultValue().getValue() instanceof String[]) {
				final ArrayList<String> result = new ArrayList<>();
				final String[] strs = StringChanges.splitKeepQuote(str, ";");
				for (final String st : strs) {
					result.add(StringChanges.fromQuote(st));
				}
				value = new FlagValue(result.toArray(new String[0]));
			} else {
				value = null;
			}
		} else {
			// not registered save raw information
			value = new FlagValue(str);
		}

		return value;
	}
}
