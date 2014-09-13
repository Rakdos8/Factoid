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
package me.tabinol.factoid.commands.executor;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.utilities.StringChanges;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandTp.
 */
public class CommandTp extends CommandExec {

    /**
     * Instantiates a new command tp.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandTp(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();
        land = Factoid.getLands().getLand(curArg);
        
        // Land not found
        if(land == null) {
        	throw new FactoidCommandException("On land tp player", entity.player, "COMMAND.TP.LANDNOTFOUND");
        }
        
        // Check adminmod or permission TP
        checkPermission(true, false, PermissionList.TP.getPermissionType(), null);

        // Try to get Location
        LandFlag flag = land.getFlagAndInherit(FlagList.SPAWN.getFlagType());
        
        if(flag == null) {
        	throw new FactoidCommandException("On land tp player", entity.player, "COMMAND.TP.NOSPAWN");
        }
        
        Location location = StringChanges.stringToLocation(flag.getValueString());
        
        if(location == null) {
        	throw new FactoidCommandException("On land tp player", entity.player, "COMMAND.TP.INVALID");
        }
        
        // Teleport player
        entity.player.teleport(location);
    }
}
