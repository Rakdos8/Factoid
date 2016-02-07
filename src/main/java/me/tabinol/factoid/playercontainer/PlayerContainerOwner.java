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

import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerOwner;

import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerOwner.
 */
public class PlayerContainerOwner extends PlayerContainer implements IPlayerContainerOwner {

    /** The land. */
    private ILand land;
    
    /**
     * Instantiates a new player container owner.
     *
     * @param land the land
     */
    public PlayerContainerOwner(ILand land) {
        
        super("", EPlayerContainerType.OWNER, false);
        this.land = land;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerOwner &&
                land == ((PlayerContainerOwner)container2).land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerOwner(land);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
    	
    	return hasAccess(player, land);
    }
        
    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        boolean value;
        ILand parent;
    	
    	if(land == null) {
    		return false;
    	}
        
    	value = land.getOwner().hasAccess(player);
    	
    	if(!value && (parent = land.getParent()) != null 
    			&& land.getFlagAndInherit(FlagList.INHERIT_OWNER.getFlagType()).getValueBoolean() == true) {
    		
    		return parent.getOwner().hasAccess(player);
    	}
    	
    	return value;
    }


    /**
     * Gets the land.
     *
     * @return the land
     */
    public ILand getLand() {
        
        return land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {

        this.land = land;
    }
}
