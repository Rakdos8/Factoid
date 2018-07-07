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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoidapi.event.LandModifyEvent;
import me.tabinol.factoidapi.event.LandModifyEvent.LandModifyReason;
import me.tabinol.factoidapi.event.PlayerContainerLandBanEvent;
import me.tabinol.factoidapi.factions.IFaction;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.parameters.IFlagType;
import me.tabinol.factoidapi.parameters.IFlagValue;
import me.tabinol.factoidapi.parameters.IPermissionType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

/**
 * The Class Land.
 */
public class Land extends DummyLand implements ILand {

	/** The Constant DEFAULT_PRIORITY. */
	public static final short DEFAULT_PRIORITY = 10;

	/** The Constant MINIM_PRIORITY. */
	public static final short MINIM_PRIORITY = 0;

	/** The Constant MAXIM_PRIORITY. */
	public static final short MAXIM_PRIORITY = 100;

	/** The uuid. */
	private final UUID uuid;

	/** The name. */
	private String name;

	/** The type. */
	private IType type = null;

	/** The areas. */
	private final Map<Integer, ICuboidArea> areas = new TreeMap<>();

	/** The children. */
	private final Map<UUID, ILand> children = new TreeMap<>();

	/** The priority. */
	private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!

	/** The genealogy. */
	private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...

	/** The parent. */
	private ILand parent = null;

	/** The owner. */
	private IPlayerContainer owner;

	/** The residents. */
	private Set<IPlayerContainer> residents = new TreeSet<>();

	/** The banneds. */
	private final Set<IPlayerContainer> banneds = new TreeSet<>();

	/** The auto save. */
	private boolean autoSave = true;

	/** The faction territory. */
	private IFaction factionTerritory = null;

	/** The money. */
	private double money = 0L;

	/** The player notify. */
	private Set<IPlayerContainerPlayer> playerNotify = new TreeSet<>();

	/** The players in land. */
	private final Set<Player> playersInLand = new HashSet<>();
	// Economy
	/** The for sale. */
	private boolean forSale = false;

	/** The for sale sign location */
	private Location forSaleSignLoc = null;

	/** The sale price. */
	private double salePrice = 0;

	/** The for rent. */
	private boolean forRent = false;

	/** The for rent sign location */
	private Location forRentSignLoc = null;

	/** The rent price. */
	private double rentPrice = 0;

	/** The rent renew. */
	private int rentRenew = 0; // How many days before renew?

	/** The rent auto renew. */
	private boolean rentAutoRenew = false;

	/** The rented. */
	private boolean rented = false;

	/** The tenant. */
	private IPlayerContainerPlayer tenant = null;

	/** The last payment. */
	private Timestamp lastPayment = new Timestamp(0);

	// Please use createLand in Lands class to create a Land
	/**
	 * Instantiates a new land.
	 *
	 * @param landName the land name
	 * @param uuid the uuid
	 * @param owner the owner
	 * @param area the area
	 * @param genealogy the genealogy
	 * @param parent the parent
	 * @param areaId the area id
	 * @param type the type
	 */
	protected Land(final String landName, final UUID uuid, final IPlayerContainer owner,
			final ICuboidArea area, final int genealogy, final Land parent, final int areaId, final IType type) {

		super(area.getWorldName().toLowerCase());
		this.uuid = uuid;
		name = landName.toLowerCase();
		this.type = type;
		if (parent != null) {
			this.parent = parent;
			parent.addChild(this);
			this.factionTerritory = parent.factionTerritory;
		}
		this.owner = owner;
		this.genealogy = genealogy;
		addArea(area, areaId);
	}

	/**
	 * Sets the default.
	 */
	@Override
	public void setDefault() {
		owner = new PlayerContainerNobody();
		residents = new TreeSet<>();
		playerNotify = new TreeSet<>();
		permissions.clear();
		flags.clear();
		doSave();
	}


