package me.tabinol.factoid.lands.approve;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Areas.CuboidArea;
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
        landNames = new TreeSet<>();
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
        saveFile();
        ApproveNotif.notifyForApprove(approve.getLandName(), approve.getOwner().getPrint());
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
                CuboidArea.getFromString(section.getString("NewArea")), pc, parent);
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
                Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "On approve file creation", ex);
            }
        }
        try {
            approveConfig.load(approveFile);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "On approve file load", ex);
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
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "On approve file save", ex);
        }
    }
}
