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
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandError;

public class CollisionsEntry {

    private final LandError error;
    private final Land land;
    private final int areaId;

    public CollisionsEntry(LandError error, Land land, int areaId) {

        this.error = error;
        this.land = land;
        this.areaId = areaId;
    }

    public LandError getError() {

        return error;
    }

    public Land getLand() {

        return land;
    }

    public int getAreaId() {

        return areaId;
    }

    public String getPrint() {

        if (error == LandError.COLLISION) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.COLLISION", land.getName(), areaId + "");
        }
        if (error == LandError.OUT_OF_PARENT) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.OUT_OF_PARENT", land.getName());
        }
        if (error == LandError.CHILD_OUT_OF_BORDER) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.CHILD_OUT_OF_BORDER", land.getName());
        }
        if (error == LandError.HAS_CHILDREN) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.HAS_CHILDREN", land.getName());
        }
        if (error == LandError.NAME_IN_USE) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.NAME_IN_USE");
        }
        if (error == LandError.IN_APPROVE_LIST) {
            return Factoid.getLanguage().getMessage("COLLISION.SHOW.IN_APPROVE_LIST");
        }

        return null;
    }
}
