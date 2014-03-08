package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;

public class CommandNotify extends CommandExec {

    public CommandNotify(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        getLandFromCommandIfNoLandSelected();
        checkSelections(false, false, true, null, null);
        checkPermission(true, true, PermissionType.LAND_NOTIFY, null);

        
        if (land.isPlayerNotify(entity.playerName)) {
            land.removePlayerNotify(entity.playerName);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.NOTIFY.QUIT", land.getName()));
        } else {
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.NOTIFY.JOIN", land.getName()));
            land.addPlayerNotify(entity.playerName);
        }
    }
}
