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

import org.bukkit.ChatColor;

public class Permission {

    PermissionType permType;
    boolean value;
    boolean heritable;

    public Permission(final PermissionType permType, final boolean value, final boolean heritable) {

        this.permType = permType;
        this.value = value;
        this.heritable = heritable;
    }

    public PermissionType getPermType() {

        return permType;
    }

    public boolean getValue() {

        return value;
    }

    public final String getValuePrint() {

        if (value) {
            return "" + ChatColor.GREEN + value;
        } else {
            return "" + ChatColor.RED + value;
        }
    }

    public boolean isHeritable() {

        return heritable;
    }

    @Override
    public String toString() {

        return permType.toString() + ":" + value + ":" + heritable;
    }
}
