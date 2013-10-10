package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.Factoid;

public abstract class PlayerContainer implements PlayerContainerInterface,Comparable<PlayerContainer> {
    
    protected String name;
    protected PlayerContainerType containerType;
    
    protected PlayerContainer(String name, PlayerContainerType containerType) {
        
        this.name = name;
        this.containerType = containerType;
    }
    
    public static PlayerContainer create(PlayerContainerType pct, String name) {
        
        if(pct == PlayerContainerType.FACTION) {
            return new PlayerContainerFaction(Factoid.getFactions().getFaction(name));
        }
        if(pct == PlayerContainerType.GROUP) {
            return new PlayerContainerGroup(name);
        }
        return new PlayerContainerPlayer(name);
    }
    
    @Override
    public String getName() {
        
        return name;
    }
    
    @Override
    public PlayerContainerType getContainerType() {
        
        return containerType;
    }

    @Override
    public int compareTo(PlayerContainer t) {
        
        if(containerType.getValue() < t.containerType.getValue()) {
            return -1;
        }
        if(containerType.getValue() > t.containerType.getValue()) {
            return 1;
        }
        return name.compareToIgnoreCase(name);
    }
    
    @Override
    public String toString() {
        
        return containerType + ":" + name;
    }
}
