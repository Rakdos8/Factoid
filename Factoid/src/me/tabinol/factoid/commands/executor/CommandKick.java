package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandKick extends CommandExec {

    private final ArgList argList;
    private final Player player;

    public CommandKick(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
        argList = entity.argList;
        player = entity.player;

    }
    
    // From other command
    public CommandKick(Player player, ArgList argList, Land land) throws FactoidCommandException {
        
        super(null, false, false);
        this.argList = argList;
        this.player = player;
        this.land = land;
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        String playerKickName = argList.getNext();

        getLandFromCommandIfNoLandSelected();

        // Only if it is from Kick command
        if (entity != null) {
            checkSelections(false, false, true, null);
            checkPermission(true, true, PermissionType.LAND_KICK, null);
        }

        // No player name?
        if (playerKickName == null) {
            throw new FactoidCommandException("Kicked", player, "COMMAND.KICK.PLAYERNULL");
        }

        Player playerKick = Factoid.getThisPlugin().getServer().getPlayer(playerKickName);

        // Player not in land?
        if (playerKick == null || land.isPlayerinLandNoVanish(playerKick, player)) {
            throw new FactoidCommandException("Kicked", player, "COMMAND.KICK.NOTINLAND");
        }
        
        //Kick the player
        playerKick.teleport(player.getLocation().getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.KICK.DONE", playerKickName, land.getName()));
        playerKick.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.KICK.KICKED", land.getName()));
        Factoid.getLog().write("Player " + playerKick + " kicked from " + land.getName() + ".");
    }
}
