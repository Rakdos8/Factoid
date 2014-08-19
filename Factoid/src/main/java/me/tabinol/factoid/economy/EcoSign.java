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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

// TODO: Auto-generated Javadoc
/**
 * Represent the economy sign.
 *
 * @author Tabinol
 */
public class EcoSign {

	/** The land. */
	Land land;

	/** The location. */
	Location location;

	/** The facing. */
	BlockFace facing;

	/** The is wall sign. */
	boolean isWallSign;

	// Create from player position
	/**
	 * Instantiates a new eco sign.
	 *
	 * @param land            the land
	 * @param player            the player
	 * @throws SignException the sign exception
	 */
	public EcoSign(Land land, Player player) throws SignException {

		Block targetBlock = player.getTargetBlock(null, 10);

		if(targetBlock == null) {
			throw new SignException();
		}
		
		if (targetBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {

			// If the block as air upside, put the block on top of it
			this.location = targetBlock.getRelative(BlockFace.UP).getLocation();
			this.facing = signFacing(player.getLocation().getYaw());
			this.isWallSign = false;
		
		} else {
			
			// A Wall Sign
			this.facing  = wallFacing(player.getLocation().getYaw());
			if(targetBlock.getRelative(facing).getType() != Material.AIR) {
				// Error no place to put the wall sign
				throw new SignException();
			}
			this.location = targetBlock.getRelative(facing).getLocation();
			this.isWallSign = true;
		}
		
		// Target is outside the land
		if(!land.isLocationInside(this.location)) {
			throw new SignException();
		}
	}

	// Create from configuration
	/**
	 * Instantiates a new eco sign.
	 *
	 * @param land
	 *            the land
	 * @param location
	 *            the location
	 * @param facing
	 *            the facing
	 * @param isWallSign
	 *            the is wall sign
	 */
	public EcoSign(Land land, Location location, BlockFace facing,
			boolean isWallSign) {

		this.location = location;
		this.facing = facing;
		this.isWallSign = isWallSign;
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
	 * Sets the location.
	 *
	 * @param location
	 *            the new location
	 */
	public void setLocation(Location location) {

		this.location = location;
	}

	/**
	 * Gets the facing.
	 *
	 * @return the facing
	 */
	public BlockFace getFacing() {

		return facing;
	}

	/**
	 * Sets the facing.
	 *
	 * @param facing
	 *            the new facing
	 */
	public void setFacing(BlockFace facing) {

		this.facing = facing;
	}

	/**
	 * Checks if is wall sign.
	 *
	 * @return true, if is wall sign
	 */
	public boolean isWallSign() {

		return isWallSign;
	}

	/**
	 * Sets the checks if is wall sign.
	 *
	 * @param isWallSign
	 *            the new checks if is wall sign
	 */
	public void setIsWallSign(boolean isWallSign) {

		this.isWallSign = isWallSign;
	}

	/**
	 * Creates the sign for sale.
	 *
	 * @param price
	 *            the price
	 * @return true, if successful
	 */
	public boolean createSignForSale(double price) {

		String[] lines = new String[4];
		lines[0] = ChatColor.GREEN
				+ Factoid.getLanguage().getMessage("SIGN.SALE.FORSALE");
		lines[1] = "";
		lines[2] = "";
		lines[3] = ChatColor.BLUE + Factoid.getPlayerMoney().toFormat(price);

		return createSign(lines);
	}

	/**
	 * Creates the sign for rent.
	 *
	 * @param price
	 *            the price
	 * @param renew
	 *            the renew
	 * @param autoRenew
	 *            the auto renew
	 * @param tenantName
	 *            the tenant name
	 * @return true, if successful
	 */
	public boolean createSignForRent(double price, int renew,
			boolean autoRenew, String tenantName) {

		String[] lines = new String[4];

		if (tenantName != null) {
			lines[0] = ChatColor.RED
					+ Factoid.getLanguage().getMessage("SIGN.RENT.RENTED");
			lines[1] = ChatColor.RED + tenantName;
		} else {
			lines[0] = ChatColor.GREEN
					+ Factoid.getLanguage().getMessage("SIGN.RENT.FORRENT");
			lines[1] = "";
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
	 * @param lines
	 *            the lines
	 * @return true, if successful
	 */
	public boolean createSign(String[] lines) {

		World world = land.getWorld();

		// Impossible to create the sign here
		if (Factoid.getLands().getLand(location) != land) {
			return false;
		}

		Material mat;
		if (isWallSign) {

			mat = Material.WALL_SIGN;
		} else {
			mat = Material.SIGN_POST;
		}

		// Create sign
		Block blockPlace = world.getBlockAt(location);
		blockPlace.setType(mat);

		Sign sign = (Sign) blockPlace.getState();

		// Add lines
		for (int t = 0; t <= 3; t++) {
			sign.setLine(t, lines[t]);
		}

		// Set facing
		((org.bukkit.material.Sign) sign.getData()).setFacingDirection(facing);

		return true;
	}

	/**
	 * Removes the sign.
	 */
	public void removeSign() {

		Block block = land.getWorld().getBlockAt(location);

		// Remove only if it is a sign;
		if (block.getType() == Material.SIGN_POST
				|| block.getType() == Material.WALL_SIGN) {
			block.setType(Material.AIR);
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

		if (yaw > 360 -11.25 || yaw <= 11.25) {
			facing = BlockFace.SOUTH;
		} else if (yaw <= (360/16*2) - 11.25) {
			facing = BlockFace.SOUTH_SOUTH_WEST;
		} else if (yaw <= (360/16*3) - 11.25) {
			facing = BlockFace.SOUTH_WEST;
		} else if (yaw <= (360/16*4) - 11.25) {
			facing = BlockFace.WEST_SOUTH_WEST;
		} else if (yaw <= (360/16*5) - 11.25) {
			facing = BlockFace.WEST;
		} else if (yaw <= (360/16*6) - 11.25) {
			facing = BlockFace.WEST_NORTH_WEST;
		} else if (yaw <= (360/16*7) - 11.25) {
			facing = BlockFace.NORTH_WEST;
		} else if (yaw <= (360/16*8) - 11.25) {
			facing = BlockFace.NORTH_NORTH_WEST;
		} else if (yaw <= (360/16*9) - 11.25) {
			facing = BlockFace.NORTH;
		} else if (yaw <= (360/16*10) - 11.25) {
			facing = BlockFace.NORTH_NORTH_EAST;
		} else if (yaw <= (360/16*11) - 11.25) {
			facing = BlockFace.NORTH_EAST;
		} else if (yaw <= (360/16*12) - 11.25) {
			facing = BlockFace.EAST_NORTH_EAST;
		} else if (yaw <= (360/16*13) - 11.25) {
			facing = BlockFace.EAST;
		} else if (yaw <= (360/16*14) - 11.25) {
			facing = BlockFace.EAST_SOUTH_EAST;
		} else if (yaw <= (360/16*15) - 11.25) {
			facing = BlockFace.SOUTH_EAST;
		} else {
			facing = BlockFace.SOUTH_SOUTH_EAST;
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
		
		if(yaw > -315 || yaw <= 45) {
			facing = BlockFace.SOUTH;
		} else if(yaw <= 135) {
			facing = BlockFace.WEST;
		} else if(yaw <= 225) {
			facing = BlockFace.NORTH;
		} else {
			facing = BlockFace.EAST;
		}
		
		return facing;
	}

}
