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
package me.tabinol.factoid.playercontainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUUID {

    public static final int PLAYER_UUID_VERSION = Factoid.getMavenAppProperties().getPropertyInt("playerUUIDVersion");
    private final String fileName;
    private final File file;
    private Map<UUID, PlayerContainerPlayer> playersFactoidUUID; // Get from FactoidUUID
    private Map<UUID, PlayerContainerPlayer> playersMinecraftUUID; // Get from MinecraftUUID
    private Map<String, PlayerContainerPlayer> playersName; // get from name

    public PlayerUUID() {

        fileName = Factoid.getThisPlugin().getDataFolder() + "/" + "playeruuid.conf";
        file = new File(fileName);
    }

    public void loadAll() {

        playersFactoidUUID = new TreeMap<UUID, PlayerContainerPlayer>();
        playersMinecraftUUID = new TreeMap<UUID, PlayerContainerPlayer>();
        playersName = new TreeMap<String, PlayerContainerPlayer>();

        Factoid.getLog().write("Loading player UUIDs");

        try {
            BufferedReader br;

            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ex) {
                // Not existing? Trying the first backup
                try {
                    br = new BufferedReader(new FileReader(new File(fileName + ".back.1")));
                } catch (FileNotFoundException ex2) {
                    // Not existing? Nothing to load!
                    return;
                }
            }

            // Security copy
            File actFile = new File(fileName + ".back.9");
            if (actFile.exists()) {
                actFile.delete();
            }
            for (int t = 8; t >= 1; t--) {
                actFile = new File(fileName + ".back." + t);
                if (actFile.exists()) {
                    actFile.renameTo(new File(fileName + ".back." + Integer.toString(t + 1)));
                }
            }

            int version = Integer.parseInt(br.readLine().split(":")[1]);
            br.readLine(); // Read remark line

            String str;

            while ((str = br.readLine()) != null && !str.equals("")) {
                // Read from String "PlayerName:FactoidUUID:MinecraftUUID"
                String[] strs = StringChanges.splitAddVoid(str, ":");

                String playerName = strs[0];
                UUID factoidUUID = stringToUuidOrNull(strs[1]);
                UUID minecraftUUID = stringToUuidOrNull(strs[2]);
                PlayerContainerPlayer pc = new PlayerContainerPlayer(factoidUUID, playerName, minecraftUUID);
                if (minecraftUUID != null) {
                    playersMinecraftUUID.put(minecraftUUID, pc);
                }
                playersFactoidUUID.put(factoidUUID, pc);
                playersName.put(playerName, pc);
            }
            br.close();

            // The if is renamed but will be saved later
            if (!file.getName().endsWith("1")) {
                file.renameTo(new File(fileName + ".back.1"));
            }
        } catch (IOException ex) {
            Logger.getLogger(PlayerUUID.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void saveAll() {

        if (Factoid.getStorage() == null || Factoid.getStorage().isInLoad()) {
            return;
        }

        Factoid.getLog().write("Saving player UUIDs");

        try {
            BufferedWriter bw;

            try {
                bw = new BufferedWriter(new FileWriter(file));
            } catch (FileNotFoundException ex) {
                // Not existing? Nothing to load!
                return;
            }

            bw.write("Version:" + PLAYER_UUID_VERSION);
            bw.newLine();
            bw.write("# PlayerName:FactoidUUID:MinecraftUUID");
            bw.newLine();

            for (PlayerContainerPlayer pc : playersName.values()) {
                // Write to String "Factoid UUID:PlayerName:Minecraft UUID"
                bw.write(pc.getPlayerName() + ":" + uuidToStringOrVoid(pc.getFactoidUUID())
                        + ":" + uuidToStringOrVoid(pc.getMinecraftUUID()));
                bw.newLine();
            }
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(PlayerUUID.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String uuidToStringOrVoid(UUID uuid) {

        return uuid != null ? uuid.toString() : "";
    }

    private UUID stringToUuidOrNull(String string) {

        UUID uuid;

        try {
            uuid = UUID.fromString(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        return uuid;
    }

    public PlayerContainerPlayer getPCPFromMinecraftUUID(UUID minecraftUUID) {
        
        return playersMinecraftUUID.get(minecraftUUID);
    }
    
    public PlayerContainerPlayer getPCPFromString(String string) {

        return getPCPFromString(string, null, null);
    }

    public PlayerContainerPlayer getPCPFromString(String string, UUID minecraftUUID, Player player) {

        UUID factoidUUID;
        PlayerContainerPlayer pcp;

        String stLower = string.toLowerCase();

        if (minecraftUUID != null) {

            // first, check if the micraftUUID is allready in database
            pcp = playersMinecraftUUID.get(minecraftUUID);

            if (pcp == null) {

                // Check if pcp can be take from the name
                pcp = playersName.get(stLower);
                if (pcp == null) {
                    // No entry, create one
                    factoidUUID = UUID.randomUUID();
                    playersName.put(stLower, pcp = new PlayerContainerPlayer(factoidUUID, stLower, minecraftUUID));
                    playersFactoidUUID.put(factoidUUID, pcp);
                    playersMinecraftUUID.put(minecraftUUID, pcp);
                    saveAll();
                    return pcp;
                }

                // Player in database but not MicraftUUID. Updating...
                pcp.setMinecraftUUID(minecraftUUID);
                playersMinecraftUUID.put(minecraftUUID, pcp);
                saveAll();
                return pcp;
            }

            // Check if there is a name change
            if (player != null && !player.getName().equalsIgnoreCase(pcp.getPlayerName())) {

                String oldName = pcp.getPlayerName();
                playersName.remove(oldName);
                pcp.setPlayerName(stLower);
                playersName.put(stLower, pcp);
                saveAll();
            }

            return pcp;

        }

        // No MinecraftUUID
        try {
            factoidUUID = UUID.fromString(string);

            // Is a UUID
            PlayerContainerPlayer pcp2 = playersFactoidUUID.get(factoidUUID);
            
            // TEMP add the MinecraftID to list
            if(!playersMinecraftUUID.containsValue(pcp2)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(pcp2.getPlayerName());
                if(offlinePlayer != null) {
                    playersMinecraftUUID.put(offlinePlayer.getUniqueId(), pcp2);
                    pcp2.setMinecraftUUID(offlinePlayer.getUniqueId());
                }
            }
                
            return pcp2;
            
        } catch (IllegalArgumentException ex) {

            // Is a PlayerName
            pcp = playersName.get(stLower);

            // If not created create one
            if (pcp == null) {

                factoidUUID = UUID.randomUUID();
                playersName.put(stLower, pcp = new PlayerContainerPlayer(factoidUUID, stLower, null));
                playersFactoidUUID.put(factoidUUID, pcp);
                saveAll();
            }

            return pcp;
        }
    }
}
