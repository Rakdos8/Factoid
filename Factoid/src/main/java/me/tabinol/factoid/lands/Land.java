/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.lands;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerContainerLandBanEvent;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class Land.
 */
public class Land extends DummyLand {

    /** The Constant DEFAULT_PRIORITY. */
    public static final short DEFAULT_PRIORITY = 10;
    
    /** The Constant MINIM_PRIORITY. */
    public static final short MINIM_PRIORITY = 0;
    
    /** The Constant MAXIM_PRIORITY. */
    public static final short MAXIM_PRIORITY = 100;
    
    /** The uuid. */
    private final UUID uuid;
    
    /** The name. */
    private String name;
    
    /** The areas. */
    private Map<Integer, CuboidArea> areas = new TreeMap<Integer, CuboidArea>();
    
    /** The children. */
    private Map<UUID, Land> children = new TreeMap<UUID, Land>();
    
    /** The priority. */
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    
    /** The genealogy. */
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    
    /** The parent. */
    private Land parent = null;
    
    /** The owner. */
    private PlayerContainer owner;
    
    /** The residents. */
    private Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
    
    /** The banneds. */
    private Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
    
    /** The auto save. */
    private boolean autoSave = true;
    
    /** The faction territory. */
    private Faction factionTerritory = null;
    
    /** The money. */
    private double money = 0L;
    
    /** The player notify. */
    private Set<PlayerContainerPlayer> playerNotify = new TreeSet<PlayerContainerPlayer>();
    
    /** The players in land. */
    private final Set<Player> playersInLand = new HashSet<Player>();
    // Economy
    /** The for sale. */
    private boolean forSale = false;
    
    /** The for sale sign location */
    private Location forSaleSignLoc = null;
    
    /** The sale price. */
    private double salePrice = 0;
    
    /** The for rent. */
    private boolean forRent = false;
    
    /** The for rent sign location */
    private Location forRentSignLoc = null;
    
    /** The rent price. */
    private double rentPrice = 0;
    
    /** The rent renew. */
    private int rentRenew = 0; // How many days before renew?
    
    /** The rent auto renew. */
    private boolean rentAutoRenew = false;
    
    /** The rented. */
    private boolean rented = false;
    
    /** The tenant. */
    private PlayerContainerPlayer tenant = null;
    
    /** The last payment. */
    private Timestamp lastPayment = new Timestamp(0);

