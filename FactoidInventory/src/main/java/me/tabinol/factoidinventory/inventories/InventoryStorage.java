/*
 FactoidInventory: Minecraft plugin for Inventory change (works with Factoid)
 Copyright (C) 2014  Michel Blanchet

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoidinventory.inventories;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidinventory.FactoidInventory;
import me.tabinol.factoidinventory.config.InventoryConfig;

public class InventoryStorage {

	public final static int STORAGE_VERSION = 1;
	public final static String INV_DIR = "inventories";
	public final static String DEFAULT_INV = "DEFAULTINV";
	public final static int MAX_FOOD_LEVEL = 20;
	public final static String DEATH = "DEATH";
	private final FactoidInventory thisPlugin;
	private final HashMap<Player, PlayerInvEntry> playerInvList; // Last inventory

	/**
	 * Player Join, Quit, Change
	 */
	public enum PlayerAction {
		JOIN,
		QUIT,
		CHANGE,
		DEATH;
	}

	/**
	 * Get a name of Game Mode Inventory type
	 */
	public enum InventoryType {
		CREATIVE,
		SURVIVAL;
		public static InventoryType getFromBoolean(final boolean isCreative) {
			if (isCreative) {
				return CREATIVE;
			}
			return SURVIVAL;
		}

	}

	public InventoryStorage() {
		this.thisPlugin = FactoidInventory.getThisPlugin();
		playerInvList = new HashMap<>();
	}

	public void deleteInventory(
		final Player player,
		final String invName,
		final boolean isCreative
	) {
		// player item file
		final String gmName = InventoryType.getFromBoolean(isCreative).name();
		final File playerItemFile = new File(thisPlugin.getDataFolder() + "/" + INV_DIR + "/"
				+ invName + "/" + player.getUniqueId().toString() + "." + gmName + ".yml");

		// Delete file if exist
		if (playerItemFile.exists()) {
			playerItemFile.delete();
		}
	}

	public void saveInventory(
		final Player player,
		final String invName,
		final boolean isCreative,
		final boolean isDeath,
		final boolean isSaveAllowed,
		final boolean isDefaultInv,
		final boolean enderChestOnly
	) {
		// If for some reasons we have to skip save (ex: SaveInventory = false)
		if (!isSaveAllowed) {
			return;
		}
		File file;
		String filePreName;

		// Create directories (if not here)
		file = new File(thisPlugin.getDataFolder() + "/" + INV_DIR);
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(thisPlugin.getDataFolder() + "/" + INV_DIR + "/" + invName);
		if (!file.exists()) {
			file.mkdir();
		}

		// Get the suffix name
		final String gmName = InventoryType.getFromBoolean(isCreative).name();

		if (isDeath) {
			filePreName = player.getUniqueId().toString() + "." + gmName + "." + DEATH + ".1";
			// Death rename
			File actFile = new File(file, "/" + player.getUniqueId().toString() + "." + gmName + "." + DEATH + ".9.yml");
			if (actFile.exists()) {
				actFile.delete();
			}
			for (int t = 8; t >= 1; t--) {
				actFile = new File(file, "/"
						+ player.getUniqueId().toString() + "." + gmName + "." + DEATH + "." + t + ".yml");
				if (actFile.exists()) {
					actFile.renameTo(new File(file, "/"
							+ player.getUniqueId().toString() + "." + gmName + "." + DEATH + "." + (t + 1) + ".yml"));
				}
			}

		} else if (isDefaultInv) {
			// Save default inventory
			filePreName = DEFAULT_INV;
		} else {
			// Save normal inventory
			filePreName = player.getUniqueId().toString() + "." + gmName;
		}

		// Save Inventory
		final YamlConfiguration configPlayerItemFile = new YamlConfiguration();
		final File playerItemFile = new File(file, "/" + filePreName + ".yml");

		try {
			configPlayerItemFile.set("Version", STORAGE_VERSION);

			// Save Only ender chest (Death)
			if (enderChestOnly) {
				configPlayerItemFile.set("Level", 0);
				configPlayerItemFile.set("Exp", 0f);
				configPlayerItemFile.set("Health", player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
				configPlayerItemFile.set("FoodLevel", MAX_FOOD_LEVEL);

				final ItemStack[] itemEnderChest = player.getEnderChest().getContents();
				for (int t = 0; t < 4; t++) {
					configPlayerItemFile.set("Armor." + t, new ItemStack(Material.AIR));
				}
				for (int t = 0; t < itemEnderChest.length; t++) {
					configPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
				}
			// Save all
			} else {
				configPlayerItemFile.set("Level", isDeath ? 0 : player.getLevel());
				configPlayerItemFile.set("Exp", isDeath ? 0f : player.getExp());
				configPlayerItemFile.set("Health", player.getHealth());
				configPlayerItemFile.set("FoodLevel", player.getFoodLevel());

				final ItemStack[] itemListSave = player.getInventory().getContents();
				final ItemStack[] itemArmorSave = player.getInventory().getArmorContents();
				final ItemStack[] itemEnderChest = player.getEnderChest().getContents();
				final ItemStack itemOffhand = player.getInventory().getItemInOffHand();
				configPlayerItemFile.set("OffHand.0", itemOffhand);
				for (int t = 0; t < itemListSave.length; t++) {
					configPlayerItemFile.set("Slot." + t, itemListSave[t]);
				}
				for (int t = 0; t < itemArmorSave.length; t++) {
					configPlayerItemFile.set("Armor." + t, itemArmorSave[t]);
				}
				for (int t = 0; t < itemEnderChest.length; t++) {
					configPlayerItemFile.set("EnderChest." + t, itemEnderChest[t]);
				}

				// PotionsEffects
				final Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
				final ConfigurationSection effectSection = configPlayerItemFile.createSection("PotionEffect");
				for (final PotionEffect effect : activePotionEffects) {
					final ConfigurationSection effectSubSection = effectSection.createSection(effect.getType().getName());
					effectSubSection.set("Duration", effect.getDuration());
					effectSubSection.set("Amplifier", effect.getAmplifier());
					effectSubSection.set("Ambient", effect.isAmbient());
				}
			}

			configPlayerItemFile.save(playerItemFile);

		} catch (final IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error on inventory save for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
		}
	}

	// Return if the inventory exist
	public boolean loadInventory(
		final Player player,
		final String invName,
		final boolean isCreative,
		final boolean fromDeath,
		final int deathVersion
	) {
		boolean invExist = false;
		String suffixName;

		// Get the suffix name
		final String gmName = InventoryType.getFromBoolean(isCreative).name();
		if (fromDeath) {
			suffixName = gmName + "." + DEATH + "." + deathVersion;
		} else {
			suffixName = gmName;
		}

		final YamlConfiguration configPlayerItemFile = new YamlConfiguration();

		// player item file
		File playerItemFile = new File(thisPlugin.getDataFolder() + "/" + INV_DIR + "/"
				+ invName + "/" + player.getUniqueId().toString() + "." + suffixName + ".yml");

		if (!fromDeath && !playerItemFile.exists()) {

			// Check for default inventory file
			playerItemFile = new File(thisPlugin.getDataFolder() + "/" + INV_DIR + "/"
					+ invName + "/" + DEFAULT_INV + ".yml");
		}

		if (playerItemFile.exists()) {
			invExist = true;

			try {
				// load Inventory
				configPlayerItemFile.load(playerItemFile);

				configPlayerItemFile.getInt("Version");

				if (!fromDeath) {
					player.setTotalExperience(configPlayerItemFile.getInt("Experience"));
					player.setLevel(configPlayerItemFile.getInt("Level"));
					final float invExp = (float) configPlayerItemFile.getDouble("Exp", 0D);
					player.setExp(invExp < 0 ? 0F : invExp > 1 ? (invExp - ((int) invExp)) : invExp);

					final double health = configPlayerItemFile.getDouble("Health");
					if (health > 0) {
						player.setHealth(health);
						player.setFoodLevel(configPlayerItemFile.getInt("FoodLevel"));
					} else {
						// Fix Death infinite loop
						player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
						player.setFoodLevel(MAX_FOOD_LEVEL);
					}
				}

				final ItemStack[] itemListLoad = new ItemStack[36];
				final ItemStack[] itemArmorLoad = new ItemStack[4];
				final ItemStack[] itemEnderChest = new ItemStack[27];
				final ItemStack offHand = configPlayerItemFile.getItemStack("OffHand.0");
				for (int t = 0; t < itemListLoad.length; t++) {
					itemListLoad[t] = configPlayerItemFile.getItemStack("Slot." + t);
				}
				for (int t = 0; t < itemArmorLoad.length; t++) {
					itemArmorLoad[t] = configPlayerItemFile.getItemStack("Armor." + t);
				}
				for (int t = 0; t < itemEnderChest.length; t++) {
					itemEnderChest[t] = configPlayerItemFile.getItemStack("EnderChest." + t);
				}

				player.getInventory().setContents(itemListLoad);
				player.getInventory().setArmorContents(itemArmorLoad);
				player.getEnderChest().setContents(itemEnderChest);
				player.getInventory().setItemInOffHand(offHand);

				// PotionsEffects
				removePotionEffects(player);
				final ConfigurationSection effectSection = configPlayerItemFile.getConfigurationSection("PotionEffect");
				if (effectSection != null) {
					for (final Map.Entry<String, Object> effectEntry : effectSection.getValues(false).entrySet()) {

						final PotionEffectType type = PotionEffectType.getByName(effectEntry.getKey());
						final ConfigurationSection effectSubSection = (ConfigurationSection) effectEntry.getValue();
						final int duration = effectSubSection.getInt("Duration");
						final int amplifier = effectSubSection.getInt("Amplifier");
						final boolean ambient = effectSubSection.getBoolean("Ambient");
						player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient), true);
					}
				}

			} catch (final IOException ex) {
				Logger.getLogger(InventoryStorage.class.getName()).log(Level.SEVERE,
						"Error on inventory load for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
			} catch (final InvalidConfigurationException ex) {
				Logger.getLogger(InventoryStorage.class.getName()).log(Level.SEVERE,
						"Invalid configuration on inventory load for player " + player.getName() + ", filename: " + playerItemFile.getPath(), ex);
			}
		} else if (!fromDeath) {

			// The file is not existing, only clear all inventory
			player.setLevel(0);
			player.setExp(0);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			player.setFoodLevel(MAX_FOOD_LEVEL);
			player.getInventory().clear();
			player.getInventory().setBoots(new ItemStack(Material.AIR));
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getEnderChest().clear();
			removePotionEffects(player);
		}

		return invExist;
	}

	private void removePotionEffects(final Player player) {
		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public void switchInventory(final Player player, final IDummyLand dummyLand, boolean toIsCreative, final PlayerAction playerAction) {
		final PlayerInvEntry invEntry = playerInvList.get(player);
		final InventorySpec fromInv = invEntry != null ? invEntry.getActualInv() : null;
		final InventorySpec toInv = FactoidInventory.getConf().getInvSpec(dummyLand);
		boolean fromIsCreative = invEntry != null ? invEntry.isCreativeInv() : false;

		// Check if we have to do this action
		if (player.hasPermission(InventoryConfig.PERM_IGNORE_INV)) {
			return;
		}

		// Force survival value if we do not change to creative inventory
		if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
				|| (fromInv != null && !fromInv.isCreativeChange())) {
			fromIsCreative = false;
		}
		if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
				|| !toInv.isCreativeChange()) {
			toIsCreative = false;
		}

		// Update player inventory information
		if (playerAction != PlayerAction.QUIT) {
			playerInvList.put(player, new PlayerInvEntry(toInv, toIsCreative));
		}

		// Return if the inventory will be exactly the same
		if (playerAction != PlayerAction.DEATH && playerAction != PlayerAction.QUIT &&
			(fromInv != null && fromInv.getInventoryName().equals(toInv.getInventoryName()) &&
			fromIsCreative == toIsCreative)
		) {
			return;
		}

		// If the player is death, save a renamed file
		if (playerAction == PlayerAction.DEATH && fromInv != null) {
			saveInventory(player, fromInv.getInventoryName(), fromIsCreative,
					true, fromInv.isSaveInventory(), false, false);
		}

		// Save last inventory (only EnderChest if death)
		if (playerAction != PlayerAction.JOIN && fromInv != null) {
			saveInventory(player, fromInv.getInventoryName(), fromIsCreative,
					false, fromInv.isSaveInventory(), false, playerAction == PlayerAction.DEATH);
		}

		// Don't load a new inventory if the player quit
		if (playerAction != PlayerAction.QUIT) {
			loadInventory(player, toInv.getInventoryName(), toIsCreative, playerAction == PlayerAction.DEATH, 0);
		}

		// If the player quit, update Offline Inventories and remove player
		if (playerAction == PlayerAction.QUIT) {
			playerInvList.remove(player);
		}
	}

	public PlayerInvEntry getPlayerInvEntry(final Player player) {
		return playerInvList.get(player);
	}
}
