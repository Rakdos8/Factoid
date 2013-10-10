package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.EnumMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class Land {

    public static final short DEFAULT_PRIORITY = 10;
    private String name;
    private TreeSet<CuboidArea> areas = new TreeSet<>();
    private TreeMap<String, Land> children = new TreeMap<>();
    private TreeMap<PlayerContainer, EnumMap<PermissionType,Permission>> permissions = new TreeMap<>(); // String for playerName
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    private Land parent = null;
    private PlayerContainer owner;
    private boolean autoSave = true;

    public Land(String landName, PlayerContainer owner, CuboidArea area) {

        createLand(landName, owner, area, 0);
    }

    // next one for a child
    public Land(String landName, PlayerContainer owner, CuboidArea area, Land parent) {

        this.parent = parent;
        parent.addChild(this);
        createLand(landName, owner, area, parent.getGenealogy() + 1);
    }

    private void createLand(String landName, PlayerContainer owner, CuboidArea area, int genealogy) {

        name = landName;
        this.owner = owner;
        this.genealogy = genealogy;
        addArea(area);
    }

    public void addArea(CuboidArea area) {

        area.setLand(this);
        areas.add(area);
        Factoid.getLands().addAreaToList(area);
        doSave();
    }

    public boolean removeArea(CuboidArea area) {

        if (areas.remove(area)) {
            Factoid.getLands().removeAreaToList(area);
            doSave();
            return true;
        }

        return false;
    }

    public boolean replaceArea(CuboidArea oldArea, CuboidArea newArea) {

        if (areas.contains(oldArea)) {
            Factoid.getLands().removeAreaToList(oldArea);
            newArea.setLand(this);
            areas.add(newArea);
            Factoid.getLands().addAreaToList(newArea);
            doSave();
            return true;
        }

        return false;
    }

    public Collection<CuboidArea> getAreas() {

        return areas;
    }

    public boolean isCollision(CuboidArea area2) {

        for (CuboidArea area1 : areas) {
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
        this.name = newName;
        doSave();
    }

    public PlayerContainer getOwner() {

        return owner;
    }

    public void setOwner(PlayerContainer owner) {

        this.owner = owner;
        doSave();
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
    
    public void addPermission(PlayerContainer pc, Permission perm) {
        
       EnumMap<PermissionType, Permission> permPlayer;
        
        if(!permissions.containsKey(pc)) {
            permPlayer = permissions.put(pc, new EnumMap<PermissionType,Permission>(PermissionType.class));
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave();
    }
    
    public boolean removePermission(PlayerContainer pc, PermissionType permType) {
        
        EnumMap<PermissionType, Permission> permPlayer;

        if(!permissions.containsKey(pc)) {
            return false;
        }
        permPlayer = permissions.get(pc);
        if(permPlayer.remove(permType) == null) {
            return false;
        }

        // remove key for PC if it is empty
        if(permPlayer.isEmpty()) {
            permissions.remove(pc);
        }
        
        doSave();
        return true;
    }
    
    public final Set<PlayerContainer> getSetPCHavePermission() {
        
        return permissions.keySet();
    }
    
    public final Collection<Permission> getPermissionsForPC(PlayerContainer pc) {
        
        return permissions.get(pc).values();
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
    
    private void doSave() {
        
        if(autoSave) {
            forceSave();
        }
    }
}
