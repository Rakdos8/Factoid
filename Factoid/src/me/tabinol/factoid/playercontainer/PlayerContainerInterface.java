package me.tabinol.factoid.playercontainer;

public interface PlayerContainerInterface {
    
    public String getName();
    
    public boolean equals(PlayerContainer container2);
    
    public PlayerContainer copyOf();
    
}
