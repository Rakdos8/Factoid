package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import org.bukkit.ChatColor;

public class CommandExpand extends CommandExec {
    
    public CommandExpand(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        /**
         * *** TO DO : COLLISIONS! ******
         */
        checkSelections(null, false, true, null, null);
        checkPermission(true, true, null, null);

        // Land land = entity.playerConf.getLandSelected();
            
        String curArg = entity.argList.getNext();

        if (entity.playerConf.getExpendingLand() == null) {
            entity.player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            Factoid.getLog().write(entity.player.getName() + " have join ExpandMode.");
            LandExpansion expand = new LandExpansion(entity.player);
            entity.playerConf.setExpandingLand(expand);
        } else if (curArg != null && curArg.equalsIgnoreCase("done")) {
            entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            Factoid.getLog().write(entity.playerName + " have quit ExpandMode.");
            LandExpansion expand = entity.playerConf.getExpendingLand();
            expand.setDone();
            entity.playerConf.setExpandingLand(null);
        } else {
            throw new FactoidCommandException("Player Expand", entity.player, "COMMAND.EXPAND.ALREADY");
        }
    }
}
