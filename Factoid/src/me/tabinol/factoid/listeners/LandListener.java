package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LandListener implements Listener {

    private ArrayList<Player> playerHeal;
    private LandHeal landHeal;

    private class LandHeal extends BukkitRunnable {

        @Override
        public void run() {

            int foodLevel;
            double health;
            double maxHealth;

            for (Player player : playerHeal) {
                if (!player.isDead()) {
                    Factoid.getLog().write("Healing: " + player.getName());
                    foodLevel = player.getFoodLevel();
                    if (foodLevel < 20) {
                        foodLevel += 5;
                        if (foodLevel > 20) {
                            foodLevel = 20;
                        }
                        player.setFoodLevel(foodLevel);
                    }
                    health = player.getHealth();
                    maxHealth = player.getMaxHealth();
                    if (health < maxHealth) {
                        health += maxHealth / 10;
                        if (health > maxHealth) {
                            health = maxHealth;
                        }
                        player.setHealth(health);
                    }
                }
            }
        }
    }

    public LandListener() {

        super();
        playerHeal = new ArrayList<>();
        landHeal = new LandHeal();
        landHeal.runTaskTimer(Factoid.getThisPlugin(), 20, 20);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        playerHeal.remove(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {

        Player player = event.getPlayer();
        DummyLand lastLand = event.getLastLand();
        String worldName = event.getToLoc().getWorld().getName();
        DummyLand land = event.getLand();
        LandFlag flag;
        String value;

        // Message quit
        if (lastLand instanceof Land && (flag = ((Land) lastLand).getFlagAndInherit(FlagType.MESSAGE_QUIT)) != null
                && (value = flag.getValueString()) != null) {
            player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + ((Land) lastLand).getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
        }
        if (land instanceof Land) {

            // is banned?
            if (((Land) land).isBanned(new PlayerContainerPlayer(player.getName()))) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.BANNED", ((Land) land).getName()));
                event.setCancelled(true);

                // Can enter
            } else if (((Land) land).checkPermissionAndInherit(player.getName(), PermissionType.LAND_ENTER) != PermissionType.LAND_ENTER.baseValue()) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("ACTION.NOENTRY", ((Land) land).getName()));
                event.setCancelled(true);
            } else {

                // Message join
                if ((flag = ((Land) land).getFlagAndInherit(FlagType.MESSAGE_JOIN)) != null
                        && (value = flag.getValueString()) != null) {
                    player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + ((Land) land).getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }
            
            //Check for Healing
            if (land.checkPermissionAndInherit(worldName, player.getName(), PermissionType.AUTO_HEAL) != PermissionType.AUTO_HEAL.baseValue()) {
                if (!playerHeal.contains(player)) {
                    playerHeal.add(player);
                }
            } else {
                playerHeal.remove(player);
            }
        }
    }
}
