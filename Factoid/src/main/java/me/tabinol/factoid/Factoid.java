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

import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.DependPlugin;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.economy.EcoScheduler;
import me.tabinol.factoid.economy.PlayerMoney;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.approve.ApproveNotif;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.types.Types;
import me.tabinol.factoid.listeners.ChatListener;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.PlayerListener18;
import me.tabinol.factoid.listeners.PvpListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.parameters.Parameters;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playerscache.PlayersCache;
import me.tabinol.factoid.storage.StorageThread;
import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.IFactoid;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;

/**
 * The Class Factoid.
 */
public class Factoid extends JavaPlugin implements IFactoid {

	/** The Economy schedule interval. */
	public static final int ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;

	/** The this plugin. */
	private static Factoid thisPlugin;

	/** The types */
	protected static Types types;

	/** The lands. */
	protected static Lands lands;

	/** The parameters. */
	protected static Parameters parameters;

	/** The player conf. */
	protected PlayerStaticConfig playerConf;

	/** The Command listener. */
	private OnCommand CommandListener;

	/** The player listener. */
	private PlayerListener playerListener;

	/** The player listener 18. */
	private PlayerListener18 playerListener18;

	/** The player listener. */
	private PvpListener pvpListener;

	/** The world listener. */
	private WorldListener worldListener;

	/** The land listener. */
	private LandListener landListener;

	/** The chat listener. */
	private ChatListener chatListener;

	/** The economy scheduler. */
	private EcoScheduler ecoScheduler;

	/** The approve notif. */
	private ApproveNotif approveNotif;

	/** The storage thread. */
	private StorageThread storageThread = null;

	/** The log. */
	private Log log;

	/** The conf. */
	private Config conf;

	/** The language. */
	private Lang language;

	/** The depend plugin. */
	private DependPlugin dependPlugin;

	/** The player money. */
	private PlayerMoney playerMoney;

	/** The players cache. */
	private PlayersCache playersCache;

	/**
	 * Gets the this plugin.
	 *
	 * @return the this plugin
	 */
	public static Factoid getThisPlugin() {

		return thisPlugin;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 * @deprecated Please use FactoidAPI
	 */
	@Deprecated
	public static Parameters getParameters() {

		return parameters;
	}

	/**
	 * Gets the lands.
	 *
	 * @return the lands
	 * @deprecated Please use FactoidAPI
	 */
	@Deprecated
	public static Lands getLands() {

		return lands;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable() */
	@Override
	public void onEnable() {
		// Static access to «this» Factoid
		thisPlugin = this;
		BKVersion.initVersion();
		FactoidAPI.initFactoidPluginAccess();
		parameters = new Parameters();
		types = new Types();
		conf = new Config();
		log = new Log();
		dependPlugin = new DependPlugin();
		if ((conf.useEconomy() == true) && (dependPlugin.getEconomy() != null)) {
			playerMoney = new PlayerMoney();
		}
		else {
			playerMoney = null;
		}
		playerConf = new PlayerStaticConfig();
		playerConf.addAll();
		language = new Lang();
		storageThread = new StorageThread();
		lands = new Lands();
		storageThread.loadAllAndStart();
		worldListener = new WorldListener();
		playerListener = new PlayerListener();
		if (BKVersion.isPlayerInteractAtEntityEventExist()) {
			playerListener18 = new PlayerListener18();
		}
		pvpListener = new PvpListener();
		landListener = new LandListener();
		chatListener = new ChatListener();
		CommandListener = new OnCommand();
		approveNotif = new ApproveNotif ();
		approveNotif.runApproveNotifLater();
		ecoScheduler = new EcoScheduler();
		ecoScheduler.runTaskTimer(this, ECO_SCHEDULE_INTERVAL, ECO_SCHEDULE_INTERVAL);
		playersCache = new PlayersCache();
		playersCache.start();
		getServer().getPluginManager().registerEvents(worldListener, this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		if (BKVersion.isPlayerInteractAtEntityEventExist()) {
			getServer().getPluginManager().registerEvents(playerListener18, this);
		}
		getServer().getPluginManager().registerEvents(pvpListener, this);
		getServer().getPluginManager().registerEvents(landListener, this);
		getServer().getPluginManager().registerEvents(chatListener, this);
		getCommand("factoid").setExecutor(CommandListener);
		getCommand("faction").setExecutor(CommandListener);
		log.write(iLanguage().getMessage("ENABLE"));
	}

	/**
	 * Reload.
	 */
	public void reload() {

		types = new Types();
		// No reload of Parameters to avoid Deregistering external parameters
		conf.reloadConfig();
		if ((conf.useEconomy() == true) && (dependPlugin.getEconomy() != null)) {
			playerMoney = new PlayerMoney();
		}
		else {
			playerMoney = null;
		}
		log.setDebug(conf.isDebug());
		language.reloadConfig();
		lands = new Lands();
		storageThread.stopNextRun();
		storageThread = new StorageThread();
		storageThread.loadAllAndStart();
		approveNotif.stopNextRun();
		approveNotif.runApproveNotifLater();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable() */
	@Override
	public void onDisable() {

		log.write(iLanguage().getMessage("DISABLE"));
		playersCache.stopNextRun();
		approveNotif.stopNextRun();
		storageThread.stopNextRun();
		playerConf.removeAll();
	}

	/**
	 * I conf.
	 *
	 * @return the config
	 */
	public Config iConf() {

		return conf;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.IFactoid#iPlayerConf() */
	@Override
	public PlayerStaticConfig iPlayerConf() {

		return playerConf;
	}

	/**
	 * I language.
	 *
	 * @return the lang
	 */
	public Lang iLanguage() {

		return language;
	}

	/**
	 * I log.
	 *
	 * @return the log
	 */
	public Log iLog() {

		return log;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.IFactoid#iParameters() */
	@Override
	public Parameters iParameters() {

		return parameters;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.IFactoid#iLands() */
	@Override
	public Lands iLands() {

		return lands;
	}

	@Override
	public Types iTypes() {

		return types;
	}

	/**
	 * I storage thread.
	 *
	 * @return the storage thread
	 */
	public StorageThread iStorageThread() {

		return storageThread;
	}

	/**
	 * I depend plugin.
	 *
	 * @return the depend plugin
	 */
	public DependPlugin iDependPlugin() {

		return dependPlugin;
	}

	/**
	 * I approve notif.
	 *
	 * @return the approve notif
	 */
	public ApproveNotif iApproveNotif () {

		return approveNotif;
	}

	/**
	 * I player money.
	 *
	 * @return the player money
	 */
	public PlayerMoney iPlayerMoney() {

		return playerMoney;
	}

	/**
	 * I players cache.
	 *
	 * @return the players cache
	 */
	public PlayersCache iPlayersCache() {

		return playersCache;
	}

	/* Creators to forward */

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.IFactoid#createPlayerContainer(me.tabinol.factoidapi.lands.ILand,
	 * me.tabinol.factoidapi.playercontainer.EPlayerContainerType, java.lang.String) */
	@Override
	public PlayerContainer createPlayerContainer(final ILand land, final EPlayerContainerType pct, final String name) {

		return PlayerContainer.create(land, pct, name);
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoidapi.IFactoid#createCuboidArea(java.lang.String, int, int, int, int, int, int) */
	@Override
	public ICuboidArea createCuboidArea(final String worldName, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {

		return new CuboidArea(worldName, x1, y1, z1, x2, y2, z2);
	}

}
