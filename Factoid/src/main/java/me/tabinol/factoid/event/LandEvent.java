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
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private DummyLand dummyLand;
    private Land land;

    public LandEvent(DummyLand dummyLand) {

        this.dummyLand = dummyLand;
        
        if(dummyLand instanceof Land) {
            land = (Land) dummyLand;
        } else {
            land = null;
        }
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Land getLand() {

        return land;
    }
    
    public DummyLand getLandOrOutside() {
        
        return dummyLand;
    }
}
