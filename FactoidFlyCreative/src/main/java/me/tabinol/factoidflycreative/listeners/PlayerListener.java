/*
 FactoidFlyCreative: Minecraft Factoid plugin for fly and creative control
 Copyright (C) 2014  Michel Blanchet
 Rebuild from ResCreative and ResFly by Kolorafa

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
package me.tabinol.factoidflycreative.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.event.LandModifyEvent;
import me.tabinol.factoidapi.event.LandModifyEvent.LandModifyReason;
import me.tabinol.factoidapi.event.PlayerLandChangeEvent;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidflycreative.FactoidFlyCreative;
import me.tabinol.factoidflycreative.config.FlyCreativeConfig;
import me.tabinol.factoidflycreative.creative.Creative;
import me.tabinol.factoidflycreative.fly.Fly;

public class PlayerListener implements Listener {

	private final Fly fly;
	private final Creative creative;
	private final FlyCreativeConfig conf;
	private final ArrayList<Player> ignoredGMPlayers;

	public PlayerListener() {

		fly = new Fly();
		creative = new Creative();
		conf = FactoidFlyCreative.getConf();
		ignoredGMPlayers = new ArrayList<>();
	}

	public void addIgnoredGMPlayers(final Player player) {
		ignoredGMPlayers.add(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		setFlyCreative(event, event.getPlayer(),
				FactoidAPI.iLands().getLandOrOutsideArea(event.getPlayer().getLocation()));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		creative.setGM(event.getPlayer(), GameMode.SURVIVAL);
		ignoredGMPlayers.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLandChange(final PlayerLandChangeEvent event) {
		setFlyCreative(event, event.getPlayer(), event.getLandOrOutside());
	}

	// Bugfix when tp is from an other world
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			runTaskLater(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(final PlayerGameModeChangeEvent event) {
		final Player player = event.getPlayer();

		if (!ignoredGMPlayers.remove(player) &&
			!conf.getIgnoredGameMode().contains(event.getNewGameMode()) &&
			!player.hasPermission(Creative.CREATIVE_IGNORE_PERM)
		) {
			event.setCancelled(true);
		}

		runTaskLater(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLandModify(final LandModifyEvent event) {
		final LandModifyReason reason = event.getLandModifyReason();

		// Test to be specific (take specific players)
		if (reason == LandModifyReason.AREA_ADD || reason == LandModifyReason.AREA_REMOVE
				|| reason == LandModifyReason.AREA_REPLACE) {

			// Land area change, all players in the world affected
			for (final Player player : event.getLand().getWorld().getPlayers()) {
				setFlyCreative(event, player, FactoidAPI.iLands().getLandOrOutsideArea(player.getLocation()));
			}
		} else if (reason != LandModifyReason.FLAG_SET && reason != LandModifyReason.FLAG_UNSET
				&& reason != LandModifyReason.RENAME) {

			// No land resize or area replace, only players in the land affected
			for (final Player player : event.getLand().getPlayersInLandAndChildren()) {
				setFlyCreative(event, player, FactoidAPI.iLands().getLandOrOutsideArea(player.getLocation()));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE &&
			creative.dropItem(event.getPlayer())
		) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryOpen(final InventoryOpenEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			creative.invOpen(event, event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			if (creative.build(event, event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			if (creative.build(event, event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClose(final InventoryCloseEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			creative.checkBannedItems(event, event.getPlayer());
		}

	}

	private void setFlyCreative(final Event event, final Player player, final IDummyLand dummyLand) {
		if (!conf.getIgnoredGameMode().contains(player.getGameMode())
				&& !creative.creative(event, player, dummyLand)) {
			fly.fly(event, player, dummyLand);
		}
	}

	private void runTaskLater(final Player player) {
		Bukkit.getScheduler().runTaskLater(
			FactoidFlyCreative.getThisPlugin(),
			() -> {
				if (player.isOnline()) {
					setFlyCreative(
						null,
						player,
						FactoidAPI.iLands().getLandOrOutsideArea(player.getLocation())
					);
				}
			},
			// 1.5 seconds of delay
			30
		);
	}

}
