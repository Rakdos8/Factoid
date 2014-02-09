package me.tabinol.factoid.config;

import java.util.HashSet;
import org.bukkit.entity.Player;

public class PlayerConfig {

    private final HashSet<Player> adminMod;

    public PlayerConfig() {

        adminMod = new HashSet<>();
    }

    // Caution : No access verification here!
    public boolean addAdminMod(Player player) {

        return adminMod.add(player);
    }

    public boolean removeAdminMod(Player player) {

        return adminMod.remove(player);
    }

    public boolean isAdminMod(Player player) {

        if (adminMod.contains(player)) {
            // Check if the player losts his permission
            if (!player.hasPermission("factoid.adminmod")) {
                adminMod.remove(player);
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
