package me.tabinol.factoid.config.players;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.scheduler.BukkitRunnable;

// Auto cancel selection
public class PlayerAutoCancelSelect extends BukkitRunnable {

    private final PlayerConfEntry entry;
    private boolean toRun;

    public PlayerAutoCancelSelect(PlayerConfEntry entry) {

        this.entry = entry;
        toRun = false;
    }

    public void runLater(Long tick) {
        
        stopNextRun();
        this.runTaskLater(Factoid.getThisPlugin(), tick);
    }
    
    @Override
    public void run() {
        try {
            toRun = false;
            new CommandCancel(entry.getPlayer(), true).commandExecute();
        } catch (FactoidCommandException ex) {
            Logger.getLogger(PlayerAutoCancelSelect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopNextRun() {
        
        if(toRun) {
            this.cancel();
        }
    }
}
