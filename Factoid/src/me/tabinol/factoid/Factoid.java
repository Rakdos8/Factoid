package me.tabinol.factoid;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.DependPlugin;
import me.tabinol.factoid.config.PlayerConfig;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.storage.Storage;
import me.tabinol.factoid.storage.StorageFlat;
import me.tabinol.factoid.scoreboard.ScoreBoard;

public class Factoid extends JavaPlugin {

    public static Object getThisServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private OnCommand CommandListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private LandListener landListener;
    private static Storage storage;
    private static Log log;
    private static Factoid thisPlugin;
    private static Config conf;
    private static PlayerConfig playerConf;
    private static Lang language;
    // Access to Factions and Lands (static)
    private static Factions factions;
    private static Lands lands;
    private static String version;
    private static DependPlugin dependPlugin;
    private static ScoreBoard Scoreboard;

    @Override
    public void onEnable() {
        
        // Static access to «this» Factoid
        thisPlugin = this;
        version = this.getDescription().getVersion();
        conf = new Config();
        playerConf = new PlayerConfig();
        log = new Log();
        dependPlugin = new DependPlugin();
        language = new Lang();
        storage = new StorageFlat();
        factions = new Factions();
        lands = new Lands(conf.getGlobalArea(), conf.getLandOutsideArea(), conf.getLandDefaultConf());
        storage.loadAll();
        worldListener = new WorldListener();
        playerListener = new PlayerListener();
        landListener = new LandListener();
        CommandListener = new OnCommand();
        Scoreboard = new ScoreBoard();
        getServer().getPluginManager().registerEvents(worldListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getCommand("factoid").setExecutor(CommandListener);
        log.write(Factoid.getLanguage().getMessage("ENABLE"));
    }
    
    public void reload() {
        
        reloadConfig();
        conf.reloadConfig();
        log.setDebug(conf.debug);
        language.reloadConfig();
        factions = new Factions();
        lands = new Lands(conf.getGlobalArea(), conf.getLandOutsideArea(), conf.getLandDefaultConf());
        storage.loadAll();
    }
    
    @Override
    public void onDisable() {
        
        log.write(Factoid.getLanguage().getMessage("DISABLE"));
        log.interrupt();
        language.interrupt();
    }

    public static Factoid getThisPlugin() {
        
        return thisPlugin;
    }
    
    public static Config getConf() {
        
        return conf;
    }
    
    public static PlayerConfig getPlayerConf() {
        
        return playerConf;
    }
    
    public static Lang getLanguage() {
        
        return language;
    }
    
    public static ScoreBoard getScoreboard() {
        
        return Scoreboard;
    }
    
    public static Log getLog() {
        
        return log;
    }
    
    public static Factions getFactions() {
        
        return factions;
    }
    
    public static Lands getLands() {
        
        return lands;
    }
    
    public static Storage getStorage() {
        
        return storage;
    }
    
    public static String getVersion() {
        
        return version;
    }
    
    public static DependPlugin getDependPlugin() {
        
        return dependPlugin;
    }
    
}
