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
 *
 * @author Tabinol
 */
public class Parameters {

    private final TreeMap<String, PermissionType> permissions;
    private final TreeMap<String, FlagType> flags;

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

    public final PermissionType registerPermissionType(String permissionName, boolean defaultValue) {

        String permissionNameUpper = permissionName.toUpperCase();
        PermissionType permissionType = new PermissionType(permissionNameUpper, defaultValue);
        permissionType.setRegistered();
        permissions.put(permissionNameUpper, permissionType);
        
        return permissionType;
    }

    public final FlagType registerFlagType(String flagName, FlagValueType flagValueType) {

        String flagNameUpper = flagName.toUpperCase();
        FlagType flagType = new FlagType(flagNameUpper, flagValueType);
        flagType.setRegistered();
        flags.put(flagNameUpper, flagType);
        
        return flagType;
    }

    public final PermissionType getPermissionType(String permissionName) {

        PermissionType pt = permissions.get(permissionName);

        if (pt != null && pt.isRegistered()) {
            return permissions.get(permissionName);
        } else {
            return null;
        }
    }

    public final FlagType getFlagType(String flagName) {

        FlagType ft = flags.get(flagName);

        if (ft != null && ft.isRegistered()) {
            return flags.get(flagName);
        } else {
            return null;
        }
    }

    public final PermissionType getPermissionTypeNoValid(String permissionName) {

        PermissionType pt = permissions.get(permissionName);
        
        if(pt == null) {
            pt = permissions.put(permissionName, new PermissionType(permissionName, false));
        }
        
        return pt;
    }

    public final FlagType getFlagTypeNoValid(String flagName) {

        FlagType ft = flags.get(flagName);
        
        if(ft == null) {
            ft = flags.put(flagName, new FlagType(flagName, FlagValueType.UNDEFINED));
        }
        
        return ft;
    }
}
