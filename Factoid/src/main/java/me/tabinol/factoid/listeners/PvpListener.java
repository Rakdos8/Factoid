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

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.utilities.ExpirableHashMap;
import me.tabinol.factoidapi.config.players.IPlayerConfEntry;
import me.tabinol.factoidapi.config.players.IPlayerStaticConfig;
import me.tabinol.factoidapi.factions.IFaction;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

/**
 * PVP Listener
 */
public class PvpListener extends CommonListener implements Listener {

	/** The Constant FIRE_EXPIRE. */
	public final static long FIRE_EXPIRE = 20 * 30;

	/** The player conf. */
	private final IPlayerStaticConfig playerConf;

	/** The player fire location. */
	private final ExpirableHashMap<Location, IPlayerContainerPlayer> playerFireLocation;

	/**
	 * Instantiates a new pvp listener.
	 */
	public PvpListener() {
		playerConf = Factoid.getThisPlugin().iPlayerConf();
		playerFireLocation = new ExpirableHashMap<Location, IPlayerContainerPlayer>(FIRE_EXPIRE);
	}

	/**
	 * On entity damage by entity.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {

		IPlayerConfEntry entry;
		IPlayerConfEntry entryVictim;

		// Check if a player break a ItemFrame
		final Player player = getSourcePlayer(event.getDamager());

		if (player != null) {
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(
					event.getEntity().getLocation());
			final Entity entity = event.getEntity();

			// For PVP
			if (entity instanceof Player &&  (entry = playerConf.get(player)) != null
					&& (entryVictim = playerConf.get(entity)) != null
					&& !isPvpValid(land, entry.getPlayerContainer(),
							entryVictim.getPlayerContainer())) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On block place.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {

		if(event.getBlockPlaced().getType() == Material.FIRE) {

			final Player player = event.getPlayer();
			checkForPvpFire(event, player);
		}

	}

	/**
	 * On block ignite.
	 *
	 * @param event
	 *			the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event) {

		final Player player = event.getPlayer();
		checkForPvpFire(event, player);
	}

	/**
	 * On block spread.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpread(final BlockSpreadEvent event) {

		final Block blockSource = event.getSource();
		final IPlayerContainerPlayer pc = playerFireLocation.get(blockSource.getLocation());

		if(pc != null) {

			// Add fire for pvp listen
			playerFireLocation.put(event.getBlock().getLocation(), pc);
		}
	}

	/**
	 * On entity damage.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {

		// Check for fire cancel
		if(event.getEntity() instanceof Player &&
				(event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK)) {

			final Player player = (Player) event.getEntity();
			final IPlayerConfEntry entry = playerConf.get(player);

			if(entry != null) {
				final Location loc = player.getLocation();
				final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);

				// Check for fire near the player
				for(final Map.Entry<Location, IPlayerContainerPlayer> fireEntry : playerFireLocation.entrySet()) {

					if(loc.getWorld() == fireEntry.getKey().getWorld()
							&& loc.distanceSquared(fireEntry.getKey()) < 5) {
						final Block block = loc.getBlock();
						if((block.getType() == Material.FIRE || block.getType() == Material.AIR)
								&& !isPvpValid(land, fireEntry.getValue(), entry.getPlayerContainer())) {

							// remove fire
							Factoid.getThisPlugin().iLog().write("Anti-pvp from "
									+ entry.getPlayerContainer().getPlayer().getName()
									+ " to " + player.getName());
							block.setType(Material.AIR);
							player.setFireTicks(0);
							event.setDamage(0);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Check when a player deposits fire and add it to list
	 *
	 * @param event the event
	 * @param player the player
	 */
	private void checkForPvpFire(final BlockEvent event, final Player player) {

		IPlayerConfEntry entry;

		if (player != null && (entry = playerConf.get(player)) != null) {

			final Location loc = event.getBlock().getLocation();
			final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);

			if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false
					|| land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {

				// Add fire for pvp listen
				playerFireLocation.put(loc, entry.getPlayerContainer());
			}
		}
	}

	/**
	 * Checks if pvp is valid.
	 *
	 * @param land the land
	 * @param attacker the attacker
	 * @param victim the victim
	 * @return true, if is pvp valid
	 */
	private boolean isPvpValid(final IDummyLand land, final IPlayerContainerPlayer attacker,
			final IPlayerContainerPlayer victim) {

		final IFaction faction = Factoid.getThisPlugin().iFactions().getPlayerFaction(attacker);
		final IFaction factionVictime = Factoid.getThisPlugin().iFactions().getPlayerFaction(victim);

		if (faction != null && faction == factionVictime
				&& land.getFlagAndInherit(FlagList.FACTION_PVP.getFlagType()).getValueBoolean() == false) {

			return false;
		} else if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {

			return false;
		}

		return true;
	}
}
