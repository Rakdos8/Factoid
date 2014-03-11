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

import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.exceptions.FactoidCommandException;

public class CommandPage extends CommandExec {

    public CommandPage(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        ChatPage chatPage = entity.playerConf.getChatPage();
        int pageNumber;

        if (chatPage == null) {
            throw new FactoidCommandException("Page", entity.player, "COMMAND.PAGE.INVALID");
        }

        String curArg = entity.argList.getNext();

        try {
            pageNumber = Integer.parseInt(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("Page", entity.player, "COMMAND.PAGE.INVALID");
        }
        chatPage.getPage(pageNumber);
    }
}
