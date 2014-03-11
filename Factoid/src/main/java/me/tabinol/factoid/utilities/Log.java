/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

        this.debug = Factoid.getConf().isDebug();
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
