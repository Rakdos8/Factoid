package me.tabinol.factoid.lands.flags;

public enum FlagType {

    //Global Flags
    UNDEFINED("UNDEFINED", FlagValueType.UNDEFINED),
    FIRESPREAD("FIRESPREAD", FlagValueType.BOOLEAN),
    FIRE("FIRE", FlagValueType.BOOLEAN),
    //Land Flags
    BUY("BUY", FlagValueType.BOOLEAN),
    SELL("SELL", FlagValueType.BOOLEAN),
    RENT("RENT", FlagValueType.BOOLEAN),
    CREEPER("CREEPER", FlagValueType.BOOLEAN),
    TNT("TNT", FlagValueType.BOOLEAN),
    MOB("MOB", FlagValueType.BOOLEAN),
    PVP("PVP", FlagValueType.BOOLEAN),
    FACTION_PVP("FACTION_PVP", FlagValueType.BOOLEAN),
    RESIDENTS("RESIDENTS", FlagValueType.STRING_LIST),
    EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST),
    EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST),
    MESSAGE_JOIN("MESSAGE_JOIN", FlagValueType.STRING),
    MESSAGE_QUIT("MESSAGE_QUIT", FlagValueType.STRING),
    KEEP_INVENTORY("KEEP_INVENTORY", FlagValueType.BOOLEAN);
    
    private final String name;
    private final FlagValueType valueType;

    private FlagType(final String name, final FlagValueType valueType) {

        this.name = name;
        this.valueType = valueType;
    }
    
    @Override
    public String toString() {
        
        return name;
    }
    
    public static FlagType getFromString(String flagName) {
        
        for(FlagType ft : values()) {
            if(ft.toString().equals(flagName)) {
                return ft;
            }
        }
        return null;
    }
    
    public final FlagValueType getFlagValueType() {
        
        return valueType;
    }

}
