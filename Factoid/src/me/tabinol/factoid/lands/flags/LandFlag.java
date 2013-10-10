package me.tabinol.factoid.lands.flags;

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
        this.valueString = valueString;
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
    
    public final String[] getValueStringList() {
        
        return valueStringList;
    }
    
    @Override
    public String toString() {
        
        if(flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            return flagType.toString() + ":" + valueBoolean + ":" + heritable;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING) {
            return flagType.toString() + ":" + valueString + ":" + heritable;
        }
        if(flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            StringBuilder sb = new StringBuilder();
            for(String st : valueStringList) {
                sb.append(st).append(";");
            }
            return flagType.toString() + ":" + valueString + ":" + heritable;
        }
        
        return null;
    }
}