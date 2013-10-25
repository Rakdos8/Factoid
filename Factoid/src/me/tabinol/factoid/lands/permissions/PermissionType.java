package me.tabinol.factoid.lands.permissions;

// Ceux qui sont en commentaire ne sont pas fait

public enum PermissionType {
    
    UNDEFINED("UNDEFINED", false),
    BUILD("BUILD", true),
    BUILD_PLACE("BUILD_PLACE", true),
    BUILD_DESTROY("BUILD_DESTROY", true),
    DROP("DROP", true),
    PICKETUP("PICKETUP", true),
    SLEEP("SLEEP", true),
    OPEN("OPEN", true),
    OPEN_CRAFT("OPEN_CRAFT", true),
    OPEN_BREW("OPEN_BREW", true),
    OPEN_FURNACE("OPEN_SMELT", true),
    OPEN_CHEST("OPEN_CHEST", true),
    OPEN_ENDERCHEST("OPEN_ENDERCHEST", true),
    OPEN_BEACON("OPEN_BEACON", true),
    OPEN_HOPPER("OPEN_HOPPER", true),
    OPEN_DROPPER("OPEN_DROPPER", true),
    USE("USE", true),
    USE_DOOR("USE_DOOR", true),
    USE_BUTTON("USE_BUTTON", true),
    USE_LEVER("USE_SWITCH", true),
    USE_PRESSUREPLATE("USE_PRESSUREPLATE", true),
    USE_TRAPPEDCHEST("USE_TRAPPEDCHEST", true),
    USE_STRING("USE_STRING", true),
    ANIMAL_KILL("ANIMAL_KILL", true),
    //ANIMAL_FEED("ANIMAL_FEED", true),
    //ANIMAL_ACCOUPLE("ANIMAL_ACCOUPLE", true),
    TAMED_KILL("TAMED_KILL", true),
    MOB_KILL("MOB_KILL", true),
    //MOB_HEAL("MOB_HEAL", true),
    VILLAGER_KILL("VILLAGER_KILL", true),
    VILLAGER_GOLEM_KILL("VILLAGER_GOLEM_KILL", true),
    HORSE_KILL("HORSE_KILL", true),
    //HORSE_EQUIP("HORSE_EQUIP", true),
    //HORSE_CLIMB("HORSE_CLIMB", true),
    BUCKET_WATER("BUCKET_WATER", true),
    BUCKET_LAVA("BUCKET_LAVA", true),
    FIRE("FIRE", true),
    //TNT("TNT", true),
    // *** LAND PERM (PAS FAIT)***
    LAND_CREATE("LAND_CREATE", true),
    LAND_ENTER("LAND_ENTER", true), // sauf lui
    LAND_REMOVE("LAND_REMOVE", true),
    LAND_KICK("LAND_KICK", false),
    LAND_BAN("LAND_BAN", false),
    LAND_WHOS("LAND_WHOS", false);
    
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
            if(pt.toString().equalsIgnoreCase(permName)) {
                return pt;
            }
        }
        return null;
    }
    
    public boolean baseValue() {
        
        return baseValue;
    }
}