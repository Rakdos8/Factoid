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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import org.bukkit.entity.Player;

public class PlayerContainerFactionTerritory extends PlayerContainer {
    
    private Land land;
    
    public PlayerContainerFactionTerritory(Land land) {
        
        super("", PlayerContainerType.FACTION_TERRITORY, false);
        this.land = land;
    }
    
    @Override
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerFactionTerritory &&
                land == ((PlayerContainerFactionTerritory)container2).land;
    }
    
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerFactionTerritory(land);
    }

    @Override
    public boolean hasAccess(Player player) {
        
        if(land.getFactionTerritory() == null) {
            return false;
        }
        
        return land.getFactionTerritory().isPlayerInList(Factoid.getPlayerConf().get(player).getPlayerContainer());
    }

    public Land getLand() {
        
        return land;
    }

    @Override
    public void setLand(Land land) {
     
        this.land = land;
    }
}
