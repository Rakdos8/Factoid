package me.tabinol.factoid.lands.flags;

public enum FlagType {

    //Global Flags
    UNDIFINED(0,"UNDIFINED","Undifined"),
    FIRESPREAD(1,"Firespread","Prevent the spread of fire."),
    FIRE(2,"Fire","Prevent fire."),
    //Land Flags
    BUY(100,"Buy","Can buy this Area."),
    SELL(111,"Sell","Can sell this Area."),
    RENT(112,"Rent","Can rent this Area."),
    CREEPER(113,"Creeper","Prevent creeper damage to this Area."),
    TNT(114,"TNT","Prevent TNT damage to this Area."),
    MOB(115,"MOB","Prevent Mob spawn in this Area."),
    PVP(116,"PVP","Prevent PVP in this Area."),
    PVPFACTION(117,"Faction PVP","Prevent Faction PVP in this Area"),
    OWNER(118,"Owner","Owner of this Area."),
    RESIDENT(119,"Resident","Resident of this Area."),
    EXCLUDEBLOCK(120,"Exclude Block","Excluded block in this Area."),
    EXCLUDEENTITIE(121,"Exclude Entitie","Excluded entity in this Area."),
    MESSAGEJOIN(122,"Message Join","Message when player join this Area."),
    MESSAGEQUIT(123,"Message Quit","Message when player quit this Area."),
    PRIORITY(124,"Priority","Priority of this Area."),
    KEEPINVENTORY(125,"Keep Inventory","If player can keep is Inventory.");
    
    private final int value;
    private final String name;
    private final String description;

    private FlagType(final int value,final String name,final String description) {

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
