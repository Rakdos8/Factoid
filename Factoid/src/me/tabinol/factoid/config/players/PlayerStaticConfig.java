package me.tabinol.factoid.config.players;

import java.util.HashMap;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import org.bukkit.command.CommandSender;

// Contain lists for player (selection, ect, ...)
public class PlayerStaticConfig {

    private final Map<CommandSender, PlayerConfEntry> playerConfList;

    public PlayerStaticConfig() {

        playerConfList = new HashMap<>();

        // Add the consle in the list
        add(Factoid.getThisPlugin().getServer().getConsoleSender());
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
}
