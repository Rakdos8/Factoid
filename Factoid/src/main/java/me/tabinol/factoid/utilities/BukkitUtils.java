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

package me.tabinol.factoid.utilities;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

// TODO: Auto-generated Javadoc
/**
 * Utility methods for Bukkit.
 *
 * @author Tabinol
 */
public class BukkitUtils {
    
    /**
     * Get the offline player from name only from the local player list.
     * @param playerName The player name (none case sensitive)
     * @return the OfflinePlayer or not if the player is not found
     */
    public static OfflinePlayer getOfflinePlayer(String playerName) {
        
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if(offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return offlinePlayer;
            }
        }

        return null;
    }
}
