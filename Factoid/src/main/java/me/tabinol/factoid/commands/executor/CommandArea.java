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

import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.commands.ConfirmEntry.ConfirmType;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import org.bukkit.ChatColor;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandArea.
 */
public class CommandArea extends CommandExec {

    /**
     * Instantiates a new command area.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandArea(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {

            checkPermission(true, true, null, null);
            checkSelections(true, null);

            CuboidArea area = entity.playerConf.getSelection().getCuboidArea();
            double price = entity.playerConf.getSelection().getAreaAddPrice();

            // Check for collision
            if (checkCollision(land.getName(), land, LandAction.AREA_ADD, 0, area, land.getParent(), 
            		land.getOwner(), price, true)) {
                return;
            }

            // Add Area
            land.addArea(area, price);

            entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
            Factoid.getLog().write(entity.playerName + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
            new CommandCancel(entity.playerConf, false).commandExecute();

        } else if (curArg.equalsIgnoreCase("remove") || curArg.equalsIgnoreCase("replace")) {

            checkPermission(true, true, null, null);
            checkSelections(true, null);

            String areaNbStr = entity.argList.getNext();
            int areaNb = 0;

            // check here if there is an area to replace
            CuboidArea areaToReplace = entity.playerConf.getSelection().getAreaToReplace();
            if (areaToReplace != null) {
                areaNb = areaToReplace.getKey();
            }

            // 0 is same has not set
            if (areaNb == 0) {
                if (areaNbStr == null) {
                    throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.EMPTY");
                }
                try {
                    areaNb = Integer.parseInt(areaNbStr);
                } catch (NumberFormatException ex) {
                    throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
                }
                if (land.getArea(areaNb) == null) {
                    throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
                }
            }

            // Only for a remove
            if (curArg.equalsIgnoreCase("remove")) {

                // Check for collision
                if (checkCollision(curArg, land, LandAction.AREA_REMOVE, areaNb, null, land.getParent(), 
                		land.getOwner(), 0, true)) {
                    return;
                }

                // Check if exist
                if (land.getArea(areaNb) == null) {
                    throw new FactoidCommandException("Area", entity.sender, "COMMAND.REMOVE.AREA.INVALID");
                }

                entity.playerConf.setConfirm(new ConfirmEntry(
                        ConfirmType.REMOVE_AREA, land, areaNb));
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CONFIRM"));

            } else {

                //Only for a replace
                checkSelections(true, true);

                CuboidArea area = entity.playerConf.getSelection().getCuboidArea();
                double price = entity.playerConf.getSelection().getAreaReplacePrice(areaNb);

                // Check for collision
                if (checkCollision(land.getName(), land, LandAction.AREA_MODIFY, areaNb, area, land.getParent(), 
                		land.getOwner(), price, true)) {
                    return;
                }

                // Replace Area
                land.replaceArea(areaNb, area, price);

                entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
                Factoid.getLog().write(entity.playerName + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
                new CommandCancel(entity.playerConf, false).commandExecute();
            }

        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            for (Map.Entry<Integer, CuboidArea> entry : land.getIdsAndAreas().entrySet()) {
                stList.append("ID: " + entry.getKey() + ", " + entry.getValue().getPrint() + Factoid.getConf().NEWLINE);
            }
            new ChatPage("COMMAND.AREA.LISTSTART", stList.toString(), entity.sender, land.getName()).getPage(1);
        } else {
            throw new FactoidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }

}
