package me.tabinol.factoid.lands.approve;

import java.util.logging.Level;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.FactoidRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class ApproveNotif extends FactoidRunnable {

    public static final Permission PERM_APPROVE = new Permission("factoid.collisionapprove");

    public ApproveNotif() {
        
        super();
    }

    public void runApproveNotifLater() {

        long notifyTime = Factoid.getConf().getApproveNotifyTime();

        // Start only if notification is activated in configuration
        if (notifyTime != 0) {
            this.runLater(notifyTime, true);
        }

    }

    @Override
    public void run() {

        int lstCount;

        if ((lstCount = Factoid.getLands().getApproveList().getApproveList().size()) != 0) {

            // If there is some notification to done
            notifyListApprove(lstCount);
        }
    }

    public void notifyForApprove(String landName, String playerName) {

        notifyPlayer(Factoid.getLanguage().getMessage("COLLISION.SHOW.NOTIFYLAND", landName, playerName + ChatColor.GREEN));
    }

    private void notifyListApprove(int lstCount) {

        notifyPlayer(Factoid.getLanguage().getMessage("COLLISION.SHOW.NOTIFY", lstCount + ""));
    }

    // Notify with a message
    private void notifyPlayer(String message) {

        for (Player players : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
            if (players.hasPermission(PERM_APPROVE)) {
                players.sendMessage(ChatColor.GREEN + "[Factoid] " + message);
            }
        }

        Factoid.getThisPlugin().getLogger().log(Level.INFO, "[Factoid] " + message);
    }
}
