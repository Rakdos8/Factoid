package me.tabinol.factoid.storage;

public abstract class Storage implements StorageInt {

    protected boolean inLoad = true; // True if the Database is in Loaded

    public Storage() {
    }
    
    public boolean isInLoad() {
        
        return inLoad;
    }
}
