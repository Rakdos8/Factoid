package me.tabinol.factoid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.storage.Storage;
import me.tabinol.factoid.storage.StorageFlat;

public class Factoid extends JavaPlugin {
    private File configFile;
    private FileConfiguration config;
    private OnCommand CommandListener;
    private PlayerListener playerListener;
    private Storage storage;
    private static Log log;
    private static Factoid thisPlugin;
    private static Config conf;
    private static Lang language;
    // Access to Factions and Lands (static)
    private static Factions factions;
    private static Lands lands;

    @Override
    public void onEnable() {
        
        // Static access to «this» Factoid
        thisPlugin = this;
        playerListener = new PlayerListener();
        CommandListener = new OnCommand();
        getServer().getPluginManager().registerEvents(playerListener, this);
        getCommand("factoid").setExecutor(CommandListener);
        conf = new Config();
        log = new Log();
        language = new Lang();
        storage = new StorageFlat();
        factions = new Factions();
        lands = new Lands();
        log.write("Factoid is Enabled.");
    }
    
    @Override
    public void onDisable() {
        
        log.write("Factoid is Disabled.");
        log.interrupt();
        language.interrupt();
    }

    // Remplacé par la classe Config
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

    // Remplacé par la classe Config
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

    public static Factoid getThisPlugin() {
        
        return thisPlugin;
    }
    
    public static Config getConf() {
        
        return conf;
    }
    
    public static Lang getLanguage() {
        
        return language;
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
}
