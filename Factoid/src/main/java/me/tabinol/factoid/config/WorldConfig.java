package me.tabinol.factoid.config;

import java.io.File;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import static me.tabinol.factoid.config.Config.GLOBAL;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Started by Lands.Class
// Load world config and lands default
public class WorldConfig {

    private final Factoid thisPlugin;
    private final FileConfiguration landDefault;
    private final FileConfiguration worldConfig;
    
    public WorldConfig() {

        thisPlugin = Factoid.getThisPlugin();
        
        // Create files (if not exist) and load
        if (!new File(thisPlugin.getDataFolder(), "landdefault.yml").exists()) {
            thisPlugin.saveResource("landdefault.yml", false);
        }
        if (!new File(thisPlugin.getDataFolder(), "worldconfig.yml").exists()) {
            thisPlugin.saveResource("worldconfig.yml", false);
        }
        landDefault = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "landdefault.yml"));
        worldConfig = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "worldconfig.yml"));
    }

    public TreeMap<String, DummyLand> getLandOutsideArea() {

        TreeMap<String, DummyLand> landList = new TreeMap<>();
        Set<String> keys = worldConfig.getConfigurationSection("").getKeys(false);

        for (String worldName : keys) {
            String worldNameLower = worldName.toLowerCase();
            Factoid.getLog().write("Create conf for World: " + worldNameLower);
            landList.put(worldNameLower, landCreate(worldNameLower, worldConfig, worldName + ".ContainerPermissions",
                    worldName + ".ContainerFlags"));
        }

        return landList;
    }

    public DummyLand getLandDefaultConf() {

        Factoid.getLog().write("Create default conf for lands");
        return landCreate(GLOBAL, landDefault, "ContainerPermissions", "ContainerFlags");
    }

    private DummyLand landCreate(String worldName, FileConfiguration fc, String perms, String flags) {

        DummyLand dl = new DummyLand(worldName);
        ConfigurationSection csPerm = fc.getConfigurationSection(perms);
        ConfigurationSection csFlags = fc.getConfigurationSection(flags);

        Factoid.getLog().write("Adding default for world: " + worldName);
        
        // Add permissions
        if (csPerm != null) {
            for (String container : csPerm.getKeys(false)) {
                PlayerContainerType containerType = PlayerContainerType.getFromString(container);
                ConfigurationSection sect1 = csPerm.getConfigurationSection(container);
                for (String ifContainer : sect1.getKeys(false)) {

                    String containerName;
                    ConfigurationSection sect2;

                    if (containerType.hasParameter()) {

                        // If there is a parameter and bug resolve for permission (xxx.yyy.zzz)
                        StringBuilder strb = new StringBuilder();
                        sect2 = sect1;
                        
                        // Check if the subsection is only one but subsubsection has not 2 (value + inherit = 2)
                        while(sect2.getKeys(false).size() == 1 &&
                                sect2.getConfigurationSection(sect2.getKeys(false).iterator().next()).getKeys(false).size() != 2) {
                            if(strb.length() != 0) {
                                strb.append(".");
                            }
                            strb.append(sect2.getKeys(false).iterator().next());
                            sect2 = sect1.getConfigurationSection(strb.toString());
                         }
                        containerName = strb.toString();
                    
                    } else {

                        // If no parameter, stay in the same section
                        containerName = "";
                        sect2 = sect1;
                    }

                    for (String perm : sect2.getKeys(false)) {
                        Factoid.getLog().write("Container: " + container + ":" + containerName + ", " + perm);
                        dl.addPermission(
                                PlayerContainer.create(null, containerType, containerName.toLowerCase()),
                                new Permission(PermissionType.valueOf(perm.toUpperCase()),
                                        fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
                                        fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Heritable")));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (String flag : csFlags.getKeys(false)) {
                Factoid.getLog().write("Flag: " + flag);
                dl.addFlag(new LandFlag(FlagType.valueOf(flag.toUpperCase()),
                        fc.getString(flags + "." + flag + ".Value"), fc.getBoolean(flags + "." + flag + ".Heritable")));
            }
        }

        return dl;
    }

}
