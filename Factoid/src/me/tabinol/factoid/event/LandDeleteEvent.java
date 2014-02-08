package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.Land;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class LandDeleteEvent extends LandEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    Land deletedLand;

    public LandDeleteEvent(final Land deletedLand) {

        super(deletedLand);
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        
        return cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
        
        cancelled = bln;
    }
}
