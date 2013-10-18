package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.Land;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Land land;

    public LandEvent(Land land) {

        this.land = land;
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Land getLand() {

        return land;
    }
}
