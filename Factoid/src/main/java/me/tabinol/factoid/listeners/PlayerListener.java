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
package me.tabinol.factoid.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.plugin.PluginManager;

import me.tabinol.factoid.BKVersion;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.commands.executor.CommandEcosign;
import me.tabinol.factoid.commands.executor.CommandEcosign.SignType;
import me.tabinol.factoid.commands.executor.CommandInfo;
import me.tabinol.factoid.commands.executor.CommandSelect;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.selection.region.PlayerMoveListen;
import me.tabinol.factoid.selection.region.RegionSelection;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.config.players.IPlayerConfEntry;
import me.tabinol.factoidapi.event.PlayerLandChangeEvent;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.parameters.IParameters.SpecialPermPrefix;
import me.tabinol.factoidapi.utilities.StringChanges;


/**
 * Players listener
 */
public class PlayerListener extends CommonListener implements Listener {

	/** The Constant DEFAULT_TIME_LAPS. */
	public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds

	/** The conf. */
	private final Config conf = Factoid.getThisPlugin().iConf();

	/** The player conf. */
	private final PlayerStaticConfig playerConf = Factoid.getThisPlugin().iPlayerConf();

	/** The time check. */
	private final int timeCheck = DEFAULT_TIME_LAPS;

	/** The pm. */
	private final PluginManager pm = Factoid.getThisPlugin().getServer().getPluginManager();

	private final Map<Entity, Boolean> getShootByFlameArrow = new HashMap<>();

