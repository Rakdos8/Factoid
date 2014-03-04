package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.PlayerStaticConfig;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.selection.LandSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCancel extends CommandExec {

    private final Player player;
    private final PlayerStaticConfig.PlayerConfEntry playerConf;

    public CommandCancel(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        playerConf = entity.playerConf;
    }

    // Called from PlayerListener
    public CommandCancel(Player player) throws FactoidCommandException {

        super(null, false, false);
        this.player = player;
        playerConf = Factoid.getPlayerConf().get(player);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        LandSelection select;

        if (playerConf.getConfirm() != null) {
            playerConf.setConfirm(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Factoid.getLog().write(player.getName() + " cancel for action");

        } else if ((select = playerConf.getAreaSelection()) != null) {

            select.resetSelection();
            playerConf.setAreaSelection(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getLog().write(player.getName() + ": Select cancel");

        } else if (playerConf.getSetFlagUI() != null) {

            playerConf.setSetFlagUI(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));

        } else if (playerConf.getLandSelected() != null) {

            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
            playerConf.setLandSelected(null);
            playerConf.setLandSelectedUI(null);
        }
    }
}
