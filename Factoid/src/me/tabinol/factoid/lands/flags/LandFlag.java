package me.tabinol.factoid.lands.flags;

import java.util.ArrayList;
import me.tabinol.factoid.utilities.StringChanges;

public class LandFlag {
    
    private FlagType flagType;
    private boolean valueBoolean = false;
    private String valueString = null;
    private String[] valueStringList = null;
    private boolean heritable;
    
    public LandFlag(final FlagType flagType, final boolean valueBoolean, final boolean heritable) {
        
        this.flagType = flagType;
        this.valueBoolean = valueBoolean;
        this.heritable = heritable;
    }

    public LandFlag(final FlagType flagType, final String valueString, final boolean heritable) {
        
        this.flagType = flagType;
        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            this.valueBoolean = Boolean.parseBoolean(valueString);
        } else if(flagType.getFlagValueType() == FlagValueType.STRING) {
            this.valueString = StringChanges.fromQuote(valueString);
        } else if(flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            ArrayList<String> result = new ArrayList<>();
            String[] strs = StringChanges.splitKeepQuote(valueString, ";");
            for(String str : strs) {
                result.add(StringChanges.fromQuote(str));
            }
            this.valueStringList = result.toArray(new String[0]);
        }
        this.heritable = heritable;
    }

    public LandFlag(final FlagType flagType, final String[] valueStringList, final boolean heritable) {
        
        this.flagType = flagType;
        this.valueStringList = valueStringList;
        this.heritable = heritable;
    }

    public boolean equals(LandFlag lf2) {
        
        return flagType == lf2.flagType;
    }
    
    public final FlagType getFlagType() {
        
        return flagType;
    }
    
    public final boolean getValueBoolean() {
        
        return valueBoolean;
    }

    public final String getValueString() {
        
        return valueString;
    }
    
    public final String getThisValueToString() {

        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            return valueBoolean + "";
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING) {
            return valueString;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            StringBuilder sb = new StringBuilder();
            for(String st : valueStringList) {
                sb.append(StringChanges.toQuote(st)).append(";");
            }
            return sb.toString();
        }
        
        return null;
    }
    
    public final String[] getValueStringList() {
        
        return valueStringList;
    }
    
    public boolean isHeritable() {
        
        return heritable;
    }
    
    @Override
    public String toString() {
        
        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            return flagType.toString() + ":" + valueBoolean + ":" + heritable;
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