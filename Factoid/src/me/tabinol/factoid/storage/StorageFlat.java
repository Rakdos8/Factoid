package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;

public class StorageFlat extends Storage implements StorageInt {

    public static final String EXT_CONF = ".conf";
    public static final int FACTIONS = 0;
    public static final int LANDS = 1;
    private String factionsDir;
    private String landsDir;
    
    public StorageFlat() {
        
        super();
        
        createDirFiles();
    }
    
    private void createDirFiles() {
        
        factionsDir = Factoid.getThisPlugin().getDataFolder() + "/" + "factions";
        landsDir = Factoid.getThisPlugin().getDataFolder() + "/" + "lands";
        
        createDir(landsDir);
        createDir(factionsDir);
    }
    
    private void createDir(String dir) {

        File file = new File(dir);

        if (!file.exists()) {
            file.mkdir();
        }
    }
    
    @Override
    public void loadAll() {

        loadFiles(FACTIONS, factionsDir);
        loadFiles(LANDS, landsDir);
    }
    
    private void loadFiles(int filetype, String dirtype) {
        
        File[] files = new File(dirtype).listFiles();
        
        for(File file : files) {
            if(file.isFile() && file.getName().toLowerCase().endsWith(".conf")) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    switch(filetype) {
                        case FACTIONS:
                            loadFaction(br);
                            break;
                        case LANDS:
                            loadLand(br);
                            break;
                        default:
                    }
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void loadFaction(BufferedReader br) {

    }

    private void loadLand(BufferedReader br) {
        
    }
    
    @Override
    public void saveLand(Land land) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLand(Land land) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveFaction(Faction faction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFaction(Faction faction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
