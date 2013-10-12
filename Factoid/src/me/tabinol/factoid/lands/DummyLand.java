package me.tabinol.factoid.lands;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class DummyLand {

    private TreeMap<PlayerContainer, EnumMap<PermissionType,Permission>> permissions = new TreeMap<>(); // String for playerName
    private EnumMap<FlagType,LandFlag> flags = new EnumMap<>(FlagType.class);
    
    public DummyLand() {
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

    public void addFlag(LandFlag flag) {
        
        flags.put(flag.getFlagType(), flag);
        doSave();
    }
    
    public boolean removeFlag(FlagType flagType) {
        
        if(flags.remove(flagType) == null) {
            return false;
        }
        doSave();
        return true;
    }
    
    public Collection<LandFlag> getFlags() {
        
        return flags.values();
    }
    
    protected void doSave() {
        // Empty, No save in DummyLand
    }
}
