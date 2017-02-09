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
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandThreadExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;


/**
 * The Class CommandResident.
 */
@InfoCommand(name="resident", forceParameter=true)
public class CommandResident extends CommandThreadExec {

	private String fonction;

	/**
	 * Instantiates a new command resident.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandResident(final CommandEntities entity) throws FactoidCommandException {
		super(entity);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
	 */
	@Override
	public void commandExecute() throws FactoidCommandException {
		checkSelections(true, null);

		// The player must be adminmod, resident, owner or tenant
		if (!entity.playerConf.isAdminMod() &&
			!land.isResident(entity.player) &&
			!land.isTenant(entity.player) &&
			!land.isOwner(entity.player)
		) {
			throw new FactoidCommandException("No permission to do this action", entity.player, "GENERAL.MISSINGPERMISSION");
		}

		fonction = entity.argList.getNext();
		if (fonction.equalsIgnoreCase("add")) {
			pc = entity.argList.getPlayerContainerFromArg(
				land,
				new EPlayerContainerType[]{
					EPlayerContainerType.EVERYBODY,
					EPlayerContainerType.OWNER,
					EPlayerContainerType.VISITOR,
					EPlayerContainerType.RESIDENT
				}
			);
			Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);
		} else if (fonction.equalsIgnoreCase("remove")) {
			pc = entity.argList.getPlayerContainerFromArg(land, null);
			Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);
		} else if (fonction.equalsIgnoreCase("list")) {
			final StringBuilder stList = new StringBuilder();
			if (!land.getResidents().isEmpty()) {
				for (final IPlayerContainer pc : land.getResidents()) {
					if (stList.length() != 0) {
						stList.append(" ");
					}
					stList.append(ChatColor.WHITE).append(pc.getPrint());
				}
				stList.append(Config.NEWLINE);
			} else {
				entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
			}
			new ChatPage("COMMAND.RESIDENT.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

		} else {
			throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
		}
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
	 */
	@Override
	public void commandThreadExecute(final PlayerCacheEntry[] playerCacheEntry)
			throws FactoidCommandException {
		convertPcIfNeeded(playerCacheEntry);

		if (fonction.equalsIgnoreCase("add")) {
			land.addResident(pc);
			entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getPrint(), land.getName()));
			Factoid.getThisPlugin().iLog().write("Resident added: " + pc.toString());

		} else if (fonction.equalsIgnoreCase("remove")) {
			if (!land.removeResident(pc)) {
				throw new FactoidCommandException("Resident", entity.player, "COMMAND.RESIDENT.REMOVENOTEXIST");
			}
			entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getPrint(), land.getName()));
			Factoid.getThisPlugin().iLog().write("Resident removed: " + pc.toString());
		}
	}
}