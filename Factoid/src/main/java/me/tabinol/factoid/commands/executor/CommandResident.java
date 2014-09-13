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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.ChatColor;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandResident.
 */
public class CommandResident extends CommandExec {

    /**
     * Instantiates a new command resident.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandResident(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, PermissionList.RESIDENT_MANAGER.getPermissionType(), null);
        
        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {
            
            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                        PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                        PlayerContainerType.RESIDENT});

            land.addResident(pc);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getPrint(), land.getName()));
            Factoid.getLog().write("Resident added: " + pc.toString());
        
        } else if (curArg.equalsIgnoreCase("remove")) {
            
            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land, null);
            if (!land.removeResident(pc)) {
                throw new FactoidCommandException("Resident", entity.player, "COMMAND.RESIDENT.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getPrint(), land.getName()));
            Factoid.getLog().write("Resident removed: " + pc.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {
            
            StringBuilder stList = new StringBuilder();
            if (!land.getResidents().isEmpty()) {
                for (PlayerContainer pc : land.getResidents()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
            }
            new ChatPage("COMMAND.RESIDENT.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        
        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}