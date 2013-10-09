package me.tabinol.factoid.playercontainer;

public enum PlayerContainerType {
    
    UNDEFINED(0),
    FACTION(1),
    GROUP(2),
    PLAYER(3);
    
    private final int value;
    
    private PlayerContainerType(final int value) {
        
        this.value = value;
    }
    
    @Override
    public String toString() {
        
        if(this == PLAYER) {
            return "Player";
        }
        if(this == GROUP) {
            return "Group";
        }
        if(this == FACTION) {
            return "Faction";
        }
        return "Undefined";
    }
    
    public int getValue() {
        
        return value;
    }
}
