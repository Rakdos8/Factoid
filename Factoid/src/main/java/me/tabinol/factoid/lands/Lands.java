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
package me.tabinol.factoid.lands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.areas.AreaIndex;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;
import me.tabinol.factoidapi.event.LandDeleteEvent;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.ILands;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.parameters.IFlagType;
import me.tabinol.factoidapi.parameters.IFlagValue;
import me.tabinol.factoidapi.parameters.IPermissionType;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

/**
 * The Class Lands.
 */
public class Lands implements ILands {

	/** The Constant INDEX_X1. */
	public final static int INDEX_X1 = 0;

	/** The Constant INDEX_Z1. */
	public final static int INDEX_Z1 = 1;

	/** The Constant INDEX_X2. */
	public final static int INDEX_X2 = 2;

	/** The Constant INDEX_Z2. */
	public final static int INDEX_Z2 = 3;

	/** The area list. */
	private final TreeMap<String, TreeSet<AreaIndex>>[] areaList; // INDEX first, Tree by worlds (then by Areas)

	/** The land uuid list. */
	private final TreeMap<UUID, ILand> landUUIDList; // Lands by UUID;

	/** The land list. */
	private final TreeMap<String, ILand> landList; // Tree by name

	/** The outside area. */
	protected TreeMap<String, DummyLand> outsideArea; // Outside a Land (in specific worlds)

	private final DummyLand defaultConfNoType; // Default config (Type not exist or Type null)

	/** The default conf. */
	private final TreeMap<IType, DummyLand> defaultConf; // Default config of a land

	/** The pm. */
	private final PluginManager pm;

	/** The approve list. */
	private final ApproveList approveList;

	/**  List of forSale. */
	private final HashSet<ILand> forSale;

	/**  List of forRent and rented. */
	private final HashSet<ILand> forRent;

	/**
	 * Instantiates a new lands.
	 */
	public Lands() {
		areaList = new TreeMap[4];
		pm = Factoid.getThisPlugin().getServer().getPluginManager();
		for (int t = 0; t < areaList.length; t++) {
			areaList[t] = new TreeMap<>();
		}
		final WorldConfig worldConfig = new WorldConfig();

		// Load World Config
		this.outsideArea = worldConfig.getLandOutsideArea();

		// Load Land default
		this.defaultConf = worldConfig.getTypeDefaultConf();
		this.defaultConfNoType = worldConfig.getDefaultconfNoType();

		landList = new TreeMap<>();
		landUUIDList = new TreeMap<>();
		approveList = new ApproveList();
		forSale = new HashSet<>();
		forRent = new HashSet<>();
	}

	public DummyLand getDefaultConf(final IType type) {
		final DummyLand land;

		// No type? Return default config
		if (type == null) {
			return defaultConfNoType;
		}

		land = defaultConf.get(type);

		// Type not found? Return default config
		if (land == null) {
			return defaultConfNoType;
		}

		return land;
	}

	/**
	 * Gets the approve list.
	 *
	 * @return the approve list
	 */
	public ApproveList getApproveList() {
		return approveList;
	}

	// For Land with no parent
	/**
	 * Creates the land.
	 *
	 * @param landName the land name
	 * @param owner the owner
	 * @param area the area
	 * @return the land
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public Land createLand(final String landName, final IPlayerContainer owner, final ICuboidArea area)
			throws FactoidLandException {
		return createLand(landName, owner, area, null, 1, null);
	}

	// For Land with parent
	/**
	 * Creates the land.
	 *
	 * @param landName the land name
	 * @param owner the owner
	 * @param area the area
	 * @param parent the parent
	 * @return the land
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public Land createLand(final String landName, final IPlayerContainer owner, final ICuboidArea area,
			final me.tabinol.factoidapi.lands.ILand parent)
			throws FactoidLandException {
		return createLand(landName, owner, area, parent, 1, null);
	}

	// For Land with parent and price
	/**
	 * Creates the land.
	 *
	 * @param landName the land name
	 * @param owner the owner
	 * @param area the area
	 * @param parent the parent
	 * @param price the price
	 * @param type the type
	 * @return the land
	 * @throws FactoidLandException the factoid land exception
	 */
	public Land createLand(
			final String landName,
			final IPlayerContainer owner,
			final ICuboidArea area,
			final me.tabinol.factoidapi.lands.ILand parent,
			final double price,
			final IType type
	) throws FactoidLandException {
		getPriceFromPlayer(area.getWorldName(), owner, price);
		return createLand(landName, owner, area, parent, 1, null, type);
	}

