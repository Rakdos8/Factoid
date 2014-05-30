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

// TODO: Auto-generated Javadoc
/**
 * The Enum FlagList.
 *
 * @author Tabinol
 */
public enum FlagList {
    
    /** The undefined. */
    UNDEFINED(FlagValueType.UNDEFINED),
    
    /** The firespread. */
    FIRESPREAD(FlagValueType.BOOLEAN),
    
    /** The fire. */
    FIRE(FlagValueType.BOOLEAN),
    
    /** The explosion. */
    EXPLOSION(FlagValueType.BOOLEAN),
    
    /** The creeper explosion. */
    CREEPER_EXPLOSION(FlagValueType.BOOLEAN),
    
    /** The tnt explosion. */
    TNT_EXPLOSION(FlagValueType.BOOLEAN),
    
    /** The creeper damage. */
    CREEPER_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The enderman damage. */
    ENDERMAN_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The wither damage. */
    WITHER_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The ghast damage. */
    GHAST_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The enderdragon damage. */
    ENDERDRAGON_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The tnt damage. */
    TNT_DAMAGE(FlagValueType.BOOLEAN),
    
    /** The mob spawn. */
    MOB_SPAWN(FlagValueType.BOOLEAN),
    
    /** The animal spawn. */
    ANIMAL_SPAWN(FlagValueType.BOOLEAN),
    // ANIMAL_CUT("ANIMAL_CUT", FlagValueType.BOOLEAN),
    /** The full pvp. */
    FULL_PVP(FlagValueType.BOOLEAN),
    
    /** The faction pvp. */
    FACTION_PVP(FlagValueType.BOOLEAN),
    //EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST),
    //EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST),
    /** The message join. */
    MESSAGE_JOIN(FlagValueType.STRING),
    
    /** The message quit. */
    MESSAGE_QUIT(FlagValueType.STRING),
    
    /** The eco block price. */
    ECO_BLOCK_PRICE(FlagValueType.DOUBLE),
    
    /** The exclude commands. */
    EXCLUDE_COMMANDS(FlagValueType.STRING_LIST);

    /** The value type. */
    FlagValueType valueType;
    
    /** The flag type. */
    FlagType flagType;
    
    /**
     * Instantiates a new flag list.
     *
     * @param valueType the value type
     */
    private FlagList(final FlagValueType valueType) {

        this.valueType = valueType;
    }
    
    /**
     * Sets the flag type.
     *
     * @param flagType the new flag type
     */
    void setFlagType(FlagType flagType) {
        
        this.flagType = flagType;
    }

    /**
     * Gets the flag type.
     *
     * @return the flag type
     */
    public FlagType getFlagType() {
        
        return flagType;
    }
}
