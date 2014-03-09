package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerContainerGroup extends PlayerContainer implements PlayerContainerInterface {
    
    public PlayerContainerGroup(String groupName) {
        
        super(groupName, PlayerContainerType.GROUP);
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
            return Factoid.getDependPlugin().getPermission().playerInGroup(player, name);
        } else {
            return false;
        }
    }
    
    @Override
    public String getPrint() {
        
        return ChatColor.BLUE + "G:" + ChatColor.WHITE + name;
    }

    @Override
    public void setLand(Land land) {
        
    }
}
