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
import me.tabinol.factoid.commands.CommandList;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Contains general information for commandExecutor
public class CommandEntities {
    
    protected final CommandList command;
    protected final CommandSender sender;
    protected final ArgList argList;
    protected final Player player;
    protected final String playerName;
    protected final PlayerConfEntry playerConf;

    public CommandEntities(CommandList command, CommandSender sender, ArgList argList) {
        
        this.command = command;
        this.sender = sender;
        this.argList = argList;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }
        
        playerName = sender.getName();
        playerConf = Factoid.getPlayerConf().get(sender);
    }

}
