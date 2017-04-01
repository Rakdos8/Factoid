package me.tabinol.factoidflycreative.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.tabinol.factoidflycreative.FactoidFlyCreative;

public class FlyCreativeConfig {

	private final FactoidFlyCreative thisPlugin;
	private FileConfiguration config;
	private Set<GameMode> ignoredGameMode;
	private boolean noDrop;
	private boolean noOpenChest;
	private boolean noBuildOutside;
	private Set<Material> bannedItems;

	public FlyCreativeConfig() {
		thisPlugin = FactoidFlyCreative.getThisPlugin();
		thisPlugin.saveDefaultConfig();
		config = thisPlugin.getConfig();
	}

	/**
	 * Loads the config
	 */
	public final void loadConfig() {
		ignoredGameMode = getGameModeList();
		noDrop = config.getBoolean("Creative.NoDrop");
		noOpenChest = config.getBoolean("Creative.NoOpenChest");
		noBuildOutside = config.getBoolean("Creative.NoBuildOutside");
		bannedItems = getMaterialList();
	}

	/**
	 * Reloads the config
	 */
	public final void reLoadConfig() {
		thisPlugin.reloadConfig();
		config = thisPlugin.getConfig();
		loadConfig();
	}

	/**
	 * @return the ignored {@link GameMode}
	 */
	public final Set<GameMode> getIgnoredGameMode() {
		return ignoredGameMode;
	}

	/**
	 * @return drop is allowed ?
	 */
	public final boolean isNoDrop() {
		return noDrop;
	}

	/**
	 * @return is opening chest allowed ?
	 */
	public final boolean isNoOpenChest() {
		return noOpenChest;
	}

	/**
	 * @return can they build outside ?
	 */
	public final boolean isNoBuildOutside() {
		return noBuildOutside;
	}

	/**
	 * @return get the list of banned {@link Material}
	 */
	public final Set<Material> getBannedItems() {
		return bannedItems;
	}

	private Set<GameMode> getGameModeList() {
		final Set<GameMode> gameModeList = Collections.synchronizedSet(EnumSet.noneOf(GameMode.class));

		for (final String gameModeName : config.getStringList("IgnoredGameMode")) {
			try {
				gameModeList.add(GameMode.valueOf(gameModeName.toUpperCase()));
			} catch(final IllegalArgumentException ex) {
				thisPlugin.getLogger().log(Level.WARNING, gameModeName + " is not a valid game mode!", ex);
			}
		}

		return gameModeList;
	}

	private Set<Material> getMaterialList() {
		final Set<Material> materialList = Collections.synchronizedSet(EnumSet.noneOf(Material.class));;

		for (final String materialName : config.getStringList("Creative.bannedItems")) {
			try {
				materialList.add(Material.valueOf(materialName.toUpperCase()));
			} catch(final IllegalArgumentException ex) {
				thisPlugin.getLogger().log(Level.WARNING, materialName + " is not a valid material!", ex);
			}
		}

		return materialList;
	}

}
