package me.tabinol.factoid.utilities;

import me.tabinol.factoid.Factoid;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Schedule task in Factoid
 *
 * @author Tabinol
 */
public abstract class FactoidRunnable extends BukkitRunnable {

    private BukkitTask taskId = null;

    public FactoidRunnable() {

        super();
        taskId = null;
    }

    public void runLater(Long tick, boolean multiple) {

        stopNextRun();

        if (multiple) {
            taskId = Bukkit.getServer().getScheduler().runTaskLater(Factoid.getThisPlugin(), this, tick);
              
        } else {
            taskId = Bukkit.getServer().getScheduler().runTaskLater(Factoid.getThisPlugin(), this, tick);
        }
    }

    public boolean isActive() {

        return taskId != null;
    }

    // *** IF IT IS NOT MULTIPLE RUN, YOU NEED TO SET DONE IN RUN() METHOD ***
    public void setOneTimeDone() {

        taskId = null;
    }

    public void stopNextRun() {

        if (taskId != null) {

            taskId.cancel();
            taskId = null;
        }
    }

}
