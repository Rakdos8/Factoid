package me.tabinol.factoid.storage;

public abstract class Storage implements StorageInt {

    protected static boolean inLoad = true; // True if the Database is in Loaded

    public Storage() {
    }
    
    public static boolean isInLoad() {
        
        return inLoad;
    }
}
