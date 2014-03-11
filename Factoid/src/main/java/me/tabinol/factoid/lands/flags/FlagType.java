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
package me.tabinol.factoid.lands.flags;

public enum FlagType {

    // Ceux qui sont pas fait sont en commentaire
    UNDEFINED(FlagValueType.UNDEFINED),
    FIRESPREAD(FlagValueType.BOOLEAN),
    FIRE(FlagValueType.BOOLEAN),
    // 3 prochains pour l'économie (pas fait)
    //BUY(FlagValueType.BOOLEAN),
    //SELL(FlagValueType.BOOLEAN),
    //RENT(FlagValueType.BOOLEAN),
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
    KEEP_INVENTORY(FlagValueType.BOOLEAN); // Pas fait (pas mis en commentaire à cause du point virgule
    
    private final FlagValueType valueType;

    private FlagType(final FlagValueType valueType) {

        this.valueType = valueType;
    }
    
    public final FlagValueType getFlagValueType() {
        
        return valueType;
    }
}
