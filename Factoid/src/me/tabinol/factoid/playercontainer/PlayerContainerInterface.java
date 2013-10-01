package me.tabinol.factoid.playercontainer;

public interface PlayerContainerInterface {
    
    public String getName();
    
    public String getContainerType();
    
    public boolean equals(PlayerContainer container2);
    
    public PlayerContainer copyOf();
    
}
