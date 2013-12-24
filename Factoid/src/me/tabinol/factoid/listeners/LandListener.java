package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.event.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.event.PlayerContainerLandBanEvent;
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
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
        String playerNameLower = player.getName().toLowerCase();

        if (playerHeal.contains(player)) {
            playerHeal.remove(player);
        }

        // OnCommand.getPlayerSelectingWorldEdit().remove(playerNameLower);
        OnCommand.getLandSelectioned().remove(playerNameLower);
        OnCommand.getLandSelectionedUI().remove(playerNameLower);
        OnCommand.getPlayerExpanding().remove(playerNameLower);
        OnCommand.getPlayerSetFlagUI().remove(playerNameLower);
        OnCommand.getAdminMod().remove(playerNameLower);
        OnCommand.getRemoveList().remove(playerNameLower);
        OnCommand.getChatPageList().remove(player);
        OnCommand.getLandSelectConfig().remove(playerNameLower);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {

        Player player = event.getPlayer();
        Land lastLand = event.getLastLand();
        Land land = event.getLand();
        DummyLand dummyLand;
        LandFlag flag;
        String value;

        if (lastLand != null) {

            // Message quit
            if ((flag = lastLand.getFlagAndInherit(FlagType.MESSAGE_QUIT)) != null
                    && (value = flag.getValueString()) != null) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + lastLand.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
            }

            //Notify players for exit
            if (!OnCommand.isAdminMod(player)) {
                notifyPlayers(lastLand, "ACTION.PLAYEREXIT", player);
            }
        }
        if (land != null) {
            dummyLand = land;

            if (!OnCommand.isAdminMod(player)) {
                // is banned or can enter
                if ((land.isBanned(player.getName())
                        || land.checkPermissionAndInherit(player.getName(), PermissionType.LAND_ENTER) != PermissionType.LAND_ENTER.baseValue())
                        && !land.isOwner(player.getName())) {
                    String message;
                    if (land.isBanned(player.getName())) {
                        message = "ACTION.BANNED";
                    } else {
                        message = "ACTION.NOENTRY";
                    }
                    if (land == lastLand || lastLand == null) {
                        tpSpawn(player, land, message);
                        return;
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(message, land.getName()));
                        event.setCancelled(true);
                        return;
                    }
                }

                //Notify players for Enter
                notifyPlayers(land, "ACTION.PLAYERENTER", player);
            }

            // Message join
            if ((flag = land.getFlagAndInherit(FlagType.MESSAGE_JOIN)) != null
                    && (value = flag.getValueString()) != null) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + land.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
            }

        } else {
            dummyLand = Factoid.getLands().getOutsideArea(event.getToLoc());
        }

        //Check for Healing
        if (dummyLand.checkPermissionAndInherit(player.getName(), PermissionType.AUTO_HEAL) != PermissionType.AUTO_HEAL.baseValue()) {
            if (!playerHeal.contains(player)) {
                playerHeal.add(player);
            }
        } else {
            if (playerHeal.contains(player)) {
                playerHeal.remove(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerContainerLandBan(PlayerContainerLandBanEvent event) {

        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.BANNED");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerContainerAddNoEnter(PlayerContainerAddNoEnterEvent event) {

        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.NOENTRY");
    }

    private void checkForBannedPlayers(Land land, PlayerContainer pc, String message) {

        for (Player players : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
            if (pc.hasAccess(players.getName())
                    && !land.isOwner(players.getName())
                    && !OnCommand.isAdminMod(players)) {
                tpSpawn(players, land, message);
            }
        }
    }

    private void notifyPlayers(Land land, String message, Player playerIn) {

        TreeSet<String> playersNotify = land.getPlayersNotify();

        for (Player player : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
            if (playersNotify.contains(player.getName().toLowerCase())) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(message, playerIn.getDisplayName(), land.getName()));
            }
        }
    }

    private void tpSpawn(Player player, Land land, String message) {

        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(message, land.getName()));
    }
}
