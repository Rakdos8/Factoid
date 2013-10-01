package me.tabinol.factoid.playercontainer;

public abstract class PlayerContainer implements PlayerContainerInterface {
    
    protected String name;
    protected String containerType;
    
    protected PlayerContainer(String name, String containerType) {
        
        this.name = name;
        this.containerType = containerType;
    }
    
    @Override
    public String getName() {
        
        return name;
    }
    
    @Override
    public String getContainerType() {
        
        return containerType;
    }
    
}
