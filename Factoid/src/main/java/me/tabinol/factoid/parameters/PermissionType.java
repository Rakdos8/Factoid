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

import me.tabinol.factoidapi.parameters.IPermissionType;

/**
 * The Class PermissionType.
 */
public class PermissionType extends ParameterType implements IPermissionType {

	/** The default value. */
	private boolean defaultValue;

	/**
	 * Instantiates a new permission type.
	 *
	 * @param permissionName the permission name
	 * @param defaultValue the default value
	 */
	PermissionType(final String permissionName, final boolean defaultValue) {
		super(permissionName);
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the new default value
	 */
	void setDefaultValue(final boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	@Override
	public boolean getDefaultValue() {
		return defaultValue;
	}
}