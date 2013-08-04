package me.tabinol.factoid.playercontainer;

public abstract class PlayerContainer implements PlayerContainerInterface {
    
    protected String name;
    
    protected PlayerContainer(String name) {
        
        this.name = name;
    }
    
    @Override
    public String getName() {
        
        return name;
    }
    
}
