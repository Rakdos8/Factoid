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
package me.tabinol.factoid.economy;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Represent the economy sign.
 *
 * @author Tabinol
 */
public class EcoSign {

	/** The land. */
	private final Land land;

	/** The location. */
	private final Location location;
	
	/** The facing. */
	private final BlockFace facing;
	
	/** The is wall sign. */
	private final boolean isWallSign;

	// Create from player position
	/**
	 * Instantiates a new eco sign.
	 *
	 * @param land            the land
	 * @param player            the player
	 * @throws SignException the sign exception
	 */
	public EcoSign(Land land, Player player) throws SignException {

		@SuppressWarnings("deprecation")
		Block targetBlock = player.getTargetBlock(null, 10);
		this.land = land;

		if(targetBlock == null) {
			throw new SignException();
		}
		
		if (targetBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {

			// If the block as air upside, put the block on top of it
			location = targetBlock.getRelative(BlockFace.UP).getLocation();
			facing = signFacing(player.getLocation().getYaw());
			isWallSign = false;
		
		} else {
			
			// A Wall Sign
			facing  = wallFacing(player.getLocation().getYaw());
			if(targetBlock.getRelative(facing).getType() != Material.AIR) {
				// Error no place to put the wall sign
				throw new SignException();
			}
			location = targetBlock.getRelative(facing).getLocation();
			isWallSign = true;
		}
		
		// Target is outside the land
		if(!land.isLocationInside(this.location)) {
			throw new SignException();
		}
		
		Factoid.getLog().write("SignToCreate: PlayerYaw: " + player.getLocation().getYaw() +
				", Location: " + location.toString() + ", Facing: " + facing.name() +
				", isWallSign: " + isWallSign);
	}
	
	/**
	 * Instantiates a new eco sign (If the sign is already existing only).
	 *
	 * @param land the land
	 * @param location the location
	 * @throws SignException the sign exception
	 */
	public EcoSign(Land land, Location location) throws SignException {
		
		this.land = land;
		this.location = location;
		
		// Load chunk
		location.getChunk().load();
		
		// Get Sign parameter
		Block blockPlace = location.getBlock();
		
		if(blockPlace.getType() == Material.WALL_SIGN) {
			isWallSign = true;
		} else if(blockPlace.getType() == Material.SIGN_POST) {
			isWallSign = false;
		} else {
			throw new SignException();
		}
		
		this.facing = ((org.bukkit.material.Sign)((Sign) blockPlace.getState()).getData()).getFacing();
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Location getLocation() {

		return location;
	}

	/**
	 * Creates the sign for sale.
	 *
	 * @param price            the price
	 * @return true, if successful
	 * @throws SignException the sign exception
	 */
	public boolean createSignForSale(double price) throws SignException {

		String[] lines = new String[4];
		lines[0] = ChatColor.GREEN
				+ Factoid.getLanguage().getMessage("SIGN.SALE.FORSALE");
		lines[1] = ChatColor.GREEN + land.getName();
		lines[2] = "";
		lines[3] = ChatColor.BLUE + Factoid.getPlayerMoney().toFormat(price);

		return createSign(lines);
	}

	/**
	 * Creates the sign for rent.
	 *
	 * @param price            the price
	 * @param renew            the renew
	 * @param autoRenew            the auto renew
	 * @param tenantName            the tenant name
	 * @return true, if successful
	 * @throws SignException the sign exception
	 */
	public boolean createSignForRent(double price, int renew,
			boolean autoRenew, String tenantName) throws SignException {

		String[] lines = new String[4];

		if (tenantName != null) {
			lines[0] = ChatColor.RED
					+ Factoid.getLanguage().getMessage("SIGN.RENT.RENTED");
			lines[1] = ChatColor.RED + tenantName;
		} else {
			lines[0] = ChatColor.GREEN
					+ Factoid.getLanguage().getMessage("SIGN.RENT.FORRENT");
			lines[1] = ChatColor.GREEN + land.getName();
		}

		if (autoRenew) {
			lines[2] = ChatColor.BLUE
					+ Factoid.getLanguage().getMessage("SIGN.RENT.AUTORENEW");
		} else {
			lines[2] = "";
		}

		lines[3] = ChatColor.BLUE + Factoid.getPlayerMoney().toFormat(price)
				+ "/" + renew;

		return createSign(lines);
	}

	/**
	 * Creates the sign.
	 *
	 * @param lines            the lines
	 * @return true, if successful
	 * @throws SignException the sign exception
	 */
	public boolean createSign(String[] lines) throws SignException {

		Block blockPlace = location.getBlock();

		// Impossible to create the sign here
		if (Factoid.getLands().getLand(location) != land) {
			return false;
		}

		// Check if the facing block is solid
		if (isWallSign) {
			if(!blockPlace.getRelative(facing.getOppositeFace()).getType().isSolid()) {
				throw new SignException();
			}
		} else {
			if(!blockPlace.getRelative(BlockFace.DOWN).getType().isSolid()) {
				throw new SignException();
			}
		}
		
		// Determinate material
		Material mat;
		if (isWallSign) {

			mat = Material.WALL_SIGN;
		} else {
			mat = Material.SIGN_POST;
		}

		// Create sign
		blockPlace.setType(mat);

		Sign sign = (Sign) blockPlace.getState();

		// Add lines
		for (int t = 0; t <= 3; t++) {
			sign.setLine(t, lines[t]);
		}

		// Set facing
		((org.bukkit.material.Sign) sign.getData()).setFacingDirection(facing);
		
		sign.update();
		
		return true;
	}

	/**
	 * Removes the sign.
	 */
	public void removeSign() {
		
		removeSign(location);
	}
	
	/**
	 * Removes the old sign.
	 *
	 * @param oldSignLocation the old sign location
	 */
	public void removeSign(Location oldSignLocation) {

		Block block = oldSignLocation.getBlock();
		
		block.getChunk().load();

		// Remove only if it is a sign;
		if (block.getType() == Material.SIGN_POST
				|| block.getType() == Material.WALL_SIGN) {
			block.setType(Material.AIR);
			
			//Drop item
			oldSignLocation.getWorld().dropItem(oldSignLocation, new ItemStack(Material.SIGN, 1));
		}
	}

	/**
	 * Sign facing.
	 *
	 * @param yaw the yaw
	 * @return the block face
	 */
	private BlockFace signFacing(float yaw) {

		BlockFace facing;

		if(yaw < 0) {
			yaw += 360;
		}

		if (yaw > 360 -11.25 || yaw <= 11.25) {
			facing = BlockFace.NORTH;
		} else if (yaw <= (360/16*2) - 11.25) {
			facing = BlockFace.NORTH_NORTH_EAST;
		} else if (yaw <= (360/16*3) - 11.25) {
			facing = BlockFace.NORTH_EAST;
		} else if (yaw <= (360/16*4) - 11.25) {
			facing = BlockFace.EAST_NORTH_EAST;
		} else if (yaw <= (360/16*5) - 11.25) {
			facing = BlockFace.EAST;
		} else if (yaw <= (360/16*6) - 11.25) {
			facing = BlockFace.EAST_SOUTH_EAST;
		} else if (yaw <= (360/16*7) - 11.25) {
			facing = BlockFace.SOUTH_EAST;
		} else if (yaw <= (360/16*8) - 11.25) {
			facing = BlockFace.SOUTH_SOUTH_EAST;
		} else if (yaw <= (360/16*9) - 11.25) {
			facing = BlockFace.SOUTH;
		} else if (yaw <= (360/16*10) - 11.25) {
			facing = BlockFace.SOUTH_SOUTH_WEST;
		} else if (yaw <= (360/16*11) - 11.25) {
			facing = BlockFace.SOUTH_WEST;
		} else if (yaw <= (360/16*12) - 11.25) {
			facing = BlockFace.WEST_SOUTH_WEST;
		} else if (yaw <= (360/16*13) - 11.25) {
			facing = BlockFace.WEST;
		} else if (yaw <= (360/16*14) - 11.25) {
			facing = BlockFace.WEST_NORTH_WEST;
		} else if (yaw <= (360/16*15) - 11.25) {
			facing = BlockFace.NORTH_WEST;
		} else {
			facing = BlockFace.NORTH_NORTH_WEST;
		}
		
		return facing;
	}
	
	/**
	 * Wall facing.
	 *
	 * @param yaw the yaw
	 * @return the block face
	 */
	private BlockFace wallFacing(float yaw) {
		
		BlockFace facing;
		
		if(yaw < 0) {
			yaw += 360;
		}
		
		if(yaw > 315 || yaw <= 45) {
			facing = BlockFace.NORTH;
		} else if(yaw <= 135) {
			facing = BlockFace.EAST;
		} else if(yaw <= 225) {
			facing = BlockFace.SOUTH;
		} else {
			facing = BlockFace.WEST;
		}
		
		return facing;
	}

}
