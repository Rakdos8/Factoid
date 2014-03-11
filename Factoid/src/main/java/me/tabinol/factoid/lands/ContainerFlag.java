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
package me.tabinol.factoid.lands;

import me.tabinol.factoid.lands.flags.LandFlag;
import java.util.HashSet;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class ContainerFlag {
    
    PlayerContainer pc;
    HashSet <LandFlag> flags;
    
    public ContainerFlag(PlayerContainer pc, HashSet <LandFlag> flags) {
        
        this.pc = pc;
        if(flags == null) {
            this.flags = new HashSet<LandFlag>();
        } else {
            this.flags = flags;
        }
    }
    
    public boolean equals(ContainerFlag cf2) {
        
        return pc.equals(cf2.pc);
    }
}