	// Only for Land load at start
	/**
	 * Creates the land.
	 *
	 * @param landName the land name
	 * @param owner the owner
	 * @param area the area
	 * @param parent the parent
	 * @param areaId the area id
	 * @param uuid the uuid
	 * @param type the type
	 * @return the land
	 * @throws FactoidLandException the factoid land exception
	 */
	public Land createLand(
			final String landName,
			final IPlayerContainer owner,
			final ICuboidArea area,
			final me.tabinol.factoidapi.lands.ILand parent,
			final int areaId,
			final UUID uuid,
			final IType type
	) throws FactoidLandException {
		final String landNameLower = landName.toLowerCase();
		final UUID landUUID = uuid == null
				? UUID.randomUUID()
				: uuid;
		final int genealogy = parent != null
				? parent.getGenealogy() + 1
				: 0;

		if (isNameExist(landName)) {
			throw new FactoidLandException(
					landName,
					(CuboidArea) area,
					LandAction.LAND_ADD,
					LandError.NAME_IN_USE
			);
		}

		final Land land = new Land(landNameLower, landUUID, owner, area, genealogy, (Land) parent, areaId, type);

		addLandToList(land);
		Factoid.getThisPlugin().iLog().write("add land: " + landNameLower);

		return land;
	}

	/**
	 * Checks if is name exist.
	 *
	 * @param landName the land name
	 * @return true, if is name exist
	 */
	@Override
	public boolean isNameExist(final String landName) {
		return landList.containsKey(landName.toLowerCase());
	}

	/**
	 * Removes the land.
	 *
	 * @param land the land
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean removeLand(final ILand land) throws FactoidLandException {
		if (land == null) {
			return false;
		}

		final LandDeleteEvent landEvent = new LandDeleteEvent(land);

		if (!landList.containsKey(land.getName())) {
			return false;
		}

		// If the land has children
		if (!land.getChildren().isEmpty()) {
			throw new FactoidLandException(land.getName(), null, LandAction.LAND_REMOVE, LandError.HAS_CHILDREN);
		}

		// Call Land Event and check if it is cancelled
		pm.callEvent(landEvent);

		if (landEvent.isCancelled()) {
			return false;
		}

		removeLandFromList((Land) land);
		if (land.getParent() != null) {
			((Land) land.getParent()).removeChild(land.getUUID());
		}
		Factoid.getThisPlugin().iStorageThread().removeLand((Land) land);
		Factoid.getThisPlugin().iLog().write("remove land: " + land);
		return true;
	}

	/**
	 * Removes the land.
	 *
	 * @param landName the land name
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean removeLand(final String landName) throws FactoidLandException {
		return removeLand(landList.get(landName.toLowerCase()));
	}

	/**
	 * Removes the land.
	 *
	 * @param uuid the uuid
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean removeLand(final UUID uuid) throws FactoidLandException {
		return removeLand(landUUIDList.get(uuid));
	}

	/**
	 * Rename land.
	 *
	 * @param landName the land name
	 * @param newName the new name
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean renameLand(final String landName, final String newName) throws FactoidLandException {
		final Land land = getLand(landName);

		if (land != null) {
			return renameLand(land, newName);
		}
		return false;
	}

	/**
	 * Rename land.
	 *
	 * @param uuid the uuid
	 * @param newName the new name
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean renameLand(final UUID uuid, final String newName) throws FactoidLandException {
		final Land land = getLand(uuid);

		if (land != null) {
			return renameLand(land, newName);
		}
		return false;
	}

	/**
	 * Rename land.
	 *
	 * @param land the land
	 * @param newName the new name
	 * @return true, if successful
	 * @throws FactoidLandException the factoid land exception
	 */
	@Override
	public boolean renameLand(final ILand land, final String newName)
			throws FactoidLandException {
		final String newNameLower = newName.toLowerCase();

		if (isNameExist(newNameLower)) {
			throw new FactoidLandException(newNameLower, null, LandAction.LAND_RENAME, LandError.NAME_IN_USE);
		}

		final Land uglyCast = (Land) land;
		uglyCast.setName(newNameLower);
		land.forceSave();

		// Removes the old name and put the new one into the Map
		landList.remove(land.getName());
		addLandToList(uglyCast);
		return true;
	}

