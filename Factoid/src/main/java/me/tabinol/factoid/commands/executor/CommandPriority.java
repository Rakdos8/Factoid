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
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.ChatColor;

public class CommandPriority extends CommandExec {

    public CommandPriority(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);
        String curArg = entity.argList.getNext();
        short newPrio;

        if (land.getParent() != null) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.NOTCHILD");
        }
        try {
            newPrio = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        if (!Calculate.isInInterval(newPrio, Land.MINIM_PRIORITY, Land.MAXIM_PRIORITY)) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        land.setPriority(newPrio);

        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage(
                "COMMAND.PRIORITY.DONE", land.getName(), land.getPriority() + ""));
        Factoid.getLog().write("Priority for land " + land.getName() + " changed for " + land.getPriority());
    }
}
