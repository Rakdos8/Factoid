package me.tabinol.factoid.commands;

import me.tabinol.factoid.lands.Land;

public class ConfirmEntry {

    // Represent a Entry for a "/factoid confirm"
    public enum ConfirmType {

        REMOVE_LAND,
        REMOVE_AREA,
        LAND_DEFAULT;
    }

    public final ConfirmType confirmType;
    public final Land land;
    public final int areaNb;

    public ConfirmEntry(ConfirmType confirmType, Land land, int areaNb) {

        this.confirmType = confirmType;
        this.land = land;
        this.areaNb = areaNb;
    }
}
