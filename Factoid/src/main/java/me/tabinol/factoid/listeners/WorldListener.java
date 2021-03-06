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

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.parameters.IFlagType;
import me.tabinol.factoidapi.parameters.IFlagValue;

/**
 * World listener
 */
public class WorldListener extends CommonListener implements Listener {

	/** The conf. */
	private final Config conf;

	/**
	 * Instantiates a new world listener.
	 */
	public WorldListener() {
		this.conf = Factoid.getThisPlugin().iConf();
	}

	/**
	 * On explosion prime.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onExplosionPrime(final ExplosionPrimeEvent event) {
		if (event.getEntity() == null) {
			return;
		}

		final Location loc = event.getEntity().getLocation();
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);
		final EntityType entityType = event.getEntityType();
		// Check for Explosion cancel
		if ((
				entityType == EntityType.CREEPER &&
				!land.getFlagAndInherit(FlagList.CREEPER_EXPLOSION.getFlagType()).getValueBoolean()
			) ||
			(
				(entityType == EntityType.PRIMED_TNT || entityType == EntityType.MINECART_TNT) &&
				!land.getFlagAndInherit(FlagList.TNT_EXPLOSION.getFlagType()).getValueBoolean()
			) ||
			(
				entityType == EntityType.ENDER_CRYSTAL &&
				!land.getFlagAndInherit(FlagList.END_CRYSTAL_EXPLOSION.getFlagType()).getValueBoolean()
			) ||
			(
				entityType == EntityType.FIREWORK &&
				!land.getFlagAndInherit(FlagList.FIREWORK_EXPLOSION.getFlagType()).getValueBoolean()
			) ||
			!land.getFlagAndInherit(FlagList.EXPLOSION.getFlagType()).getValueBoolean()
		) {
			event.setCancelled(true);

			//TODO: Checks when the EnderCrystal will be removable...
			if (entityType == EntityType.CREEPER) {
				event.getEntity().remove();
			}
		}
	}

	/**
	 * On entity explode.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onEntityExplode(final EntityExplodeEvent event) {
		if (event.getEntity() == null) {
			return;
		}

		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getLocation());
		if (conf.isOverrideExplosions()) {
			// Creeper Explosion
			if (event.getEntityType() == EntityType.CREEPER) {
				final boolean isAllowed = land.getFlagAndInherit(FlagList.CREEPER_EXPLOSION.getFlagType()).getValueBoolean();
				explodeBlocks(event, event.blockList(), FlagList.CREEPER_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), isAllowed ? 0L : 0L, false);

			//  Wither
			} else if (event.getEntityType() == EntityType.WITHER ||
				event.getEntityType() == EntityType.WITHER_SKULL
			) {
				explodeBlocks(event, event.blockList(), FlagList.WITHER_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), event.getEntityType() == EntityType.WITHER ? 7L : 1L, false);

			// Ghast
			} else if (event.getEntityType() == EntityType.FIREBALL) {
				explodeBlocks(event, event.blockList(), FlagList.GHAST_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), 1L, true);

			// TNT
			} else if (event.getEntityType() == EntityType.MINECART_TNT
					|| event.getEntityType() == EntityType.PRIMED_TNT) {
				final boolean isAllowed = land.getFlagAndInherit(FlagList.TNT_EXPLOSION.getFlagType()).getValueBoolean();
				explodeBlocks(event, event.blockList(), FlagList.TNT_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), isAllowed ? 4L : 0L, false);

			// Ender Dragon
			} else if (event.getEntityType() == EntityType.ENDER_DRAGON) {
				explodeBlocks(event, event.blockList(), FlagList.ENDERDRAGON_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), 4L, false);

			// Ender Crystal
			} else if (event.getEntityType() == EntityType.ENDER_CRYSTAL) {
				final boolean isAllowed = land.getFlagAndInherit(FlagList.END_CRYSTAL_EXPLOSION.getFlagType()).getValueBoolean();
				explodeBlocks(event, event.blockList(), FlagList.END_CRYSTAL_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), isAllowed ? 4L : 0L, false);

			// Firework
			} else if (event.getEntityType() == EntityType.FIREWORK) {
				final boolean isAllowed = land.getFlagAndInherit(FlagList.FIREWORK_EXPLOSION.getFlagType()).getValueBoolean();
				explodeBlocks(event, event.blockList(), FlagList.FIREWORK_DAMAGE.getFlagType(), event.getLocation(),
					event.getYield(), isAllowed ? 0L : 0L, false);
			}
		}
	}

	/**
	 * On hanging break.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onHangingBreak(final HangingBreakEvent event) {
		if (conf.isOverrideExplosions()) {
			// Check for painting
			if (event.getCause() == RemoveCause.EXPLOSION) {
				Factoid.getThisPlugin().iLog().write("Cancel HangingBreak : " + event.getEntity() + ", Cause: " + event.getCause());
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Explode blocks.
	 *
	 * @param event The cancellable event
	 * @param blocks the blocks
	 * @param ft the ft
	 * @param loc the loc
	 * @param yield the yield
	 * @param power the power
	 * @param setFire the set fire
	 */
	private void explodeBlocks(final Cancellable event, final List<Block> blocks, final IFlagType ft, final Location loc,
			final float yield, final float power, final boolean setFire) {
		boolean cancelEvent = false;
		final Iterator<Block> itBlock = blocks.iterator();

		Factoid.getThisPlugin().iLog().write("Explosion : " + ", Yield: " + yield + ", power: " + power);

		// Check if 1 block or more is in a protected place
		while (itBlock.hasNext() && !cancelEvent) {
			final Block block = itBlock.next();
			final IFlagValue value = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(block.getLocation()).getFlagAndInherit(ft);
			if (!value.getValueBoolean()) {
				cancelEvent = true;
			}
		}

		if (cancelEvent) {
			// Cancel Event and do a false explosion
			event.setCancelled(true);
			loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, false);
		}
	}

	/**
	 * On entity change block.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onEntityChangeBlock(final EntityChangeBlockEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getBlock().getLocation());
		final Material matFrom = event.getBlock().getType();
		final Material matTo = event.getTo();

		// Enderman removeblock
		if ((event.getEntityType() == EntityType.ENDERMAN
				&& !land.getFlagAndInherit(FlagList.ENDERMAN_DAMAGE.getFlagType()).getValueBoolean())
				|| (event.getEntityType() == EntityType.WITHER
				&& !land.getFlagAndInherit(FlagList.WITHER_DAMAGE.getFlagType()).getValueBoolean())) {
			event.setCancelled(true);

		}
		// Crop trample
		else if (matFrom == Material.FARMLAND
				&& matTo == Material.DIRT
				&& !land.getFlagAndInherit(FlagList.CROP_TRAMPLE.getFlagType()).getValueBoolean()) {
			event.setCancelled(true);
		}
	}

	/**
	 * On entity change block.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onEntityChangeBlock(final EntityBlockFormEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLand(event.getBlock().getLocation());

		// Frost walker enchant
		if (land != null &&
			event.getEntity() instanceof Player &&
			event.getNewState().getType() == Material.FROSTED_ICE &&
			!checkPermission(land, (Player) event.getEntity(), PermissionList.FROST_WALKER.getPermissionType()) &&
			event.getBlock().getType() == Material.WATER
		) {
			event.setCancelled(true);
		}
	}

	/**
	 * On block ignite.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onBlockIgnite(final BlockIgniteEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getBlock().getLocation());

		if (((event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LAVA)
				&& !land.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean())
				|| !land.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean()) {
			event.setCancelled(true);
		}
	}

	/**
	 * On block burn.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onBlockBurn(final BlockBurnEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getBlock().getLocation());

		if ((!land.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean())
				|| (!land.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean())) {
			event.setCancelled(true);
		}
	}

	/**
	 * On creature spawn.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onCreatureSpawn(final CreatureSpawnEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getEntity().getLocation());

		if ((event.getEntity() instanceof Animals
				&& !land.getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean())
				|| ((event.getEntity() instanceof Monster
				|| event.getEntity() instanceof Slime
				|| event.getEntity() instanceof Flying)
				&& !land.getFlagAndInherit(FlagList.MOB_SPAWN.getFlagType()).getValueBoolean())) {
			event.setCancelled(true);
		}
	}

	/**
	 * On leaves decay.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onLeavesDecay(final LeavesDecayEvent event) {
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getBlock().getLocation());

		if (!land.getFlagAndInherit(FlagList.LEAF_DECAY.getFlagType()).getValueBoolean()) {
			event.setCancelled(true);
		}
	}

	/**
	 * On block from to.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onBlockFromTo(final BlockFromToEvent event) {
		final Material ml = event.getBlock().getType();
		final IDummyLand land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(event.getBlock().getLocation());

		// Liquid flow
		if ((ml == Material.LAVA
				&& !land.getFlagAndInherit(FlagList.LAVA_FLOW.getFlagType()).getValueBoolean())
				|| (ml == Material.WATER
				&& !land.getFlagAndInherit(FlagList.WATER_FLOW.getFlagType()).getValueBoolean())
		) {
			event.setCancelled(true);
		}
	}

	/**
	 * On entity damage.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public final void onEntityDamage(final EntityDamageEvent event) {
		if (conf.isOverrideExplosions() &&
			event.getEntity() instanceof Hanging &&
			(event.getCause() == DamageCause.BLOCK_EXPLOSION ||
			event.getCause() == DamageCause.ENTITY_EXPLOSION ||
			event.getCause() == DamageCause.PROJECTILE)
		) {
			// Check for ItemFrame
			Factoid.getThisPlugin().iLog().write("Cancel HangingBreak : " + event.getEntity() + ", Cause: " + event.getCause());
			event.setCancelled(true);
		}
	}
}
