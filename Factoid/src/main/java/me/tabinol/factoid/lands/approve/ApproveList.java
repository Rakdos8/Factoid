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
package me.tabinol.factoid.lands.approve;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.utilities.StringChanges;


/**
 * The Class ApproveList.
 */
public class ApproveList {

	/** The approve file. */
	final private File approveFile;

	/** The approve config. */
	private FileConfiguration approveConfig;

	/** The land names. */
	final private TreeSet<String> landNames;

	/**
	 * Instantiates a new approve list.
	 */
	public ApproveList() {

		approveFile = new File(Factoid.getThisPlugin().getDataFolder() + "/approvelist.yml");
		approveConfig = new YamlConfiguration();
		landNames = new TreeSet<>();
		loadFile();
	}

	/**
	 * Adds the approve.
	 *
	 * @param approve the approve
	 */
	public void addApprove(final Approve approve) {

		landNames.add(approve.getLandName());
		final ConfigurationSection section = approveConfig.createSection(approve.getLandName());
		if (approve.getType() != null) {
			section.set("Type", approve.getType().getName());
		}
		section.set("Action", approve.getAction().toString());
		section.set("RemovedAreaId", approve.getRemovedAreaId());
		if (approve.getNewArea() != null) {
			section.set("NewArea", approve.getNewArea().toString());
		}
		section.set("Owner", approve.getOwner().toString());
		if (approve.getParent() != null) {
			section.set("Parent", approve.getParent().getName());
		}
		section.set("Price", approve.getPrice());
		section.set("DateTime", approve.getDateTime().getTimeInMillis());
		saveFile();
		Factoid.getThisPlugin().iApproveNotif ().notifyForApprove(approve.getLandName(), approve.getOwner().getPrint());
	}

	/**
	 * Gets the approve list.
	 *
	 * @return the approve list
	 */
	public TreeMap<String,Approve> getApproveList() {

		final TreeMap<String,Approve> approves = new TreeMap<>();
		final TreeMap<String,Approve> approvesToRemove = new TreeMap<>();

		// Check if land names are ok
		for (final String landName : landNames) {

			final Approve app = getApprove(landName);

			if (app != null) {

				// Approve ok, put in list
				approves.put(landName, app);
			} else {

				// Approve not ok, add it to list
				approvesToRemove.put(landName, app);
			}
		}

		// Remove wrong approves
		for (final Map.Entry<String,Approve> appEntry : approvesToRemove.entrySet()) {

			removeApprove(appEntry.getKey());
		}

		return approves;
	}

	/**
	 * Checks if is in approve.
	 *
	 * @param landName the land name
	 * @return true, if is in approve
	 */
	public boolean isInApprove(final String landName) {

		return landNames.contains(landName.toLowerCase());
	}

	/**
	 * Gets the approve.
	 *
	 * @param landName the land name
	 * @return the approve
	 */
	public Approve getApprove(final String landName) {

		Factoid.getThisPlugin().iLog().write("Get approve for: " + landName);
		final ConfigurationSection section = approveConfig.getConfigurationSection(landName);

		if (section == null) {
			Factoid.getThisPlugin().iLog().write("Error Section null");
			return null;
		}

		final String typeName = section.getString("Type");
		IType type = null;
		if (typeName != null) {
			type = FactoidAPI.iTypes().addOrGetType(typeName);
		}

		final String[] ownerS = StringChanges.splitAddVoid(section.getString("Owner"), ":");
		final PlayerContainer pc = PlayerContainer.create(null, EPlayerContainerType.getFromString(ownerS[0]), ownerS[1]);
		ILand parent = null;
		CuboidArea newArea = null;

		if (section.contains("Parent")) {
			parent = Factoid.getThisPlugin().iLands().getLand(section.getString("Parent"));

			// If the parent does not exist
			if (parent == null) {
				Factoid.getThisPlugin().iLog().write("Error, parent not found");
				return null;
			}
		}

		if (section.contains("NewArea")) {
			newArea = CuboidArea.getFromString(section.getString("NewArea"));
		}

		final LandAction action = LandAction.valueOf(section.getString("Action"));

		// If the land was deleted
		if (action != LandAction.LAND_ADD && Factoid.getThisPlugin().iLands().getLand(landName) == null) {
			return null;
		}

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(section.getLong("DateTime"));

		return new Approve(landName, type, action,
				section.getInt("RemovedAreaId"), newArea, pc,
				parent, section.getDouble("Price"), cal);
	}

	/**
	 * Removes the approve.
	 *
	 * @param approve the approve
	 */
	public void removeApprove(final Approve approve) {

		removeApprove(approve.getLandName());
	}

	/**
	 * Removes the approve.
	 *
	 * @param landName the land name
	 */
	public void removeApprove(final String landName) {

		Factoid.getThisPlugin().iLog().write("Remove Approve from list: " + landName);

		approveConfig.set(landName, null);
		landNames.remove(landName);
		saveFile();
	}

	/**
	 * Removes the all.
	 */
	public void removeAll() {

		Factoid.getThisPlugin().iLog().write("Remove all Approves from list.");

		// Delete file
		if (approveFile.exists()) {
			approveFile.delete();
		}

		// Delete list
		landNames.clear();
		approveConfig = new YamlConfiguration();

		// Reload file
		loadFile();
	}

	/**
	 * Load file.
	 */
	private void loadFile() {

		Factoid.getThisPlugin().iLog().write("Loading Approve list file");

		if (!approveFile.exists()) {
			try {
				approveFile.createNewFile();
			} catch (final IOException ex) {
				Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file creation", ex);
			}
		}
		try {
			approveConfig.load(approveFile);
		} catch (final IOException ex) {
			Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
		} catch (final InvalidConfigurationException ex) {
			Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
		}

		// add land names to list
		for (final String landName : approveConfig.getKeys(false)) {
			landNames.add(landName);
		}
	}

	/**
	 * Save file.
	 */
	private void saveFile() {

		Factoid.getThisPlugin().iLog().write("Saving Approve list file");

		try {
			approveConfig.save(approveFile);
		} catch (final IOException ex) {
			Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file save", ex);
		}
	}
}
