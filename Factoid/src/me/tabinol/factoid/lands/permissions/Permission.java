package me.tabinol.factoid.lands.permissions;

public class Permission {
    
    PermissionType permType;
    boolean value;
    boolean heritable;
    
    public Permission(final PermissionType permType, final boolean value, final boolean heritable) {
        
        this.permType = permType;
        this.value = value;
        this.heritable = heritable;
    }
    
    public PermissionType getPermType() {
        
        return permType;
    }
    
    public boolean getValue() {
        
        return value;
    }
    
    public boolean isHeritable() {
        
        return heritable;
    }
    
    @Override
    public String toString() {
        
        return permType.toString() + ":" + value + ":" + heritable;
    }
}
