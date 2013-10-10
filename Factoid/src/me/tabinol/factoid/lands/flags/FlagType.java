package me.tabinol.factoid.lands.flags;

public enum FlagType {

    //Global Flags
    UNDIFINED(0,"UNDEFINED","Undefined"),
    FIRESPREAD(1,"FIRESPREAD","Prevent the spread of fire."),
    FIRE(2,"FIRE","Prevent fire."),
    //Land Flags
    BUY(100,"BUY","Can buy this Area."),
    SELL(111,"SELL","Can sell this Area."),
    RENT(112,"RENT","Can rent this Area."),
    CREEPER(113,"CREEPER","Prevent creeper damage to this Area."),
    TNT(114,"TNT","Prevent TNT damage to this Area."),
    MOB(115,"MOB","Prevent Mob spawn in this Area."),
    PVP(116,"PVP","Prevent PVP in this Area."),
    FACTION_PVP("FACTION_PVP","?"),
    RESIDENTS("RESIDENTS""Resident of this Area."),
    EXCLUDE_BLOCKS("EXCLUDE_BLOCKS","Excluded block in this Area."),
    EXCLUDE_ENTITIES("EXCLUDE_ENTITIES","Excluded entity in this Area."),
    MESSAGE_JOIN("MESSAGE_JOIN","Message when player join this Area."),
    MESSAGE_QUIT("MESSAGE_QUIT","Message when player quit this Area."),
    KEEP_INVENTORY("KEEP_INVENTORY","If player can keep is Inventory.");
    
    private final String name;
    private final String description;

    private FlagType(final String name,final String description) {

        this.value = value;
        this.name = name;
        this.description = description;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        return description;
    }
    
    public int getCode(){
        return value;
    }
    
}
