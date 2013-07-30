package me.tabinol.factoid.playercontainer;

public abstract class PlayerContainer implements PlayerContainerInterface {
    
    private String name;
    
    public PlayerContainer(String name) {
        
        this.name = name;
    }
    
    public String getName() {
        
        return name;
    }
    
}
