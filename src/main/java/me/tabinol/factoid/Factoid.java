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
package me.tabinol.factoid;

import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.DependPlugin;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.economy.EcoScheduler;
import me.tabinol.factoid.economy.PlayerMoney;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.approve.ApproveNotif;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.PvpListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.parameters.Parameters;
import me.tabinol.factoid.playerscache.PlayersCache;
import me.tabinol.factoid.scoreboard.ScoreBoard;
import me.tabinol.factoid.storage.StorageThread;
import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.utilities.MavenAppProperties;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * The Class Factoid.
 */
public class Factoid extends JavaPlugin {

	/**  The Economy schedule interval. */
	public static final int ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;
	
	/** The Command listener. */
    private OnCommand CommandListener;
    
    /** The player listener. */
    private PlayerListener playerListener;
    
    /** The player listener. */
    private PvpListener pvpListener;

    /** The world listener. */
    private WorldListener worldListener;
    
    /** The land listener. */
    private LandListener landListener;
    
    /**  The economy scheduler. */
    private EcoScheduler ecoScheduler;
    
    /** The maven app properties. */
    private static MavenAppProperties mavenAppProperties;
    
    /** The approve notif. */
    private static ApproveNotif approveNotif;
    
    /** The storage thread. */
    private static StorageThread storageThread = null;
    
    /** The log. */
    private static Log log;
    
    /** The this plugin. */
    private static Factoid thisPlugin;
    
    /** The conf. */
    private static Config conf;
    
    /** The player conf. */
    private static PlayerStaticConfig playerConf;
    
    /** The language. */
    private static Lang language;
    
    /** The factions. */
    private static Factions factions;
    
    /** The parameters. */
    private static Parameters parameters;
    
    /** The lands. */
    private static Lands lands;
    
    /** The version. */
    private static String version;
    
    /** The depend plugin. */
    private static DependPlugin dependPlugin;
    
    /** The player money. */
    private static PlayerMoney playerMoney;
    
    /** The Scoreboard. */
    private static ScoreBoard Scoreboard;
    
    /** The players cache. */
    private static PlayersCache playersCache;
    
    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        mavenAppProperties = new MavenAppProperties();
        mavenAppProperties.loadProperties();
        // Static access to «this» Factoid
        thisPlugin = this;
        version = this.getDescription().getVersion();
        parameters = new Parameters();
        conf = new Config();
        log = new Log();
        dependPlugin = new DependPlugin();
        if (conf.useEconomy() == true && dependPlugin.getEconomy() != null) {
            playerMoney = new PlayerMoney();
        } else {
            playerMoney = null;
        }
        playerConf = new PlayerStaticConfig();
        playerConf.addAll();
        language = new Lang();
        storageThread = new StorageThread();
        factions = new Factions();
        lands = new Lands();
        storageThread.loadAllAndStart();
        worldListener = new WorldListener();
        playerListener = new PlayerListener();
        pvpListener = new PvpListener();
        landListener = new LandListener();
        CommandListener = new OnCommand();
        Scoreboard = new ScoreBoard();
        approveNotif = new ApproveNotif();
        approveNotif.runApproveNotifLater();
        ecoScheduler = new EcoScheduler();
        ecoScheduler.runTaskTimer(this, ECO_SCHEDULE_INTERVAL, ECO_SCHEDULE_INTERVAL);
        playersCache = new PlayersCache();
        playersCache.start();
        getServer().getPluginManager().registerEvents(worldListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(pvpListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getCommand("factoid").setExecutor(CommandListener);
        log.write(Factoid.getLanguage().getMessage("ENABLE"));
    }

    /**
     * Reload.
     */
    public void reload() {

        // No reload of Parameters to avoid Deregistering external parameters
        conf.reloadConfig();
        if (conf.useEconomy() == true && dependPlugin.getEconomy() != null) {
            playerMoney = new PlayerMoney();
        } else {
            playerMoney = null;
        }
        log.setDebug(conf.isDebug());
        language.reloadConfig();
        factions = new Factions();
        lands = new Lands();
        storageThread.stopNextRun();
        storageThread= new StorageThread();
        storageThread.loadAllAndStart();
        approveNotif.stopNextRun();
        approveNotif.runApproveNotifLater();
    }

    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {

        log.write(Factoid.getLanguage().getMessage("DISABLE"));
        playersCache.stopNextRun();
        approveNotif.stopNextRun();
        storageThread.stopNextRun();
        playerConf.removeAll();
        log.interrupt();
        language.interrupt();
    }

    /**
     * Gets the this plugin.
     *
     * @return the this plugin
     */
    public static Factoid getThisPlugin() {

        return thisPlugin;
    }

    /**
     * Gets the conf.
     *
     * @return the conf
     */
    public static Config getConf() {

        return conf;
    }

    /**
     * Gets the player conf.
     *
     * @return the player conf
     */
    public static PlayerStaticConfig getPlayerConf() {

        return playerConf;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public static Lang getLanguage() {

        return language;
    }

    /**
     * Gets the scoreboard.
     *
     * @return the scoreboard
     */
    public static ScoreBoard getScoreboard() {

        return Scoreboard;
    }

    /**
     * Gets the log.
     *
     * @return the log
     */
    public static Log getLog() {

        return log;
    }

    /**
     * Gets the factions.
     *
     * @return the factions
     */
    public static Factions getFactions() {

        return factions;
    }
    
    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public static Parameters getParameters() {
        
        return parameters;
    }

    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public static Lands getLands() {

        return lands;
    }

    /**
     * Gets the storage.
     *
     * @return the storage
     */
    public static StorageThread getStorageThread() {

        return storageThread;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public static String getVersion() {

        return version;
    }

    /**
     * Gets the depend plugin.
     *
     * @return the depend plugin
     */
    public static DependPlugin getDependPlugin() {

        return dependPlugin;
    }

    /**
     * Gets the approve notif.
     *
     * @return the approve notif
     */
    public static ApproveNotif getApproveNotif() {

        return approveNotif;
    }

    /**
     * Gets the maven app properties.
     *
     * @return the maven app properties
     */
    public static MavenAppProperties getMavenAppProperties() {

        return mavenAppProperties;
    }

    /**
     * Gets the player money.
     *
     * @return the player money
     */
    public static PlayerMoney getPlayerMoney() {

        return playerMoney;
    }
    
    /**
     * Gets the players cache.
     *
     * @return the players cache
     */
    public static PlayersCache getPlayersCache() {
    	
    	return playersCache;
    }
}
