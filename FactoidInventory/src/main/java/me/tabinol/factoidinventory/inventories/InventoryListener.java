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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.event.LandModifyEvent;
import me.tabinol.factoidapi.event.LandModifyEvent.LandModifyReason;
import me.tabinol.factoidapi.event.PlayerLandChangeEvent;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidinventory.FactoidInventory;

public class InventoryListener implements Listener {

	private final InventoryStorage inventoryStorage;

	public InventoryListener() {
		inventoryStorage = new InventoryStorage();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		inventoryStorage.switchInventory(player,
				getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.JOIN);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}

	/**
	 * Called when there is a shutdown
	 */
	public void removeAndSave() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			removePlayer(player);
		}
	}

	public void forceSave() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			inventoryStorage.saveInventory(player, null, true, true, false, false, false);
			final InventorySpec invSpec = FactoidInventory.getConf().getInvSpec(getDummyLand(player.getLocation()));
			inventoryStorage.saveInventory(player, invSpec.getInventoryName(),
					player.getGameMode() == GameMode.CREATIVE, false, false, false, false);
		}
	}

	public void removePlayer(final Player player) {
		inventoryStorage.switchInventory(player,
				getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.QUIT);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayerGameModeChange(final PlayerGameModeChangeEvent event) {
		final Player player = event.getPlayer();

		inventoryStorage.switchInventory(player,
				getDummyLand(player.getLocation()), event.getNewGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLandChange(final PlayerLandChangeEvent event) {
		final Player player = event.getPlayer();

		inventoryStorage.switchInventory(player,
				event.getLandOrOutside(), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLandModify(final LandModifyEvent event) {
		final LandModifyReason reason = event.getLandModifyReason();

		// Test to be specific (take specific players)
		if (reason == LandModifyReason.AREA_ADD || reason == LandModifyReason.AREA_REMOVE
				|| reason == LandModifyReason.AREA_REPLACE) {

			// Land area change, all players in the world affected
			for (final Player player : event.getLand().getWorld().getPlayers()) {
				inventoryStorage.switchInventory(player,
						FactoidAPI.iLands().getLandOrOutsideArea(player.getLocation()),
						player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
			}
		} else if (reason != LandModifyReason.PERMISSION_SET && reason != LandModifyReason.PERMISSION_SET
				&& reason != LandModifyReason.RENAME) {

			// No land resize or area replace, only players in the land affected
			for (final Player player : event.getLand().getPlayersInLandAndChildren()) {
				inventoryStorage.switchInventory(player,
						FactoidAPI.iLands().getLandOrOutsideArea(player.getLocation()),
						player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();

		inventoryStorage.switchInventory(player,
				getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.DEATH);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final PlayerInvEntry entry = inventoryStorage.getPlayerInvEntry(player);

		// For Citizens bugfix
		if (entry == null) {
			return;
		}

		// Cancel if the world is no drop
		final InventorySpec invSpec = entry.getActualInv();

		if (!invSpec.isAllowDrop()) {
			event.setCancelled(true);
		}
	}

	// On player death, prevent drop
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(final EntityDeathEvent event) {
		// Not a player
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}

		final Player player = (Player) event.getEntity();
		final PlayerInvEntry invEntry = inventoryStorage.getPlayerInvEntry(player);

		// Is from Citizens plugin
		if (invEntry == null) {
			return;
		}

		// Cancel if the world is no drop at death
		final InventorySpec invSpec = invEntry.getActualInv();

		if (!invSpec.isAllowDrop()) {
			event.setDroppedExp(0);
			event.getDrops().clear();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		final PlayerConfEntry factoidPlayer = Factoid.getThisPlugin().iPlayerConf().get(player);

		if (!factoidPlayer.isAdminMod() &&
			inventoryStorage.getPlayerInvEntry(player).getActualInv().isDisabledCommand(event.getMessage().substring(1).split(" ")[0])
		) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED
				+ "[Factoid] "
				+ Factoid.getThisPlugin().iLanguage().getMessage(
						"GENERAL.MISSINGPERMISSIONHERE"));
			return;
		}
	}

	public IDummyLand getDummyLand(final Location location) {
		return FactoidAPI.iLands().getLandOrOutsideArea(location);
	}

	public PlayerInvEntry getPlayerInvEntry(final Player player) {
		return inventoryStorage.getPlayerInvEntry(player);
	}

	public boolean loadDeathInventory(final Player player, final int deathVersion) {
		final InventorySpec invSpec = inventoryStorage.getPlayerInvEntry(player).getActualInv();

		return inventoryStorage.loadInventory(player, invSpec.getInventoryName(), player.getGameMode() == GameMode.CREATIVE, true, deathVersion);
	}

	public void saveDefaultInventory(final Player player, final InventorySpec invSpec) {
		inventoryStorage.saveInventory(
			player,
			invSpec.getInventoryName(),
			player.getGameMode() == GameMode.CREATIVE,
			false,
			true,
			true,
			false
		);
	}
}
