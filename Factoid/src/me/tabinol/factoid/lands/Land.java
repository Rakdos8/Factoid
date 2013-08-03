package me.tabinol.factoid.lands;

import java.util.HashMap;
import java.util.HashSet;

public class Land {
    
    private String name;
    private HashMap<Integer, CuboidArea> areas;
    int nextAreaId = 0;
    
    public Land(String landName, CuboidArea area) {
        
        HashMap<Integer, CuboidArea> genAreas = new HashMap<>();
        genAreas.put(nextAreaId ++, area);
        createLand(landName, genAreas);
    }
    
    public Land(String landName, HashMap<Integer, CuboidArea>areas) {
        
        createLand(landName, areas);
    }
    
    private void createLand(String landName, HashMap<Integer, CuboidArea>areas) {
        
        name = landName;
        this.areas = areas;
    }
    
    public int addArea(CuboidArea area) {
        
        int areaId = nextAreaId ++;
        
        areas.put(areaId, area);
        
        return areaId;
    }
    
    public boolean removeArea(int areaId) {
        
        if(areas.containsKey(areaId)) {
            areas.remove(areaId);
            return true;
        }
        
        return false;
    }
}
