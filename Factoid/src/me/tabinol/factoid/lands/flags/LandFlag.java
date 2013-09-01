package me.tabinol.factoid.lands.flags;

public class LandFlag {
    
    // public static final int *** Définir les sortes de flags ****
    int flagType;
    boolean inheritance;
    boolean value;
    
    public LandFlag(int flagType, boolean inheritance, boolean value) {
        
        this.flagType = flagType;
        this.inheritance = inheritance;
        this.value = value;
    }
    
    public LandFlag copyOf() {
        
        return new LandFlag(flagType, inheritance, value);
    }
    
    public boolean equals(LandFlag lf2) {
        
        return flagType == lf2.flagType;
    }
    
    
}