package me.tabinol.factoid.playercontainer;

public interface PlayerContainerInterface {
    
    public String getName();
    
    public PlayerContainerType getContainerType();
    
    public boolean equals(PlayerContainer container2);
    
    public PlayerContainer copyOf();
    
    public boolean hasAccess(String playerName);
    
    public String getPrint();
            
}
