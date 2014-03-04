package me.tabinol.factoid.lands.approve;

import java.util.logging.Level;
import me.tabinol.factoid.Factoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public class ApproveNotif extends BukkitRunnable {

    public static final Permission PERM_APPROVE = new Permission("factoid.collisionapprove");

    public ApproveNotif() {
        
        runTaskLater();
    }
    
    private void runTaskLater() {
        
        long notifyTime = Factoid.getConf().getApproveNotifyTime();
        
        if(notifyTime == 0) {
            this.runTaskLater(Factoid.getThisPlugin(), 24002);
        } else {
            this.runTaskLater(Factoid.getThisPlugin(), notifyTime);
        }
        
    } 
    
    public void run() {

        int lstCount;

        if (Factoid.getConf().getApproveNotifyTime() != 0
                && (lstCount = Factoid.getLands().getApproveList().getApproveList().size()) != 0) {

            // If there is some notification to done
            notifyListApprove(lstCount);

            // Reschedule
            runTaskLater();
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
