package me.tabinol.factoid.lands;

import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Areas.AreaIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.event.LandDeleteEvent;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

public class Lands {

    public final static int INDEX_X1 = 0;
    public final static int INDEX_Z1 = 1;
    public final static int INDEX_X2 = 2;
    public final static int INDEX_Z2 = 3;
    // INDEX first, Tree by worlds (then by Areas)
    private final TreeMap<String, TreeSet<AreaIndex>>[] areaList;
    // Tree by name
    private final TreeMap<String, Land> landList;
    // GLOBAL configuration
    private final DummyLand globalArea;
    // Outside a Land (in specific worlds)
    protected TreeMap<String, DummyLand> outsideArea;
    // Default config of a land, String = "global" or WorldName
    protected DummyLand defaultConf;
    private final PluginManager pm;
    private final ApproveList approveList;

    public Lands() {

        areaList = new TreeMap[4];
        pm = Factoid.getThisPlugin().getServer().getPluginManager();
        for (int t = 0; t < areaList.length; t++) {
            areaList[t] = new TreeMap<>();
        }
        WorldConfig worldConfig = new WorldConfig();
        
        // Load World Config
        this.outsideArea = worldConfig.getLandOutsideArea();
        this.globalArea = outsideArea.get(Config.GLOBAL);
        
        // Load Land default
        this.defaultConf = worldConfig.getLandDefaultConf();
        
        landList = new TreeMap<>();
        approveList = new ApproveList();
    }

    public ApproveList getApproveList() {

        return approveList;
    }

    // For Land with no parent
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area)
            throws FactoidLandException {

        return createLand(landName, owner, area, null, 1);
    }

    // For Land with parent
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent)
            throws FactoidLandException {

        return createLand(landName, owner, area, parent, 1);
    }

    // Only for Land load at start
    public Land createLand(String landName, PlayerContainer owner, CuboidArea area, Land parent, int areaId)
            throws FactoidLandException {

        String landNameLower = landName.toLowerCase();
        int genealogy = 0;
        Land land;

        if (parent != null) {
            genealogy = parent.getGenealogy() + 1;
        }

        if (isNameExist(landName)) {
            throw new FactoidLandException(landName, area, LandAction.LAND_ADD, LandError.NAME_IN_USE);
        }

        land = new Land(landNameLower, owner, area, genealogy, parent, areaId);

        addLandToList(land);
        Factoid.getLog().write("add land: " + landNameLower);

        return land;
    }

    public boolean isNameExist(String landName) {

        return landList.containsKey(landName.toLowerCase());
    }

    public boolean removeLand(Land land) throws FactoidLandException {

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

        removeLandToList(land);
        land.getParent().removeChild(land.getName());
        Factoid.getStorage().removeLand(land);
        Factoid.getLog().write("remove land: " + land);
        return true;
    }

    public boolean removeLand(String landName) throws FactoidLandException {

        String landLower;

        if (landName == null || !landList.containsKey(landLower = landName.toLowerCase())) {
            return false;
        }
        return removeLand(landList.get(landLower));

    }

    public boolean renameLand(String oldName, String newName) throws FactoidLandException {

        String oldNameLower = oldName.toLowerCase();
        String newNameLower = newName.toLowerCase();

        if (isNameExist(newNameLower)) {
            throw new FactoidLandException(newNameLower, null, LandAction.LAND_RENAME, LandError.NAME_IN_USE);
        }

        Land land = landList.remove(oldNameLower);

        if (land == null) {
            return false;
        }

        land.setName(newNameLower);
        landList.put(newNameLower, land);

        return true;
    }

    public Land getLand(String landName) {

        return landList.get(landName.toLowerCase());
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
        HashMap<String, Land> lands = new HashMap<>();

        for (CuboidArea area : areas) {
            lands.put(area.getLand().getName(), area.getLand());
        }

        return lands.values();
    }

    public Collection getLands(PlayerContainer owner) {

        Collection<Land> lands = new TreeSet<>();

        for (Land land : landList.values()) {
            if (land.getOwner().equals(owner)) {
                lands.add(land);
            }
        }

        return lands;
    }

    protected boolean getPermissionInWorld(String worldName, String playerName, PermissionType pt, boolean onlyInherit) {

        Boolean result;
        DummyLand dl;

        if ((dl = outsideArea.get(worldName.toLowerCase())) != null && (result = dl.getPermission(playerName, pt, onlyInherit)) != null) {
            return result;
        }
        if ((result = globalArea.getPermission(playerName, pt, onlyInherit)) != null) {
            return result;
        }

        return pt.baseValue();
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

        Collection<CuboidArea> areas = new ArrayList<>();
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
        CuboidArea actualArea = null;
        Collection<CuboidArea> areas = getCuboidAreas(loc);

        // Compare priorities of parents (or main)
        for (CuboidArea area : areas) {

            if (actualPrio < (curPrio = area.getLand().getAncestor(area.getLand().getGenealogy()).getPriority())
                    || (actualPrio == curPrio && actualGen <= area.getLand().getGenealogy())) {
                actualArea = area;
                actualPrio = curPrio;
                actualGen = area.getLand().getGenealogy();
            }
        }

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

    protected void removeAreaToList(CuboidArea area) {

        areaList[INDEX_X1].get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
    }

    private void addLandToList(Land land) {

        landList.put(land.getName(), land);
    }

    private void removeLandToList(Land land) {

        landList.remove(land.getName());
        for (CuboidArea area : land.getAreas()) {
            removeAreaToList(area);
        }
    }
}
