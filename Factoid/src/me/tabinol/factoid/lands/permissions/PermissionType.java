package me.tabinol.factoid.lands.permissions;


public enum PermissionType {
    
    UNDEFINED("UNDEFINED"),
    BUILD_PLACE("BUILD_PLACE"),
    BUILD_DESTROY("BUILD_DESTROY"),
    DROP("DROP"),
    USE_CRAFT("USE_CRAFT"),
    USE_BREW("USE_BREW"),
    USE_SMELT("USE_SMELT"),
    USE_CHEST("USE_CHEST"),
    USE_DOOR("USE_DOOR"),
    USE_SWITCH("USE_SWITCH"),
    USE_PRESSUREPLATE("USE_PRESSUREPLATE"),
    USE_TRAPPEDCHEST("USE_TRAPPEDCHEST"),
    USE_STRING("USE_STRING"),
    ANIMAL_KILL("ANIMAL_KILL"),
    ANIMAL_CUT("ANIMAL_CUT"),
    ANIMAL_FEED("ANIMAL_FEED"),
    ANIMAL_ACCOUPLE("ANIMAL_ACCOUPLE"),
    MOB_KILL("MOB_KILL"),
    MOB_HEAL("MOB_HEAL"),
    VILLAGER_KILL("VILLAGER_KILL"),
    VILLAGER_GOLEM_KILL("VILLAGER_GOLEM_KILL"),
    BUCKET_WATER("BUCKET_WATER"),
    BUCKET_LAVA("BUCKET_LAVA"),
    FIRE("FIRE"),
    TNT("TNT"),
    ENTER("ENTER"),
    REMOVE("REMOVE"),
    KICK("KICK"),
    BAN("BAN"),
    WHOS("WHOS");
    
    private final String permissionName;
    
    private PermissionType(final String permissionName) {
        
        this.permissionName = permissionName;
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
}