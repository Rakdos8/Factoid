/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class CommandPermission.
 */
public class CommandPermission extends CommandThreadExec {

	private String fonction;
	private PlayerContainer pc;

	/**
     * Instantiates a new command permission.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandPermission(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);

        fonction = entity.argList.getNext();
        
        if (fonction.equalsIgnoreCase("set")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Factoid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("unset")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Factoid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getSetPCHavePermission().isEmpty()) {
                for (PlayerContainer pc : land.getSetPCHavePermission()) {
                    stList.append(ChatColor.WHITE).append(pc.getPrint()).append(":");
                    for (Permission perm : land.getPermissionsForPC(pc)) {
                        stList.append(" ").append(ChatColor.YELLOW).append(perm.getPermType().getPrint()).append(":").append(perm.getValuePrint());
                    }
                    stList.append(Config.NEWLINE);
                }
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROWNULL"));
            }
            new ChatPage("COMMAND.PERMISSION.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws FactoidCommandException {
        
    	pc = convertPcIfNeeded(playerCacheEntry, pc);

        if (fonction.equalsIgnoreCase("set")) {

            Permission perm = entity.argList.getPermissionFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()
                    && land.isLocationInside(land.getWorld().getSpawnLocation())) {
                throw new FactoidCommandException("Permission", entity.player, "COMMAND.PERMISSION.NOENTERNOTINSPAWN");
            }
            land.addPermission(pc, perm);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getValuePrint(),
                    pc.getPrint() + ChatColor.YELLOW, land.getName()));
            Factoid.getLog().write("Permission set: " + perm.getPermType().toString() + ", value: " + perm.getValue());

            // NO_ENTER CASE (kick players)
            // Check for kick the player if he is online and in the land
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType() && perm.getValue() == false) {
                for (Player pl : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
                    if (land.isPlayerinLandNoVanish(pl, entity.player) && pc.hasAccess(pl)) {
                        new CommandKick(entity.player, new ArgList(new String[]{pl.getName()}, entity.player), land).commandExecute();
                    }
                }
            }

        } else if (fonction.equalsIgnoreCase("unset")) {

            PermissionType pt = entity.argList.getPermissionTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (!land.removePermission(pc, pt)) {
                throw new FactoidCommandException("Permission", entity.player, "COMMAND.PERMISSION.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
            Factoid.getLog().write("Permission unset: " + pt.toString());
        }
    }
}
