package me.tabinol.factoid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.listeners.PlayerListener;

public class Factoid extends JavaPlugin {
    private File configFile;
    private FileConfiguration config;
    private boolean debug = false;
    private Lang language;
    private List<String> Worlds = null;
    private Log log;
    private boolean UseEconomy = false;
    private boolean PriorityOld = false;
    private boolean CanMakeCollision = false;
    private int MaxLand = 1;
    private int MinLandSize = 1;
    private int MaxLandSize = 1;
    private int MaxAreaPerLand = 1;
    private int MinAreaSize = 1;
    private int MaxAreaSize = 1;
    private int PricebyCube = 1;
    private int MinPriceLocation = 1;
    private int MaxPriceLocation = 1;
    private int MinPriceSell = 1;
    private int MaxPriceSell = 1;
    private OnCommand CommandListener;
    private PlayerListener playerListener;
    private static Factoid thisPlugin;
    // Access to lands (static)
    private static Lands lands;
    
    @Override
    public void onDisable() {
        log.write("Factoid is Disabled.");
        log.interrupt();
        language.interrupt();
    }
	 
	
    @Override
    public void onEnable() {
        // Static access to «this» Factoid
        thisPlugin = this;
        playerListener = new PlayerListener();
        getServer().getPluginManager().registerEvents(playerListener, this);
        lands = new Lands();
        configFile = new File(getDataFolder(), "config.yml");
        firstRun();
        config = new YamlConfiguration();
        loadYamls();
        debug = config.getBoolean("general.debug");
        Worlds = config.getStringList("general.worlds");
        language = new Lang(getDataFolder(),config.getString("general.lang"),this);
        log = new Log(getDataFolder(),debug);
        log.write("Factoid is Enabled.");
        CommandListener = new OnCommand(language,log,this);
        getCommand("factoid").setExecutor(CommandListener);
        UseEconomy = config.getBoolean("general.UseEconomy");
        PriorityOld = config.getBoolean("land.PriorityOld");
        CanMakeCollision = config.getBoolean("land.CanMakeCollision");
        MaxLand = config.getInt("land.MaxLand");
        MinLandSize = config.getInt("land.MinLandSize");
        MaxLandSize = config.getInt("land.MaxLandSize");
        MaxAreaPerLand = config.getInt("land.area.MaxAreaPerLand");
        MinAreaSize = config.getInt("land.area.MinAreaSize");
        MaxAreaSize = config.getInt("land.area.MaxAreaSize");    
        
        if(UseEconomy){
            PricebyCube = config.getInt("economy.PricebyCube");
            MinPriceSell = config.getInt("economy.MinPriceSell");
            MaxPriceSell = config.getInt("economy.MaxPriceSell");
            MinPriceLocation = config.getInt("economy.MinPriceLocation");
            MaxPriceLocation = config.getInt("economy.MaxPriceLocation");   
        }
    }
	
    private void firstRun(){
        try{
            if(!configFile.exists()){
                configFile.getParentFile().mkdirs();
                copy(getResource("config.yml"), configFile);
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveYamls() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Factoid getThisPlugin() {
        
        return thisPlugin;
    }
    
    public static Lands getLands() {
        
        return lands;
    }
}
