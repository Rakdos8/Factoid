package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.ChatColor;

public class CommandAdminmod extends CommandExec {

    public CommandAdminmod(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkPermission(false, false, null, "factoid.adminmod");
        
        if (entity.playerConf.isAdminMod()) {
            entity.playerConf.setAdminMod(false);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.QUIT"));
        } else {
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.JOIN"));
            entity.playerConf.setAdminMod(true);
        }
    }
}
