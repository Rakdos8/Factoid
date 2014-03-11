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
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.selection.land.LandSelection;
import me.tabinol.factoid.lands.selection.area.AreaSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCancel extends CommandExec {

    private final Player player;
    private final PlayerConfEntry playerConf;
    private final boolean fromAutoCancel; // true: launched from autoCancel

    public CommandCancel(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
        player = entity.player;
        playerConf = entity.playerConf;
        fromAutoCancel = false;
    }

    // Called from PlayerListener
    public CommandCancel(PlayerConfEntry entry, boolean fromAutoCancel) throws FactoidCommandException {

        super(null, false, false);
        this.player = entry.getPlayer();
        playerConf = entry;
        this.fromAutoCancel = fromAutoCancel;
    }
    
    @Override
    public void commandExecute() throws FactoidCommandException {

        LandSelection selectLand;
        AreaSelection selectArea;

        if (playerConf.getConfirm() != null) {
            playerConf.setConfirm(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Factoid.getLog().write(player.getName() + " cancel for action");
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        if ((selectLand = playerConf.getLandSelection()) != null) {

            selectLand.resetSelection();
            playerConf.setLandSelection(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getLog().write(player.getName() + ": Select cancel");

            if(!fromAutoCancel) {
                return;
            }
        }
        
        if ((selectArea = playerConf.getAreaSelection()) != null) {

            selectArea.resetSelection();
            playerConf.setAreaSelection(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getLog().write(player.getName() + ": Select cancel");

            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (playerConf.getSetFlagUI() != null) {

            playerConf.setSetFlagUI(null);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));

            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (playerConf.getLandSelected() != null) {

            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
            playerConf.setLandSelected(null);
            playerConf.setLandSelectedUI(null);

            // Cancel selection (it is the last think selected
            playerConf.setAutoCancelSelect(false);
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        // No cancel done
        if(!fromAutoCancel) {
            throw new FactoidCommandException("Nothing to confirm", player, "COMMAND.CANCEL.NOCANCEL");
        }
    }
}
