package me.tabinol.factoid.playercontainer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerContainerPermission extends PlayerContainer implements PlayerContainerInterface {
    
    public PlayerContainerPermission(String bukkitPermission) {
        
        super(bukkitPermission, PlayerContainerType.PERMISSION);
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPermission &&
                name.equalsIgnoreCase(container2.getName());
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPermission(name);
    }

    @Override
    public boolean hasAccess(String playerName) {
        
        Player player = Bukkit.getPlayer(playerName);
        
        if(player != null) {
            return player.hasPermission(name);
        } else {
            return false;
        }
    }
    
    @Override
    public String getPrint() {
        
        return ChatColor.GRAY + "B:" + ChatColor.WHITE + name;
    }
}
