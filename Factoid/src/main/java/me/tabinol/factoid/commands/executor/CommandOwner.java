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
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;

import org.bukkit.ChatColor;


/**
 * The Class CommandOwner.
 */
public class CommandOwner extends CommandThreadExec {

	private PlayerContainer pc;

	/**
     * Instantiates a new command owner.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandOwner(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        
        PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land,
                new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                    PlayerContainerType.OWNER, PlayerContainerType.VISITOR});
        Factoid.getPlayersCache().getUUIDWithNames(this, pc);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws FactoidCommandException {
        
    	pc = convertPcIfNeeded(playerCacheEntry, pc);

        land.setOwner(pc);
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.OWNER.ISDONE", pc.getPrint(), land.getName()));
        Factoid.getLog().write("The land " + land.getName() + "is set to owner: " + pc.getPrint());

        // Cancel the selection
        new CommandCancel(entity.playerConf, true).commandExecute();

    }
}
