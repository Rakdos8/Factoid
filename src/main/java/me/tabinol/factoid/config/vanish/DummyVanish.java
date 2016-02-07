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
package me.tabinol.factoid.config.vanish;

import me.tabinol.factoid.BKVersion;
import me.tabinol.factoid.Factoid;

import org.bukkit.entity.Player;


/**
 * Only return false if there is no Vanish plugin.
 *
 * @author michel
 */
public class DummyVanish implements Vanish {

    /* (non-Javadoc)
     * @see me.tabinol.factoid.config.vanish.Vanish#isVanished(org.bukkit.entity.Player)
     */
    @Override
    public boolean isVanished(Player player) {
        
        return Factoid.getThisPlugin().iConf().isSpectatorIsVanish() 
        		&& BKVersion.isSpectatorMode(player);
    }
}
