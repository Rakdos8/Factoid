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
import me.tabinol.factoid.economy.PlayerMoney;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.approve.ApproveNotif;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.parameters.Parameters;
import me.tabinol.factoid.scoreboard.ScoreBoard;
import me.tabinol.factoid.storage.Storage;
import me.tabinol.factoid.storage.StorageFlat;
import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.utilities.MavenAppProperties;
import org.bukkit.plugin.java.JavaPlugin;

public class Factoid extends JavaPlugin {

    private OnCommand CommandListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private LandListener landListener;
    private static MavenAppProperties mavenAppProperties;
    private static ApproveNotif approveNotif;
    private static Storage storage = null;
    private static Log log;
    private static Factoid thisPlugin;
    private static Config conf;
    private static PlayerStaticConfig playerConf;
    private static Lang language;
    private static Factions factions;
    private static Parameters parameters;
    private static Lands lands;
    private static String version;
    private static DependPlugin dependPlugin;
    private static PlayerMoney playerMoney;
    private static ScoreBoard Scoreboard;
    
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
        approveNotif.runApproveNotifLater();
        getServer().getPluginManager().registerEvents(worldListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getCommand("factoid").setExecutor(CommandListener);
        log.write(Factoid.getLanguage().getMessage("ENABLE"));
    }

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
        storage.loadAll();
        approveNotif.stopNextRun();
        approveNotif.runApproveNotifLater();
    }

    @Override
    public void onDisable() {

        log.write(Factoid.getLanguage().getMessage("DISABLE"));
        approveNotif.stopNextRun();
        playerConf.removeAll();
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
    
    public static Parameters getParameters() {
        
        return parameters;
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

    public static MavenAppProperties getMavenAppProperties() {

        return mavenAppProperties;
    }

    public static PlayerMoney getPlayerMoney() {

        return playerMoney;
    }
}
