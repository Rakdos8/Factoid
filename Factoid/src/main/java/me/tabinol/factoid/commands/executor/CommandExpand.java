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
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import org.bukkit.ChatColor;

public class CommandExpand extends CommandExec {
    
    public CommandExpand(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        /**
         * *** TO DO : COLLISIONS! ******
         */
        checkSelections(null, false, true, null, null);
        checkPermission(true, true, null, null);

        // Land land = entity.playerConf.getLandSelected();
            
        String curArg = entity.argList.getNext();

        if (entity.playerConf.getExpendingLand() == null) {
            entity.player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            Factoid.getLog().write(entity.player.getName() + " have join ExpandMode.");
            LandExpansion expand = new LandExpansion(entity.player);
            entity.playerConf.setExpandingLand(expand);
        } else if (curArg != null && curArg.equalsIgnoreCase("done")) {
            entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            Factoid.getLog().write(entity.playerName + " have quit ExpandMode.");
            LandExpansion expand = entity.playerConf.getExpendingLand();
            expand.setDone();
            entity.playerConf.setExpandingLand(null);
        } else {
            throw new FactoidCommandException("Player Expand", entity.player, "COMMAND.EXPAND.ALREADY");
        }
    }
}
