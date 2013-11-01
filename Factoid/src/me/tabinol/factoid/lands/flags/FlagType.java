package me.tabinol.factoid.lands.flags;

public enum FlagType {

    // Ceux qui sont pas fait sont en commentaire
    UNDEFINED("UNDEFINED", FlagValueType.UNDEFINED),
    FIRESPREAD("FIRESPREAD", FlagValueType.BOOLEAN),
    FIRE("FIRE", FlagValueType.BOOLEAN),
    // 3 prochains pour l'économie (pas fait)
    //BUY("BUY", FlagValueType.BOOLEAN),
    //SELL("SELL", FlagValueType.BOOLEAN),
    //RENT("RENT", FlagValueType.BOOLEAN),
    EXPLOSION("EXPLOSION", FlagValueType.BOOLEAN),
    CREEPER_EXPLOSION("CREEPER_EXPLOSION", FlagValueType.BOOLEAN),
    TNT_EXPLOSION("TNT_EXPLOSION", FlagValueType.BOOLEAN),
    CREEPER_DAMAGE("CREEPER_DAMAGE", FlagValueType.BOOLEAN),
    ENDERMAN_DAMAGE("ENDERMAN_DAMAGE", FlagValueType.BOOLEAN),
    WHITER_DAMAGE("WITHER_DAMAGE", FlagValueType.BOOLEAN),
    GHAST_DAMAGE("GHAST_DAMAGE", FlagValueType.BOOLEAN),
    TNT_DAMAGE("TNT_DAMAGE", FlagValueType.BOOLEAN),
    MOB_SPAWN("MOB_SPAWN", FlagValueType.BOOLEAN),
    ANIMAL_SPAWN("ANIMAL_SPAWN", FlagValueType.BOOLEAN),
    // ANIMAL_CUT("ANIMAL_CUT", FlagValueType.BOOLEAN),
    FULL_PVP("FULL_PVP", FlagValueType.BOOLEAN),
    FACTION_PVP("FACTION_PVP", FlagValueType.BOOLEAN),
    //EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST),
    //EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST),
    MESSAGE_JOIN("MESSAGE_JOIN", FlagValueType.STRING),
    MESSAGE_QUIT("MESSAGE_QUIT", FlagValueType.STRING),
    KEEP_INVENTORY("KEEP_INVENTORY", FlagValueType.BOOLEAN); // Pas fait (pas mis en commentaire à cause du point virgule
    
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
            if(ft.toString().equalsIgnoreCase(flagName)) {
                return ft;
            }
        }
        return null;
    }
    
    public final FlagValueType getFlagValueType() {
        
        return valueType;
    }
}
