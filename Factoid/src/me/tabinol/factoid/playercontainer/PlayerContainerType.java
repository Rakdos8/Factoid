package me.tabinol.factoid.playercontainer;

public enum PlayerContainerType {
    
    // Order is important here The first is the permission checked first
    UNDEFINED(0,"UNDEFINED", false),
    OWNER(1,"Owner", false),
    PLAYER(2,"Player", true),
    RESIDENT(3,"Resident", false),
    VISITOR(4, "Visitor", false),
    GROUP(5,"Group", true),
    FACTION(6,"Faction", true),
    FACTION_TERRITORY(7, "FactionTerritory", false),
    EVERYBODY(8,"Everybody", false),
    NOBODY(9,"Nobody", false);
    
    private final int value;
    private final String pcName;
    private final boolean hasParameter;
    
    private PlayerContainerType(final int value, final String pcName, final boolean hasParameter) {
        
        this.value = value;
        this.pcName = pcName;
        this.hasParameter = hasParameter;
    }
    
    public int getValue() {
        
        return value;
    }
    
    public boolean hasParameter() {
        
        return hasParameter;
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
