package me.tabinol.factoid.utilities;

import me.tabinol.factoid.Factoid;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Schedule task in Factoid
 *
 * @author Tabinol
 */
public abstract class FactoidRunnable extends BukkitRunnable {

    private Integer taskId = null;

    public FactoidRunnable() {

        super();
        taskId = null;
    }

    public void runLater(Long tick, boolean multiple) {

        stopNextRun();

        if (multiple) {
            taskId = this.runTaskTimer(Factoid.getThisPlugin(), tick, tick).getTaskId();
        } else {
            taskId = this.runTaskLater(Factoid.getThisPlugin(), tick).getTaskId();
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

            Bukkit.getServer().getScheduler().cancelTask(taskId);
            taskId = null;
        }
    }

}
