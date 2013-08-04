package me.tabinol.factoid.playercontainer;

public class PlayerContainerGroup extends PlayerContainer implements PlayerContainerInterface {
    
    public PlayerContainerGroup(String groupName) {
        
        super(groupName);
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerGroup &&
                name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerGroup(name);
    }

    
}
