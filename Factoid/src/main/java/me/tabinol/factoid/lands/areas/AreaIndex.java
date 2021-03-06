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
package me.tabinol.factoid.lands.areas;

import me.tabinol.factoidapi.lands.areas.ICuboidArea;


/**
 * The Class AreaIndex.
 */
public class AreaIndex implements Comparable<AreaIndex> {

	/** The index nb. */
	private final int indexNb;

	/** The area. */
	private final me.tabinol.factoidapi.lands.areas.ICuboidArea area;

	/**
	 * Instantiates a new area index.
	 *
	 * @param indexNb the index nb
	 * @param area the area
	 */
	public AreaIndex(final int indexNb, final ICuboidArea area) {

		this.indexNb = indexNb;
		this.area = area;
	}

	/**
	 * Equals.
	 *
	 * @param index2 the index2
	 * @return true, if successful
	 */
	public boolean equals(final AreaIndex index2) {

		return indexNb == index2.indexNb && area == index2.area;
	}

	/**
	 * Copy of.
	 *
	 * @return the area index
	 */
	public AreaIndex copyOf() {

		return new AreaIndex(indexNb, area);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final AreaIndex t) {
		if (indexNb < t.indexNb) {
			return -1;
		}
		if (indexNb > t.indexNb) {
			return 1;
		}
		return ((CuboidArea) area).compareTo((CuboidArea) t.area);
	}

	/**
	 * Gets the index nb.
	 *
	 * @return the index nb
	 */
	public int getIndexNb() {

		return indexNb;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public CuboidArea getArea() {

		return (CuboidArea) area;
	}
}
