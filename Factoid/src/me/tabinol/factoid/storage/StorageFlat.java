package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerFaction;
import me.tabinol.factoid.playercontainer.PlayerContainerGroup;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;

public class StorageFlat extends Storage implements StorageInt {

    public static final String EXT_CONF = ".conf";
    private String factionsDir;
    private String landsDir;
    private boolean inLoad = false; // True if the Database is in Load

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

    private File getFactionFile(Faction faction) {

        return new File(factionsDir + "/" + faction.getName() + EXT_CONF);
    }

    private File getLandFile(Land land) {

        return new File(landsDir + "/" + land.getName() + "." + land.getGenealogy() + EXT_CONF);
    }

    @Override
    public void loadAll() {

        inLoad = true;
        loadFactions();
        loadLands();
        inLoad = false;
    }

    private void loadFactions() {

        File[] files = new File(factionsDir).listFiles();

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                try {
                    FileReader fr = new FileReader(file);
                    try (BufferedReader br = new BufferedReader(fr)) {
                        loadFaction(br);
                        br.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void loadLands() {

        File[] files = new File(landsDir).listFiles();
        int pass = 0;
        boolean empty = false;

        while (!empty) {
            for (File file : files) {
                empty = true;
                if (file.isFile() && file.getName().toLowerCase().endsWith(pass + "." + EXT_CONF)) {
                    empty = false;
                    try {
                        FileReader fr = new FileReader(file);
                        try (BufferedReader br = new BufferedReader(fr)) {
                            loadLand(br);
                            br.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            pass++;
        }
    }

    private void loadFaction(BufferedReader br) {

        ConfLoader cf = new ConfLoader(br);
        String str;
        Faction faction = new Faction(cf.getName());
        cf.readParam();
        while ((str = cf.getNextString()) != null) {
            faction.addPlayer(str);
        }
        
        Factoid.getFactions().createFaction(faction);
    }

    private void loadLand(BufferedReader br) {

        ConfLoader cf = new ConfLoader(br);
        String str;
        String landName = cf.getName();
        Land land = null;
        boolean isLandCreated = false;
        PlayerContainer owner;
        cf.readParam();
        String ownerType = cf.getValueString();
        cf.readParam();
        String ownerName = cf.getValueString();
        cf.readParam();
        String parentName = cf.getValueString();

        // create owner (PlayerContainer)
        if (ownerType.equals("Faction")) {
            owner = new PlayerContainerFaction(Factoid.getFactions().getFaction(ownerName));
        } else if (ownerType.equals("Group")) {
            owner = new PlayerContainerGroup(ownerName);
        } else {
            owner = new PlayerContainerPlayer(ownerName);
        }
        cf.readParam();

        // Create Land and add areas
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = str.split(":");
            CuboidArea area = new CuboidArea(multiStr[0],
                    Integer.parseInt(multiStr[1]),
                    Integer.parseInt(multiStr[2]),
                    Integer.parseInt(multiStr[3]),
                    Integer.parseInt(multiStr[4]),
                    Integer.parseInt(multiStr[5]),
                    Integer.parseInt(multiStr[6]));
            if (!isLandCreated) {
                if (parentName != null) {
                    land = new Land(landName, owner, area, Factoid.getLands().getLand(parentName));
                } else {
                    land = new Land(landName, owner, area);
                }
                isLandCreated = true;
            } else {
                land.addArea(area);
            }
        }
        
        cf.readParam();
        land.setPriority(cf.getValueShort());
        
        Factoid.getLands().createLand(land);
    }

    @Override
    public void saveLand(Land land) {

        if (!inLoad) {
            ConfBuilder cb = new ConfBuilder(land.getName());
            cb.writeParam("OwnerType", land.getOwner().getContainerType());
            cb.writeParam("Owner", land.getOwner().getName());
            if(land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {   
                cb.writeParam("Parent", land.getParent().getName());
            }
            cb.writeParam("CuboidAreas", land.getAreas());
            cb.writeParam("Priority", land.getPriority());
            cb.save(getLandFile(land));
        }
    }

    @Override
    public void removeLand(Land land) {

        getLandFile(land).delete();
    }

    @Override
    public void saveFaction(Faction faction) {

        if (!inLoad) {
            ConfBuilder cb = new ConfBuilder(faction.getName());
            cb.writeParam("Players", (String[]) faction.getPlayers().toArray());
            cb.save(getFactionFile(faction));
        }
    }

    @Override
    public void removeFaction(Faction faction) {

        getFactionFile(faction).delete();
    }
}
