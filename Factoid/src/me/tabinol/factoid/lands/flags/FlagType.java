package me.tabinol.factoid.lands.flags;

public enum FlagType {

    // Ceux qui sont pas fait sont en commentaire
    UNDEFINED(FlagValueType.UNDEFINED),
    FIRESPREAD(FlagValueType.BOOLEAN),
    FIRE(FlagValueType.BOOLEAN),
    // 3 prochains pour l'économie (pas fait)
    //BUY(FlagValueType.BOOLEAN),
    //SELL(FlagValueType.BOOLEAN),
    //RENT(FlagValueType.BOOLEAN),
    EXPLOSION(FlagValueType.BOOLEAN),
    CREEPER_EXPLOSION(FlagValueType.BOOLEAN),
    TNT_EXPLOSION(FlagValueType.BOOLEAN),
    CREEPER_DAMAGE(FlagValueType.BOOLEAN),
    ENDERMAN_DAMAGE(FlagValueType.BOOLEAN),
    WITHER_DAMAGE(FlagValueType.BOOLEAN),
    GHAST_DAMAGE(FlagValueType.BOOLEAN),
    ENDERDRAGON_DAMAGE(FlagValueType.BOOLEAN),
    TNT_DAMAGE(FlagValueType.BOOLEAN),
    MOB_SPAWN(FlagValueType.BOOLEAN),
    ANIMAL_SPAWN(FlagValueType.BOOLEAN),
    // ANIMAL_CUT("ANIMAL_CUT", FlagValueType.BOOLEAN),
    FULL_PVP(FlagValueType.BOOLEAN),
    FACTION_PVP(FlagValueType.BOOLEAN),
    //EXCLUDE_BLOCKS("EXCLUDE_BLOCKS", FlagValueType.STRING_LIST),
    //EXCLUDE_ENTITIES("EXCLUDE_ENTITIES", FlagValueType.STRING_LIST),
    MESSAGE_JOIN(FlagValueType.STRING),
    MESSAGE_QUIT(FlagValueType.STRING),
    KEEP_INVENTORY(FlagValueType.BOOLEAN); // Pas fait (pas mis en commentaire à cause du point virgule
    
    private final FlagValueType valueType;

    private FlagType(final FlagValueType valueType) {

        this.valueType = valueType;
    }
    
    public final FlagValueType getFlagValueType() {
        
        return valueType;
    }
}
