package me.tabinol.factoid.config;

import java.util.List;
import java.util.TreeMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private Factoid thisPlugin;
    private FileConfiguration config;
    // Configuration
    public boolean debug = false;
    public List<String> Worlds = null;
    public String Lang = "english";
    public boolean UseEconomy = false;
    public int InfoItem = 352;
    public boolean PriorityOld = false;
    public boolean CanMakeCollision = false;
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

    public Config() {

        thisPlugin = Factoid.getThisPlugin();
        thisPlugin.saveDefaultConfig();
        config = thisPlugin.getConfig();

        getConfig();
    }

    public void reloadConfig() {

        thisPlugin.reloadConfig();
    }

    private void getConfig() {

        debug = config.getBoolean("general.debug");
        Worlds = config.getStringList("general.worlds");
        Lang = config.getString("general.lang");
        UseEconomy = config.getBoolean("general.UseEconomy");
        InfoItem = config.getInt("general.InfoItem");
        PriorityOld = config.getBoolean("land.PriorityOld");
        CanMakeCollision = config.getBoolean("land.CanMakeCollision");
        MaxLand = config.getInt("land.MaxLand");
        MinLandSize = config.getInt("land.MinLandSize");
        MaxLandSize = config.getInt("land.MaxLandSize");
        MaxAreaPerLand = config.getInt("land.area.MaxAreaPerLand");
        MinAreaSize = config.getInt("land.area.MinAreaSize");
        MaxAreaSize = config.getInt("land.area.MaxAreaSize");

        if (UseEconomy) {
            PricebyCube = config.getInt("economy.PricebyCube");
            MinPriceSell = config.getInt("economy.MinPriceSell");
            MaxPriceSell = config.getInt("economy.MaxPriceSell");
            MinPriceLocation = config.getInt("economy.MinPriceLocation");
            MaxPriceLocation = config.getInt("economy.MaxPriceLocation");
        }
    }

    public TreeMap<String, Land> getLandOutsideArea() {
        
        
        TreeMap<String, Land> landList = new TreeMap<>();
        
        // config.getConfigurationSection(Lang).getKeys(debug) Je suis rendu LA*********************************************
        List<String> permlist = config.getStringList("World.Default.Global.ContainerPermissions");
        List<String> flaglist = config.getStringList("World.Default.Global.ContainerFlags");
        landList.put(Lands.GLOBAL, landCreate(Lands.GLOBAL, permlist, flaglist));
        return landList;
    }
    
    private Land landCreate(String worldName, List<String> perms, List<String> flags) {
        
        Land land = new Land(worldName, new PlayerContainerNobody(), null);

        for(String perm : perms) {
            String[] substr = perm.split(":");
            land.addPermission(PlayerContainer.create(land, PlayerContainerType.getFromString(substr[0]), substr[1]), 
                    new Permission(PermissionType.getFromString(substr[2]), Boolean.parseBoolean(substr[3]), Boolean.parseBoolean(substr[4])));
        }
        for(String flag : flags) {
            String[] substr = flag.split(":");
            land.addFlag(new LandFlag(FlagType.getFromString(substr[0]), substr[1], Boolean.parseBoolean(substr[2])));
        }
        
        return land;
    }
            
    //publicTreeMap<String, Land> getLandDefaultConf() {
        
        
    //}
}
