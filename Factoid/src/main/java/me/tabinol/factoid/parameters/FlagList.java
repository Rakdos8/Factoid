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

/**
 *
 * @author Tabinol
 */
public enum FlagList {
    
    UNDEFINED(FlagValueType.UNDEFINED),
    FIRESPREAD(FlagValueType.BOOLEAN),
    FIRE(FlagValueType.BOOLEAN),
    EXPLOSION(FlagValueType.BOOLEAN),
    CREEPER_EXPLOSION(FlagValueType.BOOLEAN),
    TNT_EXPLOSION(FlagValueType.BOOLEAN),
    CREEPER_DAMAGE(FlagValueType.BOOLEAN),
    ENDERMAN_DAMAGE(FlagValueType.BOOLEAN),
    WITHER_DAMAGE(FlagValueType.BOOLEAN),
    GHAST_DAMAGE(FlagValueType.BOOLEAN),
    ENDERDRAGON_DAMAGE(FlagValueType.BOOLEAN),
    TNT_DAMAGE(FlagValueType.BOOLEAN),
    MOB_SPAWN(FlagValueType.BOOLEAN),
    ANIMAL_SPAWN(FlagValueType.BOOLEAN),
    // ANIMAL_CUT("ANIMAL_CUT", FlagValueType.BOOLEAN),
    FULL_PVP(FlagValueType.BOOLEAN),
    FACTION_PVP(FlagValueType.BOOLEAN),
    //EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST),
    //EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST),
    MESSAGE_JOIN(FlagValueType.STRING),
    MESSAGE_QUIT(FlagValueType.STRING),
    KEEP_INVENTORY(FlagValueType.BOOLEAN),
    ECO_BLOCK_PRICE(FlagValueType.DOUBLE);

    FlagValueType valueType;
    FlagType flagType;
    
    private FlagList(final FlagValueType valueType) {

        this.valueType = valueType;
    }
    
    void setFlagType(FlagType flagType) {
        
        this.flagType = flagType;
    }

    public FlagType getFlagType() {
        
        return flagType;
    }
}
