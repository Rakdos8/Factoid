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
package me.tabinol.factoid.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;


/**
 * The Class StorageThread.
 */
public class StorageThread extends Thread {

	/** The exit request. */
	private boolean exitRequest = false;

	/** The in load. */
	protected boolean inLoad = true; // True if the Database is in Loaded

	/** The storage. */
	private final Storage storage;

	/** The land save list request. */
	private final List<Object> saveList = Collections.synchronizedList(new ArrayList<>());

	/** The land save list request. */
	private final List<Object> removeList = Collections.synchronizedList(new ArrayList<>());

	/** The lock. */
	private final Lock lock = new ReentrantLock();

	/** The lock command request. */
	private final Condition commandRequest  = lock.newCondition();

	/** The lock not saved. */
	private final Condition notSaved = lock.newCondition();

	/**
	 * Instantiates a new storage thread.
	 */
	public StorageThread() {
		this.setName("Factoid Storage");
		this.storage = new StorageFlat();
	}

	/**
	 * Load all and start.
	 */
	public void loadAllAndStart() {
		this.inLoad = true;
		this.storage.loadAll();
		this.inLoad = false;

		this.start();
	}

	/**
	 * Checks if is in load.
	 *
	 * @return true, if is in load
	 */
	public boolean isInLoad() {
		return inLoad;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		lock.lock();

		// Output request loop (waiting for a command)
		while (!exitRequest) {
			// Save Lands or Factions
			while (!saveList.isEmpty()) {
				final Object saveEntry = saveList.remove(0);
				if (saveEntry instanceof Land) {
					storage.saveLand((Land) saveEntry);
				}
			}

			// Remove Lands or Factions
			while (!removeList.isEmpty()) {
				final Object removeEntry = removeList.remove(0);
				if (removeEntry instanceof Land) {
					storage.removeLand((Land) removeEntry);
				} else if ( removeEntry instanceof NameGenealogy){
					storage.removeLand(((NameGenealogy) removeEntry).landName,
							((NameGenealogy) removeEntry).landGenealogy);
				}
			}

			// wait!
			try {
				commandRequest.await();
				Factoid.getThisPlugin().iLog().write("Storage Thread wake up!");
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		notSaved.signal();
		lock.unlock();
	}

	/**
	 * Stop next run.
	 */
	public void stopNextRun() {
		if (!isAlive()) {
			Factoid.getThisPlugin().getLogger().log(Level.SEVERE, "Problem with save Thread. Possible data loss!");
			return;
		}
		exitRequest = true;
		lock.lock();
		commandRequest.signal();
		try {
			notSaved.await();
		} catch (final InterruptedException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Save land.
	 *
	 * @param land the land
	 */
	public void saveLand(final Land land) {
		storage.saveLand(land);
		if (!inLoad) {
			wakeUp();
		}
	}

	/**
	 * Removes the land.
	 *
	 * @param land the land
	 */
	public void removeLand(final Land land) {
		storage.removeLand(land);
		if (!inLoad) {
			wakeUp();
		}
	}

	/**
	 * Removes the land.
	 *
	 * @param landName the land name
	 * @param landGenealogy The land genealogy
	 */
	public void removeLand(final String landName, final int landGenealogy) {
		storage.removeLand(landName, landGenealogy);
		if (!inLoad) {
			wakeUp();
		}
	}

	private void wakeUp() {
		lock.lock();
		commandRequest.signal();
		Factoid.getThisPlugin().iLog().write("Storage request (Thread wake up...)");
		lock.unlock();
	}

	/** Class internally used to store landName and LandGenealogy in a list */
	private static class NameGenealogy {
		private final String landName;
		private final int landGenealogy;

		private NameGenealogy(final String landName, final int landGenealogy) {
			this.landName = landName;
			this.landGenealogy = landGenealogy;
		}
	}
}
