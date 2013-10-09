package me.tabinol.factoid.playercontainer;

public abstract class PlayerContainer implements PlayerContainerInterface,Comparable<PlayerContainer> {
    
    protected String name;
    protected PlayerContainerType containerType;
    
    protected PlayerContainer(String name, PlayerContainerType containerType) {
        
        this.name = name;
        this.containerType = containerType;
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
