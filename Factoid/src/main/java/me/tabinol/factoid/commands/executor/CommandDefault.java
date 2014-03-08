package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.ChatColor;

public class CommandDefault extends CommandExec {

    public CommandDefault(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
        checkPermission(true, true, null, null);
        
        entity.playerConf.setConfirm(new ConfirmEntry(ConfirmEntry.ConfirmType.LAND_DEFAULT, land, 0));
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CONFIRM"));
    }
}
