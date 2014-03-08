package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.selection.LandSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCancel extends CommandExec {

    private final Player player;
    private final PlayerConfEntry playerConf;
    private final boolean fromAutoCancel; // true: launched from autoCancel

    public CommandCancel(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        playerConf = entity.playerConf;
        fromAutoCancel = false;
    }

    // Called from PlayerListener
    public CommandCancel(PlayerConfEntry entry, boolean fromAutoCancel) throws FactoidCommandException {

        super(null, false, false);
        this.player = entry.getPlayer();
        playerConf = entry;
        this.fromAutoCancel = fromAutoCancel;
    }
    
    @Override
    public void commandExecute() throws FactoidCommandException {

        LandSelection select;

        if (playerConf.getConfirm() != null) {
            playerConf.setConfirm(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Factoid.getLog().write(player.getName() + " cancel for action");
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        if ((select = playerConf.getAreaSelection()) != null) {

            select.resetSelection();
            playerConf.setAreaSelection(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getLog().write(player.getName() + ": Select cancel");

            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (playerConf.getSetFlagUI() != null) {

            playerConf.setSetFlagUI(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));

            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (playerConf.getLandSelected() != null) {

            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
            playerConf.setLandSelected(null);
            playerConf.setLandSelectedUI(null);

            // Cancel selection (it is the last think selected
            playerConf.setAutoCancelSelect(false);
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        // No cancel done
        if(!fromAutoCancel) {
            throw new FactoidCommandException("Nothing to confirm", player, "COMMAND.CANCEL.NOCANCEL");
        }
    }
}
