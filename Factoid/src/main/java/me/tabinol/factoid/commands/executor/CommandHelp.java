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

import org.bukkit.command.CommandSender;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.commands.MainCommand;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;


/**
 * The Class CommandHelp.
 */
@InfoCommand(name="help", mainCommand={MainCommand.FACTOID, MainCommand.FACTION}, allowConsole=true)
public class CommandHelp extends CommandExec {

	/** The sender. */
	private final CommandSender sender;

	private final OnCommand onCommand;

	private final MainCommand mainCommand;

	/** The command name. */
	private String commandName = null;


	/**
	 * Instantiates a new command help.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandHelp(final CommandEntities entity) throws FactoidCommandException {

		super(entity);
		sender = entity.sender;
		mainCommand = entity.mainCommand;
		onCommand = entity.onCommand;
	}

	// Call directly the Help without verification CommandName is UPERCASE
	/**
	 * Instantiates a new command help.
	 *
	 * @param onCommand the on command
	 * @param sender the sender
	 * @param mainCommand the main command
	 * @param commandName the command name
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandHelp(final OnCommand onCommand, final CommandSender sender,
			final MainCommand mainCommand, final String commandName) throws FactoidCommandException {

		super(null);
		this.sender = sender;
		this.mainCommand = mainCommand;
		this.commandName = commandName.toUpperCase();
		this.onCommand = onCommand;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
	 */
	@Override
	public void commandExecute() throws FactoidCommandException {

		if (commandName == null) {
			final String arg = entity.argList.getNext();

			if (arg == null) {
				commandName = "GENERAL";
			} else {
				// Will throw an exception if the command name is invalid
				try {
					final InfoCommand infoCommand = onCommand.getInfoCommand(mainCommand, arg);
					if (infoCommand != null) {
						commandName = infoCommand.name().toUpperCase();
					} else {
						// Invalid command, just arg and will run Exception with showHelp()
						commandName = arg;
					}
				} catch (final IllegalArgumentException ex) {
					commandName = "GENERAL";
				}
			}
		}

		showHelp();
	}

	/**
	 * Show help.
	 *
	 * @throws FactoidCommandException the factoid command exception
	 */
	private void showHelp() throws FactoidCommandException {

		final String help = Factoid.getThisPlugin().iLanguage().getHelp(mainCommand.name(), commandName);

		// If there is no help for this command
		if (help == null) {
			throw new FactoidCommandException("Command with no help", sender, "HELP.NOHELP");
		}

		if (commandName.equals("GENERAL")) {
			new ChatPage("HELP.LISTSTART", help, sender, null).getPage(1);
		} else {
			sender.sendMessage(help);
		}
	}

}
