package me.tabinol.factoid.utilities;

import me.tabinol.factoid.Factoid;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Schedule task in Factoid
 *
 * @author Tabinol
 */
public abstract class FactoidRunnable extends BukkitRunnable {

    private BukkitTask bukkitTask;

    public FactoidRunnable() {

        super();
        bukkitTask = null;
    }

    public void runLater(Long tick, boolean multiple) {

        stopNextRun();

        if (multiple) {
            this.runTaskTimer(Factoid.getThisPlugin(), tick, tick);
        } else {
            this.runTaskLater(Factoid.getThisPlugin(), tick);
        }
    }

    public boolean isActive() {

        return bukkitTask != null;
    }

    // *** IF IT IS NOT MULTIPLE RUN, YOU NEED TO SET DONE IN RUN() METHOD ***
    public void setOneTimeDone() {

        bukkitTask = null;
    }

    public void stopNextRun() {

        if (bukkitTask != null) {

            bukkitTask.cancel();
            bukkitTask = null;
        }
    }

}
