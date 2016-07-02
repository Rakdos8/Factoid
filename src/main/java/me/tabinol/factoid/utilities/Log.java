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


/**
 * The Class Log.
 */
public class Log {

	/** The Folder. */
	public File Folder;

	/** The debug. */
	private boolean debug = false;

	/**
	 * Instantiates a new log.
	 */
	public Log() {

		this.debug = Factoid.getThisPlugin().iConf().isDebug();
		this.Folder = Factoid.getThisPlugin().getDataFolder();
	}

	/**
	 * Write.
	 *
	 * @param text the text
	 */
	public void write(final String text) {

		if (debug) {
			final File filename = new File(Folder, "log_" + Dates.date() + ".log");

			if (!filename.exists()) {
				try {
					filename.createNewFile();
				} catch (final IOException ex) {
					Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			try(final FileWriter fileWriter = new FileWriter(filename, true);
				final BufferedWriter bufWriter = new BufferedWriter(fileWriter)
			) {
				bufWriter.newLine();
				bufWriter.write("[Factoid][v." + Factoid.getThisPlugin().getDescription().getVersion()
						+ "][" + Dates.time() + "]" + text);
				bufWriter.close();
			} catch (final IOException ex) {
				Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Sets the debug.
	 *
	 * @param newdebug the new debug
	 */
	public void setDebug(final boolean newdebug) {

		this.debug = newdebug;
	}
}
