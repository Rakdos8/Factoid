package me.tabinol.factoid.lands.permissions;


public enum PermissionType {
    
    UNDEFINED("UNDEFINED", false),
    BUILD_PLACE("BUILD_PLACE", true),
    BUILD_DESTROY("BUILD_DESTROY", true),
    DROP("DROP", true),
    USE_CRAFT("USE_CRAFT", true),
    USE_BREW("USE_BREW", true),
    USE_SMELT("USE_SMELT", true),
    USE_CHEST("USE_CHEST", true),
    USE_DOOR("USE_DOOR", true),
    USE_SWITCH("USE_SWITCH", true),
    USE_PRESSUREPLATE("USE_PRESSUREPLATE", true),
    USE_TRAPPEDCHEST("USE_TRAPPEDCHEST", true),
    USE_STRING("USE_STRING", true),
    ANIMAL_KILL("ANIMAL_KILL", true),
    ANIMAL_CUT("ANIMAL_CUT", true),
    ANIMAL_FEED("ANIMAL_FEED", true),
    ANIMAL_ACCOUPLE("ANIMAL_ACCOUPLE", true),
    MOB_KILL("MOB_KILL", true),
    MOB_HEAL("MOB_HEAL", true),
    VILLAGER_KILL("VILLAGER_KILL", true),
    VILLAGER_GOLEM_KILL("VILLAGER_GOLEM_KILL", true),
    HORSE_KILL("HORSE_KILL", true),
    HORSE_EQUIP("HORSE_EQUIP", true),
    HORSE_CLIMB("HORSE_CLIMB", true),
    HARVEST("HARVEST", true),
    BUCKET_WATER("BUCKET_WATER", true),
    BUCKET_LAVA("BUCKET_LAVA", true),
    FIRE("FIRE", true),
    TNT("TNT", true),
    ENTER("ENTER", true),
    REMOVE("REMOVE", true),
    KICK("KICK", false),
    BAN("BAN", false),
    WHOS("WHOS", false);
    
    private final String permissionName;
    private final boolean baseValue;
    
    private PermissionType(final String permissionName, boolean baseValue) {
        
        this.permissionName = permissionName;
        this.baseValue = baseValue;
    }
    
    @Override
    public String toString() {
        
        return permissionName;
    }
    
    public static PermissionType getFromString(String permName) {
        
        for(PermissionType pt : values()) {
            if(pt.toString().equals(permName)) {
                return pt;
            }
        }
        return null;
    }
    
    public boolean baseValue() {
        
        return baseValue;
    }
}