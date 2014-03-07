package me.tabinol.factoid.config.players;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.utilities.FactoidRunnable;

// Auto cancel selection
public class PlayerAutoCancelSelect extends FactoidRunnable {

    private final PlayerConfEntry entry;

    public PlayerAutoCancelSelect(PlayerConfEntry entry) {

        super();
        this.entry = entry;
    }

    @Override
    public void run() {
        
        setOneTimeDone();
        try {            
            new CommandCancel(entry, true).commandExecute();
        } catch (FactoidCommandException ex) {
            Logger.getLogger(PlayerAutoCancelSelect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
