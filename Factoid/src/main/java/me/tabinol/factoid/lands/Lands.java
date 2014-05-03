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

import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.areas.AreaIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.event.LandDeleteEvent;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class Lands {

    public final static int INDEX_X1 = 0;
    public final static int INDEX_Z1 = 1;
    public final static int INDEX_X2 = 2;
    public final static int INDEX_Z2 = 3;
    private final TreeMap<String, TreeSet<AreaIndex>>[] areaList; // INDEX first, Tree by worlds (then by Areas)
    private final TreeMap<UUID, Land> landUUIDList; // Lands by UUID;
    private final TreeMap<String, Land> landList; // Tree by name
    private final DummyLand globalArea; // GLOBAL configuration
    protected TreeMap<String, DummyLand> outsideArea; // Outside a Land (in specific worlds)
    protected DummyLand defaultConf; // Default config of a land, String = "global" or WorldName
    private final PluginManager pm;
    private final ApproveList approveList;

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
    }

    public ApproveList getApproveList() {

        return approveList;
    }

    // For Land with no parent
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area)
            throws FactoidLandException {

        return createLand(landName, owner, area, null, 1, null);
    }

    // For Land with parent
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent)
            throws FactoidLandException {

        return createLand(landName, owner, area, parent, 1, null);
    }

    // For Land with parent and price
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent, double price)
            throws FactoidLandException {
        
        getPriceFromPlayer(area.getWorldName(), owner, price);

        return createLand(landName, owner, area, parent, 1, null);
    }

    // Only for Land load at start
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

    public boolean isNameExist(String landName) {

        return landList.containsKey(landName.toLowerCase());
    }

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

    public boolean removeLand(String landName) throws FactoidLandException {

        return removeLand(landList.get(landName.toLowerCase()));
    }

    public boolean removeLand(UUID uuid) throws FactoidLandException {

        return removeLand(landUUIDList.get(uuid));
    }

    public boolean renameLand(String landName, String newName) throws FactoidLandException {

        Land land = getLand(landName);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

    public boolean renameLand(UUID uuid, String newName) throws FactoidLandException {

        Land land = getLand(uuid);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

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

    public Land getLand(String landName) {

        return landList.get(landName.toLowerCase());
    }

    public Land getLand(UUID uuid) {

        return landUUIDList.get(uuid);
    }

    public Land getLand(Location loc) {

        CuboidArea ca;

        if ((ca = getCuboidArea(loc)) == null) {
            return null;
        }
        return ca.getLand();
    }

    public Collection<Land> getLands() {

        return landList.values();
    }

    public DummyLand getLandOrOutsideArea(Location loc) {

        DummyLand land;

        if ((land = getLand(loc)) != null) {
            return land;
        }

        return getOutsideArea(loc);
    }

    public DummyLand getOutsideArea(Location loc) {

        return getOutsideArea(loc.getWorld().getName());
    }

    public DummyLand getOutsideArea(String worldName) {

        DummyLand dummyLand;
        String worldNameLower = worldName.toLowerCase();

        if ((dummyLand = outsideArea.get(worldNameLower)) == null) {
            outsideArea.put(worldNameLower, dummyLand = new DummyLand(worldNameLower));
        }

        return dummyLand;
    }

    public Collection getLands(Location loc) {

        Collection<CuboidArea> areas = getCuboidAreas(loc);
        HashMap<String, Land> lands = new HashMap<String, Land>();

        for (CuboidArea area : areas) {
            lands.put(area.getLand().getName(), area.getLand());
        }

        return lands.values();
    }

    public Collection getLands(PlayerContainer owner) {

        Collection<Land> lands = new TreeSet<Land>();

        for (Land land : landList.values()) {
            if (land.getOwner().equals(owner)) {
                lands.add(land);
            }
        }

        return lands;
    }

    protected boolean getPriceFromPlayer(String worldName, PlayerContainer pc, double price) {
        
        if(pc.getContainerType() == PlayerContainerType.PLAYER && price > 0) {
            return Factoid.getPlayerMoney().getFromPlayer(((PlayerContainerPlayer)pc).getPlayerName(), worldName, price);
        }
    
    return true;
    }
    
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

    public Collection getCuboidAreas(Location loc) {

        Collection<CuboidArea> areas = new ArrayList<CuboidArea>();
        String worldName = loc.getWorld().getName();
        int SearchIndex;
        int nbToFind;
        boolean ForwardSearch;
        TreeSet<AreaIndex> ais;
        AreaIndex ai;
        Iterator<AreaIndex> it;

        // First, determinate if what is the higest number between x1, x2, z1 and z2
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

    public CuboidArea getCuboidArea(Location loc) {

        int actualPrio = Short.MIN_VALUE;
        int curPrio;
        int actualGen = 0;
        int curGen;
        CuboidArea actualArea = null;
        Collection<CuboidArea> areas = getCuboidAreas(loc);

        Factoid.getLog().write("Area check in" + loc.toString());

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

        /* Section not needed: the priority is inneritable
         // If we need a second pass and more (for children)
         for (int t = 1; t <= actualGen; t++) {
         actualPrio = Short.MIN_VALUE;
         for (CuboidArea area : areas) {
         if (area.getLand() == actualArea.getLand().getAncestor(actualGen - t)
         && actualPrio < (curPrio = area.getLand().getPriority())) {
         actualArea = area;
         actualPrio = curPrio;
         }
         }
         }
         */
        return actualArea;
    }

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

    protected void removeAreaFromList(CuboidArea area) {

        areaList[INDEX_X1].get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
    }

    private void addLandToList(Land land) {

        landList.put(land.getName(), land);
        landUUIDList.put(land.getUUID(), land);
    }

    private void removeLandFromList(Land land) {

        landList.remove(land.getName());
        landUUIDList.remove(land.getUUID());
        for (CuboidArea area : land.getAreas()) {
            removeAreaFromList(area);
        }
    }
}
