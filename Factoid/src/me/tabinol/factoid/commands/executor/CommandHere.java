package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import static me.tabinol.factoid.config.Config.NEWLINE;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandHere extends CommandExec {

    private CuboidArea area = null;
    private final Player player;
    private final String playerName;

    public CommandHere(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        playerName = entity.playerName;
        Location playerloc = entity.player.getLocation();
        area = Factoid.getLands().getCuboidArea(playerloc);
    }

    // called from the bone
    public CommandHere(Player player, CuboidArea area) throws FactoidCommandException {

        super(null, false, false);
        this.player = player;
        this.area = area;
        playerName = player.getName();
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        if (area != null) {

            Land land = area.getLand();
            StringBuilder stList = new StringBuilder();

            // Create list
            stList.append(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", ChatColor.GREEN + land.getName() + ChatColor.YELLOW) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.PRIORITY", land.getPriority() + ""));
            if (land.getParent() != null) {
                stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.PARENT", land.getParent().getName()) + NEWLINE);
            }
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", land.getOwner().getPrint()) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.MAINPERMISSION",
                    getPermissionInColForPl(land, PermissionType.BUILD) + " "
                    + getPermissionInColForPl(land, PermissionType.USE) + " "
                    + getPermissionInColForPl(land, PermissionType.OPEN)) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.ACTIVEAREA",
                    "ID: " + area.getKey() + ", " + area.getPrint()) + NEWLINE);

            // Create the multiple page
            new ChatPage("COMMAND.CURRENT.LAND.LISTSTART", stList.toString(), player, land.getName()).getPage(1);

        } else {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
        }
    }

    private String getPermissionInColForPl(Land land, PermissionType pt) {

        boolean result = land.checkPermissionAndInherit(playerName, pt);

        if (result) {
            return ChatColor.GREEN + pt.toString();
        } else {
            return ChatColor.RED + pt.toString();
        }
    }
}
