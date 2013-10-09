package me.tabinol.factoid.lands.permissions;


public enum PermissionType {
    
    UNDEFINED(0),
    BUILD_PLACE(1),
    BUILD_DESTROY(2),
    DROP(3),
    USE_CRAFT(4),
    USE_BREW(5),
    USE_SMELT(6),
    USE_CHEST(7),
    USE_DOOR(8),
    USE_SWITCH(9),
    USE_PRESSUREPLATE(10),
    USE_TRAPPEDCHEST(11),
    USE_STRING(12),
    ANIMAL_KILL(13),
    ANIMAL_CUT(14),
    ANIMAL_FEED(15),
    ANIMAL_ACCOUPLE(16),
    MOB_KILL(17),
    MOB_HEAL(18),
    VILLAGER_KILL(19),
    VILLAGER_GOLEM_KILL(20),
    BUCKET_WATER(21),
    BUCKET_LAVA(22),
    FIRE(23),
    TNT(24),
    ENTER(25),
    REMOVE(26),
    KICK(27),
    BAN(28),
    WHOS(29);
    
    private final int value;
    
    private PermissionType(final int value) {
        
        this.value = value;
    }
}
