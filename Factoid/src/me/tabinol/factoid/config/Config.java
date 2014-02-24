package me.tabinol.factoid.config;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    public final static String GLOBAL = "_global_";
    private Factoid thisPlugin;
    private FileConfiguration config;
    private FileConfiguration landDefault;
    private FileConfiguration worldConfig;
    // Configuration
    public boolean debug = false;
    public List<String> Worlds = null;
    public String Lang = "english";
    public boolean UseEconomy = false;
    public int InfoItem = 352;
    public int SelectItem = 367;

    public enum AllowCollisionType {

        TRUE,
        APPROVE,
        FALSE;
    }
    public AllowCollisionType AllowCollision;
    public long ApproveNotifyTime = 24002;
    public int MaxLand = 1;
    public int MinLandSize = 1;
    public int MaxLandSize = 1;
    public int MaxAreaPerLand = 1;
    public int MinAreaSize = 1;
    public int MaxAreaSize = 1;
    public int MinLandHigh = 0;
    public int MaxLandHigh = 255;
    public int PricebyCube = 1;
    public int MinPriceLocation = 1;
    public int MaxPriceLocation = 1;
    public int MinPriceSell = 1;
    public int MaxPriceSell = 1;
    public boolean BeaconLigth = false;
    public boolean OverrideExplosions = true;
    public Set<FlagType> ownerConfigFlag; // Flags a owner can set
    public Set<PermissionType> ownerConfigPerm; // Permissions a owner can set

    public Config() {

        thisPlugin = Factoid.getThisPlugin();
        thisPlugin.saveDefaultConfig();
        if (!new File(thisPlugin.getDataFolder(), "landdefault.yml").exists()) {
            thisPlugin.saveResource("landdefault.yml", false);
        }
        if (!new File(thisPlugin.getDataFolder(), "worldconfig.yml").exists()) {
            thisPlugin.saveResource("worldconfig.yml", false);
        }
        config = thisPlugin.getConfig();
        reloadConfig();
    }

    public final void reloadConfig() {

        loadCustom();
        getConfig();
    }

    private void loadCustom() {

        landDefault = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "landdefault.yml"));
        worldConfig = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "worldconfig.yml"));
    }

    private void getConfig() {

        debug = config.getBoolean("general.debug");
        Worlds = StringChanges.toLower(config.getStringList("general.worlds"));
        Lang = config.getString("general.lang");
        UseEconomy = config.getBoolean("general.UseEconomy");
        InfoItem = config.getInt("general.InfoItem");
        SelectItem = config.getInt("general.SelectItem");
        // Remove error if the parameter is not here (AllowCollision)
        try {
            AllowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision").toUpperCase());
        } catch (NullPointerException ex) {
            AllowCollision = AllowCollisionType.APPROVE;
        }
        ApproveNotifyTime = config.getLong("general.ApproveNotifyTime");
        MaxLand = config.getInt("land.MaxLand");
        MinLandSize = config.getInt("land.MinLandSize");
        MaxLandSize = config.getInt("land.MaxLandSize");
        MaxAreaPerLand = config.getInt("land.area.MaxAreaPerLand");
        MinAreaSize = config.getInt("land.area.MinAreaSize");
        MaxAreaSize = config.getInt("land.area.MaxAreaSize");
        BeaconLigth = config.getBoolean("land.BeaconLigth");
        OverrideExplosions = config.getBoolean("general.OverrideExplosions");

        if (UseEconomy) {
            PricebyCube = config.getInt("economy.PricebyCube");
            MinPriceSell = config.getInt("economy.MinPriceSell");
            MaxPriceSell = config.getInt("economy.MaxPriceSell");
            MinPriceLocation = config.getInt("economy.MinPriceLocation");
            MaxPriceLocation = config.getInt("economy.MaxPriceLocation");
        }

        ownerConfigFlag = new HashSet<>();
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(FlagType.valueOf(value.toUpperCase()));
        }
        ownerConfigPerm = new HashSet<>();
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(PermissionType.valueOf(value.toUpperCase()));
        }
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

        // Add permissions
        if (csPerm != null) {
            for (String container : csPerm.getKeys(false)) {
                if (container.equalsIgnoreCase("Faction")
                        || container.equalsIgnoreCase("Group")
                        || container.equalsIgnoreCase("Player")) {
                    for (String containerName : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        for (String perm : fc.getConfigurationSection(perms + "." + container + "." + containerName).getKeys(false)) {
                            Factoid.getLog().write("Container: " + container + ":" + containerName + ", " + perm);
                            dl.addPermission(
                                    PlayerContainer.create(null, PlayerContainerType.getFromString(container), containerName.toLowerCase()),
                                    new Permission(PermissionType.valueOf(perm.toUpperCase()),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Heritable")));
                        }
                    }
                } else {
                    for (String perm : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        Factoid.getLog().write("Container: " + container + ", " + perm);
                        dl.addPermission(
                                PlayerContainer.create(null, PlayerContainerType.getFromString(container), null),
                                new Permission(PermissionType.valueOf(perm.toUpperCase()),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Value"),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Heritable")));
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
