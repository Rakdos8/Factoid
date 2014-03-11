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
package me.tabinol.factoid.config.players;

import me.tabinol.factoid.config.vanish.VanishEssentials;
import java.util.HashMap;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.vanish.DummyVanish;
import me.tabinol.factoid.config.vanish.Vanish;
import me.tabinol.factoid.config.vanish.VanishNoPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Contain lists for player (selection, ect, ...)
public class PlayerStaticConfig {

    private final Map<CommandSender, PlayerConfEntry> playerConfList;
    private final Vanish vanish;

    public PlayerStaticConfig() {

        playerConfList = new HashMap<CommandSender, PlayerConfEntry>();

        // Ceck for VanishNoPacket plugin
        if (Factoid.getDependPlugin().getVanishNoPacket() != null) {
            vanish = new VanishNoPacket();

            // Check for Essentials plugin
        } else if (Factoid.getDependPlugin().getEssentials() != null) {
            vanish = new VanishEssentials();

            // Dummy Vanish if no plugins
        } else {
            vanish = new DummyVanish();
        }
    }

    // Methods for geting a player static config
    public PlayerConfEntry add(CommandSender sender) {

        PlayerConfEntry entry = new PlayerConfEntry(sender);
        playerConfList.put(sender, entry);

        return entry;
    }

    public void remove(CommandSender sender) {

        // First, remove AutoCancelSelect
        playerConfList.get(sender).setAutoCancelSelect(false);

        playerConfList.remove(sender);
    }

    public PlayerConfEntry get(CommandSender sender) {

        return playerConfList.get(sender);
    }

    public void addAll() {

        // Add the consle in the list
        add(Factoid.getThisPlugin().getServer().getConsoleSender());

        // Add online players
        for (CommandSender sender : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
            add(sender);
        }
    }

    public void removeAll() {

        for (PlayerConfEntry entry : playerConfList.values()) {

            // First, remove AutoCancelSelect
            entry.setAutoCancelSelect(false);

        }
        playerConfList.clear();
    }
    
    public boolean isVanished(Player player) {
        
        return vanish.isVanished(player);
    }
}
