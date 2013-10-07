package me.tabinol.factoid.playercontainer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerContainerGroup extends PlayerContainer implements PlayerContainerInterface {
    
    public PlayerContainerGroup(String groupName) {
        
        super(groupName, "Group");
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

    @Override
    public boolean hasAccess(String playerName) {
        
        Player player = Bukkit.getPlayer(playerName);
        
        if(player != null) {
            return player.hasPermission("group." + name);
        } else {
            return false;
        }
    }
}
