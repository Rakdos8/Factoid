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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class PlayerContainerPlayer.
 */
public class PlayerContainerPlayer extends PlayerContainer {

    /** The minecraft uuid. */
    private final UUID minecraftUUID;
    
    // Compare before create
    /**
     * Instantiates a new player container player.
     *
     * @param minecraftUUID the minecraft uuid
     */
    public PlayerContainerPlayer(UUID minecraftUUID) {

        super("ID-" + minecraftUUID.toString(), PlayerContainerType.PLAYER, false);
        this.minecraftUUID = minecraftUUID;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayer &&
                minecraftUUID.equals(((PlayerContainerPlayer) container2).minecraftUUID);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayer(minecraftUUID);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        if(player != null) {
            return minecraftUUID.equals(player.getUniqueId());
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        StringBuilder sb = new StringBuilder();
        
        sb.append(ChatColor.DARK_RED).append("P:");
        sb.append(ChatColor.WHITE).append(Bukkit.getOfflinePlayer(minecraftUUID).getName());
        
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(Land land) {

    }
    
    /**
     * Gets the minecraft uuid.
     *
     * @return the minecraft uuid
     */
    public UUID getMinecraftUUID() {
        
        return minecraftUUID;
    }
    
    /**
     * Gets the player name.
     *
     * @return the player name
     */
    public String getPlayerName() {
        
        return Bukkit.getOfflinePlayer(minecraftUUID).getName();
    }
    
    /**
     * Checks if is online.
     *
     * @return true, if is online
     */
    public boolean isOnline() {
        
        return Bukkit.getOfflinePlayer(minecraftUUID).isOnline();
    }
    
    /**
     * Gets the player.
     *
     * @return the player
     */
    public Player getPlayer() {
        
        return Bukkit.getPlayer(minecraftUUID);
    }
    
    /**
     * Get the offline player.
     * 
     * @return the offline player
     */
    public OfflinePlayer getOfflinePlayer() {
    	
    	return Bukkit.getOfflinePlayer(minecraftUUID);
    }
}
