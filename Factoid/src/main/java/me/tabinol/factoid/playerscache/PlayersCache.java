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
package me.tabinol.factoid.playerscache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.CommandThreadExec;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;


/**
 * The Class PlayersCache.
 */
public class PlayersCache extends Thread {

	/** The Constant PLAYERS_CACHE_VERSION. */
	public static final int PLAYERS_CACHE_VERSION = 1;

	/** The file name. */
	private final String fileName;

	/** The file. */
	private final File file;

	/** The players cache list. */
	private TreeMap<String, PlayerCacheEntry> playersCacheList;

	/** The players rev cache list. */
	private TreeMap<UUID, PlayerCacheEntry> playersRevCacheList;

	/** The output list. */
	private final List<OutputRequest> outputList;

	/** The update list. */
	private final List<PlayerCacheEntry> updateList;

	/** The exit request. */
	private boolean exitRequest = false;

	/** The http profile repository. */
	private final HttpProfileRepository httpProfileRepository;

   /** The lock. */
   final Lock lock = new ReentrantLock();

   /** The lock command request. */
   final Condition commandRequest  = lock.newCondition();

   /** The lock not saved. */
   final Condition notSaved = lock.newCondition();

	/**
	 * The Class OutputRequest.
	 */
	private class OutputRequest {

		/** The command exec. */
		CommandThreadExec commandExec;

		/** The player names. */
		String[] playerNames;

		/**
		 * Instantiates a new output request.
		 *
		 * @param commandExec the command exec
		 * @param playerNames the player names
		 */
		OutputRequest(final CommandThreadExec commandExec, final String[] playerNames) {

			this.commandExec = commandExec;
			this.playerNames = playerNames;
		}
	}

	/**
	 * Instantiates a new players cache.
	 */
	public PlayersCache() {

		this.setName("Factoid Players cache");
		fileName = Factoid.getThisPlugin().getDataFolder() + "/" + "playerscache.conf";
		file = new File(fileName);
		outputList = Collections.synchronizedList(new ArrayList<OutputRequest>());
		updateList = Collections.synchronizedList(new ArrayList<PlayerCacheEntry>());
		httpProfileRepository = new HttpProfileRepository("minecraft");
		loadAll();
	}

	/**
	 * Update player.
	 *
	 * @param uuid the uuid
	 * @param playerName the player name
	 */
	public void updatePlayer(final UUID uuid, final String playerName) {

		updateList.add(new PlayerCacheEntry(uuid, playerName));
		lock.lock();
		commandRequest.signal();
		Factoid.getThisPlugin().iLog().write("Name request (Thread wake up...)");
		lock.unlock();
	}

	public String getNameFromUUID(final UUID uuid) {

		final PlayerCacheEntry entry = playersRevCacheList.get(uuid);

		if (entry != null) {
			return entry.getName();
		}

		return null;
	}

	/**
	 * Gets the UUID with names.
	 *
	 * @param commandExec the command exec
	 * @param pc the pc
	 */
	public void getUUIDWithNames(final CommandThreadExec commandExec, final IPlayerContainer pc) {

		if (pc != null && pc instanceof PlayerContainerPlayerName) {
			getUUIDWithNames(commandExec, pc.getName());
		} else {
			// Start a null player name, nothing to resolve
			getUUIDWithNames(commandExec);
		}
	}

