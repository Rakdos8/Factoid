package me.tabinol.factoid.storage;

import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;

public class StorageFlat extends Storage implements StorageInt {

    public StorageFlat() {
        
        super();
    }
    
    @Override
    public void loadAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveLand(Land land) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveFaction(Faction faction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
