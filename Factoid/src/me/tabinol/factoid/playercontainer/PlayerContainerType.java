package me.tabinol.factoid.playercontainer;

public enum PlayerContainerType {
    
    // Order is important here The first is the permission checked first
    UNDEFINED(0,"UNDEFINED"),
    OWNER(1,"Owner"),
    PLAYER(2,"Player"),
    RESIDENT(3,"Resident"),
    GROUP(4,"Group"),
    FACTION(5,"Faction"),
    EVERYBODY(6,"Everybody"),
    NOBODY(7,"Nobody");
    
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
