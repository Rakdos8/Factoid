package me.tabinol.factoid.lands.flags;

public enum FlagType {

    //Global Flags
    UNDIFINED(0),
    FIRESPREAD(1),
    FIRE(2),
    //Land Flags
    BUY(100),
    SELL(111),
    RENT(112),
    CREEPER(113),
    TNT(114),
    MOB(115),
    PVP(116),
    FACTION(117),
    OWNER(118),
    RESIDENT(119),
    EXCLUDEBLOCK(120),
    EXCLUDEENTITIE(121),
    MESSAGEJOIN(122),
    MESSAGEQUIT(123),
    PRIORITY(124);
    
    private final int value;

    private FlagType(final int value) {

        this.value = value;
    }
}
