package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.lands.Land;

public interface PlayerContainerInterface {
    
    public String getName();
    
    public PlayerContainerType getContainerType();
    
    public boolean equals(PlayerContainer container2);
    
    public PlayerContainer copyOf();
    
    public boolean hasAccess(String playerName);
    
    public String getPrint();
    
    public void setLand(Land land);
            
}
