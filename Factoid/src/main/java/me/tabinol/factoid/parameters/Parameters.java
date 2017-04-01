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
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;

import me.tabinol.factoidapi.parameters.IParameters;


/**
 * The Class Parameters.
 *
 * @author Tabinol
 */
public class Parameters implements IParameters {

	/** The permissions. */
	private final TreeMap<String, PermissionType> permissions = new TreeMap<>();

	/** The flags. */
	private final TreeMap<String, FlagType> flags = new TreeMap<>();

	/** List of unregistered flags for an update **/
	protected final List<LandFlag> unRegisteredFlags = new ArrayList<>();

	/** Special permission Map Prefix-->Material-->PermissionType */
	private final Map<SpecialPermPrefix, Map<Material, PermissionType>> specialPermMap = new EnumMap<>(SpecialPermPrefix.class);

	/**
	 * Instantiates a new parameters.
	 */
	public Parameters() {
		// Add special permissions (PLACE_XXX and DESTROY_XXX, NOPLACE_XXX, NODESTROY_XXX)
		for (final SpecialPermPrefix pref : SpecialPermPrefix.values()) {
			final Map<Material, PermissionType> matPerms = new EnumMap<>(Material.class);
			for (final Material mat : Material.values()) {
				matPerms.put(mat, registerPermissionType(pref.name() + "_" + mat.name(), false));
			}
			specialPermMap.put(pref, matPerms);
		}

		// Add all permissions
		for (final PermissionList permissionList : PermissionList.values()) {
			permissionList.setPermissionType(registerPermissionType(permissionList.name(), permissionList.baseValue));
		}
		// Add all flags
		for (final FlagList flagList : FlagList.values()) {
			flagList.setFlagType(registerFlagType(flagList.name(), flagList.baseValue));
		}
	}

	/**
	 * Register permission type.
	 *
	 * @param permissionName the permission name
	 * @param defaultValue the default value
	 * @return the permission type
	 */
	@Override
	public final PermissionType registerPermissionType(final String permissionName, final boolean defaultValue) {
		final String permissionNameUpper = permissionName.toUpperCase();
		final PermissionType permissionType = getPermissionTypeNoValid(permissionNameUpper);
		permissionType.setDefaultValue(defaultValue);
		permissionType.setRegistered();

		return permissionType;
	}

	/**
	 * Register flag type.
	 *
	 * @param flagName the flag name
	 * @param defaultValue the default value
	 * @return the flag type
	 */
	@Override
	public final FlagType  registerFlagType(final String flagName, final Object defaultValue) {
		FlagValue flagDefaultValue;

		// Check is default value is raw or is FlagDefaultValue
		if (defaultValue instanceof FlagValue) {
			flagDefaultValue = (FlagValue) defaultValue;
		} else {
			flagDefaultValue = new FlagValue(defaultValue);
		}

		final String flagNameUpper = flagName.toUpperCase();
		final FlagType flagType = getFlagTypeNoValid(flagNameUpper);
		flagType.setDefaultValue(flagDefaultValue);
		flagType.setRegistered();

		// Update flag registration (for correct type)
		final Iterator<LandFlag> iFlag = unRegisteredFlags.iterator();
		while (iFlag.hasNext()) {
		   final LandFlag flag = iFlag.next();
		   if (flagType == flag.getFlagType()) {
			   final String str = flag.getValue().getValueString();
			   flag.setValue(FlagValue.getFromString(str, flagType));
			   iFlag.remove();
		   }
		}

		return flagType;

	}

	/**
	 * Gets the permission type.
	 *
	 * @param permissionName the permission name
	 * @return the permission type
	 */
	@Override
	public final PermissionType getPermissionType(final String permissionName) {
		final PermissionType pt = permissions.get(permissionName);

		if (pt != null && pt.isRegistered()) {
			return permissions.get(permissionName);
		}
		return null;
	}

	/**
	 * Gets the flag type.
	 *
	 * @param flagName the flag name
	 * @return the flag type
	 */
	@Override
	public final FlagType getFlagType(final String flagName) {
		final FlagType ft = flags.get(flagName);

		if (ft != null && ft.isRegistered()) {
			return flags.get(flagName);
		}
		return null;
	}

	/**
	 * Gets the permission type no valid.
	 *
	 * @param permissionName the permission name
	 * @return the permission type no valid
	 */
	public final PermissionType getPermissionTypeNoValid(final String permissionName) {
		PermissionType pt = permissions.get(permissionName);

		if (pt == null) {
			pt = new PermissionType(permissionName, false);
			permissions.put(permissionName, pt);
		}
		return pt;
	}

	/**
	 * Gets the flag type no valid.
	 *
	 * @param flagName the flag name
	 * @return the flag type no valid
	 */
	public final FlagType getFlagTypeNoValid(final String flagName) {
		FlagType ft = flags.get(flagName);

		if (ft == null) {
			ft = new FlagType(flagName, new String());
			flags.put(flagName, ft);
		}
		return ft;
	}

	@Override
	public final PermissionType getSpecialPermission(final SpecialPermPrefix prefix, final Material mat) {
		final Map<Material, PermissionType> matPerms = specialPermMap.get(prefix);

		if (matPerms == null) {
			return null;
		}
		return matPerms.get(mat);
	}
}