	/**
	 * Gets the UUID with names.
	 *
	 * @param commandExec the command exec
	 * @param playerNames the player names
	 */
	public void getUUIDWithNames(final CommandThreadExec commandExec, final String... playerNames) {

		outputList.add(new OutputRequest(commandExec, playerNames));
		lock.lock();
		commandRequest.signal();
		Factoid.getThisPlugin().iLog().write("Name request (Thread wake up...)");
		lock.unlock();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		lock.lock();

		// Output request loop (waiting for a command)
		while (!exitRequest) {

   			// Check if the list is empty and execute the list
   			while (!outputList.isEmpty()) {
   				final OutputRequest outputRequest = outputList.remove(0);
   				final int length = outputRequest.playerNames.length;

   				final PlayerCacheEntry[] entries = new PlayerCacheEntry[length];

   				// Pass 1 check in playersCache or null
				final ArrayList<String> names = new ArrayList<>(); // Pass 2 list
   				for (int t = 0; t < length; t++) {
   					entries[t] = playersCacheList.get(outputRequest.playerNames[t].toLowerCase());
   					if (entries[t] == null) {
   						// Add in a list for pass 2
   						names.add(outputRequest.playerNames[t]);
   					}
   				}

   				// Pass 2 check in Minecraft website
   				if (!names.isEmpty()) {
   					Factoid.getThisPlugin().iLog().write("HTTP profile request: " + names);
   					final Profile[] profiles = httpProfileRepository.findProfilesByNames(names.toArray(new String[0]));
   					for (final Profile profile : profiles) {
   						// Put in the correct position
   						int compt = 0;
   						final boolean found = false;

   						while (compt != length && !found) {
   							if (entries[compt] == null) {
   								Factoid.getThisPlugin().iLog().write("HTTP Found : " + profile.getName() + ", " + profile.getId());
   								final UUID uuid = stringToUUID(profile.getId());
   								if (uuid != null) {
   									entries[compt] = new PlayerCacheEntry(uuid,
   											profile.getName());
   									// Update now
   									updatePlayerInlist(entries[compt]);
   								}
   							}
   							compt ++;
   						}
   					}
   				}
   				// Return the output of the request on the main thread
   				final ReturnToCommand returnToCommand = new ReturnToCommand(outputRequest.commandExec, entries);
  				Bukkit.getScheduler().callSyncMethod(Factoid.getThisPlugin(), returnToCommand);
   			}

   			// Update playerList
   			while (!updateList.isEmpty()) {
   				updatePlayerInlist(updateList.remove(0));
   			}

			// wait!
			try {
				commandRequest.await();
				Factoid.getThisPlugin().iLog().write("PlayersCache Thread wake up!");
   			} catch (final InterruptedException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
		}
		saveAll();
		notSaved.signal();
		lock.unlock();
	}

	private void updatePlayerInlist(final PlayerCacheEntry entry) {

		final String nameLower = entry.getName().toLowerCase();
		if (playersCacheList.get(nameLower) == null) {

			// Update to do
			if (playersRevCacheList.get(entry.getUUID()) != null) {
				// Player exist, but name changed
				playersCacheList.remove(nameLower);
			}

			// update name
			playersCacheList.put(nameLower, entry);
			playersRevCacheList.put(entry.getUUID(), entry);
		}
	}

	/**
	 * Stop next run.
	 */
	public void stopNextRun() {

		if (!isAlive()) {
			Factoid.getThisPlugin().getLogger().log(Level.SEVERE, "Problem with Players Cache Thread. Possible data loss!");
			return;
		}
		exitRequest = true;
		lock.lock();
		commandRequest.signal();
		try {
			notSaved.await();
		} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Load all.
	 */
	public void loadAll() {
		playersCacheList = new TreeMap<>();
		playersRevCacheList = new TreeMap<>();

		try {
			try(final BufferedReader br = new BufferedReader(new FileReader(file))) {
				Integer.parseInt(br.readLine().split(":")[1]);
				br.readLine(); // Read remark line

				String str;

				while ((str = br.readLine()) != null && !str.equals("")) {
					// Read from String "PlayerUUID:LastInventoryName:isCreative"
					final String[] strs = str.split(":");

					final String name = strs[0];
					final UUID uuid = UUID.fromString(strs[1]);
					playersCacheList.put(name.toLowerCase(), new PlayerCacheEntry(uuid, name));
					playersRevCacheList.put(uuid, new PlayerCacheEntry(uuid, name));
				}
				br.close();
			}
			catch (final FileNotFoundException ex) {
				// Not existing? Nothing to load!
				return;
			}

		} catch (final IOException ex) {
			Logger.getLogger(PlayersCache.class.getName()).log(Level.SEVERE, "I can't load the players cache list", ex);
		}
	}

	/**
	 * Save all.
	 */
	public void saveAll() {
		try {
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write("Version:" + PLAYERS_CACHE_VERSION);
				bw.newLine();
				bw.write("# Name:PlayerUUID");
				bw.newLine();

				for (final Map.Entry<String, PlayerCacheEntry> PlayerInvEntry : playersCacheList.entrySet()) {
					// Write to String "Name:PlayerUUID"
					bw.write(PlayerInvEntry.getValue().getName() + ":" + PlayerInvEntry.getValue().getUUID());
					bw.newLine();
				}
				bw.close();
			}
			catch (final FileNotFoundException ex) {
				// Not existing? Nothing to load!
				return;
			}

		} catch (final IOException ex) {
			Logger.getLogger(PlayersCache.class.getName()).log(Level.SEVERE, "I can't save the players cache list", ex);
		}
	}

	private UUID stringToUUID(final String stId) {
		final String convId = stId.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		UUID uuid;
		try {
			uuid = UUID.fromString(convId);
		} catch(final IllegalArgumentException ex) {
			// error? return null
			return null;
		}

		return uuid;
	}
}
