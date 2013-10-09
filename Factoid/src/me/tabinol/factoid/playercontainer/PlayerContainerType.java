package me.tabinol.factoid.playercontainer;

public enum PlayerContainerType {
    
    UNDEFINED(0,"UNDEFINED"),
    FACTION(1,"Faction"),
    GROUP(2,"Group"),
    PLAYER(3,"Player");
    
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
            if(pct.toString().equals(permName)) {
                return pct;
            }
        }
        return null;
    }
}
