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
package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerLandChangeEvent extends LandEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    Player player;
    Location fromLoc;
    Location toLoc;
    Land lastLand;
    DummyLand lastDummyLand;
    boolean isTp;

    public PlayerLandChangeEvent(final DummyLand lastDummyLand, final DummyLand dummyLand, final Player player, 
            final Location fromLoc, final Location toLoc, final boolean isTp) {

        super(dummyLand);
        this.lastDummyLand = lastDummyLand;
        
        if(lastDummyLand instanceof Land) {
            lastLand = (Land) lastDummyLand;
        } else {
            lastLand = null;
        }
        
        this.player = player;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.isTp = isTp;
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    @Override
    public boolean isCancelled() {
        
        return cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
        
        cancelled = bln;
    }
    
    public Player getPlayer() {
        
        return player;
    }
    
    public Land getLastLand() {
        
        return lastLand;
    }
    
    public DummyLand getLastLandOrOutside() {
        
        return lastDummyLand;
    }
    
    public Location getFromLoc() {
        
        return fromLoc;
    }

    public Location getToLoc() {
        
        return toLoc;
    }
    
    public boolean isTp() {
        
        return isTp;
    }
}
