package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import org.bukkit.ChatColor;

public class CommandFlag extends CommandExec {

    public CommandFlag(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
                String curArg = entity.argList.getNext();

        // Temporary desactivated
        if (entity.argList.length() < 2 && false) {

            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            Factoid.getLog().write("PlayerSetFlagUI for " + entity.playerName);
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Factoid.getLands().getCuboidArea(entity.player.getLocation());
            LandSetFlag setting = new LandSetFlag(entity.player, area);
            entity.playerConf.setSetFlagUI(setting);
        
        } else if (curArg.equalsIgnoreCase("set")) {

            // Permission check is on getFlagFromArg
            
            LandFlag landFlag = entity.argList.getFlagFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.playerName));
            land.addFlag(landFlag);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), 
                    landFlag.getValuePrint() + ChatColor.YELLOW));
            Factoid.getLog().write("Flag set: " + landFlag.getFlagType().toString() + ", value: " + landFlag.getValueString());

        } else if (curArg.equalsIgnoreCase("unset")) {
        
            FlagType flagType = entity.argList.getFlagTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.playerName));
            if (!land.removeFlag(flagType)) {
                throw new FactoidCommandException("Flags", entity.player, "COMMAND.FLAGS.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            Factoid.getLog().write("Flag unset: " + flagType.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getFlags().isEmpty()) {
                for (LandFlag flag : land.getFlags()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.YELLOW).append(flag.getFlagType().toString()).append(":").append(flag.getValuePrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROWNULL"));
            }
            new ChatPage("COMMAND.FLAGS.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
