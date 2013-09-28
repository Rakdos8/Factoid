package me.tabinol.factoid.storage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;

public class StorageFlat extends Storage implements StorageInt {

    private String factionPath;
    private String landsDir;
    
    public StorageFlat() {
        
        super();
        
        createDirFiles();
    }
    
    private void createDirFiles() {
        
        factionPath = Factoid.getThisPlugin().getDataFolder() + "/" + "factions.conf";
        landsDir = Factoid.getThisPlugin().getDataFolder() + "/" + "lands";
        
        // Create lands dir
        File file = new File(landsDir);
        if (!file.exists()) {
            file.mkdir();
        }
        
        // Create faction.conf
        file = new File(factionPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void loadAll() {
        loadFactions();
        loadLands();
    }
    
    private void loadFactions() {
        
    }

    private void loadLands() {
        
    }
    
    @Override
    public void addLand(Land land) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLand(Land land) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addFaction(Faction faction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFaction(Faction faction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
