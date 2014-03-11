package me.tabinol.factoid.lands.permissions;

public enum PermissionType {
    
    UNDEFINED(false),
    BUILD(true),
    BUILD_PLACE(true),
    BUILD_DESTROY(true),
    DROP(true),
    PICKETUP(true),
    SLEEP(true),
    OPEN(true),
    OPEN_CRAFT(true),
    OPEN_BREW(true),
    OPEN_FURNACE(true),
    OPEN_CHEST(true),
    OPEN_ENDERCHEST(true),
    OPEN_BEACON(true),
    OPEN_HOPPER(true),
    OPEN_DROPPER(true),
    USE(true),
    USE_DOOR(true),
    USE_BUTTON(true),
    USE_LEVER(true),
    USE_PRESSUREPLATE(true),
    USE_TRAPPEDCHEST(true),
    USE_STRING(true),
    ANIMAL_KILL(true),
    //ANIMAL_FEED(true),
    //ANIMAL_ACCOUPLE(true),
    TAMED_KILL(true),
    MOB_KILL(true),
    //MOB_HEAL(true),
    VILLAGER_KILL(true),
    VILLAGER_GOLEM_KILL(true),
    HORSE_KILL(true),
    //HORSE_EQUIP(true),
    //HORSE_CLIMB(true),
    BUCKET_WATER(true),
    BUCKET_LAVA(true),
    FIRE(true),
    //TNT(true),
    AUTO_HEAL(false),
    POTION_SPLASH(true),
    RESIDENT_MANAGER(false),
    LAND_CREATE(false),
    LAND_ENTER(true),
    LAND_REMOVE(false),
    LAND_KICK(false),
    LAND_BAN(false),
    LAND_WHO(false),
    LAND_NOTIFY(false),
    MONEY_DEPOSIT(false),
    MONEY_WITHDRAW(false),
    MONEY_BALANCE(false);
    
    private final boolean baseValue;
    
    private PermissionType(boolean baseValue) {
        
        this.baseValue = baseValue;
    }
    
    public boolean baseValue() {
        
        return baseValue;
    }
}