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

        playerConfList = new HashMap<>();

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
