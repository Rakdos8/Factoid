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
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.ActiveAreaSelection;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.LandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandSelect extends CommandExec {

    private final Player player;
    private final Location location;
    private final PlayerConfEntry playerConf;
    private final ArgList argList;

    public CommandSelect(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        location = null;
        playerConf = entity.playerConf;
        argList = entity.argList;
    }

    // Called from player action, not a command
    public CommandSelect(Player player, ArgList argList, Location location) throws FactoidCommandException {

        super(null, false, false);
        this.player = player;
        this.location = location;
        playerConf = Factoid.getPlayerConf().get(player);
        this.argList = argList;
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        // Done nothing but for future use
        checkSelections(null, null);

        String curArg;

        if (playerConf.getSelection().getCuboidArea() == null) {
            Factoid.getLog().write(player.getName() + " join select mode");

            if (!argList.isLast()) {

                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Factoid.getDependPlugin().getWorldEdit() == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(player, playerConf).MakeSelect();

                } else {

                    Land landtest;
                    if (curArg.equalsIgnoreCase("here")) {

                        // add select Here to select the the cuboid
                        if (location != null) {

                            // With an item
                            landtest = Factoid.getLands().getLand(location);
                        } else {

                            // Player location
                            landtest = Factoid.getLands().getLand(player.getLocation());
                        }

                    } else {

                        landtest = Factoid.getLands().getLand(curArg);
                    }

                    if (landtest == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");

                    }
                    PlayerContainer owner = landtest.getOwner();

                    if (!owner.hasAccess(player) && !playerConf.isAdminMod()
                            && !landtest.checkPermissionAndInherit(player, PermissionList.RESIDENT_MANAGER.getPermissonType())) {
                        throw new FactoidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getSelection().getLand() == null) {

                        playerConf.getSelection().addSelection(new LandSelection(player, landtest));

                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
                        playerConf.setAutoCancelSelect(true);
                    } else {

                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                    }
                }
            } else {

                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                ActiveAreaSelection select = new ActiveAreaSelection(player);
                playerConf.getSelection().addSelection(select);
                playerConf.setAutoCancelSelect(true);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {

            //if (playerConf.getSelection().getLand() != null) {
            //    throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
            //}

            //if (playerConf.getSelection().getCuboidArea() != null) {
                doSelectAreaDone();
            //}

        } else if (curArg != null && curArg.equalsIgnoreCase("info")) {

            doSelectAreaInfo();

        } else {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    private void doSelectAreaDone() throws FactoidCommandException {

        checkSelections(null, true);

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        playerConf.getSelection().addSelection(new AreaSelection(player, select.getCuboidArea()));
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
        } else {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
        }
    }

    private void doSelectAreaInfo() throws FactoidCommandException {

        checkSelections(null, true);

        double price;

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        CuboidArea area = select.getCuboidArea();

        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1",
                area.getPrint()));
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2",
                area.getTotalBlock() + ""));

        // Price (economy)
        price = playerConf.getSelection().getLandCreatePrice();
        if (price != 0L) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO3",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
        price = playerConf.getSelection().getAreaAddPrice();
        if (price != 0L) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO4",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
    }
}
