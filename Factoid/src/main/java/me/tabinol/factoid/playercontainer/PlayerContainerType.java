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

public enum PlayerContainerType {
    
    // Order is important here The first is the permission checked first
    UNDEFINED(0,"UNDEFINED", false),
    OWNER(1,"Owner", false),
    PLAYER(2,"Player", true),
    RESIDENT(3,"Resident", false),
    TENANT(4, "Tenant", false),
    VISITOR(5, "Visitor", false),
    GROUP(6,"Group", true),
    PERMISSION(7, "Permission", true),
    FACTION(8,"Faction", true),
    FACTION_TERRITORY(9, "FactionTerritory", false),
    EVERYBODY(10,"Everybody", false),
    NOBODY(11,"Nobody", false);
    
    private final int value;
    private final String pcName;
    private final boolean hasParameter;
    
    private PlayerContainerType(final int value, final String pcName, final boolean hasParameter) {
        
        this.value = value;
        this.pcName = pcName;
        this.hasParameter = hasParameter;
    }
    
    public int getValue() {
        
        return value;
    }
    
    public boolean hasParameter() {
        
        return hasParameter;
    }
    
    @Override
    public String toString() {
        
        return pcName;
    }
    
    public static PlayerContainerType getFromString(String pcName) {
        
        for(PlayerContainerType pct : values()) {
            if(pct.toString().equalsIgnoreCase(pcName)) {
                return pct;
            }
        }
        return null;
    }
}
