package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandBan extends CommandExec {

    public CommandBan(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
        checkPermission(true, true, PermissionType.LAND_BAN, null);

        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {

            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                        PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                        PlayerContainerType.RESIDENT});
            if (land.isLocationInside(land.getWord().getSpawnLocation())) {
                throw new FactoidCommandException("Banned", entity.player, "COMMAND.BANNED.NOTINSPAWN");
            }
            land.addBanned(pc);

            // Check for kick the player if he is online and in the land
            for (Player pl : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
                if (land.isPlayerinLandNoVanish(pl, entity.player) && pc.hasAccess(pl.getName())) {
                    new CommandKick(entity.player, new ArgList(new String[]{pl.getName()}, entity.player), land).commandExecute();
                }
            }

            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ISDONE", pc.getContainerType().toString(), pc.getName()));
            Factoid.getLog().write("Ban added: " + pc.toString());

        } else if (curArg.equalsIgnoreCase("remove")) {

            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land, null);
            if (!land.removeBanned(pc)) {
                throw new FactoidCommandException("Banned", entity.player, "COMMAND.BANNED.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
            Factoid.getLog().write("Ban removed: " + pc.toString());

        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getBanneds().isEmpty()) {
                for (PlayerContainer pc : land.getBanneds()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
            }
            new ChatPage("COMMAND.BANNED.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        
        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
