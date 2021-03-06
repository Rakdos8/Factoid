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
package me.tabinol.factoid.lands.collisions;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;
import me.tabinol.factoidapi.lands.ILand;

/**
 * The Class CollisionsEntry.
 */
public class CollisionsEntry {

	/** The error. */
	private final LandError error;

	/** The land. */
	private final ILand land;

	/** The area id. */
	private final int areaId;

	/**
	 * Instantiates a new collisions entry.
	 *
	 * @param error the error
	 * @param land the land
	 * @param areaId the area id
	 */
	public CollisionsEntry(final LandError error, final ILand land, final int areaId) {
		this.error = error;
		this.land = land;
		this.areaId = areaId;
	}

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public LandError getError() {
		return error;
	}

	/**
	 * Gets the land.
	 *
	 * @return the land
	 */
	public ILand getLand() {
		return land;
	}

	/**
	 * Gets the area id.
	 *
	 * @return the area id
	 */
	public int getAreaId() {
		return areaId;
	}

	/**
	 * Gets the prints the.
	 *
	 * @return the prints the
	 */
	public String getPrint() {
		if (error == LandError.COLLISION) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.COLLISION", land.getName(), areaId + "");
		} else if (error == LandError.OUT_OF_PARENT) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.OUT_OF_PARENT", land.getName());
		} else if (error == LandError.CHILD_OUT_OF_BORDER) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.CHILD_OUT_OF_BORDER", land.getName());
		} else if (error == LandError.HAS_CHILDREN) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.HAS_CHILDREN", land.getName());
		} else if (error == LandError.NAME_IN_USE) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.NAME_IN_USE");
		} else if (error == LandError.IN_APPROVE_LIST) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.IN_APPROVE_LIST");
		} else if (error == LandError.NOT_ENOUGH_MONEY) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.NOT_ENOUGH_MONEY");
		} else if (error == LandError.MAX_AREA_FOR_LAND) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.MAX_AREA_FOR_LAND", land.getName());
		} else if (error == LandError.MAX_LAND_FOR_PLAYER) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.MAX_LAND_FOR_PLAYER");
		} else if (error == LandError.MUST_HAVE_AT_LEAST_ONE_AREA) {
			return Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.MUST_HAVE_AT_LEAST_ONE_AREA");
		}

		return null;
	}
}
