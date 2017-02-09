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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.parameters.IPermissionType;

/**
 * Common methods for Listeners
 */
public class CommonListener {

	/**
	 * Check permission.
	 *
	 * @param land
	 *			the land
	 * @param player
	 *			the player
	 * @param pt
	 *			the pt
	 * @return true, if successful
	 */
	protected boolean checkPermission(final IDummyLand land, final Player player, final IPermissionType pt) {
		return land.checkPermissionAndInherit(player, pt) == pt
				.getDefaultValue();
	}

	/**
	 * Message permission.
	 *
	 * @param player
	 *			the player
	 */
	protected void messagePermission(final Player player) {
		player.sendMessage(ChatColor.GRAY + "[Factoid] "
				+ Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
	}

	/**
	 * Gets the source player from entity or projectile
	 *
	 * @param entity the entity
	 * @return the source player
	 */
	protected Player getSourcePlayer(final Entity entity) {
		// Check if the damager is a player
		if (entity instanceof Player) {
			return (Player) entity;
		} else if (entity instanceof Projectile) {
			final Projectile damagerProjectile = (Projectile) entity;
			if (damagerProjectile.getShooter() instanceof Player) {
				return (Player) damagerProjectile.getShooter();
			}
		}
		return null;
	}

	/**
	 * Check is the block to destroy is attached to an eco sign.
	 *
	 * @param land the land
	 * @param block the block
	 * @return true if the sign is attached
	 */
	protected boolean hasEcoSign(final Land land, final Block block) {
		return (land.getSaleSignLoc() != null && hasEcoSign(block, land.getSaleSignLoc()))
				|| (land.getRentSignLoc() != null && hasEcoSign(block, land.getRentSignLoc()));
	}

	/**
	 * Check is the block to destroy is attached to an eco sign.
	 *
	 * @param block the block
	 * @param ecoSignLoc the eco sign location
	 * @return true if the sign is attached
	 */
	private boolean hasEcoSign(final Block block, final Location ecoSignLoc) {
		if((block.getRelative(BlockFace.UP).getLocation().equals(ecoSignLoc) && block.getRelative(BlockFace.UP).getType() == Material.SIGN_POST)
				|| isEcoSignAttached(block, BlockFace.NORTH, ecoSignLoc)
				|| isEcoSignAttached(block, BlockFace.SOUTH, ecoSignLoc)
				|| isEcoSignAttached(block, BlockFace.EAST, ecoSignLoc)
				|| isEcoSignAttached(block, BlockFace.WEST, ecoSignLoc)) {
			return true;
		}

		return false;
	}

	private boolean isEcoSignAttached(final Block block, final BlockFace face, final Location ecoSignLoc) {
		final Block checkBlock = block.getRelative(face);

		if(checkBlock.getLocation().equals(ecoSignLoc) && checkBlock.getType() == Material.WALL_SIGN
				&& ((org.bukkit.material.Sign) checkBlock.getState().getData()).getFacing() == face) {
			return true;
		}

		return false;
	}
}
