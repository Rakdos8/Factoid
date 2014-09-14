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

import java.util.logging.Level;
import java.util.logging.Logger;

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
import me.tabinol.factoid.event.PlayerLandChangeEvent;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.selection.region.PlayerMoveListen;
import me.tabinol.factoid.selection.region.RegionSelection;
import me.tabinol.factoid.utilities.StringChanges;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginManager;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving player events. The class that is
 * interested in processing a player event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addPlayerListener<code> method. When
 * the player event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see PlayerEvent
 */
public class PlayerListener implements Listener {

	/** The conf. */
	private Config conf;

	/** The player conf. */
	private PlayerStaticConfig playerConf;

	/** The Constant DEFAULT_TIME_LAPS. */
	public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds

	/** The time check. */
	private int timeCheck;

	/** The pm. */
	private PluginManager pm;

	/**
	 * Instantiates a new player listener.
	 */
	public PlayerListener() {

		super();
		conf = Factoid.getConf();
		playerConf = Factoid.getPlayerConf();
		timeCheck = DEFAULT_TIME_LAPS;
		pm = Factoid.getThisPlugin().getServer().getPluginManager();
	}

	/**
	 * On player join.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		// Update players cache
		Factoid.getPlayersCache().updatePlayer(player.getUniqueId(), player.getName());
		
		// Create a new static config
		PlayerConfEntry entry = playerConf.add(player);

		updatePosInfo(event, entry, player.getLocation(), true);

		// Note pour kaz00 : L'endroit correct pour mettre ceci serait
		// dans public void onPlayerLandChange(PlayerLandChangeEvent event)
		// (LandListener.java)
		// J'ai réglé un bug de joueur «null» et je l'ai désactiver pour ne pas
		// le voit tout de suite en prod. Tu le l'activera si tu veux travailler
		// dessus.
		/*
		 * Land landScoreboard =
		 * Factoid.getLands().getLand(player.getLocation()); if (landScoreboard
		 * != null) { for (Player playerInLand :
		 * landScoreboard.getPlayersInLand()) {
		 * Factoid.getScoreboard().sendScoreboard
		 * (landScoreboard.getPlayersInLand(), playerInLand,
		 * landScoreboard.getName()); } }
		 */
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
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		// Remove player from the land
		DummyLand land = playerConf.get(player).getLastLand();
		if (land instanceof Land) {
			((Land) land).removePlayerInLand(player);
		}

