package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.ChatColor;

public class CommandPriority extends CommandExec {

    public CommandPriority(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null);
        checkPermission(true, false, null, null);
        String curArg = entity.argList.getNext();
        short newPrio;

        if (land.getParent() != null) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.NOTCHILD");
        }
        try {
            newPrio = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        if (!Calculate.isInInterval(newPrio, Land.MINIM_PRIORITY, Land.MAXIM_PRIORITY)) {
            throw new FactoidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        land.setPriority(newPrio);

        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage(
                "COMMAND.PRIORITY.DONE", land.getName(), land.getPriority() + ""));
        Factoid.getLog().write("Priority for land " + land.getName() + " changed for " + land.getPriority());
    }
}
