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
package me.tabinol.factoid.lands.approve;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ApproveList {

    final private File approveFile;
    final private FileConfiguration approveConfig;
    final private TreeSet<String> landNames;

    public ApproveList() {

        approveFile = new File(Factoid.getThisPlugin().getDataFolder() + "/approvelist.yml");
        approveConfig = new YamlConfiguration();
        landNames = new TreeSet<String>();
        loadFile();
    }

    public void addApprove(Approve approve) {

        landNames.add(approve.getLandName());
        ConfigurationSection section = approveConfig.createSection(approve.getLandName());
        section.set("Action", approve.getAction().toString());
        section.set("RemovedAreaId", approve.getRemovedAreaId());
        section.set("NewArea", approve.getNewArea().toString());
        section.set("Owner", approve.getOwner().toString());
        if (approve.getParent() != null) {
            section.set("Parent", approve.getParent().getName());
        }
        section.set("Price", approve.getPrice() + "");
        saveFile();
        Factoid.getApproveNotif().notifyForApprove(approve.getLandName(), approve.getOwner().getPrint());
    }

    public Set<String> getApproveList() {

        return landNames;
    }

    public boolean isInApprove(String landName) {

        return landNames.contains(landName.toLowerCase());
    }

    public Approve getApprove(String landName) {

        Factoid.getLog().write("Get approve for: " + landName);
        ConfigurationSection section = approveConfig.getConfigurationSection(landName);

        if (section == null) {
            Factoid.getLog().write("Error Section null");
            return null;
        }

        String[] ownerS = StringChanges.splitAddVoid(section.getString("Owner"), ":");
        PlayerContainer pc = PlayerContainer.create(null, PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);
        Land parent = null;
        if (section.contains("Parent")) {
            parent = Factoid.getLands().getLand(section.getString("Parent"));
            if (parent == null) {
                Factoid.getLog().write("Error, parent not found");
                return null;
            }
        }

        return new Approve(landName, LandAction.valueOf(section.getString("Action")),
                section.getInt("RemovedAreaId"),
                CuboidArea.getFromString(section.getString("NewArea")), pc, 
                parent, section.getDouble("Price"));
    }

    public void removeApprove(Approve approve) {

        Factoid.getLog().write("Remove Approve from list: " + approve.getLandName());

        approveConfig.set(approve.getLandName(), null);
        landNames.remove(approve.getLandName());
        saveFile();
    }

    private void loadFile() {

        Factoid.getLog().write("Loading Approve list file");

        if (!approveFile.exists()) {
            try {
                approveFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file creation", ex);
            }
        }
        try {
            approveConfig.load(approveFile);
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
        }

        // add land names to list
        for (String landName : approveConfig.getKeys(false)) {
            landNames.add(landName);
        }
    }

    private void saveFile() {

        Factoid.getLog().write("Saving Approve list file");

        try {
            approveConfig.save(approveFile);
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file save", ex);
        }
    }
}
