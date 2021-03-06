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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.parameters.IFlagType;
import me.tabinol.factoidapi.parameters.ILandFlag;


/**
 * The Class CommandFlag.
 */
@InfoCommand(name="flag", forceParameter=true)
public class CommandFlag extends CommandExec {

	private List<IDummyLand> precDL;
	private StringBuilder stList;

	/**
	 * Instantiates a new command flag.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	public CommandFlag(final CommandEntities entity) throws FactoidCommandException {

		super(entity);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
	 */
	@Override
	public void commandExecute() throws FactoidCommandException {

		checkSelections(true, null);
				final String curArg = entity.argList.getNext();

		/*
		if (entity.argList.length() < 2) {

			entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
			Factoid.getThisPlugin().iLog().write("PlayerSetFlagUI for " + entity.playerName);
			entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.HINT"));
			CuboidArea area = Factoid.getThisPlugin().iLands().getCuboidArea(entity.player.getLocation());
			LandSetFlag setting = new LandSetFlag(entity.player, area);
			entity.playerConf.setSetFlagUI(setting);


		} else
		*/

		if (curArg.equalsIgnoreCase("set")) {

			// Permission check is on getFlagFromArg

			final ILandFlag landFlag = entity.argList.getFlagFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));

			if (!landFlag.getFlagType().isRegistered()) {
				throw new FactoidCommandException("Flag not registered", entity.player, "COMMAND.FLAGS.FLAGNULL");
			}

			((Land)land).addFlag(landFlag);
			entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " +
			Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(),
					landFlag.getValue().getValuePrint() + ChatColor.YELLOW));
			Factoid.getThisPlugin().iLog().write("Flag set: " + landFlag.getFlagType().toString() + ", value: " +
					landFlag.getValue().getValue().toString());

		} else if (curArg.equalsIgnoreCase("unset")) {

			final IFlagType flagType = entity.argList.getFlagTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
			if (!land.removeFlag(flagType)) {
				throw new FactoidCommandException("Flags", entity.player, "COMMAND.FLAGS.REMOVENOTEXIST");
			}
			entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
			Factoid.getThisPlugin().iLog().write("Flag unset: " + flagType.toString());

		} else if (curArg.equalsIgnoreCase("list")) {

			precDL = new ArrayList<>();
			stList = new StringBuilder();

			// For the actual land
			importDisplayFlagsFrom(land, false);

			// For default Type
			if (land.getType() != null) {
				stList.append(ChatColor.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
						land.getType().getName())).append(Config.NEWLINE);
				importDisplayFlagsFrom(((Lands) FactoidAPI.iLands()).getDefaultConf(land.getType()), false);
			}

			// For parent (if exist)
			ILand parLand = land;
			while ((parLand = parLand.getParent()) != null) {
				stList.append(ChatColor.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMPARENT",
						ChatColor.GREEN + parLand.getName() + ChatColor.DARK_GRAY)).append(Config.NEWLINE);
				importDisplayFlagsFrom(parLand, true);
			}

			// For world
			stList.append(ChatColor.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMWORLD",
					land.getWorldName())).append(Config.NEWLINE);
			importDisplayFlagsFrom(((Lands) FactoidAPI.iLands()).getOutsideArea(land.getWorldName()), true);

			new ChatPage("COMMAND.FLAGS.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

		} else {
			throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
		}
	}

	private void importDisplayFlagsFrom(final IDummyLand land, final boolean onlyInherit) {

		final StringBuilder stSubList = new StringBuilder();
		for (final ILandFlag flag : land.getFlags()) {
			if (stSubList.length() != 0 && !stSubList.toString().endsWith(" ")) {
				stSubList.append(" ");
			}
			if ((!onlyInherit || flag.isHeritable()) && !flagInList(flag)) {
				stSubList.append(flag.getFlagType().getPrint()).append(":").append(flag.getValue().getValuePrint());
			}
		}

		if (stSubList.length() > 0) {
			stList.append(stSubList).append(Config.NEWLINE);
			precDL.add(land);
		}
	}

	private boolean flagInList(final ILandFlag flag) {

		for (final IDummyLand listLand : precDL) {
			for (final ILandFlag listFlag : listLand.getFlags()) {
				if (flag.getFlagType() == listFlag.getFlagType()) {
					return true;
				}
			}
		}

		return false;
	}
}
