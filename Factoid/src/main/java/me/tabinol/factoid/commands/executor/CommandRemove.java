package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.collisions.Collisions;
import org.bukkit.ChatColor;

public class CommandRemove extends CommandExec {

    public CommandRemove(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
        checkPermission(true, true, null, null);

        // Check for collision
        if (checkCollision(land.getName(), land, Collisions.LandAction.LAND_REMOVE, 0, null, land.getParent(), true)) {
            return;
        }

        entity.playerConf.setConfirm(new ConfirmEntry(ConfirmEntry.ConfirmType.REMOVE_LAND, land, 0));
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CONFIRM"));
    }
}
