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

import java.util.Collection;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.ChatColor;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandList.
 *
 * @author Tabinol
 */
public class CommandList extends CommandExec {

    /**
     * Instantiates a new command list.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandList(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);

    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();
        String worldName = null;
        PlayerContainer pc = null;

        if (curArg != null) {
            if (curArg.equalsIgnoreCase("world")) {

                // Get worldName
                worldName = entity.argList.getNext().toLowerCase();
                if (worldName == null) {
                    // No worldName has parameter
                    worldName = entity.player.getLocation().getWorld().getName().toLowerCase();
                }

            } else {

                // Get the player Container
                entity.argList.setPos(0);
                pc = entity.argList.getPlayerContainerFromArg(null, null);

            }
        }

        // Check if the player is AdminMod or send only owned lands
        Collection<Land> lands;

        if (entity.playerConf.isAdminMod()) {
            lands = Factoid.getLands().getLands();
        } else {
            lands = Factoid.getLands().getLands(entity.playerConf.getPlayerContainer());
        }

        // Get the list of the land
        StringBuilder stList = new StringBuilder();
        stList.append(ChatColor.YELLOW);

        for (Land land : lands) {
            if ((worldName == null || worldName.equals(land.getWorldName()))
                    && (pc == null || land.getOwner().equals(pc))) {
                stList.append(land.getName()).append(" ");
            }
        }

        new ChatPage("COMMAND.LAND.LISTSTART", stList.toString(), entity.player, null).getPage(1);
    }
}
