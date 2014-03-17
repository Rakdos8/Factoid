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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerContainerPlayer extends PlayerContainer {

    private UUID minecraftUUID; // UUID of player in Minecraft (Change only form null to UUID?)
    private final UUID factoidUUID; // Never Changed
    private String playerName;
    private Player player; // Is player id the player is online
    
    // Compare before create
    protected PlayerContainerPlayer(UUID factoidUUID, String playerName, UUID MinecraftUUID) {

        super("", PlayerContainerType.PLAYER);
        this.factoidUUID = factoidUUID;
        this.minecraftUUID = MinecraftUUID;
        name = factoidUUID.toString(); // Change the name to the Factoid UUID
        this.playerName = playerName.toLowerCase();
        player = null;
    }

    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayer &&
                factoidUUID.equals(((PlayerContainerPlayer) container2).factoidUUID);
    }

    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayer(factoidUUID, name, minecraftUUID);
    }

    @Override
    public boolean hasAccess(Player player) {
        
        if(player != null) {
            return player.getUniqueId().equals(minecraftUUID);
        } else {
            return false;
        }
    }

    @Override
    public String getPrint() {

        StringBuilder sb = new StringBuilder();
        
        // If the name is not complete, add (*)
        if(minecraftUUID == null) {
            sb.append(ChatColor.WHITE).append("(*)");
        }
        
        sb.append(ChatColor.DARK_RED).append("P:");
        sb.append(ChatColor.WHITE).append(playerName);
        
        return sb.toString();
    }

    @Override
    public void setLand(Land land) {

    }
    
    public UUID getFactoidUUID() {
        
        return factoidUUID;
    }
    
    public UUID getMinecraftUUID() {
        
        return minecraftUUID;
    }
    
    public void setMinecraftUUID(UUID minecraftUUID) {
        
        this.minecraftUUID = minecraftUUID;
    }
    
    public String getPlayerName() {
        
        return playerName;
    }
    
    public void setPlayerNmae(String playerName) {
        
        this.playerName = playerName.toLowerCase();
    }
    
    public boolean isOnline() {
        
        return player != null;
    }
    
    public void setPlayer(Player player) {
        
        this.player = player;
    }
    
    public Player getPlayer() {
        
        return player;
    }
}
