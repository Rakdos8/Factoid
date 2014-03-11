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
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.collisions.Collisions;
import org.bukkit.ChatColor;

public class CommandRename extends CommandExec {

    public CommandRename(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, true, null, null);
        checkPermission(true, true, null, null);
        
        String curArg = entity.argList.getNext();
        if (BannedWords.isBannedWord(curArg)) {
            throw new FactoidCommandException("CommandRename", entity.player, "COMMAND.RENAME.HINTUSE");
        }

        // Check for collision
        if (checkCollision(curArg, land, Collisions.LandAction.LAND_RENAME, 0, null, land.getParent(), true)) {
            return;
        }

        String oldName = land.getName();

        try {
            Factoid.getLands().renameLand(oldName, curArg);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandRename.class.getName()).log(Level.SEVERE, "On land rename", ex);
            throw new FactoidCommandException("On land rename", entity.player, "GENERAL.ERROR");
        }
        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RENAME.ISDONE", oldName, curArg));
        Factoid.getLog().write(entity.playerName + " has renamed " + oldName + " to " + curArg);
    }
}
