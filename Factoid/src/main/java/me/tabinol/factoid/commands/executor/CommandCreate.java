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

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.AreaSelection;
import org.bukkit.ChatColor;

public class CommandCreate extends CommandExec {

    public CommandCreate(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(null, true);
        // checkPermission(false, false, null, null);

        AreaSelection select = (AreaSelection) entity.playerConf.getSelection().getSelection(SelectionType.AREA);

        CuboidArea area = select.getCuboidArea();
        Double price = entity.playerConf.getSelection().getLandCreatePrice();

        // Quit select mod
        // entity.playerConf.setAreaSelection(null);
        // entity.playerConf.setLandSelected(null);
        // select.resetSelection();

        String curArg = entity.argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.HINTUSE");
        }

        Land parent;

        // Check for parent
        if ((parent = entity.playerConf.getSelection().getLand()) == null && !entity.argList.isLast()) {

            parent = Factoid.getLands().getLand(entity.argList.getNext());

            if (parent == null) {
                throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.PARENTNOTEXIST");
            }
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!entity.playerConf.isAdminMod()
                && ((parent == null && !Factoid.getLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(entity.player, PermissionType.LAND_CREATE))
                || (parent != null && !parent.checkPermissionAndInherit(entity.player, PermissionType.LAND_CREATE)))) {
            throw new FactoidCommandException("CommandCreate", entity.player, "GENERAL.MISSINGPERMISSION");
        }

        // Check for collision
        if (checkCollision(curArg, null, LandAction.LAND_ADD, 0, area, parent, price, true)) {
            new CommandCancel(entity.playerConf, true).commandExecute();
            return;
        }

        // Create Land
        Land land = null;
        
        // If the player is adminmod, the owner is nobody
        PlayerContainer owner;
        if(entity.playerConf.isAdminMod()) {
            owner = new PlayerContainerNobody();
        } else {
            owner = entity.playerConf.getPlayerContainer();
        }
        
        try {
            land = Factoid.getLands().createLand(curArg, owner, area, parent, price);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, "On land create", ex);
        }

        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
        Factoid.getLog().write(entity.playerName + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
        
        // Cancel et select the land
        new CommandCancel(entity.playerConf, true).commandExecute();
        new CommandSelect(entity.player, new ArgList(new String[] {land.getName()}, 
                entity.player), null).commandExecute();
    }
}
