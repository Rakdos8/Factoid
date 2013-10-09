package me.tabinol.factoid.lands.permissions;


public enum PermissionType {
    
    UNDEFINED(0,"UNDEFINED"),
    BUILD_PLACE(1,"BUILD_PLACE"),
    BUILD_DESTROY(2,"BUILD_DESTROY"),
    DROP(3,"DROP"),
    USE_CRAFT(4,"USE_CRAFT"),
    USE_BREW(5,"USE_BREW"),
    USE_SMELT(6,"USE_SMELT"),
    USE_CHEST(7,"USE_CHEST"),
    USE_DOOR(8,"USE_DOOR"),
    USE_SWITCH(9,"USE_SWITCH"),
    USE_PRESSUREPLATE(10,"USE_PRESSUREPLATE"),
    USE_TRAPPEDCHEST(11,"USE_TRAPPEDCHEST"),
    USE_STRING(12,"USE_STRING"),
    ANIMAL_KILL(13,"ANIMAL_KILL"),
    ANIMAL_CUT(14,"ANIMAL_CUT"),
    ANIMAL_FEED(15,"ANIMAL_FEED"),
    ANIMAL_ACCOUPLE(16,"ANIMAL_ACCOUPLE"),
    MOB_KILL(17,"MOB_KILL"),
    MOB_HEAL(18,"MOB_HEAL"),
    VILLAGER_KILL(19,"VILLAGER_KILL"),
    VILLAGER_GOLEM_KILL(20,"VILLAGER_GOLEM_KILL"),
    BUCKET_WATER(21,"BUCKET_WATER"),
    BUCKET_LAVA(22,"BUCKET_LAVA"),
    FIRE(23,"FIRE"),
    TNT(24,"TNT"),
    ENTER(25,"ENTER"),
    REMOVE(26,"REMOVE"),
    KICK(27,"KICK"),
    BAN(28,"BAN"),
    WHOS(29,"WHOS");
    
    private final int value;
    private final String permissionName;
    
    private PermissionType(final int value, final String permissionName) {
        
        this.value = value;
        this.permissionName = permissionName;
    }
    
    @Override
    public String toString() {
        
        return permissionName;
    }
    
    public PermissionType getFromString(String permName) {
        
        for(PermissionType pt : values()) {
            if(pt.toString().equals(permName)) {
                return pt;
            }
        }
        return null;
    }
}