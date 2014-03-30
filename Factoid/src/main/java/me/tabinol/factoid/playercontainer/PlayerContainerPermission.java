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

import me.tabinol.factoid.lands.Land;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PlayerContainerPermission extends PlayerContainer {

    private Permission perm;
    
    public PlayerContainerPermission(String bukkitPermission) {

        super(bukkitPermission, PlayerContainerType.PERMISSION);
        perm = new Permission(bukkitPermission);
    }

    @Override
    public boolean equals(PlayerContainer container2) {

        return container2 instanceof PlayerContainerPermission
                && name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {

        return new PlayerContainerPermission(name);
    }

    @Override
    public boolean hasAccess(Player player) {

        return player.hasPermission(perm);
    }

    @Override
    public String getPrint() {

        return ChatColor.GRAY + "B:" + ChatColor.WHITE + name;
    }

    @Override
    public void setLand(Land land) {

    }
}
