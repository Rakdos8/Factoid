package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;

public abstract class PlayerContainer implements PlayerContainerInterface, Comparable<PlayerContainer> {

    protected String name;
    protected PlayerContainerType containerType;

    protected PlayerContainer(String name, PlayerContainerType containerType) {

        this.name = name.toLowerCase();
        this.containerType = containerType;
    }

    public static PlayerContainer create(Land land, PlayerContainerType pct, String name) {

        if (pct == PlayerContainerType.FACTION) {
            return new PlayerContainerFaction(Factoid.getFactions().getFaction(name));
        }
        if (pct == PlayerContainerType.GROUP) {
            return new PlayerContainerGroup(name);
        }
        if (pct == PlayerContainerType.RESIDENT) {
            return new PlayerContainerResident(land);
        }
        if (pct == PlayerContainerType.FACTION_TERRITORY) {
            return new PlayerContainerFactionTerritory(land);
        }
        if (pct == PlayerContainerType.OWNER) {
            return new PlayerContainerOwner(land);
        }
        if (pct == PlayerContainerType.EVERYBODY) {
            return new PlayerContainerEverybody();
        }
        if (pct == PlayerContainerType.NOBODY) {
            return new PlayerContainerNobody();
        }
        return new PlayerContainerPlayer(name);
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public PlayerContainerType getContainerType() {

        return containerType;
    }

    @Override
    public int compareTo(PlayerContainer t) {

        if (containerType.getValue() < t.containerType.getValue()) {
            return -1;
        }
        if (containerType.getValue() > t.containerType.getValue()) {
            return 1;
        }
        return name.compareToIgnoreCase(t.name);
    }

    @Override
    public String toString() {

        return containerType.toString() + ":" + name;
    }
}
