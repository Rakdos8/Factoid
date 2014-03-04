package me.tabinol.factoid.commands;

import java.lang.reflect.InvocationTargetException;
import me.tabinol.factoid.commands.executor.CommandEntities;
import me.tabinol.factoid.commands.executor.CommandExec;
import me.tabinol.factoid.commands.executor.CommandHelp;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import static me.tabinol.factoid.commands.CommandList.valueOf;

public class OnCommand extends Thread implements CommandExecutor {

    public OnCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        if (cmd.getName().equalsIgnoreCase("factoid") || cmd.getName().equalsIgnoreCase("claim")) {

            ArgList argList = new ArgList(arg, sender);
            try {
                // Check the command to send
                getCommand(sender, argList);
                return true;
                // If error on command, send the message to the player
            } catch (FactoidCommandException ex) {
                return true;
            }
        }

        return false;
    }

    // Get command from args
    public void getCommand(CommandSender sender, ArgList argList) throws FactoidCommandException {

        try {
            CommandList cl;

            String command = argList.getNext();

            // Show help if there is no arguments
            if (command == null) {
                new CommandHelp(sender, "GENERAL").commandExecute();
            }

            // take the name
            cl = getCommandValue(command, sender);

            // Do de command (get the class name from the CommandName)
            Class commandClass = Class.forName("me.tabinol.factoid.commands.executor.Command"
                    + StringChanges.FirstUpperThenLower(cl.name()));
            CommandExec ce = (CommandExec) commandClass.getConstructor(CommandEntities.class)
                    .newInstance(new CommandEntities(cl, sender, argList));
            if (ce.isExecutable()) {
                ce.commandExecute();
            }

            // a huge number of Exception to catch!
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (SecurityException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InstantiationException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InvocationTargetException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        }
    }

    // Get the command value from command list
    private CommandList getCommandValue(String command, CommandSender sender) throws FactoidCommandException {

        CommandList cl;

        try {
            cl = valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {

            // Check if the second name works
            CommandList.SecondName sn = null;
            try {
                sn = CommandList.SecondName.valueOf(command.toLowerCase());
            } catch (IllegalArgumentException e2) {

                // The command does not exist
                throw new FactoidCommandException("Command not existing", sender, "COMMAND.NOTEXIST");
            }
            cl = sn.mainCommand;
        }

        return cl;

    }
}
