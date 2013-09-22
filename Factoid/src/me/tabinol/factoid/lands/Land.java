package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Land {

    public static final short DEFAULT_PRIORITY = 10;
    private String name;
    private TreeMap<Integer, CuboidArea> areas = new TreeMap<>();
    private TreeMap<String, Land> children = new TreeMap<>();
    private int nextAreaId = 0; // start to 0 increase 1 each time there are an area added
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    private Land parent = null;
    private PlayerContainer owner;

    public Land(String landName, PlayerContainer owner, CuboidArea area) {

        createLand(landName, owner, area, 0);
    }

    // 2 next one for a child
    public Land(String landName, PlayerContainer owner, CuboidArea area, Land parent) {

        createLand(landName, owner, area, parent.getGenealogy() + 1);
    }

    private void createLand(String landName, PlayerContainer owner, CuboidArea area, int genealogy) {

        name = landName;
        this.owner = owner;
        this.genealogy = genealogy;
        addArea(area);
    }

    public int addArea(CuboidArea area) {

        int areaId = nextAreaId++;

        area.setLand(this);
        areas.put(areaId, area);
        Factoid.getLands().addAreaToList(area);

        return areaId;
    }

    public boolean removeArea(int areaId) {

        CuboidArea area;
        if ((area = areas.remove(areaId)) != null) {
            Factoid.getLands().removeAreaToList(area);
            return true;
        }

        return false;
    }

    public boolean updateArea(int areaId, CuboidArea newArea) {

        if (areas.containsKey(areaId)) {
            Factoid.getLands().removeAreaToList(areas.get(areaId));
            newArea.setLand(this);
            areas.put(areaId, newArea);
            Factoid.getLands().addAreaToList(newArea);
            return true;
        }

        return false;
    }

    public Collection<CuboidArea> getAreas() {

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
    
    public int getGenealogy() {
        
        return genealogy;
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
