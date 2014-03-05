package me.tabinol.factoid;

import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.DependPlugin;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.approve.ApproveNotif;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.storage.Storage;
import me.tabinol.factoid.storage.StorageFlat;
import me.tabinol.factoid.scoreboard.ScoreBoard;

public class Factoid extends JavaPlugin {

    private OnCommand CommandListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private LandListener landListener;
    private static ApproveNotif approveNotif;
    private static Storage storage;
    private static Log log;
    private static Factoid thisPlugin;
    private static Config conf;
    private static PlayerStaticConfig playerConf;
    private static Lang language;
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
        playerConf = new PlayerStaticConfig();
        log = new Log();
        dependPlugin = new DependPlugin();
        language = new Lang();
        storage = new StorageFlat();
        factions = new Factions();
        lands = new Lands();
        storage.loadAll();
        worldListener = new WorldListener();
        playerListener = new PlayerListener();
        landListener = new LandListener();
        CommandListener = new OnCommand();
        Scoreboard = new ScoreBoard();
        approveNotif = new ApproveNotif();
        getServer().getPluginManager().registerEvents(worldListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getCommand("factoid").setExecutor(CommandListener);
        log.write(Factoid.getLanguage().getMessage("ENABLE"));
    }
    
    public void reload() {
        
        reloadConfig();
        conf.reloadConfig();
        log.setDebug(conf.isDebug());
        language.reloadConfig();
        factions = new Factions();
        lands = new Lands();
        storage.loadAll();
    }
    
    @Override
    public void onDisable() {
        
        approveNotif.cancel();
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
    
    public static PlayerStaticConfig getPlayerConf() {
        
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
    
    public static ApproveNotif getApproveNotif() {
        
        return approveNotif;
    }
    
}
