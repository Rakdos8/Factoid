/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.exceptions.FileLoadException;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.StringChanges;

public class StorageFlat extends Storage implements StorageInt {

    public static final String EXT_CONF = ".conf";
    public static final int LAND_VERSION = Factoid.getMavenAppProperties().getPropertyInt("landVersion");
    public static final int FACTION_VERSION = Factoid.getMavenAppProperties().getPropertyInt("factionVersion");
    private String factionsDir;
    private String landsDir;
    private boolean toResave = false; // If a new version of .conf file, we need to save again

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

        // New version, we have to save all
        if (toResave) {
            saveAll();
        }
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
        ArrayList<PlayerContainerPlayer> playerNames = new ArrayList<PlayerContainerPlayer>();
        UUID uuid;

        try {
            cf = new ConfLoader(file);
            String str;
            int version = cf.getVersion();
            uuid = cf.getUUID();
            cf.readParam();
            while ((str = cf.getNextString()) != null) {
                playerNames.add((PlayerContainerPlayer) PlayerContainer.getFromString(str));
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

        // Create Faction
        faction = Factoid.getFactions().createFaction(cf.getName(), uuid);
        for (PlayerContainerPlayer player : playerNames) {
            faction.addPlayer(player);
        }
    }

    private void loadLand(File file) {

        int version;
        ConfLoader cf = null;
        UUID uuid;
        String landName;
        Land land = null;
        Map<Integer, CuboidArea> areas = new TreeMap<Integer, CuboidArea>();
        boolean isLandCreated = false;
        PlayerContainer owner;
        String parentName;
        String factionTerritory;
        Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
        Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
        Map<PlayerContainer, TreeMap<PermissionType, Permission>> permissions
                = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        Set<LandFlag> flags = new HashSet<LandFlag>();
        short priority;
        double money;
        Set<PlayerContainerPlayer> pNotifs = new TreeSet<PlayerContainerPlayer>();
        Land parent;

        try {
            cf = new ConfLoader(file);
            String str;
            version = cf.getVersion();
            uuid = cf.getUUID();
            landName = cf.getName();
            cf.readParam();
            String ownerS = cf.getValueString();

            // create owner (PlayerContainer)
            owner = PlayerContainer.getFromString(ownerS);
            if (owner == null) {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Invalid owner.");
            }

            cf.readParam();
            parentName = cf.getValueString();
            cf.readParam();
            factionTerritory = cf.getValueString();

            cf.readParam();

            // Create areas
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":", 2);
                areas.put(Integer.parseInt(multiStr[0]), CuboidArea.getFromString(multiStr[1]));
            }

            cf.readParam();

            //Residents
            while ((str = cf.getNextString()) != null) {
                residents.add(PlayerContainer.getFromString(str));
            }
            cf.readParam();

            //Banneds
            while ((str = cf.getNextString()) != null) {
                banneds.add(PlayerContainer.getFromString(str));
            }
            cf.readParam();

            //Create permissions
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":");
                TreeMap<PermissionType, Permission> permPlayer;
                PlayerContainer pc = PlayerContainer.getFromString(multiStr[0] + ":" + multiStr[1]);
                PermissionType permType = Factoid.getParameters().getPermissionTypeNoValid(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new TreeMap<PermissionType, Permission>();
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
                FlagType ft = Factoid.getParameters().getFlagTypeNoValid(multiStr[0]);
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
                pNotifs.add((PlayerContainerPlayer) PlayerContainer.getFromString(str));
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

                    parent = Factoid.getLands().getLand(UUID.fromString(parentName));

                    try {
                        land = Factoid.getLands().createLand(landName, owner, entry.getValue(), parent,
                                entry.getKey(), uuid);
                    } catch (FactoidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                    }
                } else {
                    try {
                        land = Factoid.getLands().createLand(landName, owner, entry.getValue(), null, entry.getKey(), uuid);
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
        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (Map.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (LandFlag flag : flags) {
            land.addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (PlayerContainerPlayer pNotif : pNotifs) {
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
            ConfBuilder cb = new ConfBuilder(land.getName(), land.getUUID(), getLandFile(land), LAND_VERSION);
            cb.writeParam("Owner", land.getOwner().toString());

            //Parent
            if (land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {
                cb.writeParam("Parent", land.getParent().getUUID().toString());
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
            strs = new ArrayList<String>();
            for (PlayerContainerPlayer pc : land.getPlayersNotify()) {
                strs.add(pc.toString());
            }
            cb.writeParam("PlayersNotify", strs.toArray(new String[0]));

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
            ConfBuilder cb = new ConfBuilder(faction.getName(), faction.getUUID(), getFactionFile(faction), FACTION_VERSION);

            List<String> strs = new ArrayList<String>();
            for (PlayerContainerPlayer pc : faction.getPlayers()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Players", strs.toArray(new String[0]));

            cb.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on saving Faction: " + faction.getName(), ex);
        }
    }

    @Override
    public void removeFaction(Faction faction) {

        getFactionFile(faction).delete();
    }

    private void saveAll() {

        for (Land land : Factoid.getLands().getLands()) {

            land.forceSave();
        }

        for (Faction faction : Factoid.getFactions().getFactions()) {

            faction.forceSave();
        }
    }
}
