package me.tabinol.factoid.playercontainer;

public enum PlayerContainerType {
    
    UNDEFINED(0,"UNDEFINED"),
    FACTION(1,"Faction"),
    GROUP(2,"Group"),
    OWNER(3,"Owner"),
    RESIDENT(4,"Resident"),
    PLAYER(5,"Player"),
    EVERYBODY(6,"Everybody");
    
    private final int value;
    private final String pcName;
    
    private PlayerContainerType(final int value, final String pcName) {
        
        this.value = value;
        this.pcName = pcName;
    }
    
    public int getValue() {
        
        return value;
    }
    
    @Override
    public String toString() {
        
        return pcName;
    }
    
    public static PlayerContainerType getFromString(String permName) {
        
        for(PlayerContainerType pct : values()) {
            if(pct.toString().equalsIgnoreCase(permName)) {
                return pct;
            }
        }
        return null;
    }
}
