package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.ChatColor;

public class CommandResident extends CommandExec {

    public CommandResident(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
        checkPermission(true, true, PermissionType.RESIDENT_MANAGER, null);
        
        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {
            
            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                        PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                        PlayerContainerType.RESIDENT});

            land.addResident(pc);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getPrint(), land.getName()));
            Factoid.getLog().write("Resident added: " + pc.toString());
        
        } else if (curArg.equalsIgnoreCase("remove")) {
            
            PlayerContainer pc = entity.argList.getPlayerContainerFromArg(land, null);
            if (!land.removeResident(pc)) {
                throw new FactoidCommandException("Resident", entity.player, "COMMAND.RESIDENT.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getPrint(), land.getName()));
            Factoid.getLog().write("Resident removed: " + pc.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {
            
            StringBuilder stList = new StringBuilder();
            if (!land.getResidents().isEmpty()) {
                for (PlayerContainer pc : land.getResidents()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
            }
            new ChatPage("COMMAND.RESIDENT.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        
        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}