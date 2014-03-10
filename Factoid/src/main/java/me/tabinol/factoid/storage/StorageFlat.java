package me.tabinol.factoid.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.exceptions.FileLoadException;
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
    public static final int LAND_VERSION = Factoid.getMavenAppProperties().getPropertyInt("landVersion");
    public static final int FACTION_VERSION = Factoid.getMavenAppProperties().getPropertyInt("factionVersion");
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
                loadFaction(file);
                loadedfactions++;
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
                    loadLand(file);
                    loadedlands++;
                }
            }
            pass++;
        }
        Factoid.getLog().write(loadedlands + " land(s) loaded.");
    }

    private void loadFaction(File file) {

        Faction faction;
        ConfLoader cf = null;

        try {
            cf = new ConfLoader(file);
            String str;
            int version = cf.getVersion();
            faction = new Faction(cf.getName());
            cf.readParam();
            while ((str = cf.getNextString()) != null) {
                faction.addPlayer(str);
            }

            cf.close();

            // Catch errors here
        } catch (NullPointerException ex) {
            try {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Problem with parameter.");
            } catch (FileLoadException ex2) {
                // Catch load
                return;
            }
        } catch (FileLoadException ex) {
            // Catch load
            return;
        }

        Factoid.getFactions().createFaction(faction);
    }

    private void loadLand(File file) {

        ConfLoader cf = null;
        String landName;
        Land land = null;
        Map<Integer, CuboidArea> areas = new TreeMap<Integer, CuboidArea>();
        boolean isLandCreated = false;
        PlayerContainer owner;
        String parentName;
        String factionTerritory;
        Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
        Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
        Map<PlayerContainer, EnumMap<PermissionType, Permission>> permissions
                = new TreeMap<PlayerContainer, EnumMap<PermissionType, Permission>>();
        Set<LandFlag> flags = new HashSet<LandFlag>();
        short priority;
        double money;
        Set<String> pNotifs = new TreeSet<String>();

        try {
            cf = new ConfLoader(file);
            String str;
            int version = cf.getVersion();
            landName = cf.getName();
            cf.readParam();
            String[] ownerS = StringChanges.splitAddVoid(cf.getValueString(), ":");
            cf.readParam();
            parentName = cf.getValueString();
            cf.readParam();
            factionTerritory = cf.getValueString();

            // create owner (PlayerContainer)
            owner = PlayerContainer.create(null, PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);

            if (owner == null) {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Invalid owner.");
            }

            cf.readParam();

            // Create areas
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":", 2);
                areas.put(Integer.parseInt(multiStr[0]), CuboidArea.getFromString(multiStr[1]));
            }

            cf.readParam();

            //Residents
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = StringChanges.splitAddVoid(str, ":");
                residents.add(PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]));
            }
            cf.readParam();

            //Banneds
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = StringChanges.splitAddVoid(str, ":");
                banneds.add(PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]));
            }
            cf.readParam();

            //Create permissions
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":");
                EnumMap<PermissionType, Permission> permPlayer;
                PlayerContainer pc = PlayerContainer.create(land, PlayerContainerType.getFromString(multiStr[0]), multiStr[1]);
                PermissionType permType = PermissionType.valueOf(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new EnumMap<PermissionType, Permission>(PermissionType.class);
                    permissions.put(pc, permPlayer);
                } else {
                    permPlayer = permissions.get(pc);
                }
                permPlayer.put(permType, new Permission(permType,
                        Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
            }
            cf.readParam();

            //Create flags
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = StringChanges.splitKeepQuote(str, ":");
                FlagType ft = FlagType.valueOf(multiStr[0]);
                flags.add(new LandFlag(ft, multiStr[1], Boolean.parseBoolean(multiStr[2])));
            }
            cf.readParam();

            //Set Priority
            priority = cf.getValueShort();
            cf.readParam();

            //Money
            money = cf.getValueDouble();
            cf.readParam();

            //Players Notify
            while ((str = cf.getNextString()) != null) {
                pNotifs.add(str);
            }

            cf.close();

            // Catch errors here
        } catch (NullPointerException ex) {
            try {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Problem with parameter.");
            } catch (FileLoadException ex2) {
                // Catch load
                return;
            }
        } catch (FileLoadException ex) {
            // Catch load
            return;
        }

        // Create land
        for (Map.Entry<Integer, CuboidArea> entry : areas.entrySet()) {
            if (!isLandCreated) {
                if (parentName != null) {
                    try {
                        land = Factoid.getLands().createLand(landName, owner, entry.getValue(), Factoid.getLands().getLand(parentName),
                                entry.getKey());
                    } catch (FactoidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                    }
                } else {
                    try {
                        land = Factoid.getLands().createLand(landName, owner, entry.getValue(), null, entry.getKey());
                    } catch (FactoidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                    }
                }
                isLandCreated = true;
            } else {
                land.addArea(entry.getValue(), entry.getKey());
            }
        }

        // Load land params form memory
        if (factionTerritory != null) {
            land.setFactionTerritory(Factoid.getFactions().getFaction(factionTerritory));
        }
        for (PlayerContainer resident : residents) {
            land.addResident(resident);
        }
        for (PlayerContainer banned : banneds) {
            land.addResident(banned);
        }
        for (Map.Entry<PlayerContainer, EnumMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (EnumMap.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (LandFlag flag : flags) {
            land.addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (String pNotif : pNotifs) {
            land.addPlayerNotify(pNotif);
        }
    }

    @Override
    public void saveLand(Land land) {
        try {
            ArrayList<String> strs;

            if (inLoad) {
                return;
            }

            Factoid.getLog().write("Saving land: " + land.getName());
            ConfBuilder cb = new ConfBuilder(land.getName(), getLandFile(land), LAND_VERSION);
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

            cb.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on saving Faction: " + land.getName(), ex);
        }
    }

    @Override
    public void removeLand(Land land) {

        getLandFile(land).delete();
    }

    @Override
    public void saveFaction(Faction faction) {
        try {
            if (inLoad) {
                return;
            }

            Factoid.getLog().write("Saving faction: " + faction.getName());
            ConfBuilder cb = new ConfBuilder(faction.getName(), getFactionFile(faction), FACTION_VERSION);
            cb.writeParam("Players", (String[]) faction.getPlayers().toArray());

            cb.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on saving Faction: " + faction.getName(), ex);
        }
    }

    @Override
    public void removeFaction(Faction faction) {

        getFactionFile(faction).delete();
    }
}