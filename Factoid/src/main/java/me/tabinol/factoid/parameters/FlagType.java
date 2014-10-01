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
 * The Class FlagType.
 */
public class FlagType extends ParameterType {

    /** The value type. */
    private FlagValueType valueType;

    /**
     * Instantiates a new flag type.
     *
     * @param flagName the flag name
     * @param valueType the value type
     */
    FlagType(String flagName, FlagValueType valueType) {

        super(flagName);
        this.valueType = valueType;
    }
    
    /**
     * Sets the flag value type.
     *
     * @param valueType the new flag value type
     */
    void setFlagValueType(FlagValueType valueType) {
        
        this.valueType = valueType;
    }
    
    /**
     * Gets the flag value type.
     *
     * @return the flag value type
     */
    public final FlagValueType getFlagValueType() {
        
        return valueType;
    }
}