	/**
	 * On player join.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// Update players cache
		Factoid.getThisPlugin().iPlayersCache().updatePlayer(player.getUniqueId(), player.getName());

		// Create a new static config
		final PlayerConfEntry entry = playerConf.add(player);

		// BugFix Citizens plugin
		if (entry == null) {
			return;
		}

		updatePosInfo(event, entry, player.getLocation(), true);

		// Check if AdminMod is auto
		if (player.hasPermission("factoid.adminmod.auto")) {
			playerConf.get(player).setAdminMod(true);
		}
	}

	// Must be running after LandListener
	/**
	 * On player quit.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		// Get static config
		final PlayerConfEntry entry = playerConf.get(player);

		// BugFix Citizens plugin
		if (entry == null) {
			return;
		}

		// Remove player from the land
		final IDummyLand land = playerConf.get(player).getLastLand();
		if (land instanceof ILand) {
			((Land) land).removePlayerInLand(player);
		}

		// Remove player from Static Config
		playerConf.remove(player);
	}

	/**
	 * On player teleport.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		final Location loc = event.getTo();
		final Player player = event.getPlayer();
		final PlayerConfEntry entry = playerConf.get(player);
		final IDummyLand land;

		// BugFix Citizens plugin
		if (entry == null) {
			return;
		}

		if (!entry.hasTpCancel()) {
			updatePosInfo(event, entry, loc, false);
		} else {
			entry.setTpCancel(false);
		}

		land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());

		// TP With ender pearl
		if (!playerConf.get(event.getPlayer()).isAdminMod()
				&& event.getCause() == TeleportCause.ENDER_PEARL
				&& !checkPermission(land, player,
						PermissionList.ENDERPEARL_TP.getPermissionType())) {
			messagePermission(player);
			event.setCancelled(true);
		}
	}

	/**
	 * On player move.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final PlayerConfEntry entry = playerConf.get(player);

		if (player == null || entry == null) {
			return;
		}
		final long last = entry.getLastMoveUpdate();
		final long now = System.currentTimeMillis();
		if (now - last < timeCheck) {
			return;
		}
		entry.setLastMoveUpdate(now);
		if (event.getFrom().getWorld() == event.getTo().getWorld() &&
			event.getFrom().distance(event.getTo()) == 0
		) {
				return;
		}
		updatePosInfo(event, entry, event.getTo(), false);
	}

	/**
	 * On player interact.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Material ml = event.getClickedBlock().getType();
		final Player player = event.getPlayer();
		final Action action = event.getAction();
		final PlayerConfEntry entry = playerConf.get(player);
		final Location loc = event.getClickedBlock().getLocation();

		Factoid.getThisPlugin().iLog().write(
				"PlayerInteract player name: " + event.getPlayer().getName()
						+ ", Action: " + event.getAction()
						+ ", Material: " + ml.name());

		// For infoItem
		if (player.getInventory().getItemInMainHand() != null
				&& action == Action.LEFT_CLICK_BLOCK
				&& player.getInventory().getItemInMainHand().getType() == conf.getInfoItem()) {
			try {
				new CommandInfo(player,
						Factoid.getThisPlugin().iLands().getCuboidArea(
						event.getClickedBlock().getLocation()))
						.commandExecute();
			} catch (final FactoidCommandException ex) {
				Logger.getLogger(PlayerListener.class.getName()).log(
						Level.SEVERE, "Error when trying to get area", ex);
			}
			event.setCancelled(true);
		}
		// For Select
		else if (player.getInventory().getItemInMainHand() != null
				&& action == Action.LEFT_CLICK_BLOCK
				&& player.getInventory().getItemInMainHand().getType() == conf.getSelectItem()) {
			try {
				new CommandSelect(player, new ArgList(new String[] { "here" },
						player), event.getClickedBlock().getLocation())
						.commandExecute();
			} catch (final FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}
			event.setCancelled(true);
		}
		// For Select Cancel
		else if (player.getInventory().getItemInMainHand() != null
				&& action == Action.RIGHT_CLICK_BLOCK
				&& player.getInventory().getItemInMainHand().getType() == conf.getSelectItem()
				&& entry != null
				&& entry.getSelection().hasSelection()) {
			try {
				new CommandCancel(entry, false).commandExecute();
			} catch (final FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}
			event.setCancelled(true);
		}
		// For economy (buy or rent/unrent)
		else if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
				&& (ml == Material.SIGN || ml == Material.WALL_SIGN)) {
			final ILand trueLand = Factoid.getThisPlugin().iLands().getLand(loc);

			if (trueLand != null) {
				Factoid.getThisPlugin().iLog().write("EcoSignClick: ClickLoc: " + loc + ", SignLoc: " + trueLand.getSaleSignLoc());
				try {
					if (trueLand.getSaleSignLoc() != null
							&& trueLand.getSaleSignLoc().getBlock().equals(loc.getBlock())) {
						event.setCancelled(true);
						new CommandEcosign(playerConf.get(player), trueLand,
								action, SignType.SALE).commandExecute();

					} else if (trueLand.getRentSignLoc() != null
							&& trueLand.getRentSignLoc().getBlock().equals(loc.getBlock())) {
						event.setCancelled(true);
						new CommandEcosign(playerConf.get(player), trueLand,
								action, SignType.RENT).commandExecute();
					}
				} catch (final FactoidCommandException ex) {
					// Empty, message is sent by the catch
				}
			}
		}
		// Citizen bug, check if entry exist before
		else if (entry != null && !entry.isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| (((action == Action.RIGHT_CLICK_BLOCK // BEGIN of USE
					&& (BKVersion.isDoor(ml)
							|| BKVersion.isButton(ml)
							|| ml == Material.LEVER
							|| ml == Material.ENCHANTING_TABLE
							|| ml == Material.ANVIL
							|| ml == Material.SPAWNER
							|| ml == Material.DAYLIGHT_DETECTOR))
							|| (action == Action.PHYSICAL && (BKVersion.isDoor(ml) || ml == Material.STRING))
						) && !checkPermission(
								land, player,
								PermissionList.USE.getPermissionType())
						) // End of USE
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& BKVersion.isDoor(ml) && !checkPermission(
								land, player,
								PermissionList.USE_DOOR.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& BKVersion.isButton(ml) && !checkPermission(
								land, player,
								PermissionList.USE_BUTTON.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& ml == Material.LEVER && !checkPermission(land,
								player,
								PermissionList.USE_LEVER.getPermissionType()))
					|| (action == Action.PHYSICAL
							&& BKVersion.isDoor(ml) && !checkPermission(
								land, player,
								PermissionList.USE_PRESSUREPLATE
										.getPermissionType()))
					|| (action == Action.PHYSICAL && ml == Material.STRING && !checkPermission(
							land, player,
							PermissionList.USE_STRING.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.SPAWNER
							&& !checkPermission(land, player, PermissionList.USE_MOBSPAWNER.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.DAYLIGHT_DETECTOR
							&& !checkPermission(land, player, PermissionList.USE_LIGHTDETECTOR.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ENCHANTING_TABLE
							&& !checkPermission(land, player, PermissionList.USE_ENCHANTTABLE.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ANVIL
							&& !checkPermission(land, player, PermissionList.USE_ANVIL.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.REPEATER
							&& !checkPermission(land, player, PermissionList.USE_REPEATER.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.COMPARATOR
							&& !checkPermission(land, player, PermissionList.USE_COMPARATOR.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK && ml == Material.NOTE_BLOCK
							&& !checkPermission(land, player, PermissionList.USE_NOTEBLOCK.getPermissionType()))) {
				if (action != Action.PHYSICAL) {
					messagePermission(player);
				}
				event.setCancelled(true);
			}
			else if (action == Action.RIGHT_CLICK_BLOCK
					&& (((ml == Material.CHEST // Begin of OPEN
							|| ml == Material.TRAPPED_CHEST
							|| ml == Material.ENDER_CHEST
							|| ml == Material.CRAFTING_TABLE
							|| ml == Material.BREWING_STAND
							|| ml == Material.FURNACE
							|| ml == Material.BEACON
							|| ml == Material.DROPPER
							|| ml == Material.HOPPER
							|| ml == Material.DISPENSER
							|| ml == Material.JUKEBOX
							|| event.getClickedBlock().getState() instanceof ShulkerBox
						) && !checkPermission(land, player, PermissionList.OPEN.getPermissionType())
						) // End of OPEN
							|| (ml == Material.CHEST && !checkPermission(land,
									player,
									PermissionList.OPEN_CHEST
											.getPermissionType()))
							|| (ml == Material.TRAPPED_CHEST && !checkPermission(land,
									player,
									PermissionList.OPEN_TRAPPEDCHEST
											.getPermissionType()))
							|| (event.getClickedBlock().getState() instanceof ShulkerBox && !checkPermission(
									land, player,
									PermissionList.OPEN_SHULKER_BOX
											.getPermissionType()))
							|| (ml == Material.ENDER_CHEST && !checkPermission(
									land, player,
									PermissionList.OPEN_ENDERCHEST
											.getPermissionType()))
							|| (ml == Material.CRAFTING_TABLE && !checkPermission(
									land, player,
									PermissionList.OPEN_CRAFT
											.getPermissionType()))
							|| (ml == Material.BREWING_STAND && !checkPermission(
									land, player,
									PermissionList.OPEN_BREW.getPermissionType()))
							|| (ml == Material.FURNACE && !checkPermission(
									land, player,
									PermissionList.OPEN_FURNACE
											.getPermissionType()))
							|| (ml == Material.BEACON && !checkPermission(land,
									player,
									PermissionList.OPEN_BEACON
											.getPermissionType()))
							|| (ml == Material.DISPENSER && !checkPermission(land,
									player,
									PermissionList.OPEN_DISPENSER
											.getPermissionType()))
							|| (ml == Material.DROPPER && !checkPermission(
									land, player,
									PermissionList.OPEN_DROPPER
											.getPermissionType())) || (ml == Material.HOPPER && !checkPermission(
							land, player,
							PermissionList.OPEN_HOPPER.getPermissionType()))
							|| (ml == Material.JUKEBOX && !checkPermission(
									land, player,
									PermissionList.OPEN_JUKEBOX.getPermissionType())))
					// For dragon egg fix
					|| (ml == Material.DRAGON_EGG && (!checkPermission(land,
							event.getPlayer(),
							PermissionList.BUILD.getPermissionType()) || !checkPermission(
							land, event.getPlayer(),
							PermissionList.BUILD_DESTROY.getPermissionType())))) {
				messagePermission(player);
				event.setCancelled(true);
			}
			// For armor stand and every type of minecart (tnt, storage, regular, etc)
			else if (player.getInventory().getItemInMainHand() != null
					&& action == Action.RIGHT_CLICK_BLOCK
					&& (BKVersion.isArmorStand(player.getInventory().getItemInMainHand().getType()) ||
						player.getInventory().getItemInMainHand().getType().name().contains("MINECART"))
					&& ((land instanceof ILand && ((ILand) land).isBanned(event.getPlayer()))
						|| !checkPermission(land, event.getPlayer(),
								PermissionList.BUILD.getPermissionType())
						|| !checkPermission(land, event.getPlayer(),
								PermissionList.BUILD_PLACE.getPermissionType()))) {
				messagePermission(player);
				event.setCancelled(true);
			}
			// For head place fix (do not spawn a wither)
			else if (action == Action.RIGHT_CLICK_BLOCK
					&& event.getMaterial() == Material.PLAYER_HEAD
					&& (!checkPermission(land, event.getPlayer(), PermissionList.BUILD.getPermissionType()) ||
						!checkPermission(land, event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))
			) {
				messagePermission(player);
				event.setCancelled(true);
			}
			// End Crystal placed
			else if (action == Action.RIGHT_CLICK_BLOCK &&
				Material.END_CRYSTAL == event.getMaterial() &&
				!checkPermission(land, player, PermissionList.PLACE_END_CRYSTAL.getPermissionType())
			) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On block place.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		// Check for fire init
		final Player player = event.getPlayer();

		if (event.getBlock().getType() == Material.FIRE) {
			if (checkForPutFire(event, player)) {
				event.setCancelled(true);
			}
		} else if (!playerConf.get(player).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getBlock().getLocation());
			final Material mat = event.getBlock().getType();

			if (land instanceof ILand && ((ILand) land).isBanned(player)) {
				// Player banned!!
				messagePermission(player);
				event.setCancelled(true);

			} else if (!checkPermission(land, player, PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType())) {
				if (checkPermission(land, player,
						FactoidAPI.iParameters().getSpecialPermission(SpecialPermPrefix.PLACE, mat))) {
					messagePermission(player);
					event.setCancelled(true);
				}
			} else if (!checkPermission(land, player,
					FactoidAPI.iParameters().getSpecialPermission(SpecialPermPrefix.NOPLACE, mat))) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On hanging place.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHangingPlace(final HangingPlaceEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getEntity().getLocation());
			final Player player = event.getPlayer();

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| !checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_PLACE.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player interact entity.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			final Player player = event.getPlayer();
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
				event.getRightClicked().getLocation());
			if (land instanceof ILand) {
				if (event.getRightClicked() instanceof Tameable &&
					!event.getPlayer().equals(((Tameable) event.getRightClicked()).getOwner()) &&
					!checkPermission(land, player, PermissionList.USE_TAMEABLE.getPermissionType())
				) {
					messagePermission(player);
					event.setCancelled(true);
				} else if (event.getRightClicked() instanceof Merchant &&
					!checkPermission(land, player, PermissionList.TRADE.getPermissionType())
				) {
					messagePermission(player);
					event.setCancelled(true);
				} else if (event.getRightClicked() instanceof StorageMinecart &&
					!checkPermission(land, player, PermissionList.OPEN_CHEST.getPermissionType())
				) {
					messagePermission(player);
					event.setCancelled(true);
				} else if (event.getRightClicked() instanceof Minecart &&
					!checkPermission(land, player, PermissionList.USE.getPermissionType())
				) {
					messagePermission(player);
					event.setCancelled(true);
				} else if (event.getRightClicked() instanceof ItemFrame &&
					(!checkPermission(land, player,PermissionList.BUILD.getPermissionType()) ||
					!checkPermission(land, player,PermissionList.BUILD_PLACE.getPermissionType()))
				) {
					messagePermission(player);
					event.setCancelled(true);
				}
			}
		}
	}

	/**
	 * On block break.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();

		if (!playerConf.get(player).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getBlock().getLocation());
			final Material mat = event.getBlock().getType();

			if (land instanceof ILand && (((ILand) land).isBanned(player)
					|| hasEcoSign((Land) land, event.getBlock()))) {
				// Player banned (or ecosign)
				messagePermission(player);
				event.setCancelled(true);
			} else if (!checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				if (checkPermission(land, player,
						FactoidAPI.iParameters().getSpecialPermission(SpecialPermPrefix.DESTROY, mat))) {
					messagePermission(player);
					event.setCancelled(true);
				}
			} else if (!checkPermission(land, player,
						FactoidAPI.iParameters().getSpecialPermission(SpecialPermPrefix.NODESTROY, mat))) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On hanging break by entity.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
		final Player player;

		if (event.getRemover() instanceof Player
				&& !playerConf.get((player = (Player) event.getRemover()))
						.isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getEntity().getLocation());

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| !checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player drop item.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final IPlayerConfEntry entry = playerConf.get(player);

		if (entry != null && !entry.isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					player.getLocation());

			if (!checkPermission(land, event.getPlayer(),
					PermissionList.DROP.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player pickup item.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(final EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player player = (Player) event.getEntity();

		if (!playerConf.get(player).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());
			if (!checkPermission(land, player, PermissionList.PICKETUP.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player bed enter.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getBed().getLocation());

			if ((land instanceof ILand && ((ILand) land).isBanned(event
					.getPlayer()))
					|| (!checkPermission(land, event.getPlayer(),
							PermissionList.SLEEP.getPermissionType()))) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On entity damage by entity.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		final Player player = getSourcePlayer(event.getDamager());

		// Check for non-player kill
		if (player != null) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getEntity().getLocation());
			final Entity entity = event.getEntity();
			final EntityType et = entity.getType();
			final IPlayerConfEntry entry = playerConf.get(player);

			// Must not be a Citizens as a Damager nor a Target
			if (entry != null) {
				// Flame bow case :(
				if (entity instanceof Player &&
					playerConf.get(entity) != null &&
					(event.getDamager() instanceof Projectile || event.getCause() == DamageCause.FIRE_TICK) &&
					event.getDamager().getType() != EntityType.SHULKER &&
					!land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean()
				) {
					event.setCancelled(true);
					getShootByFlameArrow.put(entity, true);
					return;
				}
				getShootByFlameArrow.put(entity, false);

				// kill an entity (none player)
				if (!entry.isAdminMod()
					&& (
						(land instanceof ILand && ((ILand) land).isBanned(player))
						|| ((BKVersion.isArmorStand(et) || entity instanceof Hanging)
								&& (!checkPermission(land, player,
										PermissionList.BUILD.getPermissionType())
								|| !checkPermission(land, player,
										PermissionList.BUILD_DESTROY.getPermissionType())))
						|| (entity instanceof Minecart)
								&& (!checkPermission(land, player,
										PermissionList.BUILD.getPermissionType())
								|| !checkPermission(land, player,
										PermissionList.BUILD_DESTROY.getPermissionType()))
						|| (entity instanceof Animals && !checkPermission(
								land, player,
								PermissionList.ANIMAL_KILL
										.getPermissionType()))
						|| (entity instanceof Monster && !checkPermission(
								land, player,
								PermissionList.MOB_KILL
										.getPermissionType()))
						|| (et == EntityType.VILLAGER && !checkPermission(
								land, player,
								PermissionList.VILLAGER_KILL
										.getPermissionType()))
						|| (et == EntityType.IRON_GOLEM && !checkPermission(
								land, player,
								PermissionList.VILLAGER_GOLEM_KILL
										.getPermissionType()))
						|| (et == EntityType.HORSE && !checkPermission(
								land, player,
								PermissionList.HORSE_KILL
											.getPermissionType())) || (entity instanceof Tameable
							&& ((Tameable) entity).isTamed()
							&& ((Tameable) entity).getOwner() != player && !checkPermission(
								land, player,
								PermissionList.TAMED_KILL
										.getPermissionType()))
					)
				) {
					messagePermission(player);
					event.setCancelled(true);
				}
			}
		}
	}

	/**
	 * On player bucket fill.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getBlockClicked().getLocation());
			final Material mt = event.getBlockClicked().getType();

			if ((land instanceof ILand && ((ILand) land).isBanned(event
					.getPlayer()))
					|| (mt == Material.LAVA_BUCKET && !checkPermission(land,
							event.getPlayer(),
							PermissionList.BUCKET_LAVA.getPermissionType()))
					|| (mt == Material.WATER_BUCKET && !checkPermission(land,
							event.getPlayer(),
							PermissionList.BUCKET_WATER.getPermissionType()))) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player bucket empty.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			final Block block = event.getBlockClicked().getRelative(event.getBlockFace());
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					block.getLocation());
			final Material mt = event.getBucket();

			if ((land instanceof ILand && ((ILand) land).isBanned(event
					.getPlayer()))
					|| (mt == Material.LAVA_BUCKET && !checkPermission(land,
							event.getPlayer(),
							PermissionList.BUCKET_LAVA.getPermissionType()))
					|| (mt == Material.WATER_BUCKET && !checkPermission(land,
							event.getPlayer(),
							PermissionList.BUCKET_WATER.getPermissionType()))) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On entity change block.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
		// Crop trample
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
				event.getBlock().getLocation());
		final Material matFrom = event.getBlock().getType();
		final Material matTo = event.getTo();
		final Player player;

		if (event.getEntity() instanceof Player
				&& playerConf.get(player = (Player) event.getEntity()) != null // Citizens bugfix
		&& ((land instanceof ILand && ((ILand) land).isBanned(player))
				|| (matFrom == Material.FARMLAND
				&& matTo == Material.DIRT
				&& !checkPermission(land, player,
						PermissionList.CROP_TRAMPLE.getPermissionType())))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	// Must be after Essentials
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final IPlayerConfEntry entry = playerConf.get(player);
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());

		final String strLoc = land.getFlagAndInherit(FlagList.SPAWN.getFlagType()).getValueString();
		final Location loc = StringChanges.stringToLocation(strLoc != null ? strLoc : "");

		// For repsawn after death
		if (loc != null &&
			entry != null &&
			!StringUtils.isEmpty(strLoc) &&
			land.checkPermissionAndInherit(player, PermissionList.TP_DEATH.getPermissionType())
		) {
			// Respawn in the bed if its in the land zone
			if (player.getBedSpawnLocation() != null &&
				land instanceof ILand &&
				((ILand) land).isLocationInside(player.getBedSpawnLocation())
			) {
				event.setRespawnLocation(player.getBedSpawnLocation());
			}
			else {
				event.setRespawnLocation(loc);
			}
		}
	}

	/**
	 * On player respawn2.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	// For land listener
	public void onPlayerRespawn2(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final PlayerConfEntry entry = playerConf.get(player);
		final Location loc = event.getRespawnLocation();

		updatePosInfo(event, entry, loc, false);
	}

	/**
	 * On block ignite.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event) {
		if (checkForPutFire(event, event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	/**
	 * On potion splash.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplash(final PotionSplashEvent event) {
		if (event.getEntity() != null
				&& event.getEntity().getShooter() instanceof Player) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getPotion().getLocation());
			final Player player = (Player) event.getEntity().getShooter();

			if (!checkPermission(land, player,
					PermissionList.POTION_SPLASH.getPermissionType())) {
				if (player.isOnline()) {
					messagePermission(player);
				}
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On entity regain health.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
		final Entity entity = event.getEntity();
		final Player player;
		final IPlayerConfEntry entry;

		if (entity != null && event.getEntity() instanceof Player
				&& (event.getRegainReason() == RegainReason.REGEN
				|| event.getRegainReason() == RegainReason.SATIATED)
				&& (entry = playerConf.get((player = (Player) event.getEntity()))) != null
				&& !entry.isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());

			if (!checkPermission(land, player, PermissionList.FOOD_HEAL.getPermissionType())) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player item consume.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final IPlayerConfEntry entry = playerConf.get(player);

		if (entry != null && !entry.isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());
			// Disallow the chorus fruit tp when eaten
			if (event.getItem().getType() == Material.CHORUS_FRUIT &&
					!land.getFlagAndInherit(FlagList.CHORUS_FRUIT_TP.getFlagType()).getValueBoolean()
			) {
				messagePermission(player);
				event.setCancelled(true);
			}
			else if (!checkPermission(land, player, PermissionList.EAT.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player command preprocess.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();

		if (!playerConf.get(player).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(player.getLocation());
			final String[] excludedCommands = land.getFlagAndInherit(FlagList.EXCLUDE_COMMANDS.getFlagType()).getValueStringList();

			if (excludedCommands.length > 0) {
				final String commandTyped = event.getMessage().substring(1).split(" ")[0];
				final PluginCommand plCommand = Bukkit.getPluginCommand(commandTyped);

				for (final String commandTest : excludedCommands) {
					final String sanitizedCommand = commandTest.replace("/", "");
					if ((plCommand == null && sanitizedCommand.equalsIgnoreCase(commandTyped)) ||
						(
							plCommand != null &&
							(
								sanitizedCommand.equalsIgnoreCase(plCommand.getLabel()) ||
								plCommand.getAliases().stream()
									.anyMatch(alias -> alias.equalsIgnoreCase(sanitizedCommand))
							)
						)
					) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED
								+ "[Factoid] "
								+ Factoid.getThisPlugin().iLanguage().getMessage(
										"GENERAL.MISSINGPERMISSIONHERE"));
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVehicleDamage(final VehicleDamageEvent event) {
		if (checkVehiclePermission(event.getAttacker(), event.getVehicle())) {
			messagePermission((Player) event.getAttacker());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVehicleDestroy(final VehicleDestroyEvent event) {
		if (checkVehiclePermission(event.getAttacker(), event.getVehicle())) {
			messagePermission((Player) event.getAttacker());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {
		final DummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getEntity().getLocation());

		// If the targeted entity is a Player
		if (event.getEntity() instanceof Player) {
			// The land as the GOD flag activated ?
			if (!checkPermission(land, (Player) event.getEntity(), PermissionList.GOD.getPermissionType())) {
				event.setCancelled(true);
			}
			// Or the player were shot by a flame bow
			else if (Boolean.TRUE.equals(getShootByFlameArrow.get(event.getEntity())) &&
				!land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() &&
				(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)
			) {
				event.setCancelled(true);
			}
		}
		// If it's in a real cubo and it's an ArmorStand or a passive monster (or can be tamed)
		else if (land instanceof Land &&
			(event.getEntity() instanceof ArmorStand ||
			event.getEntity() instanceof Minecart ||
			event.getEntity() instanceof Animals ||
			event.getEntity() instanceof Tameable)
		) {
			if (event.getCause() == DamageCause.PROJECTILE ||
				event.getCause() == DamageCause.FIRE_TICK
			) {
				event.setCancelled(true);
			} else if (event.getCause() == DamageCause.ENTITY_EXPLOSION &&
				!land.getFlagAndInherit(FlagList.FIREWORK_DAMAGE.getFlagType()).getValueBoolean()
			) {
				event.setCancelled(true);
			}
		}
	}

	private boolean checkVehiclePermission(
		final Entity attacker,
		final Vehicle vehicle
	) {
		if (!(attacker instanceof Player)) {
			return false;
		}
		final Player player = (Player) attacker;
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
			vehicle.getLocation()
		);
		final IPlayerConfEntry entry = playerConf.get(player);

		// Forget if it's a Citizens or AdminMod
		if (entry == null || entry.isAdminMod()) {
			return false;
		}

		// If attacker banned, or can don't have BUILD_DESTROY permission
		return (land instanceof ILand && ((ILand) land).isBanned(player)) ||
			!checkPermission(land, player, PermissionList.BUILD.getPermissionType()) ||
			!checkPermission(land, player, PermissionList.BUILD_DESTROY.getPermissionType());
	}

	/**
	 * Check when a player deposits fire
	 *
	 * @param event the event
	 * @param player the player
	 * @return if the event must be cancelled
	 */
	private boolean checkForPutFire(final BlockEvent event, final Player player) {
		if (player != null && !playerConf.get(player).isAdminMod()) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getBlock().getLocation());

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| (!checkPermission(land, player,
							PermissionList.FIRE.getPermissionType()))) {
				messagePermission(player);
				return true;
			}
		}

		return false;
	}

	/**
	 * Update pos info.
	 *
	 * @param event the event
	 * @param entry the entry
	 * @param loc the loc
	 * @param newPlayer the new player
	 */
	private void updatePosInfo(
		final Event event,
		final PlayerConfEntry entry,
		final Location loc,
		final boolean newPlayer
	) {
		// BugFix Citizens plugin
		if (entry == null) {
			return;
		}

		final Player player = entry.getPlayer();
		final boolean isTp = event instanceof PlayerTeleportEvent;
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);
		final IDummyLand landOld = newPlayer ? land : entry.getLastLand();

		if (newPlayer) {
			entry.setLastLand(land);
		}
		final PlayerLandChangeEvent landEvent = new PlayerLandChangeEvent(newPlayer ? null : (DummyLand) landOld,
			land, player, entry.getLastLoc(), loc, isTp);

		if (newPlayer || land != landOld) {
			// First parameter : If it is a new player, it is null, if not new
			// player, it is "landOld"
			pm.callEvent(landEvent);

			if (landEvent.isCancelled()) {
				if (isTp) {
					((PlayerTeleportEvent) event).setCancelled(true);
					return;
				}
				if (land == landOld || newPlayer) {
					player.teleport(player.getWorld().getSpawnLocation());
				} else {
					final Location retLoc = entry.getLastLoc();
					player.teleport(new Location(retLoc.getWorld(), retLoc
							.getX(), retLoc.getBlockY(), retLoc.getZ(), loc
							.getYaw(), loc.getPitch()));
				}
				entry.setTpCancel(true);
				return;
			}
			entry.setLastLand(land);

			// Update player in the lands
			if (landOld instanceof ILand && landOld != land) {
				((me.tabinol.factoid.lands.Land) landOld).removePlayerInLand(player);
			}
			if (land instanceof ILand) {
				((me.tabinol.factoid.lands.Land) land).addPlayerInLand(player);
			}
		}
		entry.setLastLoc(loc);

		// Update visual selection
		if (entry.getSelection().hasSelection()) {
			for (final RegionSelection sel : entry.getSelection().getSelections()) {
				if (sel instanceof PlayerMoveListen) {
					((PlayerMoveListen) sel).playerMove();
				}
			}
		}
	}
}
