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

import java.util.ArrayList;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.land.LandMakeSquare;
import me.tabinol.factoid.lands.selection.area.AreaMakeSquare;
import me.tabinol.factoid.lands.selection.land.LandSelection;
import me.tabinol.factoid.lands.selection.area.AreaSelection;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;

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

        checkSelections(false, false, null, null, null);

        String curArg;
        
        // Note : Il faudrait implémenter la vérification de tes nouvelles variables dans checkSelections
        if (playerConf.getAreaSelection() == null && 
                playerConf.getLandSelection() == null) {
            Factoid.getLog().write(player.getName() + " join select mode");

            if (!argList.isLast()) {

                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Factoid.getDependPlugin().getWorldEdit() == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(player, playerConf).MakeSelect();

                    // Desactivated
                } else if (curArg.equalsIgnoreCase("area") && false) {
                    Land land = null;
                    CuboidArea area = null;
                    if (argList.length() == 2) {

                        String landName = argList.getNext().split(".")[0];
                        String areaNameString = argList.getNext().split(".")[1];
                        int areaName = 0;
                        if (StringChanges.isInt(areaNameString)) {
                            areaName = StringChanges.toInteger(areaNameString);
                        }
                        land = Factoid.getLands().getLand(landName);
                        area = land.getArea(areaName);
                    } else if (entity.playerConf.getLandSelected() != null) {

                        land = entity.playerConf.getLandSelected();
                        int areaName = 0;
                        if (StringChanges.isInt(argList.getNext())) {
                            areaName = StringChanges.toInteger(argList.getNext());
                        }
                        area = land.getArea(areaName);
                    }

                    if (land == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");
                    }

                    PlayerContainer owner = land.getOwner();

                    if (!owner.hasAccess(player.getName()) && !playerConf.isAdminMod()
                            && !land.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
                        throw new FactoidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getAreaSelected() == null) {

                        playerConf.setAreaSelected(area);
                        List<AreaMakeSquare> listdummy = new ArrayList<AreaMakeSquare>();
                        AreaMakeSquare Areamake = new AreaMakeSquare(player, null,
                                area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2(), true);
                        Areamake.makeSquare();
                        listdummy.add(Areamake);
                        playerConf.setAreaSelectedUI(listdummy);

                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTIONEDAREA", area.getKey() + ""));
                        playerConf.setAutoCancelSelect(true);
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", land.getName()));
                    }
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

                        landtest = Factoid.getLands().getLand(curArg.toString());
                    }

                    if (landtest == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");

                    }
                    PlayerContainer owner = landtest.getOwner();

                    if (!owner.hasAccess(player.getName()) && !playerConf.isAdminMod()
                            && !landtest.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
                        throw new FactoidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getLandSelected() == null) {

                        playerConf.setLandSelected(landtest);
                        List<LandMakeSquare> listdummy = new ArrayList<LandMakeSquare>();
                        for (CuboidArea area : landtest.getAreas()) {
                            LandMakeSquare landmake = new LandMakeSquare(player, null,
                                    area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2(), true);
                            landmake.makeSquare();
                            listdummy.add(landmake);
                        }
                        playerConf.setLandSelectedUI(listdummy);

                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTIONEDLAND", landtest.getName()));
                        playerConf.setAutoCancelSelect(true);
                    } else {

                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                    }
                }
            } else {

                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                LandSelection select = new LandSelection(player);
                playerConf.setLandSelection(select);
                playerConf.setAutoCancelSelect(true);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {

            if (playerConf.getLandSelected() != null) {
                throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
            }

            if (playerConf.getAreaSelected() == null && playerConf.getAreaSelection() != null) {

                doSelectAreaDone();
            } else {

                doSelectLandDone();
            }

        } else {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    // Évite de faire ce genre copié-coller svp! Ceci peut être facillement mit en une seule méthode
    private void doSelectLandDone() throws FactoidCommandException {

        checkSelections(null, null, null, true, null);

        LandSelection select = playerConf.getLandSelection();
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
            select.setSelected();
        } else {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
            select.setSelected();
        }
    }

    private void doSelectAreaDone() throws FactoidCommandException {

        checkSelections(null, null, null, null, true);

        AreaSelection select = playerConf.getAreaSelection();
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
            select.setSelected();
        } else {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
            select.setSelected();
        }
    }

}
