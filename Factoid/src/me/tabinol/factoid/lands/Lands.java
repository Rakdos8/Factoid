package me.tabinol.factoid.lands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.Location;

public class Lands {

    public final static int INDEX_X1 = 0;
    public final static int INDEX_Z1 = 1;
    public final static int INDEX_X2 = 2;
    public final static int INDEX_Z2 = 3;
    public final static String GLOBAL = "_Global_";
    // INDEX first, Tree by worlds (then by Areas)
    private TreeMap<String, TreeSet<AreaIndex>>[] areaList;
    // Tree by name
    private TreeMap<String, Land> landList;
    // Lands created for outside a Land, String = "Global" or WorldName
    protected TreeMap<String, DummyLand> outsideArea;
    // Default config of a land, String = "Global" or WorldName
    protected DummyLand defaultConf;

    public Lands(TreeMap<String, DummyLand> outsideArea, DummyLand defaultConf) {

        areaList = new TreeMap[4];
        for (int t = 0; t < areaList.length; t++) {
            areaList[t] = new TreeMap<>();
        }
        this.outsideArea = outsideArea;
        this.defaultConf = defaultConf;
        landList = new TreeMap<>();
    }

    public boolean createLand(Land land) {

        if (landList.containsKey(land.getName())) {
            return false;
        }
        addLandToList(land);
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.LAND.CREATE", land.getName()));
        return true;
    }

    public boolean removeLand(Land land) {

        if (!landList.containsKey(land.getName())) {
            return false;
        }
        removeLandToList(land);
        land.getParent().removeChild(land.getName());
        Factoid.getStorage().removeLand(land);
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.LAND.REMOVE", land.getName()));
        return true;
    }

    public boolean removeLand(String landName) {

        String landLower;
        
        if (landName == null || !landList.containsKey(landLower = landName.toLowerCase())) {
            return false;
        }
        Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.LAND.REMOVE", landName));
        return removeLand(landList.get(landLower));

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

    public DummyLand getLandOrOutsideArea(Location loc) {

        DummyLand land;

        if ((land = getLand(loc)) != null) {
            return land;
        }
        if((land = outsideArea.get(loc.getWorld().getName())) != null) {
            return land;
        }
        
        return outsideArea.get(GLOBAL);
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
        if ((dl = outsideArea.get(Lands.GLOBAL)) != null && (result = dl.getPermission(playerName, pt, onlyInherit)) != null) {
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
        if ((dl = outsideArea.get(Lands.GLOBAL)) != null && (result = dl.getFlag(ft, onlyInherit)) != null) {
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

            if (actualPrio < (curPrio = area.getLand().getAncestor(area.getLand().getGenealogy()).getPriority())) {
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
