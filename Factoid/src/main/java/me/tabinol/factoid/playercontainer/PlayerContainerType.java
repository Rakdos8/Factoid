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

// TODO: Auto-generated Javadoc
/**
 * The Enum PlayerContainerType.
 */
public enum PlayerContainerType {
    
    // Order is important here The first is the permission checked first
    /** The undefined. */
    UNDEFINED(0,"UNDEFINED", false),
    
    /** The owner. */
    OWNER(1,"Owner", false),
    
    /** The player. */
    PLAYER(2,"Player", true),
    
    /** The resident. */
    RESIDENT(3,"Resident", false),
    
    /** The tenant. */
    TENANT(4, "Tenant", false),
    
    /** The visitor. */
    VISITOR(5, "Visitor", false),
    
    /** The group. */
    GROUP(6,"Group", true),
    
    /** The permission. */
    PERMISSION(7, "Permission", true),
    
    /** The faction. */
    FACTION(8,"Faction", true),
    
    /** The faction territory. */
    FACTION_TERRITORY(9, "FactionTerritory", false),
    
    /** The everybody. */
    EVERYBODY(10,"Everybody", false),
    
    /** The nobody. */
    NOBODY(11,"Nobody", false);
    
    /** The value. */
    private final int value;
    
    /** The pc name. */
    private final String pcName;
    
    /** The has parameter. */
    private final boolean hasParameter;
    
    /**
     * Instantiates a new player container type.
     *
     * @param value the value
     * @param pcName the pc name
     * @param hasParameter the has parameter
     */
    private PlayerContainerType(final int value, final String pcName, final boolean hasParameter) {
        
        this.value = value;
        this.pcName = pcName;
        this.hasParameter = hasParameter;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        
        return value;
    }
    
    /**
     * Checks for parameter.
     *
     * @return true, if successful
     */
    public boolean hasParameter() {
        
        return hasParameter;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        
        return pcName;
    }
    
    /**
     * Gets the from string.
     *
     * @param pcName the pc name
     * @return the from string
     */
    public static PlayerContainerType getFromString(String pcName) {
        
        for(PlayerContainerType pct : values()) {
            if(pct.toString().equalsIgnoreCase(pcName)) {
                return pct;
            }
        }
        return null;
    }
}