	/**
	 * Gets the land.
	 *
	 * @param landName the land name
	 * @return the land
	 */
	@Override
	public Land getLand(final String landName) {
		return (Land) landList.get(landName.toLowerCase());
	}

	/**
	 * Gets the land.
	 *
	 * @param uuid the uuid
	 * @return the land
	 */
	@Override
	public Land getLand(final UUID uuid) {
		return (Land) landUUIDList.get(uuid);
	}

	/**
	 * Gets the land.
	 *
	 * @param loc the loc
	 * @return the land
	 */
	@Override
	public Land getLand(final Location loc) {
		final ICuboidArea ca;

		if ((ca = getCuboidArea(loc)) == null) {
			return null;
		}
		return (Land) ca.getLand();
	}

	/**
	 * Gets the lands.
	 *
	 * @return the lands
	 */
	@Override
	public Collection<ILand> getLands() {
		return landList.values();
	}

	/**
	 * Gets the land or outside area.
	 *
	 * @param loc the loc
	 * @return the land or outside area
	 */
	@Override
	public DummyLand getLandOrOutsideArea(final Location loc) {
		final Land land = getLand(loc);
		if (land != null) {
			return land;
		}

		return getOutsideArea(loc);
	}

	/**
	 * Gets the outside area.
	 *
	 * @param loc the loc
	 * @return the outside area
	 */
	@Override
	public DummyLand getOutsideArea(final Location loc) {
		return getOutsideArea(loc.getWorld().getName());
	}

	/**
	 * Gets the outside area.
	 *
	 * @param worldName the world name
	 * @return the outside area
	 */
	@Override
	public DummyLand getOutsideArea(final String worldName) {
		final String worldNameLower = worldName.toLowerCase();

		// Not exist, create one
		if (!outsideArea.containsKey(worldNameLower)) {
			final DummyLand dummyLand = new DummyLand(worldNameLower);
			outsideArea.get(Config.GLOBAL).copyPermsFlagsTo(dummyLand);
			outsideArea.put(worldNameLower, dummyLand);
		}
		return outsideArea.get(worldNameLower);
	}

	/**
	 * Gets the lands.
	 *
	 * @param loc the loc
	 * @return the lands
	 */
	@Override
	public Collection<ILand> getLands(final Location loc) {
		final Collection<ICuboidArea> areas = getCuboidAreas(loc);
		final HashMap<String, ILand>
			lands = new HashMap<>();

		for (final ICuboidArea area : areas) {
			lands.put(area.getLand().getName(), area.getLand());
		}

		return lands.values();
	}

	/**
	 * Gets the lands.
	 *
	 * @param owner the owner
	 * @return the lands
	 */
	@Override
	public Collection<ILand> getLands(final IPlayerContainer owner) {
		final Collection<ILand>
			lands = new HashSet<>();

		for (final ILand land : landList.values()) {
			if (land.getOwner().equals(owner)) {
				lands.add(land);
			}
		}

		return lands;
	}

	/**
	 * Gets the lands from type.
	 *
	 * @param type the type
	 * @return the lands
	 */
	@Override
	public Collection<ILand> getLands(final IType type) {
		final Collection<ILand>
		lands = new HashSet<>();

		for (final ILand land : landList.values()) {
			if (land.getType() == type) {
				lands.add(land);
			}
		}

		return lands;
	}

	/**
	 * Gets the price from player.
	 *
	 * @param worldName the world name
	 * @param pc the pc
	 * @param price the price
	 * @return the price from player
	 */
	protected boolean getPriceFromPlayer(final String worldName, final IPlayerContainer pc, final double price) {
		if (pc.getContainerType() == EPlayerContainerType.PLAYER && price > 0) {
			return Factoid.getThisPlugin().iPlayerMoney()
					.getFromPlayer(
							((IPlayerContainerPlayer)pc).getOfflinePlayer(),
							worldName,
							price
					);
		}
		return true;
	}

