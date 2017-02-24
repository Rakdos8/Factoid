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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;


/**
 * The Class CommandReload.
 */
@InfoCommand(name="reload", allowConsole=true)
public class CommandReload extends CommandExec {

	/**
	 * Instantiates a new command reload.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandReload(final CommandEntities entity) throws FactoidCommandException {

		super(entity);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
	 */
	@Override
	public void commandExecute() throws FactoidCommandException {

		checkPermission(false, false, null, "factoid.reload");

		entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.RELOAD.START"));
		Factoid.getThisPlugin().reload();
		entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.RELOAD.COMPLETE"));
	}
}
