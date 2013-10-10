package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerFaction;
import me.tabinol.factoid.playercontainer.PlayerContainerGroup;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;

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

        factionsDir = Factoid.getThisPlugin().getDataFolder() + "/" + "factions" + "/";
        landsDir = Factoid.getThisPlugin().getDataFolder() + "/" + "lands" + "/";

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
        int loadedfactions = 0;

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                try {
                    FileReader fr = new FileReader(file);
                    try (BufferedReader br = new BufferedReader(fr)) {
                        loadFaction(br);
                        br.close();
                        loadedfactions++;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
                    Factoid.getLog().write("[Error] '"+ex.getMessage()+"'");
                }
            }
        }
                Factoid.getLog().write("[Factoid] Loaded '"+loadedfactions+"' Factions");
    }

    private void loadLands() {

        File[] files = new File(landsDir).listFiles();
        int loadedlands = 0;
        int pass = 0;
        boolean empty = false;

        if(files.length == 0) {
            return;
        }
        
        while (!empty) {
            for (File file : files) {
                empty = true;
                if (file.isFile() && file.getName().toLowerCase().endsWith(pass + EXT_CONF)) {
                    empty = false;
                    try {
                        FileReader fr = new FileReader(file);
                        try (BufferedReader br = new BufferedReader(fr)) {
                            loadLand(br);
                            br.close();
                            loadedlands++;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, null, ex);
                        Factoid.getLog().write("[Error] '"+ex.getMessage()+"'");
                    }
                }
            }
            pass++;
        }
        Factoid.getLog().write("[Factoid] Loaded '"+loadedlands+"' Lands");
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
        PlayerContainer pc;
        cf.readParam();
        String[] ownerS = cf.getValueString().split(":");
        cf.readParam();
        String parentName = cf.getValueString();

        // create owner (PlayerContainer)
        pc = PlayerContainer.create(PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);

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
                    land = new Land(landName, pc, area, Factoid.getLands().getLand(parentName));
                } else {
                    land = new Land(landName, pc, area);
                }
                isLandCreated = true;
            } else {
                land.addArea(area);
            }
        }
        cf.readParam();
        
        //Create permissions
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = str.split(":");
            pc = PlayerContainer.create(PlayerContainerType.getFromString(multiStr[0]), multiStr[1]);
            land.addPermission(pc, new Permission(PermissionType.getFromString(multiStr[2]),
                    Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
        }
        
        cf.readParam();
        land.setPriority(cf.getValueShort());
        
        Factoid.getLands().createLand(land);
    }

    @Override
    public void saveLand(Land land) {

        ArrayList<String> strs;
        
        if (!inLoad) {
            ConfBuilder cb = new ConfBuilder(land.getName());
            cb.writeParam("Owner", land.getOwner().toString());
            if(land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {   
                cb.writeParam("Parent", land.getParent().getName());
            }
            cb.writeParam("CuboidAreas", land.getAreas());
            
            //permissions
            strs = new ArrayList<>();
            for(PlayerContainer pc : land.getSetPCHavePermission()) {
                for(Permission perm : land.getPermissionsForPC(pc)) {
                    strs.add(pc.toString() + ":" + perm.toString());
                }
            }
            cb.writeParam(landsDir, strs.toArray(new String[0]));
            
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
