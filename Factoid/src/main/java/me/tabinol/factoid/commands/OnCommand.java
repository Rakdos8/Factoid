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
package me.tabinol.factoid.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.reflections.Reflections;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.executor.CommandHelp;
import me.tabinol.factoid.exceptions.FactoidCommandException;


/**
 * The Class OnCommand.
 */
public class OnCommand extends Thread implements CommandExecutor {

	private final Map<MainCommand, Map<String, Class<?>>> commands = new EnumMap<MainCommand, Map<String, Class<?>>>(MainCommand.class);

	/**
	 * Instantiates a new on command.
	 */
	public OnCommand() {
		// Create Command list
		for(final MainCommand mainCommandList : MainCommand.values()) {
			commands.put(mainCommandList, new TreeMap<String, Class<?>>());
		}

		// Gets all annotations
		final Reflections reflections = new Reflections("me.tabinol.factoid.commands.executor");
		final Set<Class<?>> classCommands = reflections.getTypesAnnotatedWith(InfoCommand.class);

		for(final Class<?> presentClass : classCommands) {
			// Store command information
			final InfoCommand infoCommand = presentClass.getAnnotation(InfoCommand.class);
			for(final MainCommand command : infoCommand.mainCommand()) {
				final Map<String, Class<?>> subCommand = commands.get(command);
				subCommand.put(infoCommand.name().toLowerCase(), presentClass);
				for(final String alias : infoCommand.aliases()) {
					subCommand.put(alias.toLowerCase(), presentClass);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] arg) {
		// Others commands then /factoid, /claim and /fd will not be send.
		final ArgList argList = new ArgList(arg, sender);
			try {
				// Check the command to send
				getCommand(sender, cmd, argList);
				return true;
				// If error on command, send the message to the player
			} catch (final FactoidCommandException ex) {
				return true;
			}
	}

	// Get command from args
	private void getCommand(final CommandSender sender, final Command cmd, final ArgList argList) throws FactoidCommandException {
		try {
			final MainCommand mainCommand = MainCommand.valueOf(cmd.getName().toUpperCase());

			// Show help if there is no arguments
			if (argList.isLast()) {
				new CommandHelp(this, sender, mainCommand, "GENERAL").commandExecute();
				return;
			}

			final String command = argList.getNext().toLowerCase();

			// take the name
			final Class<?> cv = commands.get(mainCommand).get(command.toLowerCase());

			// The command does not exist
			if(cv == null) {
				throw new FactoidCommandException("Command not existing", sender, "COMMAND.NOTEXIST", mainCommand.name());
			}

			// Remove page from memory if needed
			if(cv != commands.get(MainCommand.FACTOID).get("page")) {
				Factoid.getThisPlugin().iPlayerConf().get(sender).setChatPage(null);
			}

			// Do the command
			final InfoCommand ci = cv.getAnnotation(InfoCommand.class);
			final CommandExec ce = (CommandExec) cv.getConstructor(CommandEntities.class)
					.newInstance(new CommandEntities(mainCommand, ci, sender, argList, this));
			if (ce.isExecutable()) {
				ce.commandExecute();
			}

			// a huge number of Exception to catch!
		} catch (final NoSuchMethodException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		} catch (final SecurityException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		} catch (final InstantiationException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		} catch (final IllegalAccessException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		} catch (final IllegalArgumentException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		} catch (final InvocationTargetException ex) {
			Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
			throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
		}
	}

	public InfoCommand getInfoCommand(final MainCommand mainCommand, final String command) {
		final Class<?> infoClass = commands.get(mainCommand).get(command.toLowerCase());

		if(infoClass == null) {
			return null;
		}

		return infoClass.getAnnotation(InfoCommand.class);
	}
}
