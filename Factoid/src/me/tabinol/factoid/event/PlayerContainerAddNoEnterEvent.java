package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.event.HandlerList;

public class PlayerContainerAddNoEnterEvent extends LandEvent {

    private static final HandlerList handlers = new HandlerList();
    PlayerContainer playerContainer;

    public PlayerContainerAddNoEnterEvent(final Land land, final PlayerContainer playerContainer) {

        super(land);
        this.playerContainer = playerContainer;
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public PlayerContainer getPlayerContainer() {

        return playerContainer;
    }
}
