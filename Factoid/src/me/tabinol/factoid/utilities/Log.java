package me.tabinol.factoid.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;

public class Log extends Thread {

    public File Folder;
    private boolean debug = false;

    public Log() {

        this.debug = Factoid.getConf().debug;
        this.Folder = Factoid.getThisPlugin().getDataFolder();
    }

    public void write(String text) {

        if (debug) {
            File filename = new File(Folder, "log_" + Dates.date() + ".log");
            BufferedWriter bufWriter = null;
            FileWriter fileWriter = null;

            if (!filename.exists()) {
                try {
                    filename.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                fileWriter = new FileWriter(filename, true);
                bufWriter = new BufferedWriter(fileWriter);
                bufWriter.newLine();
                bufWriter.write("[Factoid][v." + Factoid.getVersion() + "][" + Dates.time() + "]" + text);
                bufWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    bufWriter.close();
                    fileWriter.close();
                } catch (IOException ex) {
                    Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void setDebug(boolean newdebug) {

        this.debug = newdebug;
    }
}