		// Remove player from Static Config
		playerConf.remove(player);
	}

	/**
	 * On player teleport.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {

		Location loc = event.getTo();
		Player player = event.getPlayer();
		PlayerConfEntry entry = playerConf.get(player);
		DummyLand land;

		// BugFix Citizens plugin
		if (entry == null) {
			return;
		}

		if (!entry.hasTpCancel()) {
			updatePosInfo(event, entry, loc, false);
		} else {
			entry.setTpCancel(false);
		}

		land = Factoid.getLands().getLandOrOutsideArea(player.getLocation());

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
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		PlayerConfEntry entry = playerConf.get(player);

		if (player == null) {
			return;
		}
		long last = entry.getLastMoveUpdate();
		long now = System.currentTimeMillis();
		if (now - last < timeCheck) {
			return;
		}
		entry.setLastMoveUpdate(now);
		if (event.getFrom().getWorld() == event.getTo().getWorld()) {
			if (event.getFrom().distance(event.getTo()) == 0) {
				return;
			}
		}
		updatePosInfo(event, entry, event.getTo(), false);
	}

	/**
	 * On player interact.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {

		DummyLand land;
		Material ml = event.getClickedBlock().getType();
		Player player = event.getPlayer();
		Action action = event.getAction();
		PlayerConfEntry entry;
		Location loc = event.getClickedBlock().getLocation();

		Factoid.getLog().write(
				"PlayerInteract player name: " + event.getPlayer().getName()
						+ ", Action: " + event.getAction());

		// For infoItem
		if (player.getItemInHand() != null && action == Action.LEFT_CLICK_BLOCK
				&& player.getItemInHand().getTypeId() == conf.getInfoItem()) {
			try {
				new CommandInfo(player, Factoid.getLands().getCuboidArea(
						event.getClickedBlock().getLocation()))
						.commandExecute();
			} catch (FactoidCommandException ex) {
				Logger.getLogger(PlayerListener.class.getName()).log(
						Level.SEVERE, "Error when trying to get area", ex);
			}
			event.setCancelled(true);

			// For Select
		} else if (player.getItemInHand() != null
				&& action == Action.LEFT_CLICK_BLOCK
				&& player.getItemInHand().getTypeId() == conf.getSelectItem()) {

			try {
				new CommandSelect(player, new ArgList(new String[] { "here" },
						player), event.getClickedBlock().getLocation())
						.commandExecute();
			} catch (FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}

			event.setCancelled(true);

			// For Select Cancel
		} else if (player.getItemInHand() != null
				&& action == Action.RIGHT_CLICK_BLOCK
				&& player.getItemInHand().getTypeId() == conf.getSelectItem()
				&& (entry = playerConf.get(player)).getSelection()
						.hasSelection()) {

			try {
				new CommandCancel(entry, false).commandExecute();
			} catch (FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}

			event.setCancelled(true);

			// For economy (buy or rent/unrent)
		} else if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
				&& (ml == Material.SIGN_POST || ml == Material.WALL_SIGN)) {

			Land trueLand = Factoid.getLands().getLand(loc);

			
			if (trueLand != null) {

			    Factoid.getLog().write("EcoSignClick: ClickLoc: " + loc + ", SignLoc" + trueLand.getSaleSignLoc());
			    
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
				} catch (FactoidCommandException ex) {
					// Empty, message is sent by the catch
				}
			}

			// Citizen bug, check if entry exist before
		} else if ((entry = playerConf.get(player)) != null
				&& !entry.isAdminMod()) {
			land = Factoid.getLands().getLandOrOutsideArea(loc);
			if ((land instanceof Land && ((Land) land).isBanned(player))
					|| (((action == Action.RIGHT_CLICK_BLOCK // BEGIN of USE
					&& (ml == Material.WOODEN_DOOR || ml == Material.TRAP_DOOR
							|| ml == Material.STONE_BUTTON
							|| ml == Material.WOOD_BUTTON
							|| ml == Material.LEVER
							|| ml == Material.TRAPPED_CHEST || ml == Material.FENCE_GATE)) || (action == Action.PHYSICAL && (ml == Material.WOOD_PLATE
							|| ml == Material.STONE_PLATE || ml == Material.STRING))) && !checkPermission(
								land, player,
								PermissionList.USE.getPermissionType())) // End
																		// of
																		// "USE"
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& (ml == Material.WOODEN_DOOR
									|| ml == Material.TRAP_DOOR || ml == Material.FENCE_GATE) && !checkPermission(
								land, player,
								PermissionList.USE_DOOR.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& (ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON) && !checkPermission(
								land, player,
								PermissionList.USE_BUTTON.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& ml == Material.LEVER && !checkPermission(land,
								player,
								PermissionList.USE_LEVER.getPermissionType()))
					|| (action == Action.PHYSICAL
							&& (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE) && !checkPermission(
								land, player,
								PermissionList.USE_PRESSUREPLATE
										.getPermissionType()))
					|| (action == Action.RIGHT_CLICK_BLOCK
							&& ml == Material.TRAPPED_CHEST && !checkPermission(
								land, player,
								PermissionList.USE_TRAPPEDCHEST
										.getPermissionType()))
					|| (action == Action.PHYSICAL && ml == Material.STRING && !checkPermission(
							land, player,
							PermissionList.USE_STRING.getPermissionType()))) {

				if (action != Action.PHYSICAL) {
					messagePermission(player);
				}
				event.setCancelled(true);
			} else if (action == Action.RIGHT_CLICK_BLOCK
					&& (((ml == Material.CHEST
							|| ml == Material.ENDER_CHEST // Begin of OPEN
							|| ml == Material.WORKBENCH
							|| ml == Material.BREWING_STAND
							|| ml == Material.FURNACE || ml == Material.BEACON
							|| ml == Material.DROPPER || ml == Material.HOPPER) && !checkPermission(
								land, player,
								PermissionList.OPEN.getPermissionType())) // End
																			// of
																			// OPEN
							|| (ml == Material.CHEST && !checkPermission(land,
									player,
									PermissionList.OPEN_CHEST
											.getPermissionType()))
							|| (ml == Material.ENDER_CHEST && !checkPermission(
									land, player,
									PermissionList.OPEN_ENDERCHEST
											.getPermissionType()))
							|| (ml == Material.WORKBENCH && !checkPermission(
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
							|| (ml == Material.DROPPER && !checkPermission(
									land, player,
									PermissionList.OPEN_DROPPER
											.getPermissionType())) || (ml == Material.HOPPER && !checkPermission(
							land, player,
							PermissionList.OPEN_HOPPER.getPermissionType())))
					// For dragon egg fix
					|| (ml == Material.DRAGON_EGG && (!checkPermission(land,
							event.getPlayer(),
							PermissionList.BUILD.getPermissionType()) || !checkPermission(
							land, event.getPlayer(),
							PermissionList.BUILD_DESTROY.getPermissionType())))) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On block place.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBlock().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(event
					.getPlayer()))
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD_PLACE.getPermissionType())) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On hanging place.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getEntity().getLocation());
			Player player = event.getPlayer();

			if ((land instanceof Land && ((Land) land).isBanned(player))
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
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!playerConf.get(event.getPlayer()).isAdminMod()
				&& event.getRightClicked() instanceof ItemFrame) {

			Player player = (Player) event.getPlayer();
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getRightClicked().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(player))
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
	 * On block break.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBlock().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(event
					.getPlayer()))
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player bucket fill.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBlockClicked().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(event
					.getPlayer()))
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, event.getPlayer(),
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On hanging break by entity.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

		Player player;

		if (event.getRemover() instanceof Player
				&& !playerConf.get((player = (Player) event.getRemover()))
						.isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getEntity().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(player))
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
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		Player player = event.getPlayer();
		PlayerConfEntry entry = playerConf.get(player);

		if (entry != null && !entry.isAdminMod()) {
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
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
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getPlayer().getLocation());

			if (!checkPermission(land, event.getPlayer(),
					PermissionList.PICKETUP.getPermissionType())) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player bed enter.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBed().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(event
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
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		PlayerConfEntry entry;
		PlayerConfEntry entryVictime;

		// Check if a player break a ItemFrame
		if (event.getDamager() instanceof Player
				&& (entry = playerConf.get((Player) event.getDamager())) != null // Citizens
																					// bugfix
				&& !entry.isAdminMod() && event.getEntity() instanceof Hanging) {

			Player player = (Player) event.getDamager();
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getEntity().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(player))
					|| !checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		} else {
			Player player = null;
			Projectile damagerProjectile;

			// Check if the damager is a player
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile
					&& event.getDamager().getType() != EntityType.EGG
					&& event.getDamager().getType() != EntityType.SNOWBALL) {
				damagerProjectile = (Projectile) event.getDamager();
				if (damagerProjectile.getShooter() instanceof Player) {
					player = (Player) damagerProjectile.getShooter();
				}
			}

			if (player != null) {
				DummyLand land = Factoid.getLands().getLandOrOutsideArea(
						event.getEntity().getLocation());
				Entity entity = event.getEntity();
				EntityType et = entity.getType();

				// kill en entity (none player)
				if (event.getDamager() instanceof Player
						&& (entry = playerConf.get(player)) != null // Citizens
																	// bugfix
						&& !entry.isAdminMod()
						&& ((land instanceof Land && ((Land) land)
								.isBanned(player))
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
								&& ((Tameable) entity).isTamed() == true
								&& ((Tameable) entity).getOwner() != player && !checkPermission(
									land, player,
									PermissionList.TAMED_KILL
											.getPermissionType())))) {

					messagePermission(player);
					event.setCancelled(true);

					// For PVP
				} else if (entity instanceof Player
						&& (entryVictime = playerConf.get((Player) entity)) != null
						&& (entry = playerConf.get(player)) != null) { // Citizens
																		// bugfix

					LandFlag flag;
					Faction faction = Factoid.getFactions().getPlayerFaction(
							entry.getPlayerContainer());
					Faction factionVictime = Factoid
							.getFactions()
							.getPlayerFaction(entryVictime.getPlayerContainer());

					if (faction != null
							&& faction == factionVictime
							&& (flag = land
									.getFlagAndInherit(FlagList.FACTION_PVP
											.getFlagType())) != null
							&& flag.getValueBoolean() == false) {
						// player.sendMessage(ChatColor.GRAY + "[Factoid] " +
						// Factoid.getLanguage().getMessage("ACTION.NOFACTIONPVP"));
						event.setCancelled(true);
					} else if ((flag = land.getFlagAndInherit(FlagList.FULL_PVP
							.getFlagType())) != null
							&& flag.getValueBoolean() == false) {
						// player.sendMessage(ChatColor.GRAY + "[Factoid] " +
						// Factoid.getLanguage().getMessage("ACTION.NOPVP"));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	/**
	 * On player bucket empty.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBlockClicked().getLocation());
			Material mt = event.getBucket();

			if ((land instanceof Land && ((Land) land).isBanned(event
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	// Must be after Essentials
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		PlayerConfEntry entry = playerConf.get(player);
		DummyLand land = Factoid.getLands().getLandOrOutsideArea(
				player.getLocation());
		String strLoc;
		Location loc;

		// For repsawn after death
		if (entry != null
				&& land.checkPermissionAndInherit(player,
						PermissionList.TP_DEATH.getPermissionType())
				&& (strLoc = land.getFlagAndInherit(
						FlagList.SPAWN.getFlagType()).getValueString()) != null
				&& (loc = StringChanges.stringToLocation(strLoc)) != null) {
			event.setRespawnLocation(loc);
		}
	}

	/**
	 * On player respawn2.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	// For land listener
	public void onPlayerRespawn2(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		PlayerConfEntry entry = playerConf.get(player);
		Location loc = event.getRespawnLocation();

		updatePosInfo(event, entry, loc, false);
	}

	/**
	 * On block ignite.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {

		if (event.getPlayer() != null && event.getPlayer() != null
				&& !playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getBlock().getLocation());

			if ((land instanceof Land && ((Land) land).isBanned(event
					.getPlayer()))
					|| (!checkPermission(land, event.getPlayer(),
							PermissionList.FIRE.getPermissionType()))) {
				messagePermission(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On potion splash.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {

		if (event.getEntity() != null
				&& event.getEntity().getShooter() instanceof Player) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getPotion().getLocation());
			Player player = (Player) event.getEntity().getShooter();

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
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		
		Entity entity = event.getEntity();
		Player player;
		PlayerConfEntry entry;
		
		if(entity != null && event.getEntity() instanceof Player
				&& (event.getRegainReason() == RegainReason.REGEN
				|| event.getRegainReason() == RegainReason.SATIATED)
				&& (entry = playerConf.get((player = (Player) event.getEntity()))) != null
				&& !entry.isAdminMod()) {
		
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(player.getLocation());
			
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
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		Player player = event.getPlayer();
		PlayerConfEntry entry;
		
		if((entry = playerConf.get(player)) != null
				&& !entry.isAdminMod()) {
		
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(player.getLocation());
			
			if (!checkPermission(land, player, PermissionList.EAT.getPermissionType())) {
				messagePermission(player);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On player command preprocess.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();

		if (!playerConf.get(event.getPlayer()).isAdminMod()) {

			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					player.getLocation());
			LandFlag flagAndInherit = land
					.getFlagAndInherit(FlagList.EXCLUDE_COMMANDS.getFlagType());

			if (flagAndInherit != null
					&& flagAndInherit.getValueStringList().length > 0) {
				String commandTyped = event.getMessage().substring(1)
						.split(" ")[0];

				for (String commandTest : flagAndInherit.getValueStringList()) {

					if (commandTest.equalsIgnoreCase(commandTyped)) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED
								+ "[Factoid] "
								+ Factoid.getLanguage().getMessage(
										"GENERAL.MISSINGPERMISSIONHERE"));
						return;
					}
				}
			}
		}
	}

	/**
	 * Check permission.
	 * 
	 * @param land
	 *            the land
	 * @param player
	 *            the player
	 * @param pt
	 *            the pt
	 * @return true, if successful
	 */
	private boolean checkPermission(DummyLand land, Player player,
			PermissionType pt) {

		return land.checkPermissionAndInherit(player, pt) == pt
				.getDefaultValue();
	}

	/**
	 * Message permission.
	 * 
	 * @param player
	 *            the player
	 */
	private void messagePermission(Player player) {

		player.sendMessage(ChatColor.GRAY + "[Factoid] "
				+ Factoid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
	}

	/**
	 * Update pos info.
	 * 
	 * @param event
	 *            the event
	 * @param entry
	 *            the entry
	 * @param loc
	 *            the loc
	 * @param newPlayer
	 *            the new player
	 */
	private void updatePosInfo(Event event, PlayerConfEntry entry,
			Location loc, boolean newPlayer) {

		DummyLand land;
		DummyLand landOld;
		PlayerLandChangeEvent landEvent;
		Boolean isTp;
		Player player = entry.getPlayer();

		land = Factoid.getLands().getLandOrOutsideArea(loc);

		if (newPlayer) {
			entry.setLastLand(landOld = land);
		} else {
			landOld = entry.getLastLand();
		}
		if (newPlayer || land != landOld) {
			isTp = event instanceof PlayerTeleportEvent;
			// First parameter : If it is a new player, it is null, if not new
			// player, it is "landOld"
			landEvent = new PlayerLandChangeEvent(newPlayer ? null : landOld,
					land, player, entry.getLastLoc(), loc, isTp);
			pm.callEvent(landEvent);
			if (landEvent.isCancelled()) {
				if (isTp) {
					((PlayerTeleportEvent) event).setCancelled(true);
					return;
				}
				if (land == landOld || newPlayer) {
					player.teleport(player.getWorld().getSpawnLocation());
				} else {
					Location retLoc = entry.getLastLoc();
					player.teleport(new Location(retLoc.getWorld(), retLoc
							.getX(), retLoc.getBlockY(), retLoc.getZ(), loc
							.getYaw(), loc.getPitch()));
				}
				entry.setTpCancel(true);
				return;
			}
			entry.setLastLand(land);

			// Update player in the lands
			if (landOld instanceof Land && landOld != land) {
				((Land) landOld).removePlayerInLand(player);
			}
			if (land instanceof Land) {
				((Land) land).addPlayerInLand(player);
			}
		}
		entry.setLastLoc(loc);

		// Update visual selection
		if (entry.getSelection().hasSelection()) {
			for (RegionSelection sel : entry.getSelection().getSelections()) {
				if (sel instanceof PlayerMoveListen) {
					((PlayerMoveListen) sel).playerMove();
				}
			}
		}
	}
}
