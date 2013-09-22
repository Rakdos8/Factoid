package me.tabinol.factoid.lands;

import java.util.TreeMap;
import java.util.TreeSet;
import org.bukkit.Location;

public class Lands {

    public final static int INDEX_X1 = 0;
    public final static int INDEX_Z1 = 1;
    public final static int INDEX_X2 = 2;
    public final static int INDEX_Z2 = 3;
    // INDEX first, Tree by worlds (then by Areas)
    TreeMap<String, TreeSet<AreaIndex>>[] areaList;
    // Tree by name
    TreeMap<String, Land> landList;

    public Lands() {

        areaList = new TreeMap[4];
        for (int t = 0; t < 6; t++) {
            areaList[t] = new TreeMap<>();
        }
        landList = new TreeMap<>();
    }

    public boolean createLand(Land land) {

        if (landList.containsKey(land.getName())) {
            return false;
        }
        addLandToList(land);
        return true;
    }

    public boolean removeLand(Land land) {

        if (!landList.containsKey(land.getName())) {
            return false;
        }
        removeLandToList(land);
        return true;
    }

    public boolean removeLand(String landName) {

        if (landName == null || !landList.containsKey(landName)) {
            return false;
        }
        return removeLand(landList.get(landName));

    }

    public Land getLand(String landName) {

        return landList.get(landName);
    }

    public Land getLand(Location loc) {

        return getCuboidArea(loc).getLand();
    }

    public CuboidArea getCuboidArea(Location loc) {

        String worldName = loc.getWorld().getName();
        int SearchIndex;
        int nbToFind;
        boolean ForwardSearch;
        TreeSet<AreaIndex> ais;
        AreaIndex ai;
        int actualPrio = Short.MIN_VALUE;
        CuboidArea actualArea = null;

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

        // Now check for area in location
        ais = areaList[SearchIndex].get(worldName);
        if (ForwardSearch) {
            ai = ais.pollFirst();
        } else {
            ai = ais.pollLast();
        }

        while (ai != null && checkContinueSearch(ai.getArea(), nbToFind, SearchIndex)) {

            if (ai.getArea().isLocationInside(loc)
                    && ai.getArea().getLand().getPriority() > actualPrio + (ai.getArea().getLand().getGenealogy() * 100000)) {
                actualArea = ai.getArea();
                actualPrio = actualArea.getLand().getPriority();
            }

            ai = searchNext(ais, ai, ForwardSearch);
        }

        return actualArea;
    }

    private boolean checkContinueSearch(CuboidArea area, int nbToFind, int SearchIndex) {

        switch (SearchIndex) {
            case INDEX_X1:
                if (nbToFind > area.getX1()) {
                    return false;
                }
                return true;
            case INDEX_X2:
                if (nbToFind < area.getX2()) {
                    return false;
                }
                return true;
            case INDEX_Z1:
                if (nbToFind > area.getZ1()) {
                    return false;
                }
                return true;
            case INDEX_Z2:
                if (nbToFind < area.getZ2()) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    private AreaIndex searchNext(TreeSet<AreaIndex> ais, AreaIndex ai, boolean ForwardSearch) {

        if (ForwardSearch) {
            return ais.higher(ai);
        }
        return ais.lower(ai);
    }

    protected void addAreaToList(CuboidArea area) {

        if (!areaList[0].containsKey(area.getWorldName())) {
            for (int t = 0; t < 4; t++) {
                areaList[t].put(area.getWorldName(), new TreeSet<AreaIndex>());
            }
        }
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
        for (CuboidArea area : land.getAreas()) {
            addAreaToList(area);
        }
    }

    private void removeLandToList(Land land) {

        landList.remove(land.getName());
    }
}