    // Please use createLand in Lands class to create a Land
    /**
     * Instantiates a new land.
     *
     * @param landName the land name
     * @param uuid the uuid
     * @param owner the owner
     * @param area the area
     * @param genealogy the genealogy
     * @param parent the parent
     * @param areaId the area id
     */
    protected Land(String landName, UUID uuid, PlayerContainer owner,
            CuboidArea area, int genealogy, Land parent, int areaId) {

        super(area.getWorldName().toLowerCase());
        this.uuid = uuid;
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
                flags = (TreeMap<FlagType, LandFlag>) Factoid.getLands().defaultConf.flags.clone();
            }
            copyPerms();
        }
        addArea(area, areaId);
    }

    /**
     * Sets the default.
     */
    public void setDefault() {
        owner = new PlayerContainerNobody();
        residents = new TreeSet<PlayerContainer>();
        playerNotify = new TreeSet<PlayerContainerPlayer>();
        permissions = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        flags = (TreeMap<FlagType, LandFlag>) Factoid.getLands().defaultConf.flags.clone();
        copyPerms();
        doSave();
    }

    /**
     * Copy perms.
     */
    private void copyPerms() {

        for (PlayerContainer pc : Factoid.getLands().defaultConf.permissions.keySet()) {
            permissions.put(PlayerContainer.create(this, pc.getContainerType(), pc.getName()),
                    (TreeMap<PermissionType, Permission>) Factoid.getLands().defaultConf.permissions.get(pc).clone());
        }
    }

    /**
     * Adds the area.
     *
     * @param area the area
     */
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

    /**
     * Adds the area.
     *
     * @param area the area
     * @param price the price
     * @param mustPay If the owner has to pay
     */
    public void addArea(CuboidArea area, double price, boolean mustPay) {

        if(mustPay) {
        	Factoid.getLands().getPriceFromPlayer(worldName, owner, price);
        }
        addArea(area);
    }

    /**
     * Adds the area.
     *
     * @param area the area
     * @param key the key
     */
    public void addArea(CuboidArea area, int key) {

        area.setLand(this);
        areas.put(key, area);
        Factoid.getLands().addAreaToList(area);
        doSave();
    }

    /**
     * Removes the area.
     *
     * @param key the key
     * @return true, if successful
     */
    public boolean removeArea(int key) {

        CuboidArea area;

        if ((area = areas.remove(key)) != null) {
            Factoid.getLands().removeAreaFromList(area);
            doSave();
            return true;
        }

        return false;
    }

    /**
     * Removes the area.
     *
     * @param area the area
     * @return true, if successful
     */
    public boolean removeArea(CuboidArea area) {

        Integer key = getAreaKey(area);

        if (key != null) {
            return removeArea(key);
        }

        return false;
    }

    /**
     * Replace area.
     *
     * @param key the key
     * @param newArea the new area
     * @param price the price
     * @param mustPay If the owner has to pay
     * @return true, if successful
     */
    public boolean replaceArea(int key, CuboidArea newArea, double price, boolean mustPay) {

        if (mustPay) {
        	Factoid.getLands().getPriceFromPlayer(worldName, owner, price);
        }

        return replaceArea(key, newArea);
    }

    /**
     * Replace area.
     *
     * @param key the key
     * @param newArea the new area
     * @return true, if successful
     */
    public boolean replaceArea(int key, CuboidArea newArea) {

        CuboidArea area;

        if ((area = areas.remove(key)) != null) {
            Factoid.getLands().removeAreaFromList(area);
            newArea.setLand(this);
            areas.put(key, newArea);
            Factoid.getLands().addAreaToList(newArea);
            doSave();
            return true;
        }

        return false;
    }

    /**
     * Gets the area.
     *
     * @param key the key
     * @return the area
     */
    public CuboidArea getArea(int key) {

        return areas.get(key);
    }

    /**
     * Gets the area key.
     *
     * @param area the area
     * @return the area key
     */
    public Integer getAreaKey(CuboidArea area) {

        for (Map.Entry<Integer, CuboidArea> entry : areas.entrySet()) {
            if (entry.getValue() == area) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the areas key.
     *
     * @return the areas key
     */
    public Set<Integer> getAreasKey() {

        return areas.keySet();
    }

    /**
     * Gets the ids and areas.
     *
     * @return the ids and areas
     */
    public Map<Integer, CuboidArea> getIdsAndAreas() {

        return areas;
    }

    /**
     * Gets the areas.
     *
     * @return the areas
     */
    public Collection<CuboidArea> getAreas() {

        return areas.values();
    }

    /**
     * Checks if is location inside.
     *
     * @param loc the loc
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc) {

        for (CuboidArea area1 : areas.values()) {
            if (area1.isLocationInside(loc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the nb blocks outside.
     *
     * @param areaComp the area comp
     * @return the nb blocks outside
     */
    public long getNbBlocksOutside(CuboidArea areaComp) {

        // Get the Volume of the area
        long volume = areaComp.getTotalBlock();

        // Put the list of areas in the land to an array
        CuboidArea[] areaAr = areas.values().toArray(new CuboidArea[0]);

        for (int t = 0; t < areaAr.length; t++) {

            // Get the result collision cuboid
            CuboidArea colArea = areaAr[t].getCollisionArea(areaComp);

            if (colArea != null) {

                // Substract the volume of collision
                volume -= colArea.getTotalBlock();

                // Compare each next areas to the collision area and add
                // the collision of the collision to cancel multiple subtracts
                for (int a = t + 1; a < areaAr.length; a++) {

                    CuboidArea colAreaToNextArea = areaAr[a].getCollisionArea(colArea);

                    if (colAreaToNextArea != null) {
                        volume += colAreaToNextArea.getTotalBlock();
                    }
                }
            }
        }

        return volume;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUUID() {

        return uuid;
    }

    /**
     * Sets the name.
     *
     * @param newName the new name
     */
    protected void setName(String newName) {

        setAutoSave(false);
        Factoid.getStorage().removeLand(this);
        this.name = newName;
        setAutoSave(true);
        doSave();
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public PlayerContainer getOwner() {

        return owner;
    }

    /**
     * Checks if is owner.
     *
     * @param player the player
     * @return true, if is owner
     */
    public boolean isOwner(Player player) {

        return owner.hasAccess(player);
    }

    /**
     * Gets the faction territory.
     *
     * @return the faction territory
     */
    public Faction getFactionTerritory() {

        return factionTerritory;
    }

    /**
     * Sets the faction territory.
     *
     * @param faction the new faction territory
     */
    public void setFactionTerritory(Faction faction) {

        this.factionTerritory = faction;
        for (Land child : children.values()) {
            child.setFactionTerritory(faction);
        }
        doSave();
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(PlayerContainer owner) {

        this.owner = owner;
        doSave();
    }

    /**
     * Adds the resident.
     *
     * @param resident the resident
     */
    public void addResident(PlayerContainer resident) {

        resident.setLand(this);
        residents.add(resident);
        doSave();
    }

    /**
     * Removes the resident.
     *
     * @param resident the resident
     * @return true, if successful
     */
    public boolean removeResident(PlayerContainer resident) {

        if (residents.remove(resident)) {
            doSave();
            return true;
        }

        return false;
    }

    /**
     * Gets the residents.
     *
     * @return the residents
     */
    public final Set<PlayerContainer> getResidents() {

        return residents;
    }

    /**
     * Checks if is resident.
     *
     * @param player the player
     * @return true, if is resident
     */
    public boolean isResident(Player player) {

        for (PlayerContainer resident : residents) {
            if (resident.hasAccess(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the banned.
     *
     * @param banned the banned
     */
    public void addBanned(PlayerContainer banned) {

        banned.setLand(this);
        banneds.add(banned);
        doSave();

        // Start Event
        Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
                new PlayerContainerLandBanEvent(this, banned));
    }

    /**
     * Removes the banned.
     *
     * @param banned the banned
     * @return true, if successful
     */
    public boolean removeBanned(PlayerContainer banned) {

        if (banneds.remove(banned)) {
            doSave();
            return true;
        }

        return false;
    }

    /**
     * Gets the banneds.
     *
     * @return the banneds
     */
    public final Set<PlayerContainer> getBanneds() {

        return banneds;
    }

    /**
     * Checks if is banned.
     *
     * @param player the player
     * @return true, if is banned
     */
    public boolean isBanned(Player player) {

        for (PlayerContainer banned : banneds) {
            if (banned.hasAccess(player)) {
                return true;
            }
        }
        return false;
    }

    // Note : a child get the parent priority
    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public short getPriority() {

        if (parent != null) {
            return parent.getPriority();
        }

        return priority;
    }

    /**
     * Gets the genealogy.
     *
     * @return the genealogy
     */
    public int getGenealogy() {

        return genealogy;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(short priority) {

        this.priority = priority;
        doSave();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Land getParent() {

        return parent;
    }

    /**
     * Gets the ancestor.
     *
     * @param gen the gen
     * @return the ancestor
     */
    public Land getAncestor(int gen) { // 1 parent, 2 grand-parent, 3 ...

        Land ancestor = this;

        for (int t = 0; t < gen; t++) {
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    /**
     * Checks if is descendants.
     *
     * @param land the land
     * @return true, if is descendants
     */
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

    /**
     * Adds the child.
     *
     * @param land the land
     */
    private void addChild(Land land) {

        children.put(land.uuid, land);
        doSave();
    }

    /**
     * Removes the child.
     *
     * @param uuid the uuid
     */
    protected void removeChild(UUID uuid) {

        children.remove(uuid);
        doSave();
    }

    /**
     * Gets the child.
     *
     * @param uuid the uuid
     * @return the child
     */
    public Land getChild(UUID uuid) {

        return children.get(uuid);
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Collection<Land> getChildren() {

        return children.values();
    }

    /**
     * Sets the auto save.
     *
     * @param autoSave the new auto save
     */
    public void setAutoSave(boolean autoSave) {

        this.autoSave = autoSave;
    }

    /**
     * Force save.
     */
    public void forceSave() {

        Factoid.getStorage().saveLand(this);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.lands.DummyLand#doSave()
     */
    @Override
    protected void doSave() {

        if (autoSave) {
            forceSave();
        }
    }

    /**
     * Check land permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the boolean
     */
    protected Boolean checkLandPermissionAndInherit(Player player, PermissionType pt, boolean onlyInherit) {

        Boolean permValue;

        if ((permValue = getPermission(player, pt, onlyInherit)) != null) {
            return permValue;
        } else if (parent != null) {
            return parent.checkPermissionAndInherit(player, pt, true);
        }

        return Factoid.getLands().getPermissionInWorld(worldName, player, pt, true);
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the land flag and inherit
     */
    protected LandFlag getLandFlagAndInherit(FlagType ft, boolean onlyInherit) {

        LandFlag flag;

        if ((flag = getFlag(ft, onlyInherit)) != null) {
            return flag;
        } else if (parent != null) {
            return parent.getFlagAndInherit(ft, true);
        }

        return Factoid.getLands().getFlagInWorld(worldName, ft, true);
    }

    /**
     * Adds the money.
     *
     * @param money the money
     */
    public void addMoney(double money) {

        this.money += money;
        doSave();
    }

    /**
     * Substract money.
     *
     * @param money the money
     */
    public void substractMoney(double money) {

        this.money -= money;
        doSave();
    }

    /**
     * Gets the money.
     *
     * @return the money
     */
    public double getMoney() {

        return money;
    }

    /**
     * Adds the player notify.
     *
     * @param player the player
     */
    public void addPlayerNotify(PlayerContainerPlayer player) {

        playerNotify.add(player);
        doSave();
    }

    /**
     * Removes the player notify.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerNotify(PlayerContainerPlayer player) {

        boolean ret = playerNotify.remove(player);
        doSave();

        return ret;
    }

    /**
     * Checks if is player notify.
     *
     * @param player the player
     * @return true, if is player notify
     */
    public boolean isPlayerNotify(PlayerContainerPlayer player) {

        return playerNotify.contains(player);
    }

    /**
     * Gets the players notify.
     *
     * @return the players notify
     */
    public Set<PlayerContainerPlayer> getPlayersNotify() {

        return playerNotify;
    }

    /**
     * Adds the player in land.
     *
     * @param player the player
     */
    public void addPlayerInLand(Player player) {

        playersInLand.add(player);
    }

    /**
     * Removes the player in land.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerInLand(Player player) {

        return playersInLand.remove(player);
    }

    // No parent verify
    /**
     * Checks if is player in land.
     *
     * @param player the player
     * @return true, if is player in land
     */
    public boolean isPlayerInLand(Player player) {

        return playersInLand.contains(player);
    }

    /**
     * Checks if is playerin land no vanish.
     *
     * @param player the player
     * @param fromPlayer the from player
     * @return true, if is playerin land no vanish
     */
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
    /**
     * Gets the players in land.
     *
     * @return the players in land
     */
    public Set<Player> getPlayersInLand() {

        return playersInLand;
    }

    /**
     * Gets the players in land no vanish.
     *
     * @param fromPlayer the from player
     * @return the players in land no vanish
     */
    public Set<Player> getPlayersInLandNoVanish(Player fromPlayer) {

        Set<Player> playerList = new HashSet<Player>();

        for (Player player : playersInLand) {
            if (!Factoid.getPlayerConf().isVanished(player) || Factoid.getPlayerConf().get(fromPlayer).isAdminMod()) {
                playerList.add(player);
            }
        }
        for (Land landChild : children.values()) {
            playerList.addAll(landChild.getPlayersInLandNoVanish(fromPlayer));
        }

        return playerList;
    }

    /**
     * Checks if is for sale.
     *
     * @return true, if is for sale
     */
    public boolean isForSale() {

        return forSale;
    }

    /**
     * Sets the for sale.
     *
     * @param isForSale the is for sale
     * @param salePrice the sale price
     * @param sign the sign
     */
    public void setForSale(boolean isForSale, double salePrice, Location signLoc) {

        forSale = isForSale;
        if (forSale) {
            this.salePrice = salePrice;
            this.forSaleSignLoc = signLoc;
            Factoid.getLands().addForSale(this);
        } else {
            this.salePrice = 0;
            this.forSaleSignLoc = null;
            Factoid.getLands().removeForSale(this);
        }
        doSave();
    }

    public Location getSaleSignLoc() {
    	
    	return forSaleSignLoc;
    }

    public void setSaleSignLoc(Location forSaleSignLoc) {
    	
    	this.forSaleSignLoc = forSaleSignLoc;
    	doSave();
    }

    /**
     * Gets the sale price.
     *
     * @return the sale price
     */
    public double getSalePrice() {

        return salePrice;
    }

    /**
     * Checks if is for rent.
     *
     * @return true, if is for rent
     */
    public boolean isForRent() {

        return forRent;
    }

    /**
     * Sets the for rent.
     *
     * @param rentPrice the rent price
     * @param rentRenew the rent renew
     * @param rentAutoRenew the rent auto renew
     */
    public void setForRent(double rentPrice, int rentRenew, boolean rentAutoRenew, Location signLoc) {

        forRent = true;
        this.rentPrice = rentPrice;
        this.rentRenew = rentRenew;
        this.rentAutoRenew = rentAutoRenew;
        this.forRentSignLoc = signLoc;
        Factoid.getLands().addForRent(this);
        doSave();
    }

    public Location getRentSignLoc() {
    	
    	return forRentSignLoc;
    }
    
    public void setRentSignLoc(Location forRentSignLoc) {
    	
    	this.forRentSignLoc = forRentSignLoc;
    	doSave();
    }
    
    /**
     * Un set for rent.
     */
    public void unSetForRent() {

        forRent = false;
        rentPrice = 0;
        rentRenew = 0;
        rentAutoRenew = false;
        forRentSignLoc = null;
        Factoid.getLands().removeForRent(this);
        doSave();
    }

    /**
     * Gets the rent price.
     *
     * @return the rent price
     */
    public double getRentPrice() {

        return rentPrice;
    }

    /**
     * Gets the rent renew.
     *
     * @return the rent renew
     */
    public int getRentRenew() {

        return rentRenew;
    }

    /**
     * Gets the rent auto renew.
     *
     * @return the rent auto renew
     */
    public boolean getRentAutoRenew() {

        return rentAutoRenew;
    }

    /**
     * Checks if is rented.
     *
     * @return true, if is rented
     */
    public boolean isRented() {

        return rented;
    }

    /**
     * Sets the rented.
     *
     * @param tenant the new rented
     */
    public void setRented(PlayerContainerPlayer tenant) {

        rented = true;
        this.tenant = tenant;
        updateRentedPayment(); // doSave() done in this method
    }

    /**
     * Update rented payment.
     */
    public void updateRentedPayment() {

        lastPayment = new Timestamp(new Date().getTime());
        doSave();
    }

    /**
     * Un set rented.
     */
    public void unSetRented() {

        rented = false;
        tenant = null;
        lastPayment = new Timestamp(0);
        doSave();
    }

    /**
     * Gets the tenant.
     *
     * @return the tenant
     */
    public PlayerContainerPlayer getTenant() {

        return tenant;
    }

    /**
     * Checks if is tenant.
     *
     * @param player the player
     * @return true, if is tenant
     */
    public boolean isTenant(Player player) {

        return rented && tenant.hasAccess(player);
    }

    /**
     * Sets the last payment time.
     *
     * @param lastPayment the new last payment time
     */
    public void setLastPaymentTime(Timestamp lastPayment) {
    	
    	this.lastPayment = lastPayment;
    	doSave();
    }
    
    /**
     * Gets the last payment time.
     *
     * @return the last payment time
     */
    public Timestamp getLastPaymentTime() {

        return lastPayment;
    }
}
