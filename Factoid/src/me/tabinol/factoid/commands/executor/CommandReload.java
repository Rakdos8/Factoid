package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.ChatColor;

public class CommandReload extends CommandExec {
    
    public CommandReload(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkPermission(false, false, null, "factoid.reload");

        entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.START"));
        Factoid.getThisPlugin().reload();
        entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.COMPLETE"));
    }
}
