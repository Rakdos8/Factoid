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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.ExpirableTreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerEvent;

/**
 * The listener interface for receiving pvp events.
 * The class that is interested in processing a pvp
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPvpListener<code> method. When
 * the pvp event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PlayerEvent
 */
public class PvpListener extends CommonListener implements Listener {

	/** The Constant FIRE_EXPIRE. */
	public final static long FIRE_EXPIRE = 20 * 30;

	/** The player conf. */
	private PlayerStaticConfig playerConf;

	/** The player fire location. */
	private ExpirableTreeMap<Location, PlayerContainerPlayer> playerFireLocation;

	/**
	 * Instantiates a new pvp listener.
	 */
	public PvpListener() {

		super();
		playerConf = Factoid.getPlayerConf();
		playerFireLocation = new ExpirableTreeMap<Location, PlayerContainerPlayer>(FIRE_EXPIRE);
	}

	/**
	 * On entity damage by entity.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		PlayerConfEntry entry;
		PlayerConfEntry entryVictim;

		// Check if a player break a ItemFrame
		Player player = getSourcePlayer(event.getDamager());

		if (player != null) {
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getEntity().getLocation());
			Entity entity = event.getEntity();

			// For PVP
			if (entity instanceof Player &&  (entry = playerConf.get(player)) != null
					&& (entryVictim = playerConf.get((Player) entity)) != null
					&& !isPvpValid(land, entry.getPlayerContainer(),
							entryVictim.getPlayerContainer())) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * On block ignite.
	 * 
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {

		Player player = event.getPlayer();
		PlayerConfEntry entry;
		
		if (event.getPlayer() != null && (entry = playerConf.get(player)) != null) {

			Location loc = event.getBlock().getLocation();
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false
					|| land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {
				
				// Add fire for pvp listen
				playerFireLocation.put(loc, entry.getPlayerContainer());
			}
		}
	}
	
	/**
	 * On block spread.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		Block blockSource = event.getSource();
		PlayerContainerPlayer pc = playerFireLocation.get(blockSource.getLocation());
		
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
	public void onEntityDamage(EntityDamageEvent event) {
		
		// Check for fire cancel
		if(event.getEntity() instanceof Player && 
				event.getCause() == DamageCause.FIRE) {
			
			Player player = (Player) event.getEntity();
			PlayerConfEntry entry = playerConf.get(player);
			
			if(entry != null) {
				Location loc = player.getLocation();
				DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);
				for(Map.Entry<Location, PlayerContainerPlayer> fireEntry : playerFireLocation.entrySet()) {
					
					if(loc.distanceSquared(fireEntry.getKey()) < 1.5) {
						Block block = loc.getBlock();
						if(block.getType() == Material.FIRE 
								&& !isPvpValid(land, fireEntry.getValue(), entry.getPlayerContainer())) {
							block.setType(Material.AIR);
							event.setCancelled(true);
						}
					}
				}
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
	private boolean isPvpValid(DummyLand land, PlayerContainerPlayer attacker, 
			PlayerContainerPlayer victim) {
		
		Faction faction = Factoid.getFactions().getPlayerFaction(attacker);
		Faction factionVictime = Factoid.getFactions().getPlayerFaction(victim);

		if (faction != null && faction == factionVictime
				&& land.getFlagAndInherit(FlagList.FACTION_PVP.getFlagType()).getValueBoolean() == false) {
				
			return false;
		} else if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {

			return false;
		}
		
		return true;	
	}
}
