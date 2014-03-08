package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.World;

public class DummyLand {

    protected TreeMap<PlayerContainer, EnumMap<PermissionType, Permission>> permissions; // String for playerName
    protected EnumMap<FlagType, LandFlag> flags;
    protected String worldName;

    public DummyLand(String worldName) {

        permissions = new TreeMap<>();
        flags = new EnumMap<>(FlagType.class);
        this.worldName = worldName;
    }

    public String getWorldName() {

        return worldName;
    }

    public World getWord() {

        return Factoid.getThisPlugin().getServer().getWorld(worldName);
    }

    public void addPermission(PlayerContainer pc, Permission perm) {

        EnumMap<PermissionType, Permission> permPlayer;

        if (!permissions.containsKey(pc)) {
            permPlayer = new EnumMap<>(PermissionType.class);
            permissions.put(pc, permPlayer);
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave();

        // Start Event
        if (this instanceof Land && perm.getPermType() == PermissionType.LAND_ENTER
                && perm.getValue() != PermissionType.LAND_ENTER.baseValue()) {
            Factoid.getThisPlugin().getServer().getPluginManager().callEvent(
                    new PlayerContainerAddNoEnterEvent((Land) this, pc));
        }

    }

    public boolean removePermission(PlayerContainer pc, PermissionType permType) {

        EnumMap<PermissionType, Permission> permPlayer;

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

    public final Set<PlayerContainer> getSetPCHavePermission() {

        return permissions.keySet();
    }

    public final Collection<Permission> getPermissionsForPC(PlayerContainer pc) {

        return permissions.get(pc).values();
    }

    public Boolean checkPermissionAndInherit(String playerName, PermissionType pt) {

        return checkPermissionAndInherit(playerName, pt, false);
    }

    public Boolean checkPermissionNoInherit(String playerName, PermissionType pt) {

        return getPermission(playerName, pt, false);
    }

    protected Boolean checkPermissionAndInherit(String playerName, PermissionType pt, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).checkLandPermissionAndInherit(playerName, pt, onlyInherit);
        }
        return Factoid.getLands().getPermissionInWorld(worldName, playerName, pt, onlyInherit);
    }

    protected Boolean getPermission(String playerName, PermissionType pt, boolean onlyInherit) {

        for (Map.Entry<PlayerContainer, EnumMap<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            if (permissionEntry.getKey().hasAccess(playerName)) {
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

    public void addFlag(LandFlag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();
    }

    public boolean removeFlag(FlagType flagType) {

        if (flags.remove(flagType) == null) {
            return false;
        }
        doSave();
        return true;
    }

    public Collection<LandFlag> getFlags() {

        return flags.values();
    }

    public LandFlag getFlagAndInherit(FlagType ft) {

        return getFlagAndInherit(ft, false);
    }

    public LandFlag getFlagNoInherit(FlagType ft) {

        return getFlag(ft, false);
    }

    protected LandFlag getFlagAndInherit(FlagType ft, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).getLandFlagAndInherit(ft, onlyInherit);
        }
        return Factoid.getLands().getFlagInWorld(worldName, ft, onlyInherit);
    }

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

    protected void doSave() {

        if (this instanceof Land) {
            ((Land) this).doSave();
        }
    }
}
