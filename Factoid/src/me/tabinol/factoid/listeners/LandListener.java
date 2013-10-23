package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LandListener implements Listener {

    public LandListener() {

        super();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {

        Player player = event.getPlayer();
        Land lastLand = event.getLastLand();
        Land land = event.getLand();
        LandFlag flag;
        String value;

        // Message quit
        if (lastLand != null && (flag = lastLand.getFlagAndInherit(FlagType.MESSAGE_QUIT)) != null
                && (value = flag.getValueString()) != null) {
            player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + lastLand.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
        }
        if (land != null) {

            // is banned?
            if (land.isBanned(new PlayerContainerPlayer(player.getName()))) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.BANNED", land.getName()));
                event.setCancelled(true);
            } else {

                // Message join
                if ((flag = land.getFlagAndInherit(FlagType.MESSAGE_JOIN)) != null
                        && (value = flag.getValueString()) != null) {
                    player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + land.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }
        }
    }
}
