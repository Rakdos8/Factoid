package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.command.CommandSender;

public class CommandHelp extends CommandExec {

    private final CommandSender sender;
    private String commandName;

    public CommandHelp(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, false);
        sender = entity.sender;
    }

    // Call directly the Help without verification CommandName is UPERCASE
    public CommandHelp(CommandSender sender, String commandName) throws FactoidCommandException {

        super(null, true, false);
        this.sender = sender;
        this.commandName = commandName;
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        if (commandName == null) {
            String arg = entity.argList.getNext();

            if (arg == null) {
                commandName = "GENERAL";
            } else {
                // Will throw an exception if the command name is invalid
                try {
                    CommandList cl = CommandList.valueOf(arg.toUpperCase());
                    commandName = cl.name();
                } catch (IllegalArgumentException ex) {
                    commandName = "GENERAL";
                }
            }
        }
        
        showHelp();
    }

    private void showHelp() throws FactoidCommandException {

        if (commandName.equals("GENERAL")) {
            new ChatPage("HELP.LISTSTART", Factoid.getLanguage().getHelp(commandName), sender, null).getPage(1);
        } else {
            sender.sendMessage(Factoid.getLanguage().getHelp(commandName));
        }
    }

}
