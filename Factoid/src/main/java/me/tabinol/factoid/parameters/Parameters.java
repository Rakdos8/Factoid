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

import java.util.TreeMap;


/**
 * The Class Parameters.
 *
 * @author Tabinol
 */
public class Parameters {

    /** The permissions. */
    private final TreeMap<String, PermissionType> permissions;
    
    /** The flags. */
    private final TreeMap<String, FlagType> flags;

    /**
     * Instantiates a new parameters.
     */
    public Parameters() {

        permissions = new TreeMap<String, PermissionType>();
        flags = new TreeMap<String, FlagType>();

        // Add flags and permissions
        for (PermissionList permissionList : PermissionList.values()) {
            permissionList.setPermissionType(registerPermissionType(permissionList.name(), permissionList.baseValue));
        }
        for (FlagList flagList : FlagList.values()) {
            flagList.setFlagType(registerFlagType(flagList.name(), flagList.valueType));
        }
    }

    /**
     * Register permission type.
     *
     * @param permissionName the permission name
     * @param defaultValue the default value
     * @return the permission type
     */
    public final PermissionType registerPermissionType(String permissionName, boolean defaultValue) {

        String permissionNameUpper = permissionName.toUpperCase();
        PermissionType permissionType = getPermissionTypeNoValid(permissionNameUpper);
        permissionType.setDefaultValue(defaultValue);
        permissionType.setRegistered();
        
        return permissionType;
    }

    /**
     * Register flag type.
     *
     * @param flagName the flag name
     * @param flagValueType the flag value type
     * @return the flag type
     */
    public final FlagType registerFlagType(String flagName, FlagValueType flagValueType) {

        String flagNameUpper = flagName.toUpperCase();
        FlagType flagType = getFlagTypeNoValid(flagNameUpper);
        flagType.setFlagValueType(flagValueType);
        flagType.setRegistered();
        
        return flagType;
    }

    /**
     * Gets the permission type.
     *
     * @param permissionName the permission name
     * @return the permission type
     */
    public final PermissionType getPermissionType(String permissionName) {

        PermissionType pt = permissions.get(permissionName);

        if (pt != null && pt.isRegistered()) {
            return permissions.get(permissionName);
        } else {
            return null;
        }
    }

    /**
     * Gets the flag type.
     *
     * @param flagName the flag name
     * @return the flag type
     */
    public final FlagType getFlagType(String flagName) {

        FlagType ft = flags.get(flagName);

        if (ft != null && ft.isRegistered()) {
            return flags.get(flagName);
        } else {
            return null;
        }
    }

    /**
     * Gets the permission type no valid.
     *
     * @param permissionName the permission name
     * @return the permission type no valid
     */
    public final PermissionType getPermissionTypeNoValid(String permissionName) {

        PermissionType pt = permissions.get(permissionName);
        
        if(pt == null) {
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
    public final FlagType getFlagTypeNoValid(String flagName) {

        FlagType ft = flags.get(flagName);
        
        if(ft == null) {
            ft = new FlagType(flagName, FlagValueType.UNDEFINED);
            flags.put(flagName, ft);
        }
        
        return ft;
    }
}
