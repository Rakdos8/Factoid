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
import static me.tabinol.factoid.config.Config.NEWLINE;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandInfo extends CommandExec {

    private CuboidArea area;
    private final Player player;
    private final String playerName;
    private final ArgList argList;

    public CommandInfo(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        playerName = entity.playerName;
        Location playerloc = entity.player.getLocation();
        area = Factoid.getLands().getCuboidArea(playerloc);
        argList = entity.argList;
    }

    // called from the bone
    public CommandInfo(Player player, CuboidArea area) throws FactoidCommandException {

        super(null, false, false);
        this.player = player;
        this.area = area;
        playerName = player.getName();
        argList = null;
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        land = null;

        // Get the land name from arg
        if (argList != null && !argList.isLast()) {
            land = Factoid.getLands().getLand(argList.getNext());

            if (land == null) {
                throw new FactoidCommandException("CommandInfo", player, "COMMAND.INFO.NOTEXIST");
            }

            // If the land is in parameter, cancel Area
            area = null;
        }

        // get the Land from area
        if (land == null && area != null) {
            land = area.getLand();
        }

        // Show the land
        if (land != null) {
            // Create list
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.NAME",
                    ChatColor.GREEN + land.getName() + ChatColor.YELLOW, ChatColor.GREEN + land.getUUID().toString() + ChatColor.YELLOW));
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.PRIORITY", land.getPriority() + ""));
            stList.append(NEWLINE);
            if (land.getParent() != null) {
                stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.PARENT", land.getParent().getName()));
                stList.append(NEWLINE);
            }
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.OWNER", land.getOwner().getPrint()));
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.MAINPERMISSION",
                    getPermissionInColForPl(land, Factoid.getParameters().getPermissionType("BUILD")) + " "
                    + getPermissionInColForPl(land, Factoid.getParameters().getPermissionType("USE")) + " "
                    + getPermissionInColForPl(land, Factoid.getParameters().getPermissionType("OPEN"))));
            stList.append(NEWLINE);
            if (area != null) {
                stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.INFO.LAND.ACTIVEAREA",
                        "ID: " + area.getKey() + ", " + area.getPrint()));
                stList.append(NEWLINE);
            }
            // Create the multiple page
            new ChatPage("COMMAND.INFO.LAND.LISTSTART", stList.toString(), player, land.getName()).getPage(1);

        } else {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.INFO.NOLAND"));
        }
    }

    private String getPermissionInColForPl(Land land, PermissionType pt) {

        boolean result = land.checkPermissionAndInherit(player, pt);

        if (result) {
            return ChatColor.GREEN + pt.getName();
        } else {
            return ChatColor.RED + pt.getName();
        }
    }
}
