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
