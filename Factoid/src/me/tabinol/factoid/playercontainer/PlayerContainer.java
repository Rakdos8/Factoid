package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
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
            Faction faction = Factoid.getFactions().getFaction(name);
            if (faction != null) {
                return new PlayerContainerFaction(faction);
            } else {
                return null;
            }
        }
        if (pct == PlayerContainerType.GROUP) {
            return new PlayerContainerGroup(name);
        }
        if (pct == PlayerContainerType.RESIDENT) {
            return new PlayerContainerResident(land);
        }
        if (pct == PlayerContainerType.VISITOR) {
            return new PlayerContainerVisitor(land);
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
        if (pct == PlayerContainerType.PLAYER) {
            return new PlayerContainerPlayer(name);
        }
        if (pct == PlayerContainerType.PERMISSION) {
            return new PlayerContainerPermission(name);
        }
        return null;
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
    
    @Override
    public String getPrint() {
        
        return containerType.toString();
    }
}
