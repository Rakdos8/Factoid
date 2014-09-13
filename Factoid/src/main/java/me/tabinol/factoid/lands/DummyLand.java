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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class DummyLand.
 */
public class DummyLand {

    /** The permissions. */
    protected TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>> permissions; // String for playerName
    
    /** The flags. */
    protected TreeMap<FlagType, LandFlag> flags;
    
    /** The world name. */
    protected String worldName;

    /**
     * Instantiates a new dummy land.
     *
     * @param worldName the world name
     */
    public DummyLand(String worldName) {

        permissions = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        flags = new TreeMap<FlagType, LandFlag>();
        this.worldName = worldName;
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    public String getWorldName() {

        return worldName;
    }

    /**
     * Gets the world.
     *
     * @return the world
     */
    public World getWorld() {

        return Factoid.getThisPlugin().getServer().getWorld(worldName);
    }

    /**
     * Adds the permission.
     *
     * @param pc the pc
     * @param perm the perm
     */
    public void addPermission(PlayerContainer pc, Permission perm) {

        TreeMap<PermissionType, Permission> permPlayer;

        if (this instanceof Land) {
            pc.setLand((Land) this);
        }
        
        if (!permissions.containsKey(pc)) {
            permPlayer = new TreeMap<PermissionType, Permission>();
            permissions.put(pc, permPlayer);
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave();

        // Start Event
        if (this instanceof Land && perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                && perm.getValue() != perm.getPermType().getDefaultValue()) {
            Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
                    new PlayerContainerAddNoEnterEvent((Land) this, pc));
        }

    }

    /**
     * Removes the permission.
     *
     * @param pc the pc
     * @param permType the perm type
     * @return true, if successful
     */
    public boolean removePermission(PlayerContainer pc, PermissionType permType) {

        TreeMap<PermissionType, Permission> permPlayer;

        if (!permissions.containsKey(pc)) {
            return false;
        }
        permPlayer = permissions.get(pc);
        if (permPlayer.remove(permType) == null) {
            return false;
        }

        // remove key for PC if it is empty
        if (permPlayer.isEmpty()) {
            permissions.remove(pc);
        }

        doSave();
        return true;
    }

    /**
     * Gets the sets the pc have permission.
     *
     * @return the sets the pc have permission
     */
    public final Set<PlayerContainer> getSetPCHavePermission() {

        return permissions.keySet();
    }

    /**
     * Gets the permissions for pc.
     *
     * @param pc the pc
     * @return the permissions for pc
     */
    public final Collection<Permission> getPermissionsForPC(PlayerContainer pc) {

        return permissions.get(pc).values();
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @return the boolean
     */
    public Boolean checkPermissionAndInherit(Player player, PermissionType pt) {

        return checkPermissionAndInherit(player, pt, false);
    }

    /**
     * Check permission no inherit.
     *
     * @param player the player
     * @param pt the pt
     * @return the boolean
     */
    public Boolean checkPermissionNoInherit(Player player, PermissionType pt) {

        return getPermission(player, pt, false);
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the boolean
     */
    protected Boolean checkPermissionAndInherit(Player player, PermissionType pt, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).checkLandPermissionAndInherit(player, pt, onlyInherit);
        }
        return Factoid.getLands().getPermissionInWorld(worldName, player, pt, onlyInherit);
    }

    /**
     * Gets the permission.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the permission
     */
    protected Boolean getPermission(Player player, PermissionType pt, boolean onlyInherit) {

        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            if (permissionEntry.getKey().hasAccess(player)) {
                Permission perm = permissionEntry.getValue().get(pt);
                if (perm != null) {
                    Factoid.getLog().write("Container: " + permissionEntry.getKey().toString() + ", PermissionType: " + perm.getPermType() + ", Value: " + perm.getValue() + ", Heritable: " + perm.isHeritable());
                    if ((onlyInherit && perm.isHeritable()) || !onlyInherit) {
                        return perm.getValue();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(LandFlag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();
    }

    /**
     * Removes the flag.
     *
     * @param flagType the flag type
     * @return true, if successful
     */
    public boolean removeFlag(FlagType flagType) {

        if (flags.remove(flagType) == null) {
            return false;
        }
        doSave();
        return true;
    }

    /**
     * Gets the flags.
     *
     * @return the flags
     */
    public Collection<LandFlag> getFlags() {

        return flags.values();
    }

    /**
     * Gets the flag and inherit.
     *
     * @param ft the ft
     * @return the flag and inherit
     */
    public LandFlag getFlagAndInherit(FlagType ft) {

        return getFlagAndInherit(ft, false);
    }

    /**
     * Gets the flag no inherit.
     *
     * @param ft the ft
     * @return the flag no inherit
     */
    public LandFlag getFlagNoInherit(FlagType ft) {

        return getFlag(ft, false);
    }

    /**
     * Gets the flag and inherit.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag and inherit
     */
    protected LandFlag getFlagAndInherit(FlagType ft, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).getLandFlagAndInherit(ft, onlyInherit);
        }
        return Factoid.getLands().getFlagInWorld(worldName, ft, onlyInherit);
    }

    /**
     * Gets the flag.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag
     */
    protected LandFlag getFlag(FlagType ft, boolean onlyInherit) {

        LandFlag flag = flags.get(ft);
        if (flag != null) {
            Factoid.getLog().write("Flag: " + flag.toString());

            if ((onlyInherit && flag.isHeritable()) || !onlyInherit) {
                return flag;
            }
        }

        return null;
    }

    /**
     * Do save.
     */
    protected void doSave() {

        if (this instanceof Land) {
            ((Land) this).doSave();
        }
    }
}
