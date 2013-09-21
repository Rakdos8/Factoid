package me.tabinol.factoid.lands;

import java.util.TreeMap;
import java.util.TreeSet;
import org.bukkit.Location;

public class Lands {
    
    // Tree by worlds (then by Areas)
    TreeMap<String,TreeSet<CuboidArea>> areaList = new TreeMap<>();
    // Tree by name
    TreeMap<String, Land> landList = new TreeMap<>();
    
    public Lands() {
    }
    
    public boolean createLand(Land land) {
        
        if(landList.containsKey(land.getName())) {
            return false;
        }
        addLandToList(land);
        return true;
    }
    
    public boolean removeLand(Land land) {
        
        if(!landList.containsKey(land.getName())) {
            return false;
        }
        removeLandToList(land);
        return true;
    }
    
    public boolean removeLand(String landName) {

        if(landName == null || !landList.containsKey(landName)) {
            return false;
        }
        return removeLand(landList.get(landName));
        
    }
    
    public Land getLand(String landName) {
        
        return landList.get(landName);
    }
    
    public CuboidArea getCuboidArea(Location loc) {
        
        // TreeSet<CuboidArea> listWorld = areaList.get(loc.getWorld());
        // if(listWorld.)
        return null;
    }
    
    private void addAreaToList(CuboidArea area) {
        
        if(!areaList.containsKey(area.getWorldName())) {
            areaList.put(area.getWorldName(), new TreeSet<CuboidArea>());
        }
        areaList.get(area.getWorldName()).add(area);
    }
    
    private void removeAreaToList(CuboidArea area) {
        
        areaList.get(area.getWorldName()).remove(area);
    }
    
    private void addLandToList(Land land) {
        
        landList.put(land.getName(), land);
        for(CuboidArea area : land.getAreas()) {
            addAreaToList(area);
        }
    }
    
    private void removeLandToList(Land land) {
        
        landList.remove(land.getName());
    }


    
}
