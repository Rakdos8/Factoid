/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.playercontainer;

import java.util.UUID;
import me.tabinol.factoid.lands.Land;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerContainerPlayer extends PlayerContainer {

    private final UUID minecraftUUID;
    
    // Compare before create
    public PlayerContainerPlayer(UUID minecraftUUID) {

        super("ID-" + minecraftUUID.toString(), PlayerContainerType.PLAYER, false);
        this.minecraftUUID = minecraftUUID;
    }

    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayer &&
                minecraftUUID.equals(((PlayerContainerPlayer) container2).minecraftUUID);
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayer(minecraftUUID);
    }

    @Override
    public boolean hasAccess(Player player) {
        
        if(player != null) {
            return minecraftUUID.equals(player.getUniqueId());
        } else {
            return false;
        }
    }

    @Override
    public String getPrint() {

        StringBuilder sb = new StringBuilder();
        
        sb.append(ChatColor.DARK_RED).append("P:");
        sb.append(ChatColor.WHITE).append(Bukkit.getOfflinePlayer(minecraftUUID).getName());
        
        return sb.toString();
    }

    @Override
    public void setLand(Land land) {

    }
    
    public UUID getMinecraftUUID() {
        
        return minecraftUUID;
    }
    
    public String getPlayerName() {
        
        return Bukkit.getOfflinePlayer(minecraftUUID).getName();
    }
    
    public boolean isOnline() {
        
        return Bukkit.getOfflinePlayer(minecraftUUID).isOnline();
    }
    
    public Player getPlayer() {
        
        return Bukkit.getPlayer(minecraftUUID);
    }
}
