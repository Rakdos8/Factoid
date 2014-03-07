package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandWho extends CommandExec {

    public CommandWho(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        getLandFromCommandIfNoLandSelected();
        checkSelections(false, false, true, null);
        checkPermission(true, true, PermissionType.LAND_WHO, null);

        // Create list
        StringBuilder stList = new StringBuilder();
        for (Player player : land.getPlayersInLandNoVanish(entity.player)) {
            stList.append(player.getDisplayName()).append(Config.NEWLINE);
        }

        if (stList.length() != 0) {
            new ChatPage("COMMAND.WHO.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        } else {
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.WHO.LISTNULL", land.getName()));
        }
    }
}
