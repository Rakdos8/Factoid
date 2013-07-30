package me.tabinol.factoid.lands;

import java.util.HashSet;

public class Land {
    
    private String name;
    private HashSet<CuboidArea> areas;
    
    public Land(String landName, CuboidArea area) {
        
        HashSet<CuboidArea> genAreas = new HashSet<>();
        genAreas.add(area);
        createLand(landName, genAreas);
    }
    
    public Land(String landName, HashSet<CuboidArea>areas) {
        
        createLand(landName, areas);
    }
    
    private void createLand(String landName, HashSet<CuboidArea>areas) {
        
        name = landName;
        this.areas = areas;
    }
}