	/**
	 * Gets the permission in world.
	 *
	 * @param worldName the world name
	 * @param player the player
	 * @param pt the pt
	 * @param onlyInherit the only inherit
	 * @return the permission in world
	 */
	protected boolean getPermissionInWorld(final String worldName, final Player player, final IPermissionType pt, final boolean onlyInherit) {
		final DummyLand dl = outsideArea.get(worldName.toLowerCase());
		if (dl != null) {
			final Boolean value = dl.getPermission(player, pt, onlyInherit);
			return value != null
					? value
					: pt.getDefaultValue();
		}
		return pt.getDefaultValue();
	}

	/**
	 * Gets the flag in world.
	 *
	 * @param worldName the world name
	 * @param ft the ft
	 * @param onlyInherit the only inherit
	 * @return the flag value in world
	 */
	protected IFlagValue getFlagInWorld(final String worldName, final IFlagType ft, final boolean onlyInherit) {
		final DummyLand dl = outsideArea.get(worldName.toLowerCase());
		if (dl != null) {
			final IFlagValue value = dl.getFlag(ft, onlyInherit);
			return value != null
					? value
					: ft.getDefaultValue();
		}
		return ft.getDefaultValue();
	}

	/**
	 * Gets the cuboid areas.
	 *
	 * @param loc the loc
	 * @return the cuboid areas
	 */
	@Override
	public Collection<ICuboidArea> getCuboidAreas(final Location loc) {
		final Collection<ICuboidArea> areas = new ArrayList<>();
		final String worldName = loc.getWorld().getName();
		final int SearchIndex;
		final int nbToFind;
		final boolean ForwardSearch;
		final TreeSet<AreaIndex> ais;
		AreaIndex ai;
		final Iterator<AreaIndex> it;

		// First, determinate if what is the highest number between x1, x2, z1 and z2
		if (Math.abs(loc.getBlockX()) > Math.abs(loc.getBlockZ())) {
			nbToFind = loc.getBlockX();
			if (loc.getBlockX() < 0) {
				SearchIndex = INDEX_X1;
				ForwardSearch = true;
			} else {
				SearchIndex = INDEX_X2;
				ForwardSearch = false;
			}
		} else {
			nbToFind = loc.getBlockZ();
			if (loc.getBlockZ() < 0) {
				SearchIndex = INDEX_Z1;
				ForwardSearch = true;
			} else {
				SearchIndex = INDEX_Z2;
				ForwardSearch = false;
			}
		}
		Factoid.getThisPlugin().iLog().write("Search Index dir: " + SearchIndex + ", Forward Search: " + ForwardSearch);

		// Now check for area in location
		ais = areaList[SearchIndex].get(worldName);
		if (ais == null || ais.isEmpty()) {
			return areas;
		}
		if (ForwardSearch) {
			it = ais.iterator();
		} else {
			it = ais.descendingIterator();
		}

		// Adds all areas to the list
		while (it.hasNext() && checkContinueSearch((ai = it.next()).getArea(), nbToFind, SearchIndex)) {
			if (ai.getArea().isLocationInside(loc)) {
				Factoid.getThisPlugin().iLog().write("add this area in list for cuboid: " + ai.getArea().getLand().getName());
				areas.add(ai.getArea());
			}
		}
		Factoid.getThisPlugin().iLog().write("Number of Areas found for location : " + areas.size());

		return areas;
	}

	/**
	 * Gets the cuboid area.
	 *
	 * @param loc the loc
	 * @return the cuboid area
	 */
	@Override
	public ICuboidArea getCuboidArea(final Location loc) {
		int actualPrio = Short.MIN_VALUE;
		int curPrio;
		int actualGen = 0;
		int curGen;
		ICuboidArea actualArea = null;
		final Location resLoc; // Resolved location

		// Give the position from the sky to underbedrock if the Y is greater than 255 or lower than 0
		if (loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
			resLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMaxHeight() - 1, loc.getZ());
		} else if (loc.getBlockY() < 0){
			resLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
		} else {
			resLoc = loc;
		}

		final Collection<ICuboidArea> areas = getCuboidAreas(resLoc);

		Factoid.getThisPlugin().iLog().write("Area check in" + resLoc.toString());

