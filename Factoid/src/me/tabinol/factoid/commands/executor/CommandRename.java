package me.tabinol.factoid.commands.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.collisions.Collisions;
import org.bukkit.ChatColor;

public class CommandRename extends CommandExec {

    public CommandRename(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null);
        checkPermission(true, true, null, null);
        
        String curArg = entity.argList.getNext();
        if (BannedWords.isBannedWord(curArg)) {
            throw new FactoidCommandException("CommandRename", entity.player, "COMMAND.RENAME.HINTUSE");
        }

        // Check for collision
        if (checkCollision(curArg, land, Collisions.LandAction.LAND_RENAME, 0, null, land.getParent(), true)) {
            return;
        }

        String oldName = land.getName();

        try {
            Factoid.getLands().renameLand(oldName, curArg);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandRename.class.getName()).log(Level.SEVERE, "On land rename", ex);
            throw new FactoidCommandException("On land rename", entity.player, "GENERAL.ERROR");
        }
        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RENAME.ISDONE", oldName, curArg));
        Factoid.getLog().write(entity.playerName + " has renamed " + oldName + " to " + curArg);
    }
}
