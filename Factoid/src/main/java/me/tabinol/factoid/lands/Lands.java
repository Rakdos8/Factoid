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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.event.LandDeleteEvent;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.areas.AreaIndex;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

// TODO: Auto-generated Javadoc
/**
 * The Class Lands.
 */
public class Lands {

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
    private final TreeMap<UUID, Land> landUUIDList; // Lands by UUID;
    
    /** The land list. */
    private final TreeMap<String, Land> landList; // Tree by name
    
    /** The global area. */
    private final DummyLand globalArea; // GLOBAL configuration
    
    /** The outside area. */
    protected TreeMap<String, DummyLand> outsideArea; // Outside a Land (in specific worlds)
    
    /** The default conf. */
    protected DummyLand defaultConf; // Default config of a land, String = "global" or WorldName
    
    /** The pm. */
    private final PluginManager pm;
    
    /** The approve list. */
    private final ApproveList approveList;
    
    /**  List of forSale. */
    private final HashSet<Land> forSale;

    /**  List of forRent and rented. */
    private final HashSet<Land> forRent;
    /**
     * Instantiates a new lands.
     */
    public Lands() {

        areaList = new TreeMap[4];
        pm = Factoid.getThisPlugin().getServer().getPluginManager();
        for (int t = 0; t < areaList.length; t++) {
            areaList[t] = new TreeMap<String, TreeSet<AreaIndex>>();
        }
        WorldConfig worldConfig = new WorldConfig();

        // Load World Config
        this.outsideArea = worldConfig.getLandOutsideArea();
        this.globalArea = outsideArea.get(Config.GLOBAL);

        // Load Land default
        this.defaultConf = worldConfig.getLandDefaultConf();

        landList = new TreeMap<String, Land>();
        landUUIDList = new TreeMap<UUID, Land>();
        approveList = new ApproveList();
        forSale = new HashSet<Land>();
        forRent = new HashSet<Land>();
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
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area)
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
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent)
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
     * @return the land
     * @throws FactoidLandException the factoid land exception
     */
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent, double price)
            throws FactoidLandException {
        
        getPriceFromPlayer(area.getWorldName(), owner, price);

        return createLand(landName, owner, area, parent, 1, null);
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
     * @return the land
     * @throws FactoidLandException the factoid land exception
     */
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent, int areaId, UUID uuid)
            throws FactoidLandException {

        String landNameLower = landName.toLowerCase();
        int genealogy = 0;
        Land land;
        UUID landUUID;
        
        if (uuid == null) {
            landUUID = UUID.randomUUID();
        } else {
            landUUID = uuid;
        }

        if (parent != null) {
            genealogy = parent.getGenealogy() + 1;
        }

        if (isNameExist(landName)) {
            throw new FactoidLandException(landName, area, LandAction.LAND_ADD, LandError.NAME_IN_USE);
        }

        land = new Land(landNameLower, landUUID, owner, area, genealogy, parent, areaId);

        addLandToList(land);
        Factoid.getLog().write("add land: " + landNameLower);

        return land;
    }

    /**
     * Checks if is name exist.
     *
     * @param landName the land name
     * @return true, if is name exist
     */
    public boolean isNameExist(String landName) {

        return landList.containsKey(landName.toLowerCase());
    }

    /**
     * Removes the land.
     *
     * @param land the land
     * @return true, if successful
     * @throws FactoidLandException the factoid land exception
     */
    public boolean removeLand(Land land) throws FactoidLandException {

        if (land == null) {
            return false;
        }

        LandDeleteEvent landEvent = new LandDeleteEvent(land);

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

        removeLandFromList(land);
        if (land.getParent() != null) {
            land.getParent().removeChild(land.getUUID());
        }
        Factoid.getStorage().removeLand(land);
        Factoid.getLog().write("remove land: " + land);
        return true;
    }

    /**
     * Removes the land.
     *
     * @param landName the land name
     * @return true, if successful
     * @throws FactoidLandException the factoid land exception
     */
    public boolean removeLand(String landName) throws FactoidLandException {

        return removeLand(landList.get(landName.toLowerCase()));
    }

    /**
     * Removes the land.
     *
     * @param uuid the uuid
     * @return true, if successful
     * @throws FactoidLandException the factoid land exception
     */
    public boolean removeLand(UUID uuid) throws FactoidLandException {

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
    public boolean renameLand(String landName, String newName) throws FactoidLandException {

        Land land = getLand(landName);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

    /**
     * Rename land.
     *
     * @param uuid the uuid
     * @param newName the new name
     * @return true, if successful
     * @throws FactoidLandException the factoid land exception
     */
    public boolean renameLand(UUID uuid, String newName) throws FactoidLandException {

        Land land = getLand(uuid);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

    /**
     * Rename land.
     *
     * @param land the land
     * @param newName the new name
     * @return true, if successful
     * @throws FactoidLandException the factoid land exception
     */
    public boolean renameLand(Land land, String newName) throws FactoidLandException {

        String oldNameLower = land.getName();
        String newNameLower = newName.toLowerCase();

        if (isNameExist(newNameLower)) {
            throw new FactoidLandException(newNameLower, null, LandAction.LAND_RENAME, LandError.NAME_IN_USE);
        }

        landList.remove(oldNameLower);

        land.setName(newNameLower);
        landList.put(newNameLower, land);

        return true;
    }

    /**
     * Gets the land.
     *
     * @param landName the land name
     * @return the land
     */
    public Land getLand(String landName) {

        return landList.get(landName.toLowerCase());
    }

    /**
     * Gets the land.
     *
     * @param uuid the uuid
     * @return the land
     */
    public Land getLand(UUID uuid) {

        return landUUIDList.get(uuid);
    }

    /**
     * Gets the land.
     *
     * @param loc the loc
     * @return the land
     */
    public Land getLand(Location loc) {

        CuboidArea ca;

        if ((ca = getCuboidArea(loc)) == null) {
            return null;
        }
        return ca.getLand();
    }

    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public Collection<Land> getLands() {

        return landList.values();
    }

    /**
     * Gets the land or outside area.
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public DummyLand getLandOrOutsideArea(Location loc) {

        DummyLand land;

        if ((land = getLand(loc)) != null) {
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
    public DummyLand getOutsideArea(Location loc) {

        return getOutsideArea(loc.getWorld().getName());
    }

    /**
     * Gets the outside area.
     *
     * @param worldName the world name
     * @return the outside area
     */
    public DummyLand getOutsideArea(String worldName) {

        DummyLand dummyLand;
        String worldNameLower = worldName.toLowerCase();

        if ((dummyLand = outsideArea.get(worldNameLower)) == null) {
            outsideArea.put(worldNameLower, dummyLand = new DummyLand(worldNameLower));
        }

        return dummyLand;
    }

    /**
     * Gets the lands.
     *
     * @param loc the loc
     * @return the lands
     */
    public Collection<Land> getLands(Location loc) {

        Collection<CuboidArea> areas = getCuboidAreas(loc);
        HashMap<String, Land> lands = new HashMap<String, Land>();

        for (CuboidArea area : areas) {
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
    public Collection<Land> getLands(PlayerContainer owner) {

        Collection<Land> lands = new HashSet<Land>();

        for (Land land : landList.values()) {
            if (land.getOwner().equals(owner)) {
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
    protected boolean getPriceFromPlayer(String worldName, PlayerContainer pc, double price) {
        
        if(pc.getContainerType() == PlayerContainerType.PLAYER && price > 0) {
            return Factoid.getPlayerMoney().getFromPlayer(((PlayerContainerPlayer)pc).getOfflinePlayer(), worldName, price);
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
    protected boolean getPermissionInWorld(String worldName, Player player, PermissionType pt, boolean onlyInherit) {

        Boolean result;
        DummyLand dl;

        if ((dl = outsideArea.get(worldName.toLowerCase())) != null && (result = dl.getPermission(player, pt, onlyInherit)) != null) {
            return result;
        }
        if ((result = globalArea.getPermission(player, pt, onlyInherit)) != null) {
            return result;
        }

        return pt.getDefaultValue();
    }

    /**
     * Gets the flag in world.
     *
     * @param worldName the world name
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag in world
     */
    protected LandFlag getFlagInWorld(String worldName, FlagType ft, boolean onlyInherit) {

        LandFlag result;
        DummyLand dl;

        if ((dl = outsideArea.get(worldName.toLowerCase())) != null && (result = dl.getFlag(ft, onlyInherit)) != null) {
            return result;
        }
        if ((result = globalArea.getFlag(ft, onlyInherit)) != null) {
            return result;
        }

        return null;
    }

    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public Collection<CuboidArea> getCuboidAreas(Location loc) {

        Collection<CuboidArea> areas = new ArrayList<CuboidArea>();
        String worldName = loc.getWorld().getName();
        int SearchIndex;
        int nbToFind;
        boolean ForwardSearch;
        TreeSet<AreaIndex> ais;
        AreaIndex ai;
        Iterator<AreaIndex> it;

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
        Factoid.getLog().write("Search Index dir: " + SearchIndex + ", Forward Search: " + ForwardSearch);

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
                Factoid.getLog().write("add this area in list for cuboid: " + ai.getArea().getLand().getName());
                areas.add(ai.getArea());
            }
        }
        Factoid.getLog().write("Number of Areas found for location : " + areas.size());

        return areas;
    }

    /**
     * Gets the cuboid area.
     *
     * @param loc the loc
     * @return the cuboid area
     */
    public CuboidArea getCuboidArea(Location loc) {

        int actualPrio = Short.MIN_VALUE;
        int curPrio;
        int actualGen = 0;
        int curGen;
        CuboidArea actualArea = null;
        Location resLoc; // Resolved location
        
        // Give the position from the sky to underbedrock if the Y is greater than 255 or lower than 0
        if(loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
        	resLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMaxHeight() - 1, loc.getZ()); 
        } else if(loc.getBlockY() < 0){
        	resLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ()); 
        } else resLoc = loc; 
        
        Collection<CuboidArea> areas = getCuboidAreas(resLoc);

        Factoid.getLog().write("Area check in" + resLoc.toString());

        // Compare priorities of parents (or main)
        for (CuboidArea area : areas) {

            Factoid.getLog().write("Check for: " + area.getLand().getName()
                    + ", area: " + area.toString());

            curPrio = area.getLand().getPriority();
            curGen = area.getLand().getGenealogy();

            if (actualPrio < curPrio
                    || (actualPrio == curPrio && actualGen <= curGen)) {
                actualArea = area;
                actualPrio = curPrio;
                actualGen = area.getLand().getGenealogy();

                Factoid.getLog().write("Found, update:  actualPrio: " + actualPrio + ", actualGen: " + actualGen);
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
    private boolean checkContinueSearch(CuboidArea area, int nbToFind, int SearchIndex) {

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
    protected void addAreaToList(CuboidArea area) {

        if (!areaList[0].containsKey(area.getWorldName())) {
            for (int t = 0; t < 4; t++) {
                areaList[t].put(area.getWorldName(), new TreeSet<AreaIndex>());
            }
        }
        Factoid.getLog().write("Add area for " + area.getLand().getName());
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
    protected void removeAreaFromList(CuboidArea area) {

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
    private void addLandToList(Land land) {

        landList.put(land.getName(), land);
        landUUIDList.put(land.getUUID(), land);
    }

    /**
     * Removes the land from list.
     *
     * @param land the land
     */
    private void removeLandFromList(Land land) {

        landList.remove(land.getName());
        landUUIDList.remove(land.getUUID());
        for (CuboidArea area : land.getAreas()) {
            removeAreaFromList(area);
        }
    }
    
    /**
     * Adds the for sale.
     *
     * @param land the land
     */
    protected void addForSale(Land land) {
    	
    	forSale.add(land);
    }
    
    /**
     * Removes the for sale.
     *
     * @param land the land
     */
    protected void removeForSale(Land land) {
    	
    	forSale.remove(land);
    }
    
    /**
     * Gets the for sale.
     *
     * @return the for sale
     */
    public Collection<Land> getForSale() {
    	
    	return forSale;
    }

    /**
     * Adds the for rent.
     *
     * @param land the land
     */
    protected void addForRent(Land land) {
    	
    	forRent.add(land);
    }
    
    /**
     * Removes the for rent.
     *
     * @param land the land
     */
    protected void removeForRent(Land land) {
    	
    	forRent.remove(land);
    }
    
    /**
     * Gets the for rent.
     *
     * @return the for rent
     */
    public Collection<Land> getForRent() {
    	
    	return forRent;
    }
}
