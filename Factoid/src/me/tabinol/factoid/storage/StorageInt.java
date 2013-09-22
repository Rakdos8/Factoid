package me.tabinol.factoid.storage;

import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;

public interface StorageInt {
    
    public void loadAll();
    
    public void saveLand(Land land);
    
    public void saveFaction(Faction faction);
    
}
