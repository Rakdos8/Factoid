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

import me.tabinol.factoid.lands.Land;
import org.bukkit.entity.Player;


/**
 * The Interface PlayerContainerInterface.
 */
public interface PlayerContainerInterface {
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName();
    
    /**
     * Gets the container type.
     *
     * @return the container type
     */
    public PlayerContainerType getContainerType();
    
    /**
     * Equals.
     *
     * @param container2 the container2
     * @return true, if successful
     */
    public boolean equals(PlayerContainer container2);
    
    /**
     * Compare to.
     *
     * @param t the t
     * @return the int
     */
    public int compareTo(PlayerContainer t);
    
    /**
     * Copy of.
     *
     * @return the player container
     */
    public PlayerContainer copyOf();
    
    /**
     * Checks for access.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean hasAccess(Player player);
    
    /**
     * Gets the prints the.
     *
     * @return the prints the
     */
    public String getPrint();
    
    /**
     * Sets the land.
     *
     * @param land the new land
     */
    public void setLand(Land land);
            
}
