package me.tabinol.factoid.config;

import java.util.List;
import me.tabinol.factoid.Factoid;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private Factoid thisPlugin;
    // Configuration
    public boolean debug = false;
    public List<String> Worlds = null;
    public String Lang = "english";
    public boolean UseEconomy = false;
    public int InfoItem = 361;
    public boolean PriorityOld = false;
    public boolean CanMakeCollision = false;
    public int MaxLand = 1;
    public int MinLandSize = 1;
    public int MaxLandSize = 1;
    public int MaxAreaPerLand = 1;
    public int MinAreaSize = 1;
    public int MaxAreaSize = 1;
    public int PricebyCube = 1;
    public int MinPriceLocation = 1;
    public int MaxPriceLocation = 1;
    public int MinPriceSell = 1;
    public int MaxPriceSell = 1;

    public Config() {

        thisPlugin = Factoid.getThisPlugin();
        thisPlugin.saveDefaultConfig();
        getConfig();
    }

    public void reloadConfig() {

        thisPlugin.reloadConfig();
    }

    private void getConfig() {

        FileConfiguration config = thisPlugin.getConfig();
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
}
