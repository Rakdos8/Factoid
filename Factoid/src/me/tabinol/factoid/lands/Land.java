package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.HashMap;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Land {

    public final short DEFAULT_PRIORITY = 10;
    private String name;
    private HashMap<Integer, CuboidArea> areas;
    private HashMap<String, Land> children = new HashMap<>();
    private int nextAreaId = 0; // start to 0 increase 1 each time there are an area added
    private short priority = DEFAULT_PRIORITY;
    private Land parent = null;
    private PlayerContainer owner;

    public Land(String landName, PlayerContainer owner, CuboidArea area) {

        setAreaBeforeCreate(landName, owner, area);
    }

    public Land(String landName, PlayerContainer owner, HashMap<Integer, CuboidArea> areas) {

        createLand(landName, owner, areas);
    }

    // 2 next one for a child
    public Land(String landName, PlayerContainer owner, CuboidArea area, Land parent) {

        this.parent = parent;
        setAreaBeforeCreate(landName, owner, area);
    }

    public Land(String landName, PlayerContainer owner, HashMap<Integer, CuboidArea> areas, Land parent) {

        this.parent = parent;
        createLand(landName, owner, areas);
    }

    private void setAreaBeforeCreate(String landName, PlayerContainer owner, CuboidArea area) {

        HashMap<Integer, CuboidArea> genAreas = new HashMap<>();
        genAreas.put(nextAreaId++, area);
        createLand(landName, owner, genAreas);
    }

    private void createLand(String landName, PlayerContainer owner, HashMap<Integer, CuboidArea> areas) {

        name = landName;
        this.owner = owner;
        this.areas = areas;
        nextAreaId = areas.size();
    }

    public int addArea(CuboidArea area) {

        int areaId = nextAreaId++;

        areas.put(areaId, area);

        return areaId;
    }

    public boolean removeArea(int areaId) {

        if (areas.remove(areaId) != null) {
            return true;
        }

        return false;
    }

    public boolean updateArea(int areaId, CuboidArea newArea) {

        if (areas.containsKey(areaId)) {
            areas.put(areaId, newArea);
            return true;
        }

        return false;
    }

    public Collection getAreas() {

        return areas.values();
    }

    public boolean isCollision(CuboidArea area2) {

        for (CuboidArea area1 : areas.values()) {
            if (area1.isCollision(area2)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {

        return name;
    }

    public void setName(String newName) {

        this.name = newName;
    }
    
    public PlayerContainer getOwner() {
        
        return owner;
    }
    
    public void setOwner(PlayerContainer owner) {
        
        this.owner = owner;
    }

    // Note : a child get the parent priority
    public short getPriority() {

        if (parent != null) {
            return parent.getPriority();
        }

        return priority;
    }

    public void setPriority(short priority) {

        this.priority = priority;
    }

    public Land getParent() {

        return parent;
    }

    public void addChild(Land land) {

        children.put(land.name, land);
    }

    public void removeChild(String landName) {

        children.remove(landName);
    }

    public Land getChild(String landName) {

        return children.get(landName);
    }

    public Collection getChildren() {

        return children.values();
    }
}
