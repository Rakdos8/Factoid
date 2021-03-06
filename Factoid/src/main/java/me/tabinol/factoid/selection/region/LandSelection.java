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
package me.tabinol.factoid.selection.region;

import java.util.TreeMap;

import org.bukkit.entity.Player;

import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;


/**
 * The Class LandSelection.
 */
public class LandSelection extends RegionSelection {

	/** The land. */
	private final ILand land;

	/** The visual areas. */
	private final TreeMap<ICuboidArea, AreaSelection> visualAreas = new TreeMap<>();

	/**
	 * Instantiates a new land selection.
	 *
	 * @param player the player
	 * @param land the land
	 */
	public LandSelection(final Player player, final ILand land) {
		super(SelectionType.LAND, player);
		this.land = land;

		// Add visual areas
		for (final ICuboidArea area : land.getAreas()) {
			visualAreas.put(area, new AreaSelection(player, area, true));
		}
	}

	/**
	 * Gets the land.
	 *
	 * @return the land
	 */
	public ILand getLand() {
		return land;
	}

	@Override
	public void removeSelection() {
		for (final AreaSelection areaSel : visualAreas.values()) {
			areaSel.removeSelection();
		}

		visualAreas.clear();
	}

}