		// Compare priorities of parents (or main)
		for (final ICuboidArea area : areas) {
			Factoid.getThisPlugin().iLog().write("Check for: " + area.getLand().getName()
					+ ", area: " + area.toString());

			curPrio = area.getLand().getPriority();
			curGen = area.getLand().getGenealogy();

			if (actualPrio < curPrio
					|| (actualPrio == curPrio && actualGen <= curGen)) {
				actualArea = area;
				actualPrio = curPrio;
				actualGen = area.getLand().getGenealogy();

				Factoid.getThisPlugin().iLog().write("Found, update:  actualPrio: " + actualPrio + ", actualGen: " + actualGen);
			}
		}

		return actualArea;
	}

	/**
	 * Check continue search.
	 *
	 * @param area the area
	 * @param nbToFind the nb to find
	 * @param SearchIndex the search index
	 * @return true, if successful
	 */
	private boolean checkContinueSearch(final ICuboidArea area, final int nbToFind, final int SearchIndex) {
		switch (SearchIndex) {
			case INDEX_X1:
				if (nbToFind >= area.getX1()) {
					return true;
				}
				return false;
			case INDEX_X2:
				if (nbToFind <= area.getX2()) {
					return true;
				}
				return false;
			case INDEX_Z1:
				if (nbToFind >= area.getZ1()) {
					return true;
				}
				return false;
			case INDEX_Z2:
				if (nbToFind <= area.getZ2()) {
					return true;
				}
				return false;
			default:
				return false;
		}
	}

	/**
	 * Adds the area to list.
	 *
	 * @param area the area
	 */
	protected void addAreaToList(final ICuboidArea area) {
		if (!areaList[0].containsKey(area.getWorldName())) {
			for (int t = 0; t < 4; t++) {
				areaList[t].put(area.getWorldName(), new TreeSet<>());
			}
		}
		Factoid.getThisPlugin().iLog().write("Add area for " + area.getLand().getName());
		areaList[INDEX_X1].get(area.getWorldName()).add(new AreaIndex(area.getX1(), area));
		areaList[INDEX_Z1].get(area.getWorldName()).add(new AreaIndex(area.getZ1(), area));
		areaList[INDEX_X2].get(area.getWorldName()).add(new AreaIndex(area.getX2(), area));
		areaList[INDEX_Z2].get(area.getWorldName()).add(new AreaIndex(area.getZ2(), area));
	}

	/**
	 * Removes the area from list.
	 *
	 * @param area the area
	 */
	protected void removeAreaFromList(final ICuboidArea area) {
		areaList[INDEX_X1].get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
		areaList[INDEX_Z1].get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
		areaList[INDEX_X2].get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
		areaList[INDEX_Z2].get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
	}

	/**
	 * Adds the land to list.
	 *
	 * @param land the land
	 */
	private void addLandToList(final Land land) {
		landList.put(land.getName(), land);
		landUUIDList.put(land.getUUID(), land);
	}

	/**
	 * Removes the land from list.
	 *
	 * @param land the land
	 */
	private void removeLandFromList(final Land land) {
		landList.remove(land.getName());
		landUUIDList.remove(land.getUUID());
		for (final ICuboidArea area : land.getAreas()) {
			removeAreaFromList(area);
		}
	}

	/**
	 * Adds the for sale.
	 *
	 * @param land the land
	 */
	protected void addForSale(final Land land) {
		forSale.add(land);
	}

	/**
	 * Removes the for sale.
	 *
	 * @param land the land
	 */
	protected void removeForSale(final Land land) {
		forSale.remove(land);
	}

	/**
	 * Gets the for sale.
	 *
	 * @return the for sale
	 */
	@Override
	public Collection<ILand> getForSale() {
		return forSale;
	}

	/**
	 * Adds the for rent.
	 *
	 * @param land the land
	 */
	protected void addForRent(final Land land) {
		forRent.add(land);
	}

	/**
	 * Removes the for rent.
	 *
	 * @param land the land
	 */
	protected void removeForRent(final Land land) {
		forRent.remove(land);
	}

	/**
	 * Gets the for rent.
	 *
	 * @return the for rent
	 */
	@Override
	public Collection<ILand> getForRent() {
		return forRent;
	}
}
