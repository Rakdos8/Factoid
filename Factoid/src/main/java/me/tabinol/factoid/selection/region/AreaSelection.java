/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published BYTE
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.selection.region;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;


/**
 * The Class AreaSelection.
 */
public class AreaSelection extends RegionSelection implements Listener {

	private static final byte BYTE = 0;

	/** The area. */
	protected ICuboidArea area;

	/** The is collision. */
	protected boolean isCollision = false;

	/** The block list. */
	private final Map<Location, Material> blockList = new HashMap<>();

	/** The is from land. */
	private final boolean isFromLand;

	/** Parent detected */
	private IDummyLand parentDetected = null;

	/**
	 * Instantiates a new area selection.
	 *
	 * @param player the player
	 * @param area the area
	 */
	public AreaSelection(final Player player, final ICuboidArea area) {
		this(player, area, false);
	}

	// Called from Land Selection list
	/**
	 * Instantiates a new area selection.
	 *
	 * @param player the player
	 * @param area the area
	 * @param isFromLand the is from land
	 */
	public AreaSelection(final Player player, final ICuboidArea area, final boolean isFromLand) {
		super(SelectionType.AREA, player);

		this.area = area;
		this.isFromLand = isFromLand;

		makeVisualSelection();
	}

	/**
	 * Make visual selection.
	 */
	@SuppressWarnings("deprecation")
	final void makeVisualSelection() {
		// Get the size (x and z) no abs (already adjusted)
		final int diffX = area.getX2() - area.getX1();
		final int diffZ = area.getZ2() - area.getZ1();

		// Do not show a too big select to avoid crash or severe lag
		final int maxSize = Factoid.getThisPlugin().iConf().getMaxVisualSelect();
		final int maxDisPlayer = Factoid.getThisPlugin().iConf().getMaxVisualSelectFromPlayer();
		final Location playerLoc = player.getLocation();
		if (diffX > maxSize || diffZ > maxSize
				|| abs(area.getX1() - playerLoc.getBlockX()) > maxDisPlayer
				|| abs(area.getX2() - playerLoc.getBlockX()) > maxDisPlayer
				|| abs(area.getZ1() - playerLoc.getBlockZ()) > maxDisPlayer
				|| abs(area.getZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
			Factoid.getThisPlugin().iLog().write("Selection disabled!");
			return;
		}

		// Detect the current land from the 8 points
		final IDummyLand land1 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
		final IDummyLand land2 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX1(), area.getY1(), area.getZ2()));
		final IDummyLand land3 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX2(), area.getY1(), area.getZ1()));
		final IDummyLand land4 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX2(), area.getY1(), area.getZ2()));
		final IDummyLand land5 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX1(), area.getY2(), area.getZ1()));
		final IDummyLand land6 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX1(), area.getY2(), area.getZ2()));
		final IDummyLand land7 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX2(), area.getY2(), area.getZ1()));
		final IDummyLand land8 = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
				area.getWord(), area.getX2(), area.getY2(), area.getZ2()));

		if (land1 == land2
				&& land1 == land3
				&& land1 == land4
				&& land1 == land5
				&& land1 == land6
				&& land1 == land7
				&& land1 == land8
		) {
			parentDetected = land1;
		} else {
			parentDetected = Factoid.getThisPlugin().iLands().getOutsideArea(land1.getWorldName());
		}

		final boolean canCreate = Factoid.getThisPlugin().iPlayerConf().get(player).isAdminMod()
				|| parentDetected.checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

		//MakeSquare
		for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
			for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
				if (posX == area.getX1() || posX == area.getX2()
						|| posZ == area.getZ1() || posZ == area.getZ2()) {
					final Location newLoc = new Location(area.getWord(), posX, this.getYNearPlayer(posX, posZ) - 1, posZ);
					blockList.put(newLoc, newLoc.getBlock().getType());

					if (!isFromLand) {
						// Active Selection
						final IDummyLand testCuboidArea = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(newLoc);
						if (canCreate
								&& (
										parentDetected == testCuboidArea
										|| (testCuboidArea instanceof ILand && ((ILand) testCuboidArea).isDescendants(area.getLand()))
								)
						) {
							this.player.sendBlockChange(newLoc, Material.SPONGE, BYTE);
						} else {
							this.player.sendBlockChange(newLoc, Material.REDSTONE_BLOCK, BYTE);
							isCollision = true;
						}
					} else {
						// Passive Selection (created area)
						if ((posX == area.getX1() && posZ == area.getZ1() + 1)
								|| (posX == area.getX1() && posZ == area.getZ2() - 1)
								|| (posX == area.getX2() && posZ == area.getZ1() + 1)
								|| (posX == area.getX2() && posZ == area.getZ2() - 1)
								|| (posX == area.getX1() + 1 && posZ == area.getZ1())
								|| (posX == area.getX2() - 1 && posZ == area.getZ1())
								|| (posX == area.getX1() + 1 && posZ == area.getZ2())
								|| (posX == area.getX2() - 1 && posZ == area.getZ2())) {
							// Subcorner
							this.player.sendBlockChange(newLoc, Material.IRON_BLOCK, BYTE);
						} else if ((posX == area.getX1() && posZ == area.getZ1())
								|| (posX == area.getX2() && posZ == area.getZ1())
								|| (posX == area.getX1() && posZ == area.getZ2())
								|| (posX == area.getX2() && posZ == area.getZ2())) {
							// Exact corner
							this.player.sendBlockChange(newLoc, Material.BEACON, BYTE);
						}
					}
				} else {
					// Square center, skip!
					posZ = area.getZ2() - 1;
				}
			}
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void removeSelection() {
		for (final Map.Entry<Location, Material> EntrySet : this.blockList.entrySet()) {
			this.player.sendBlockChange(EntrySet.getKey(), EntrySet.getValue(), BYTE);
		}

		blockList.clear();
	}

	/**
	 * Gets the cuboid area.
	 *
	 * @return the cuboid area
	 */
	public ICuboidArea getCuboidArea() {
		return area;
	}

	/**
	 * Gets the collision.
	 *
	 * @return the collision
	 */
	public boolean getCollision() {
		return isCollision;
	}

	public ILand getParentDetected() {
		if (parentDetected instanceof ILand) {
			return (ILand) parentDetected;
		}
		return null;
	}

	/**
	 * Gets the y near player before air.
	 *
	 * @param x the x
	 * @param z the z
	 * @return the y near player
	 */
	private int getYNearPlayer(final int x, final int z) {
		final Location loc = new Location(player.getWorld(), x, player.getLocation().getY() - 1, z);

		if (loc.getBlock().getType() == Material.AIR) {
			while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
					&& loc.getBlockY() > 1) {
				loc.subtract(0, 1, 0);
			}
		} else {
			while (loc.getBlock().getType() != Material.AIR && loc.getBlockY() < player.getWorld().getMaxHeight()) {
				loc.add(0, 1, 0);
			}
		}
		return loc.getBlockY();
	}

}