	/**
	 * Adds the area.
	 *
	 * @param area the area
	 */
	@Override
	public void addArea(final ICuboidArea area) {

		int nextKey = 0;

		if (areas.isEmpty()) {
			nextKey = 1;
		} else {
			for (final int key : areas.keySet()) {

				if (nextKey < key) {
					nextKey = key;
				}
			}
			nextKey++;
		}

		addArea(area, nextKey);
	}

	/**
	 * Adds the area.
	 *
	 * @param area the area
	 * @param price the price
	 */
	public void addArea(final ICuboidArea area, final double price) {

		if (price > 0) {
			Factoid.getThisPlugin().iLands().getPriceFromPlayer(worldName, owner, price);
		}
		addArea(area);
	}

	/**
	 * Adds the area.
	 *
	 * @param area the area
	 * @param key the key
	 */
	public void addArea(final ICuboidArea area, final int key) {

		((CuboidArea) area).setLand(this);
		areas.put(key, area);
		Factoid.getThisPlugin().iLands().addAreaToList(area);
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new LandModifyEvent(this, LandModifyReason.AREA_ADD, area));
	}

	/**
	 * Removes the area.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	@Override
	public boolean removeArea(final int key) {

		final ICuboidArea area;

		if ((area = areas.remove(key)) != null) {
			Factoid.getThisPlugin().iLands().removeAreaFromList(area);
			doSave();

			// Start Event
			Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
					new LandModifyEvent(this, LandModifyReason.AREA_REMOVE, area));

			return true;
		}

		return false;
	}

	/**
	 * Removes the area.
	 *
	 * @param area the area
	 * @return true, if successful
	 */
	@Override
	public boolean removeArea(final ICuboidArea area) {

		final Integer key = getAreaKey(area);

		if (key != null) {
			return removeArea(key);
		}

		return false;
	}

	/**
	 * Replace area.
	 *
	 * @param key the key
	 * @param newArea the new area
	 * @param price the price
	 * @return true, if successful
	 */
	public boolean replaceArea(final int key, final ICuboidArea newArea, final double price) {

		if (price > 0) {
			Factoid.getThisPlugin().iLands().getPriceFromPlayer(worldName, owner, price);
		}

		return replaceArea(key, newArea);
	}

	/**
	 * Replace area.
	 *
	 * @param key the key
	 * @param newArea the new area
	 * @return true, if successful
	 */
	@Override
	public boolean replaceArea(final int key, final ICuboidArea newArea) {

		final ICuboidArea area;

		if ((area = areas.remove(key)) != null) {
			Factoid.getThisPlugin().iLands().removeAreaFromList(area);
			((CuboidArea) newArea).setLand(this);
			areas.put(key, newArea);
			Factoid.getThisPlugin().iLands().addAreaToList(newArea);
			doSave();

			// Start Event
			Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
					new LandModifyEvent(this, LandModifyReason.AREA_REPLACE, area));

			return true;
		}

		return false;
	}

	/**
	 * Gets the area.
	 *
	 * @param key the key
	 * @return the area
	 */
	@Override
	public ICuboidArea getArea(final int key) {

		return areas.get(key);
	}

	/**
	 * Gets the area key.
	 *
	 * @param area the area
	 * @return the area key
	 */
	@Override
	public Integer getAreaKey(final ICuboidArea area) {

		for (final Map.Entry<Integer, ICuboidArea> entry : areas.entrySet()) {
			if (entry.getValue() == area) {
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * Gets the areas key.
	 *
	 * @return the areas key
	 */
	@Override
	public Set<Integer> getAreasKey() {

		return areas.keySet();
	}

	/**
	 * Gets the ids and areas.
	 *
	 * @return the ids and areas
	 */
	@Override
	public Map<Integer, ICuboidArea> getIdsAndAreas() {

		return areas;
	}

	/**
	 * Gets the areas.
	 *
	 * @return the areas
	 */
	@Override
	public Collection<ICuboidArea> getAreas() {

		return areas.values();
	}

	/**
	 * Checks if is location inside.
	 *
	 * @param loc the loc
	 * @return true, if is location inside
	 */
	@Override
	public boolean isLocationInside(final Location loc) {
		for (final ICuboidArea area1 : areas.values()) {
			if (area1.isLocationInside(loc)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the nb blocks outside.
	 *
	 * @param areaComp the area comp
	 * @return the nb blocks outside
	 */
	@Override
	public long getNbBlocksOutside(final ICuboidArea areaComp) {
		// Get the Volume of the area
		long volume = areaComp.getTotalBlock();

		// Put the list of areas in the land to an array
		final ICuboidArea[] areaAr = areas.values().toArray(new ICuboidArea[0]);

		for (int t = 0; t < areaAr.length; t++) {

			// Get the result collision cuboid
			final ICuboidArea colArea = areaAr[t].getCollisionArea(areaComp);

			if (colArea != null) {

				// Substract the volume of collision
				volume -= colArea.getTotalBlock();

				// Compare each next areas to the collision area and add
				// the collision of the collision to cancel multiple subtracts
				for (int a = t + 1; a < areaAr.length; a++) {

					final ICuboidArea colAreaToNextArea = areaAr[a].getCollisionArea(colArea);

					if (colAreaToNextArea != null) {
						volume += colAreaToNextArea.getTotalBlock();
					}
				}
			}
		}

		return volume;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {

		return name;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	@Override
	public UUID getUUID() {

		return uuid;
	}

	/**
	 * Sets the name.
	 *
	 * @param newName the new name
	 */
	protected void setName(final String newName) {

		setAutoSave(false);
		Factoid.getThisPlugin().iStorageThread().removeLand(this);
		this.name = newName;
		setAutoSave(true);
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new LandModifyEvent(this, LandModifyReason.RENAME, name));
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	@Override
	public IPlayerContainer getOwner() {

		return owner;
	}

	/**
	 * Checks if is owner.
	 *
	 * @param player the player
	 * @return true, if is owner
	 */
	@Override
	public boolean isOwner(final Player player) {

		return owner.hasAccess(player);
	}

	/**
	 * Gets the faction territory.
	 *
	 * @return the faction territory
	 */
	@Override
	public IFaction getFactionTerritory() {

		return factionTerritory;
	}

	/**
	 * Sets the faction territory.
	 *
	 * @param faction the new faction territory
	 */
	@Override
	public void setFactionTerritory(final IFaction faction) {

		this.factionTerritory = faction;
		for (final ILand child : children.values()) {
			child.setFactionTerritory(faction);
		}
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new LandModifyEvent(this, LandModifyReason.FACTION_TERRITORY_CHANGE, faction));
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	@Override
	public void setOwner(final IPlayerContainer owner) {

		this.owner = owner;
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new LandModifyEvent(this, LandModifyReason.OWNER_CHANGE, owner));
	}

	/**
	 * Adds the resident.
	 *
	 * @param resident the resident
	 */
	@Override
	public void addResident(final IPlayerContainer resident) {

		((PlayerContainer) resident).setLand(this);
		residents.add(resident);
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new LandModifyEvent(this, LandModifyReason.RESIDENT_ADD, resident));
	}

	/**
	 * Removes the resident.
	 *
	 * @param resident the resident
	 * @return true, if successful
	 */
	@Override
	public boolean removeResident(final IPlayerContainer resident) {

		if (residents.remove(resident)) {
			doSave();

			// Start Event
			Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
					new LandModifyEvent(this, LandModifyReason.RESIDENT_REMOVE, resident));

			return true;
		}

		return false;
	}

	/**
	 * Gets the residents.
	 *
	 * @return the residents
	 */
	@Override
	public final Set<IPlayerContainer> getResidents() {

		return residents;
	}

	/**
	 * Checks if is resident.
	 *
	 * @param player the player
	 * @return true, if is resident
	 */
	@Override
	public boolean isResident(final Player player) {

		for (final IPlayerContainer resident : residents) {
			if (resident.hasAccess(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the banned.
	 *
	 * @param banned the banned
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void addBanned(final IPlayerContainer banned) {

		((PlayerContainer) banned).setLand(this);
		banneds.add(banned);
		doSave();

		// Start Event
		Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
				new PlayerContainerLandBanEvent(this, banned));
	}

	/**
	 * Removes the banned.
	 *
	 * @param banned the banned
	 * @return true, if successful
	 */
	@Override
	public boolean removeBanned(final IPlayerContainer banned) {

		if (banneds.remove(banned)) {
			doSave();
			return true;
		}

		return false;
	}

	/**
	 * Gets the banneds.
	 *
	 * @return the banneds
	 */
	@Override
	public final Set<IPlayerContainer> getBanneds() {

		return banneds;
	}

	/**
	 * Checks if is banned.
	 *
	 * @param player the player
	 * @return true, if is banned
	 */
	@Override
	public boolean isBanned(final Player player) {

		for (final IPlayerContainer banned : banneds) {
			if (banned.hasAccess(player)) {
				return true;
			}
		}
		return false;
	}

	// Note : a child get the parent priority
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public short getPriority() {

		if (parent != null) {
			return parent.getPriority();
		}

		return priority;
	}

	/**
	 * Gets the genealogy.
	 *
	 * @return the genealogy
	 */
	@Override
	public int getGenealogy() {

		return genealogy;
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority the new priority
	 */
	@Override
	public void setPriority(final short priority) {

		this.priority = priority;
		doSave();
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	@Override
	public Land getParent() {

		return (Land) parent;
	}

	@Override
	public void setParent(final ILand newParent) {

		// Remove files
		removeChildFiles();
		Factoid.getThisPlugin().iStorageThread().removeLand(name, genealogy);

		// remove parent (if needed)
		if (parent != null) {
			((Land)parent).removeChild(uuid);
			parent = null;
			genealogy = 0;
			Factoid.getThisPlugin().iLog().write("remove parent from land: " + name);
		}

		// Add parent
		if (newParent != null) {
			((Land)newParent).addChild(this);
			parent = newParent;
			priority = parent.getPriority();
			genealogy = parent.getGenealogy() + 1;
			Factoid.getThisPlugin().iLog().write("add parent " + parent.getName() + " to land: " + name);
		}

		// Save
		doSave();

		// Save children files
		saveChildFiles();
	}

	private void removeChildFiles() {

		for (final ILand child : children.values()) {
			child.setAutoSave(false);
			Factoid.getThisPlugin().iStorageThread().removeLand((Land)child);
			((Land)child).removeChildFiles();
		}
	}

	private void saveChildFiles() {

		for (final ILand child : children.values()) {
			child.setPriority(priority);
			((Land)child).genealogy = genealogy + 1;
			child.setAutoSave(true);
			child.forceSave();
			((Land)child).saveChildFiles();
		}
	}

	/**
	 * Gets the ancestor.
	 *
	 * @param gen the gen
	 * @return the ancestor
	 */
	@Override
	public Land getAncestor(final int gen) { // 1 parent, 2 grand-parent, 3 ...

		Land ancestor = this;

		for (int t = 0; t < gen; t++) {
			ancestor = ancestor.getParent();
		}

		return ancestor;
	}

	/**
	 * Checks if is descendants.
	 *
	 * @param land the land
	 * @return true, if is descendants
	 */
	@Override
	public boolean isDescendants(final ILand land) {

		if (land == this) {
			return true;
		}

		for (final ILand landT : children.values()) {
			if (landT.isDescendants(land) == true) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds the child.
	 *
	 * @param land the land
	 */
	private void addChild(final Land land) {

		children.put(land.uuid, land);
		doSave();
	}

	/**
	 * Removes the child.
	 *
	 * @param uuid the uuid
	 */
	protected void removeChild(final UUID uuid) {

		children.remove(uuid);
		doSave();
	}

	/**
	 * Gets the child.
	 *
	 * @param uuid the uuid
	 * @return the child
	 */
	@Override
	public Land getChild(final UUID uuid) {

		return (Land) children.get(uuid);
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	@Override
	public Collection<ILand> getChildren() {

		return children.values();
	}

	/**
	 * Sets the auto save.
	 *
	 * @param autoSave the new auto save
	 */
	@Override
	public void setAutoSave(final boolean autoSave) {

		this.autoSave = autoSave;
	}

	/**
	 * Force save.
	 */
	@Override
	public void forceSave() {

		Factoid.getThisPlugin().iStorageThread().saveLand(this);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.lands.DummyLand#doSave()
	 */
	@Override
	protected void doSave() {

		if (autoSave) {
			forceSave();
		}
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.lands.ILand#addPermission(me.tabinol.factoidapi.playercontainer.IPlayerContainer, me.tabinol.factoidapi.parameters.IPermissionType, boolean, boolean)
	 */
	@Override
	public void addPermission(final IPlayerContainer pc, final IPermissionType permType,
			final boolean value, final boolean inheritance) {

		addPermission(pc, new Permission((PermissionType) permType, value, inheritance));
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.lands.ILand#addFlag(me.tabinol.factoidapi.parameters.IFlagType, java.lang.Object, boolean)
	 */
	@Override
	public void addFlag(final IFlagType flagType, final Object value, final boolean inheritance) {

		addFlag(new LandFlag((FlagType) flagType, value, inheritance));
	}

	/**
	 * Check land permission and inherit.
	 *
	 * @param player the player
	 * @param pt the pt
	 * @param onlyInherit the only inherit
	 * @return the boolean
	 */
	protected Boolean checkLandPermissionAndInherit(final Player player, final IPermissionType pt, final boolean onlyInherit) {
		if (isBanned(player)) {
			return false;
		}

		final Boolean permValue = getPermission(player, pt, onlyInherit);
		if (permValue != null) {
			return permValue;
		} else if (parent != null) {
			return ((Land) parent).checkPermissionAndInherit(player, pt, true);
		}

		return Factoid.getThisPlugin().iLands().getPermissionInWorld(worldName, player, pt, true);
	}

	/**
	 * Gets the land flag and inherit.
	 *
	 * @param ft the ft
	 * @param onlyInherit the only inherit
	 * @return the land flag value
	 */
	protected IFlagValue getLandFlagAndInherit(final IFlagType ft, final boolean onlyInherit) {

		final IFlagValue flagValue;

		if ((flagValue = getFlag(ft, onlyInherit)) != null) {
			return flagValue;
		} else if (parent != null) {
			return ((Land) parent).getFlagAndInherit(ft, true);
		}

		return Factoid.getThisPlugin().iLands().getFlagInWorld(worldName, ft, true);
	}

	/**
	 * Adds the money.
	 *
	 * @param money the money
	 */
	@Override
	public void addMoney(final double money) {

		this.money += money;
		doSave();
	}

	/**
	 * Substract money.
	 *
	 * @param money the money
	 */
	@Override
	public void substractMoney(final double money) {

		this.money -= money;
		doSave();
	}

	/**
	 * Gets the money.
	 *
	 * @return the money
	 */
	@Override
	public double getMoney() {

		return money;
	}

	/**
	 * Adds the player notify.
	 *
	 * @param player the player
	 */
	@Override
	public void addPlayerNotify(final IPlayerContainerPlayer player) {

		playerNotify.add(player);
		doSave();
	}

	/**
	 * Removes the player notify.
	 *
	 * @param player the player
	 * @return true, if successful
	 */
	@Override
	public boolean removePlayerNotify(final IPlayerContainerPlayer player) {

		final boolean ret = playerNotify.remove(player);
		doSave();

		return ret;
	}

	/**
	 * Checks if is player notify.
	 *
	 * @param player the player
	 * @return true, if is player notify
	 */
	@Override
	public boolean isPlayerNotify(final IPlayerContainerPlayer player) {

		return playerNotify.contains(player);
	}

	/**
	 * Gets the players notify.
	 *
	 * @return the players notify
	 */
	@Override
	public Set<IPlayerContainerPlayer> getPlayersNotify() {

		return playerNotify;
	}

	/**
	 * Adds the player in land.
	 *
	 * @param player the player
	 */
	public void addPlayerInLand(final Player player) {

		playersInLand.add(player);
	}

	/**
	 * Removes the player in land.
	 *
	 * @param player the player
	 * @return true, if successful
	 */
	public boolean removePlayerInLand(final Player player) {

		return playersInLand.remove(player);
	}

	// No parent verify
	/**
	 * Checks if is player in land.
	 *
	 * @param player the player
	 * @return true, if is player in land
	 */
	@Override
	public boolean isPlayerInLand(final Player player) {

		return playersInLand.contains(player);
	}

	/**
	 * Checks if is playerin land no vanish.
	 *
	 * @param player the player
	 * @param fromPlayer the from player
	 * @return true, if is playerin land no vanish
	 */
	@Override
	public boolean isPlayerinLandNoVanish(final Player player, final Player fromPlayer) {

		if (playersInLand.contains(player)
				&& (!Factoid.getThisPlugin().iPlayerConf().isVanished(player)
						|| Factoid.getThisPlugin().iPlayerConf().get(fromPlayer).isAdminMod())) {
			return true;
		}

		// Check Chidren
		for (final ILand landChild : children.values()) {
			if (landChild.isPlayerinLandNoVanish(player, fromPlayer)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the players in land.
	 *
	 * @return the players in land
	 */
	@Override
	public Set<Player> getPlayersInLand() {

		return playersInLand;
	}

	/**
	 * Gets the players in land and children.
	 *
	 * @return the players in land and children
	 */
	@Override
	public Set<Player> getPlayersInLandAndChildren() {

		final Set<Player> playLandChild = new HashSet<>();

		playLandChild.addAll(playersInLand);

		for (final ILand child : children.values()) {
			playLandChild.addAll(child.getPlayersInLandAndChildren());
		}

		return playLandChild;
	}

	/**
	 * Gets the players in land no vanish.
	 *
	 * @param fromPlayer the from player
	 * @return the players in land no vanish
	 */
	@Override
	public Set<Player> getPlayersInLandNoVanish(final Player fromPlayer) {

		final Set<Player> playerList = new HashSet<>();

		for (final Player player : playersInLand) {
			if (!Factoid.getThisPlugin().iPlayerConf().isVanished(player) || Factoid.getThisPlugin().iPlayerConf().get(fromPlayer).isAdminMod()) {
				playerList.add(player);
			}
		}
		for (final ILand landChild : children.values()) {
			playerList.addAll(landChild.getPlayersInLandNoVanish(fromPlayer));
		}

		return playerList;
	}

	/**
	 * Checks if is for sale.
	 *
	 * @return true, if is for sale
	 */
	@Override
	public boolean isForSale() {

		return forSale;
	}

	/**
	 * Sets the for sale.
	 *
	 * @param isForSale the is for sale
	 * @param salePrice the sale price
	 * @param signLoc the sign location
	 */
	public void setForSale(final boolean isForSale, final double salePrice, final Location signLoc) {
		forSale = isForSale;
		if (forSale) {
			this.salePrice = salePrice;
			forSaleSignLoc = signLoc;
			Factoid.getThisPlugin().iLands().addForSale(this);
		} else {
			this.salePrice = 0;
			forSaleSignLoc = null;
			banneds.clear();
			residents.clear();
			Factoid.getThisPlugin().iLands().removeForSale(this);
		}
		doSave();
	}

	@Override
	public Location getSaleSignLoc() {
		return forSaleSignLoc;
	}

	public void setSaleSignLoc(final Location forSaleSignLoc) {
		this.forSaleSignLoc = forSaleSignLoc;
		doSave();
	}

	/**
	 * Gets the sale price.
	 *
	 * @return the sale price
	 */
	@Override
	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * Checks if is for rent.
	 *
	 * @return true, if is for rent
	 */
	@Override
	public boolean isForRent() {
		return forRent;
	}

	/**
	 * Sets the for rent.
	 *
	 * @param rentPrice the rent price
	 * @param rentRenew the rent renew
	 * @param rentAutoRenew the rent auto renew
	 * @param signLoc the sign location
	 */
	public void setForRent(final double rentPrice, final int rentRenew, final boolean rentAutoRenew, final Location signLoc) {
		forRent = true;
		this.rentPrice = rentPrice;
		this.rentRenew = rentRenew;
		this.rentAutoRenew = rentAutoRenew;
		this.forRentSignLoc = signLoc;
		Factoid.getThisPlugin().iLands().addForRent(this);
		doSave();
	}

	@Override
	public Location getRentSignLoc() {

		return forRentSignLoc;
	}

	public void setRentSignLoc(final Location forRentSignLoc) {
		this.forRentSignLoc = forRentSignLoc;
		doSave();
	}

	/**
	 * Un set for rent.
	 */
	public void unSetForRent() {
		forRent = false;
		rentPrice = 0;
		rentRenew = 0;
		rentAutoRenew = false;
		forRentSignLoc = null;
		Factoid.getThisPlugin().iLands().removeForRent(this);
		doSave();
	}

	/**
	 * Gets the rent price.
	 *
	 * @return the rent price
	 */
	@Override
	public double getRentPrice() {
		return rentPrice;
	}

	/**
	 * Gets the rent renew.
	 *
	 * @return the rent renew
	 */
	@Override
	public int getRentRenew() {
		return rentRenew;
	}

	/**
	 * Gets the rent auto renew.
	 *
	 * @return the rent auto renew
	 */
	@Override
	public boolean getRentAutoRenew() {

		return rentAutoRenew;
	}

	/**
	 * Checks if is rented.
	 *
	 * @return true, if is rented
	 */
	@Override
	public boolean isRented() {

		return rented;
	}

	/**
	 * Sets the rented.
	 *
	 * @param tenant the new rented
	 */
	public void setRented(final IPlayerContainerPlayer tenant) {

		rented = true;
		this.tenant = tenant;
		updateRentedPayment(); // doSave() done in this method
	}

	/**
	 * Update rented payment.
	 */
	public void updateRentedPayment() {

		lastPayment = new Timestamp(new Date().getTime());
		doSave();
	}

	/**
	 * Un set rented.
	 */
	public void unSetRented() {
		rented = false;
		tenant = null;
		banneds.clear();
		residents.clear();
		lastPayment = new Timestamp(0);
		doSave();
	}

	/**
	 * Gets the tenant.
	 *
	 * @return the tenant
	 */
	@Override
	public IPlayerContainerPlayer getTenant() {

		return tenant;
	}

	/**
	 * Checks if is tenant.
	 *
	 * @param player the player
	 * @return true, if is tenant
	 */
	@Override
	public boolean isTenant(final Player player) {

		return rented && tenant.hasAccess(player);
	}

	/**
	 * Sets the last payment time.
	 *
	 * @param lastPayment the new last payment time
	 */
	public void setLastPaymentTime(final Timestamp lastPayment) {

		this.lastPayment = lastPayment;
		doSave();
	}

	/**
	 * Gets the last payment time.
	 *
	 * @return the last payment time
	 */
	@Override
	public Timestamp getLastPaymentTime() {

		return lastPayment;
	}

	@Override
	public IType getType() {

		return type;
	}

	@Override
	public void setType(final IType arg0) {

		type = arg0;
		doSave();
	}
}
