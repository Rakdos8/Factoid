package me.tabinol.factoid.playercontainer;

public class PlayerContainerPlayer extends PlayerContainer implements PlayerContainerInterface {
    
    public PlayerContainerPlayer(String playerName) {
        
        super(playerName, "Player");
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayer &&
                name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayer(name);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        return (name.equalsIgnoreCase(playerName));
    }
}
