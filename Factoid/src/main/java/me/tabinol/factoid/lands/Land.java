package me.tabinol.factoid.lands;

import me.tabinol.factoid.lands.Areas.CuboidArea;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerContainerLandBanEvent;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Land extends DummyLand {

    public static final short DEFAULT_PRIORITY = 10;
    public static final short MINIM_PRIORITY = 0;
    public static final short MAXIM_PRIORITY = 100;
    private String name;
    private Map<Integer, CuboidArea> areas = new TreeMap<Integer, CuboidArea>();
    private Map<String, Land> children = new TreeMap<String, Land>();
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    private Land parent = null;
    private PlayerContainer owner;
    private Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
    private Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
    private boolean autoSave = true;
    private Faction factionTerritory = null;
    private double money = 0L;
    private Set<String> playerNotify = new TreeSet<String>();
    private final Set<Player> playersInLand = new HashSet<Player>();

    // Please use createLand in Lands class to create a Land
    protected Land(String landName, PlayerContainer owner, CuboidArea area, int genealogy, Land parent, int areaId) {

        super(area.getWorldName().toLowerCase());
        name = landName.toLowerCase();
        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
            this.factionTerritory = parent.factionTerritory;
        }
        this.owner = owner;
        this.genealogy = genealogy;
        if (!Factoid.getStorage().isInLoad()) {
            if (!Factoid.getLands().defaultConf.flags.isEmpty()) {
                flags = Factoid.getLands().defaultConf.flags.clone();
            }
            copyPerms();
        }
        addArea(area, areaId);
    }

    public void setDefault() {
        owner = new PlayerContainerNobody();
        residents = new TreeSet<PlayerContainer>();
        playerNotify = new TreeSet<String>();
        permissions = new TreeMap<PlayerContainer, EnumMap<PermissionType, Permission>>();
        flags = Factoid.getLands().defaultConf.flags.clone();
        copyPerms();
        doSave();
    }

    private void copyPerms() {

        for (PlayerContainer pc : Factoid.getLands().defaultConf.permissions.keySet()) {
            permissions.put(PlayerContainer.create(this, pc.getContainerType(), pc.getName()),
                    Factoid.getLands().defaultConf.permissions.get(pc).clone());
        }
    }

    public void addArea(CuboidArea area) {

        int nextKey = 0;

        if (areas.isEmpty()) {
            nextKey = 1;
        } else {
            for (int key : areas.keySet()) {

                if (nextKey < key) {
                    nextKey = key;
                }
            }
            nextKey++;
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

    public boolean removeArea(CuboidArea area) {

        Integer key = getAreaKey(area);

        if (key != null) {
            return removeArea(key);
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

    public Integer getAreaKey(CuboidArea area) {

        for (Map.Entry<Integer, CuboidArea> entry : areas.entrySet()) {
            if (entry.getValue() == area) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Set<Integer> getAreasKey() {

        return areas.keySet();
    }

    public Map<Integer, CuboidArea> getIdsAndAreas() {

        return areas;
    }

    public Collection<CuboidArea> getAreas() {

        return areas.values();
    }

    /*
     public boolean isCollision(CuboidArea area2) {

     for (CuboidArea area1 : areas.values()) {
     if (area1.isCollision(area2)) {
     return true;
     }
     }

     return false;
     }
     */
    public boolean isLocationInside(Location loc) {

        for (CuboidArea area1 : areas.values()) {
            if (area1.isLocationInside(loc)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {

        return name;
    }

    protected void setName(String newName) {

        setAutoSave(false);
        Factoid.getStorage().removeLand(this);
        this.name = newName;
        setAutoSave(true);
        doSave();
    }

    public PlayerContainer getOwner() {

        return owner;
    }

    public boolean isOwner(String playerName) {

        return owner.hasAccess(playerName);
    }

    public Faction getFactionTerritory() {

        return factionTerritory;
    }

    public void setFactionTerritory(Faction faction) {

        this.factionTerritory = faction;
        for (Land child : children.values()) {
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

        if (residents.remove(resident)) {
            doSave();
            return true;
        }

        return false;
    }

    public final Set<PlayerContainer> getResidents() {

        return residents;
    }

    public boolean isResident(String playerName) {

        for (PlayerContainer resident : residents) {
            if (resident.hasAccess(playerName)) {
                return true;
            }
        }
        return false;
    }

    public void addBanned(PlayerContainer banned) {

        banneds.add(banned);
        doSave();

        // Start Event
        Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
                new PlayerContainerLandBanEvent(this, banned));
    }

    public boolean removeBanned(PlayerContainer banned) {

        if (banneds.remove(banned)) {
            doSave();
            return true;
        }

        return false;
    }

    public final Set<PlayerContainer> getBanneds() {

        return banneds;
    }

    public boolean isBanned(String playerName) {

        for (PlayerContainer banned : banneds) {
            if (banned.hasAccess(playerName)) {
                return true;
            }
        }
        return false;
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

        for (int t = 0; t < gen; t++) {
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    public boolean isDescendants(Land land) {

        if (land == this) {
            return true;
        }

        for (Land landT : children.values()) {
            if (landT.isDescendants(land) == true) {
                return true;
            }
        }

        return false;
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

    public Collection<Land> getChildren() {

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

        if (autoSave) {
            forceSave();
        }
    }

    protected Boolean checkLandPermissionAndInherit(String playerName, PermissionType pt, boolean onlyInherit) {

        Boolean permValue;

        if ((permValue = getPermission(playerName, pt, onlyInherit)) != null) {
            return permValue;
        } else if (parent != null) {
            return parent.checkPermissionAndInherit(playerName, pt, true);
        }

        return Factoid.getLands().getPermissionInWorld(worldName, playerName, pt, true);
    }

    protected LandFlag getLandFlagAndInherit(FlagType ft, boolean onlyInherit) {

        LandFlag flag;

        if ((flag = getFlag(ft, onlyInherit)) != null) {
            return flag;
        } else if (parent != null) {
            return parent.getFlagAndInherit(ft, true);
        }

        return Factoid.getLands().getFlagInWorld(worldName, ft, true);
    }

    public void addMoney(double money) {

        this.money += money;
        doSave();
    }

    public void substractMoney(double money) {

        this.money -= money;
        doSave();
    }

    public double getMoney() {

        return money;
    }

    public void addPlayerNotify(String playerName) {

        playerNotify.add(playerName.toLowerCase());
        doSave();
    }

    public boolean removePlayerNotify(String playerName) {

        boolean ret = playerNotify.remove(playerName.toLowerCase());
        doSave();

        return ret;
    }

    public boolean isPlayerNotify(String playerName) {

        return playerNotify.contains(playerName.toLowerCase());
    }

    public Set<String> getPlayersNotify() {

        return playerNotify;
    }

    public void addPlayerInLand(Player player) {

        playersInLand.add(player);
    }

    public boolean removePlayerInLand(Player player) {

        return playersInLand.remove(player);
    }

    // No parent verify
    public boolean isPlayerInLand(Player player) {

        return playersInLand.contains(player);
    }

    public boolean isPlayerinLandNoVanish(Player player, Player fromPlayer) {

        if (playersInLand.contains(player)
                && (!Factoid.getPlayerConf().isVanished(player) || Factoid.getPlayerConf().get(fromPlayer).isAdminMod())) {
            return true;
        }

        // Check Chidren
        for (Land landChild : children.values()) {
            if (landChild.isPlayerinLandNoVanish(player, fromPlayer)) {
                return true;
            }
        }

        return false;
    }

    // No parent verify
    public Set<Player> getPlayersInLand() {

        return playersInLand;
    }

    public Set<Player> getPlayersInLandNoVanish(Player fromPlayer) {

        Set<Player> playerList = new HashSet<Player>();

        for (Player player : playersInLand) {
            if (!Factoid.getPlayerConf().isVanished(player) || Factoid.getPlayerConf().get(fromPlayer).isAdminMod()) {
                playerList.add(player);
                for (Land landChild : children.values()) {
                    playerList.addAll(landChild.getPlayersInLandNoVanish(fromPlayer));
                }
            }
        }

        return playerList;
    }
}
