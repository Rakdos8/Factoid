package me.tabinol.factoid.lands.flags;

public enum FlagType {

    UNDEFINED("UNDEFINED", FlagValueType.UNDEFINED, false),
    FIRESPREAD("FIRESPREAD", FlagValueType.BOOLEAN, false),
    FIRE("FIRE", FlagValueType.BOOLEAN, false),
    BUY("BUY", FlagValueType.BOOLEAN, true),
    SELL("SELL", FlagValueType.BOOLEAN, true),
    RENT("RENT", FlagValueType.BOOLEAN, true),
    CREEPER_DAMAGE("CREEPER_DAMAGE", FlagValueType.BOOLEAN, false),
    ENDERMAN_DAMAGE("ENDERMAN_DAMAGE", FlagValueType.BOOLEAN, false),
    WHITER_DAMAGE("WITHER_DAMAGE", FlagValueType.BOOLEAN, false),
    GHAST_DAMAGE("GHAST_DAMAGE", FlagValueType.BOOLEAN, false),
    TNT_DAMAGE("TNT_DAMAGE", FlagValueType.BOOLEAN, false),
    HOSTILE_SPAWN("HOSTILE_SPAWN", FlagValueType.BOOLEAN, false),
    FULL_PVP("FULL_PVP", FlagValueType.BOOLEAN, false),
    FACTION_PVP("FACTION_PVP", FlagValueType.BOOLEAN, false),
    EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST, false),
    EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST, false),
    MESSAGE_JOIN("MESSAGE_JOIN", FlagValueType.STRING, true),
    MESSAGE_QUIT("MESSAGE_QUIT", FlagValueType.STRING, true),
    KEEP_INVENTORY("KEEP_INVENTORY", FlagValueType.BOOLEAN, true);
    
    private final String name;
    private final FlagValueType valueType;
    private boolean onlyForLand;

    private FlagType(final String name, final FlagValueType valueType, boolean onlyForLand) {

        this.name = name;
        this.valueType = valueType;
        this.onlyForLand = onlyForLand;
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

    public boolean isOnlyForLand() {
        
        return onlyForLand;
    }
}
