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
