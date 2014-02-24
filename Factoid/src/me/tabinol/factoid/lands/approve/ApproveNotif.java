package me.tabinol.factoid.lands.approve;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.Lang;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public class ApproveNotif extends BukkitRunnable {

    public static final Permission PERM_APPROVE = new Permission("factoid.collisionapprove");

    public ApproveNotif() {
        
        runTaskLater();
    }
    
    private void runTaskLater() {
        
        long notifyTime = Factoid.getConf().ApproveNotifyTime;
        
        if(notifyTime == 0) {
            this.runTaskLater(Factoid.getThisPlugin(), 24002);
        } else {
            this.runTaskLater(Factoid.getThisPlugin(), notifyTime);
        }
        
    } 
    
    public void run() {

        int lstCount;

        if (Factoid.getConf().ApproveNotifyTime != 0
                && (lstCount = Factoid.getLands().getApproveList().getApproveList().size()) != 0) {

            // If there is some notification to done
            notifyListApprove(lstCount);

            // Reschedule
            runTaskLater();
        }
    }

    public static void notifyForApprove(String landName, String playerName) {

        Lang.notifyPlayer(Factoid.getLanguage().getMessage("COLLISION.SHOW.NOTIFYLAND", landName, playerName + ChatColor.GREEN), PERM_APPROVE);
    }

    private void notifyListApprove(int lstCount) {

        Lang.notifyPlayer(Factoid.getLanguage().getMessage("COLLISION.SHOW.NOTIFY", lstCount + ""), PERM_APPROVE);
    }

}
