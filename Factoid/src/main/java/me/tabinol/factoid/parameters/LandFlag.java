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

import java.util.ArrayList;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.ChatColor;


/**
 * The Class LandFlag.
 */
public class LandFlag {
    
    /** The flag type. */
    private FlagType flagType;
    
    /** The value boolean. */
    private boolean valueBoolean = false;
    
    /** The value double. */
    private double valueDouble = 0;
    
    /** The value string. */
    private String valueString = null;
    
    /** The value string list. */
    private String[] valueStringList = null;
    
    /** The heritable. */
    private boolean heritable;
    
    /**
     * Instantiates a new land flag.
     *
     * @param flagType the flag type
     * @param valueBoolean the value boolean
     * @param heritable the heritable
     */
    public LandFlag(final FlagType flagType, final boolean valueBoolean, final boolean heritable) {
        
        this.flagType = flagType;
        this.valueBoolean = valueBoolean;
        this.heritable = heritable;
    }

    /**
     * Instantiates a new land flag.
     *
     * @param flagType the flag type
     * @param valueDouble the value double
     * @param heritable the heritable
     */
    public LandFlag(final FlagType flagType, final double valueDouble, final boolean heritable) {
        
        this.flagType = flagType;
        this.valueDouble = valueDouble;
        this.heritable = heritable;
    }

    /**
     * Instantiates a new land flag.
     *
     * @param flagType the flag type
     * @param valueString the value string
     * @param heritable the heritable
     */
    public LandFlag(final FlagType flagType, final String valueString, final boolean heritable) {
        
        this.flagType = flagType;
        
        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN
                || flagType.getFlagValueType() == FlagValueType.UNDEFINED) {
            this.valueBoolean = Boolean.parseBoolean(valueString);
        }
        
        if(flagType.getFlagValueType() == FlagValueType.DOUBLE
                || flagType.getFlagValueType() == FlagValueType.UNDEFINED) {
            try {
            this.valueDouble = Double.parseDouble(valueString);
            } catch(NumberFormatException ex) {
                // null
            }
        }
        
        if(flagType.getFlagValueType() == FlagValueType.STRING
                || flagType.getFlagValueType() == FlagValueType.UNDEFINED) {
            this.valueString = StringChanges.fromQuote(valueString);
        }
        
        if(flagType.getFlagValueType() == FlagValueType.STRING_LIST
                || flagType.getFlagValueType() == FlagValueType.UNDEFINED) {
            ArrayList<String> result = new ArrayList<String>();
            String[] strs = StringChanges.splitKeepQuote(valueString, ";");
            for(String str : strs) {
                result.add(StringChanges.fromQuote(str));
            }
            this.valueStringList = result.toArray(new String[0]);
        }
        this.heritable = heritable;
    }

    /**
     * Instantiates a new land flag.
     *
     * @param flagType the flag type
     * @param valueStringList the value string list
     * @param heritable the heritable
     */
    public LandFlag(final FlagType flagType, final String[] valueStringList, final boolean heritable) {
        
        this.flagType = flagType;
        this.valueStringList = valueStringList;
        this.heritable = heritable;
    }

    /**
     * Equals.
     *
     * @param lf2 the lf2
     * @return true, if successful
     */
    public boolean equals(LandFlag lf2) {
        
        return flagType == lf2.flagType;
    }
    
    /**
     * Gets the flag type.
     *
     * @return the flag type
     */
    public final FlagType getFlagType() {
        
        return flagType;
    }
    
    /**
     * Gets the value boolean.
     *
     * @return the value boolean
     */
    public final boolean getValueBoolean() {
        
        return valueBoolean;
    }

    /**
     * Gets the value double.
     *
     * @return the value double
     */
    public final double getValueDouble() {
        
        return valueDouble;
    }

    /**
     * Gets the value string.
     *
     * @return the value string
     */
    public final String getValueString() {
        
        return valueString;
    }
    
    /**
     * Gets the value print.
     *
     * @return the value print
     */
    public final String getValuePrint() {

        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            if(valueBoolean) {
                return "" + ChatColor.GREEN + valueBoolean;
            } else {
                return "" + ChatColor.RED + valueBoolean;
            }
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING) {
            return valueString;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            StringBuilder sb = new StringBuilder();
            for(String st : valueStringList) {
                if(sb.length() != 0) {
                    sb.append("; ");
                }
                sb.append(StringChanges.toQuote(st));
            }
            return sb.toString();
        }
        
        return null;
    }
    
    /**
     * Gets the value string list.
     *
     * @return the value string list
     */
    public final String[] getValueStringList() {
        
        return valueStringList;
    }
    
    /**
     * Checks if is heritable.
     *
     * @return true, if is heritable
     */
    public boolean isHeritable() {
        
        return heritable;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            return flagType.toString() + ":" + valueBoolean + ":" + heritable;
        }
        if(flagType.getFlagValueType() == FlagValueType.DOUBLE) {
            return flagType.toString() + ":" + valueDouble + ":" + heritable;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING) {
            return flagType.toString() + ":" + StringChanges.toQuote(valueString) + ":" + heritable;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            StringBuilder sb = new StringBuilder();
            for(String st : valueStringList) {
                sb.append(StringChanges.toQuote(st)).append(";");
            }
            return flagType.toString() + ":" + sb.toString() + ":" + heritable;
        }
        
        return null;
    }
}