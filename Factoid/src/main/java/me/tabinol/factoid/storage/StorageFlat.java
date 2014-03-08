package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.StringChanges;

public class StorageFlat extends Storage implements StorageInt {

    public static final String EXT_CONF = ".conf";
    public static final int ACTUAL_VERSION = Factoid.getMavenAppProperties().getPropertyInt("landVersion");
    private String factionsDir;
    private String landsDir;

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

        if (files.length == 0) {
            Factoid.getLog().write(loadedfactions + " faction(s) loaded.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    loadFaction(br);
                    br.close();
                    loadedfactions++;
                } catch (IOException ex) {
                    Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "On loading faction: " + file.getName(), ex);
                    Factoid.getLog().write("See console: Error on loading: " + file.getName());
                }
            }
        }
        Factoid.getLog().write(loadedfactions + " faction(s) loaded.");
    }

    private void loadLands() {

        File[] files = new File(landsDir).listFiles();
        int loadedlands = 0;
        int pass = 0;
        boolean empty = false;

        if (files.length == 0) {
            Factoid.getLog().write(loadedlands + " land(s) loaded.");
            return;
        }

        while (!empty) {
            empty = true;
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(pass + EXT_CONF)) {
                    empty = false;
                    try {
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);
                        loadLand(br);
                        br.close();
                        loadedlands++;
                    } catch (IOException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "On loading land: " + file.getName(), ex);
                        Factoid.getLog().write("See console: Error on loading: " + file.getName());
                    }
                }
            }
            pass++;
        }
        Factoid.getLog().write(loadedlands + " land(s) loaded.");
    }

    private void loadFaction(BufferedReader br) {

        ConfLoader cf = new ConfLoader(br);
        String str;
        int version = cf.getVersion();
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
        int version = cf.getVersion();
        String landName = cf.getName();
        Land land = null;
        boolean isLandCreated = false;
        PlayerContainer pc;
        cf.readParam();
        String[] ownerS = StringChanges.splitAddVoid(cf.getValueString(), ":");
        cf.readParam();
        String parentName = cf.getValueString();
        cf.readParam();
        String factionTerritory = cf.getValueString();

        // create owner (PlayerContainer)
        pc = PlayerContainer.create(null, PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);

        cf.readParam();

        // Create Land and add areas
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = str.split(":", 2);
            CuboidArea area = CuboidArea.getFromString(multiStr[1]);
            if (!isLandCreated) {
                if (parentName != null) {
                    try {
                        land = Factoid.getLands().createLand(landName, pc, area, Factoid.getLands().getLand(parentName),
                                Integer.parseInt(multiStr[0]));
                    } catch (FactoidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                    }
                } else {
                    try {
                        land = Factoid.getLands().createLand(landName, pc, area, null, Integer.parseInt(multiStr[0]));
                    } catch (FactoidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                    }
                }
                isLandCreated = true;
            } else {
                land.addArea(area);
            }
        }
        cf.readParam();

        //FactionTerritory
        if (factionTerritory != null) {
            land.setFactionTerritory(Factoid.getFactions().getFaction(factionTerritory));
        }

        //Residents
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = StringChanges.splitAddVoid(str, ":");
            pc = PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]);
            land.addResident(pc);
        }
        cf.readParam();

        //Banneds
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = StringChanges.splitAddVoid(str, ":");
            pc = PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]);
            land.addBanned(pc);
        }
        cf.readParam();

        //Create permissions
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = str.split(":");
            pc = PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]);
            land.addPermission(pc, new Permission(PermissionType.valueOf(multiStr[2]),
                    Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
        }
        cf.readParam();

        //Create flags
        while ((str = cf.getNextString()) != null) {
            String[] multiStr = StringChanges.splitKeepQuote(str, ":");
            FlagType ft = FlagType.valueOf(multiStr[0]);
            land.addFlag(new LandFlag(ft, multiStr[1], Boolean.parseBoolean(multiStr[2])));
        }
        cf.readParam();

        //Set Priority
        land.setPriority(cf.getValueShort());
        cf.readParam();

        //Money
        land.addMoney(cf.getValueDouble());
        cf.readParam();

        //Players Notify
        while ((str = cf.getNextString()) != null) {
            land.addPlayerNotify(str);
        }
        cf.readParam();
    }

    @Override
    public void saveLand(Land land) {

        ArrayList<String> strs;

        if (!inLoad) {
            Factoid.getLog().write("Saving land: " + land.getName());
            ConfBuilder cb = new ConfBuilder(land.getName());
            cb.writeParam("Owner", land.getOwner().toString());

            //Parent
            if (land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {
                cb.writeParam("Parent", land.getParent().getName());
            }

            //factionTerritory
            if (land.getFactionTerritory() == null) {
                cb.writeParam("FactionTerritory", (String) null);
            } else {
                cb.writeParam("FactionTerritory", land.getFactionTerritory().getName());
            }

            //CuboidAreas
            strs = new ArrayList<String>();
            for (int index : land.getAreasKey()) {
                strs.add(index + ":" + land.getArea(index).toString());
            }
            cb.writeParam("CuboidAreas", strs.toArray(new String[0]));

            //Residents
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getResidents()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Residents", strs.toArray(new String[0]));

            //Banneds
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getBanneds()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Banneds", strs.toArray(new String[0]));

            //Permissions
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getSetPCHavePermission()) {
                for (Permission perm : land.getPermissionsForPC(pc)) {
                    strs.add(pc.toString() + ":" + perm.toString());
                }
            }
            cb.writeParam("Permissions", strs.toArray(new String[0]));

            //Flags
            strs = new ArrayList<String>();
            for (LandFlag flag : land.getFlags()) {
                strs.add(flag.toString());
            }
            cb.writeParam("Flags", strs.toArray(new String[0]));

            // Priority
            cb.writeParam("Priority", land.getPriority());

            // Money
            cb.writeParam("Money", land.getMoney());

            // PlayersNotify
            cb.writeParam("PlayersNotify", land.getPlayersNotify().toArray(new String[0]));

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
            Factoid.getLog().write("Saving faction: " + faction.getName());
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
