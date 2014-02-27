package me.tabinol.factoid.commands;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.create.CommandCreate;
import me.tabinol.factoid.commands.select.CommandSelect;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum CommandList {

    RELOAD(true, false),
    SELECT(false, false),
    EXPAND(false, false),
    CREATE(false, true),
    AREA(false, true),
    OWNER(false, true),
    FLAG(false, true),
    PERMISSION(false, true),
    RESIDENT(false, true),
    BAN(false, true),
    REMOVE(false, false),
    CONFIRM(false, false),
    CANCEL(false, false),
    HERE(false, false),
    ADMINMOD(false, false),
    PAGE(false, true),
    DEFAULT(false, false),
    PRIORITY(false, true),
    APPROVE(false, true),
    RENAME(false, true),
    HELP(true, false);

    private final boolean canDoFromConsole; // Can be typed from console?
    private final boolean parameterOnly; // Not working whitout parameter? (will show help)

    private CommandList(boolean canDoFromConsole, boolean parameterOnly) {

        this.canDoFromConsole = canDoFromConsole;
        this.parameterOnly = parameterOnly;
    }

    private enum SecondName {

        // For command second name
        CURRENT(HERE);

        final CommandList name;

        private SecondName(CommandList name) {

            this.name = name;
        }
    }

    public boolean canDoFromConsole() {

        return canDoFromConsole;
    }

    public static void getCommand(CommandSender sender, ArgList argList) throws FactoidCommandException {

        CommandList cl;

        String command = argList.getNext();

        // Show help if there is no arguments
        if (command == null) {
            showHelp(sender, "GENERAL");
        }

        // take the name
        cl = getCommandValue(command, sender);

        //Check if a parameter is needed and show help if the needed parameter is not here
        if (cl.parameterOnly && argList.isLast()) {
            showHelp(sender, cl.name());
            return;
        }

        // now execute de command
        doCommand(sender, cl, argList);

    }

    private static CommandList getCommandValue(String command, CommandSender sender) throws FactoidCommandException {

        CommandList cl;

        try {
            cl = valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {

            // Check if the second name works
            SecondName sn = null;
            try {
                sn = SecondName.valueOf(command.toLowerCase());
            } catch (IllegalArgumentException e2) {

                // The command does not exist
                throw new FactoidCommandException("Command not existing", sender, "COMMAND.NOTEXIST");
            }
            cl = sn.name;
        }

        return cl;

    }

    private static void doCommand(CommandSender sender, CommandList cl, ArgList argList) throws FactoidCommandException {

        // Commands that can be done from the console
        switch (cl) {
            case RELOAD:
                OnCommand.doCommandReload(sender);
                break;
            default:
                // If it is console, do not go forward
                if (!(sender instanceof Player)) {
                    throw new FactoidCommandException("Impossible to do from console", sender, "CONSOLE");
                }

                Player player = (Player) sender;

                switch (cl) {
                    case HELP:
                        showHelp(player, argList);
                        break;
                    case SELECT:
                        new CommandSelect(player, argList, null);
                        break;
                    case EXPAND:
                        OnCommand.doCommandExpand(player, argList);
                        break;
                    case CREATE:
                        new CommandCreate(CommandCreate.CreateType.LAND, player, argList, 0);
                        break;
                    case AREA:
                        OnCommand.doCommandArea(player, argList);
                        break;
                    case OWNER:
                        OnCommand.doCommandOwner(player, argList);
                        break;
                    case FLAG:
                        OnCommand.doCommandFlag(player, argList);
                        break;
                    case PERMISSION:
                        OnCommand.doCommandPermission(player, argList);
                        break;
                    case RESIDENT:
                        OnCommand.doCommandResident(player, argList);
                        break;
                    case BAN:
                        OnCommand.doCommandBanned(player, argList);
                        break;
                    case REMOVE:
                        OnCommand.doCommandRemove(player);
                        break;
                    case CONFIRM:
                        OnCommand.doCommandConfirm(player);
                        break;
                    case CANCEL:
                        OnCommand.doCommandCancel(player);
                        break;
                    case HERE:
                        OnCommand.doCommandHere(player);
                        break;
                    case ADMINMOD:
                        OnCommand.doCommandAdminmod(player);
                        break;
                    case PAGE:
                        OnCommand.doCommandPage(player, argList);
                        break;
                    case DEFAULT:
                        OnCommand.doCommandDefault(player);
                        break;
                    case PRIORITY:
                        OnCommand.doCommandPrority(player, argList);
                        break;
                    case APPROVE:
                        OnCommand.doCommandApprove(player, argList);
                        break;
                    case RENAME:
                        OnCommand.doCommandRename(player, argList);
                        break;
                    default:
                }
        }

    }

    private static void showHelp(CommandSender sender, ArgList argList) throws FactoidCommandException {

        String arg = argList.getNext();

        if (arg == null) {
            showHelp(sender, "GENERAL");
        } else {
            // Will throw an exception if the command name is invalid
            CommandList cl = getCommandValue(arg, sender);
            showHelp(sender, cl.name());
        }

    }

    private static void showHelp(CommandSender sender, String commandName) throws FactoidCommandException {

        if (commandName.equals("GENERAL")) {
            OnCommand.createPage("HELP.LISTSTART", Factoid.getLanguage().getHelp(commandName), sender, null);
        } else {
            sender.sendMessage(Factoid.getLanguage().getHelp(commandName));
        }
    }
}
