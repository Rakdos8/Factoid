package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Land extends DummyLand {

    public static final short DEFAULT_PRIORITY = 10;
    private String name;
    private TreeMap<Integer, CuboidArea> areas = new TreeMap<>();
    private TreeMap<String, Land> children = new TreeMap<>();
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    private Land parent = null;
    private PlayerContainer owner;
    private TreeSet<PlayerContainer> residents = new TreeSet<>();
    private TreeSet<PlayerContainer> banneds = new TreeSet<>();
    private boolean autoSave = true;
    private Faction factionTerritory = null;

    public Land(String landName, PlayerContainer owner, CuboidArea area) {

        super(area.getWorldName().toLowerCase());
        createLand(landName, owner, area, 0, null, 1);
    }
    
    //for AreaID only
    public Land(String landName, PlayerContainer owner, CuboidArea area, int areaId) {

        super(area.getWorldName().toLowerCase());
        createLand(landName, owner, area, 0, null, areaId);
    }

    // next one for a child
    public Land(String landName, PlayerContainer owner, CuboidArea area, Land parent) {

        super(area.getWorldName().toLowerCase());
        createLand(landName, owner, area, parent.getGenealogy() + 1, parent, 1);
    }
    
    // Only to load with a specific areaid
    public Land(String landName, PlayerContainer owner, CuboidArea area, Land parent, int areaId) {

        super(area.getWorldName().toLowerCase());
        createLand(landName, owner, area, parent.getGenealogy() + 1, parent, areaId);
    }

    private void createLand(String landName, PlayerContainer owner, CuboidArea area, int genealogy, Land parent, int areaId) {

        name = landName.toLowerCase();
        if(parent != null) {
            this.parent = parent;
            parent.addChild(this);
            this.factionTerritory = parent.factionTerritory;
        }
        this.owner = owner;
        this.genealogy = genealogy;
        flags = Factoid.getLands().defaultConf.flags.clone();
        copyPerms();
        addArea(area, areaId);
    }
    
    private void copyPerms() {
        
        permissions = new TreeMap<>();
        for(PlayerContainer pc : Factoid.getLands().defaultConf.permissions.keySet()) {
            permissions.put(PlayerContainer.create(this, pc.getContainerType(), pc.getName()),
                    Factoid.getLands().defaultConf.permissions.get(pc).clone());
        }
    }

    public void addArea(CuboidArea area) {

        int nextKey;
        
        if(areas.isEmpty()) {
            nextKey = 1;
        } else {
            nextKey = areas.lastKey() + 1;
        }
        addArea(area, nextKey);
    }
    
    public void addArea(CuboidArea area, int key) {
        
        area.setLand(this);
        areas.put(key, area);
        Factoid.getLands().addAreaToList(area);
        doSave();
    }

    public boolean removeArea(int key) {

        CuboidArea area;
        
        if ((area = areas.remove(key)) != null) {
            Factoid.getLands().removeAreaToList(area);
            doSave();
            return true;
        }

        return false;
    }

    public boolean replaceArea(int key, CuboidArea newArea) {

        CuboidArea area;
        
        if ((area = areas.remove(key)) != null) {
            Factoid.getLands().removeAreaToList(area);
            newArea.setLand(this);
            areas.put(key, newArea);
            Factoid.getLands().addAreaToList(newArea);
            doSave();
            return true;
        }

        return false;
    }
    
    public CuboidArea getArea(int key) {
        
        return areas.get(key);
    }

    public Set<Integer> getAreasKey() {
        
        return areas.keySet();
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

        Factoid.getStorage().removeLand(this);
        this.name = newName.toLowerCase();
        doSave();
    }
    
    public PlayerContainer getOwner() {

        return owner;
    }
    
    public Faction getFactionTerritory() {
        
        return factionTerritory;
    }

    public void setFactionTerritory(Faction faction) {
        
        this.factionTerritory = faction;
        for(Land child : children.values()) {
            child.setFactionTerritory(faction);
        }
        doSave();
    }
    
    public void setOwner(PlayerContainer owner) {

        this.owner = owner;
        doSave();
    }
    
    public void addResident(PlayerContainer resident) {
        
        residents.add(resident);
        doSave();
    }
    
    public boolean removeResident(PlayerContainer resident) {
        
        if(residents.remove(resident)) {
            doSave();
            return true;
        }
        
        return false;
    }
    
    public final TreeSet<PlayerContainer> getResidents() {
        
        return residents;
    }
    
    public boolean isResident(PlayerContainer resident) {
        
        return residents.contains(resident);
    }

    public void addBanned(PlayerContainer banned) {
        
        banneds.add(banned);
        doSave();
    }
    
    public boolean removeBanned(PlayerContainer banned) {
        
        if(banneds.remove(banned)) {
            doSave();
            return true;
        }
        
        return false;
    }
    
    public final TreeSet<PlayerContainer> getBanneds() {
        
        return banneds;
    }
    
    public boolean isBanned(PlayerContainer banned) {
        
        return banneds.contains(banned);
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
        doSave();
    }

    public Land getParent() {

        return parent;
    }
    
    public Land getAncestor(int gen) { // 1 parent, 2 grand-parent, 3 ...
        
        Land ancestor = this;
        
        for (int t = 0; t < gen; t ++) {
            ancestor = ancestor.getParent();
        }
        
        return ancestor;
    }

    private void addChild(Land land) {

        children.put(land.name, land);
        doSave();
    }

    protected void removeChild(String landName) {

        children.remove(landName);
        doSave();
    }
    
    public Land getChild(String landName) {

       return children.get(landName);
    }

    public Collection getChildren() {

        return children.values();
    }
    
    public void setAutoSave(boolean autoSave) {
        
        this.autoSave = autoSave;
    }
    
    public void forceSave() {
        
        Factoid.getStorage().saveLand(this);
    }
    
    @Override
    protected void doSave() {
        
        if(autoSave) {
            forceSave();
        }
    }
    
    @Override
    protected Boolean checkPermissionAndInherit(String playerName, PermissionType pt, boolean onlyInherit) {

        Boolean permValue;
        
        if ((permValue = getPermission(playerName, pt, onlyInherit)) != null) {
            return permValue;
        } else if (parent != null) {
            return parent.checkPermissionAndInherit(playerName, pt, true);
        }
        
        return Factoid.getLands().getPermissionInWorld(worldName, playerName, pt, true);
    }
    
    @Override
    protected LandFlag getFlagAndInherit(FlagType ft, boolean onlyInherit) {

        LandFlag flag;
        
        if ((flag = getFlag(ft, onlyInherit)) != null) {
            return flag;
        } else if (parent != null) {
            return parent.getFlagAndInherit(ft, true);
        } 

        return Factoid.getLands().getFlagInWorld(worldName, ft, true);
    }
}
