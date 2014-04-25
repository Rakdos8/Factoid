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
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.utilities.StringChanges;

public abstract class PlayerContainer implements PlayerContainerInterface, Comparable<PlayerContainer> {

    protected String name;
    protected PlayerContainerType containerType;

    protected PlayerContainer(String name, PlayerContainerType containerType) {

        this.name = name.toLowerCase();
        this.containerType = containerType;
    }

    public static PlayerContainer create(Land land, PlayerContainerType pct, String name) {

        if (pct == PlayerContainerType.FACTION) {
            Faction faction = Factoid.getFactions().getFaction(name);
            if (faction != null) {
                return new PlayerContainerFaction(faction);
            } else {
                return null;
            }
        }
        if (pct == PlayerContainerType.GROUP) {
            return new PlayerContainerGroup(name);
        }
        if (pct == PlayerContainerType.RESIDENT) {
            return new PlayerContainerResident(land);
        }
        if (pct == PlayerContainerType.VISITOR) {
            return new PlayerContainerVisitor(land);
        }
        if (pct == PlayerContainerType.FACTION_TERRITORY) {
            return new PlayerContainerFactionTerritory(land);
        }
        if (pct == PlayerContainerType.OWNER) {
            return new PlayerContainerOwner(land);
        }
        if (pct == PlayerContainerType.EVERYBODY) {
            return new PlayerContainerEverybody();
        }
        if (pct == PlayerContainerType.NOBODY) {
            return new PlayerContainerNobody();
        }
        if (pct == PlayerContainerType.PLAYER) {
            // TEMP for switch from FactoidID to MinecraftID
            if(name.startsWith("ID-")) {
                UUID playerUUID = UUID.fromString(name.replaceFirst("ID-", ""));
                return Factoid.getPlayerUUID().getPCPFromMinecraftUUID(playerUUID);
            } else {
                return Factoid.getPlayerUUID().getPCPFromString(name);
            }
        }
        if (pct == PlayerContainerType.PERMISSION) {
            return new PlayerContainerPermission(name);
        }
        return null;
    }

    @Override
    public String getName() {

        return name;
    }
    
    @Override
    public PlayerContainerType getContainerType() {

        return containerType;
    }

    @Override
    public int compareTo(PlayerContainer t) {

        if (containerType.getValue() < t.containerType.getValue()) {
            return -1;
        }
        if (containerType.getValue() > t.containerType.getValue()) {
            return 1;
        }

        // No ignorecase (Already Lower, except UUID)
        return name.compareTo(t.name);
    }

    @Override
    public String toString() {

        return containerType.toString() + ":" + name;
    }

    @Override
    public String getPrint() {

        return containerType.toString();
    }

    public static PlayerContainer getFromString(String string) {

        if (!string.contains(":")) {

            // It is a player
            return Factoid.getPlayerUUID().getPCPFromString(string);
        } else {
            
            // We don't know
            String strs[] = StringChanges.splitAddVoid(string, ":");
            PlayerContainerType type = PlayerContainerType.getFromString(strs[0]);
            return create(null, type, strs[1]);
        }
    }
}
