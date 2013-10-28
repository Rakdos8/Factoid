package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.Land;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerLandChangeEvent extends LandEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    Player player;
    Location fromLoc;
    Location toLoc;
    Land lastLand;
    boolean isTp;

    public PlayerLandChangeEvent(final Land lastLand, final Land land, final Player player, 
            final Location fromLoc, final Location toLoc, final boolean isTp) {

        super(land);
        this.lastLand = lastLand;
        this.player = player;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.isTp = isTp;
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
    
    public Player getPlayer() {
        
        return player;
    }
    
    public Land getLastLand() {
        
        return lastLand;
    }
    
    public Location getFromLoc() {
        
        return fromLoc;
    }

    public Location getToLoc() {
        
        return toLoc;
    }
    
    public boolean isTp() {
        
        return isTp;
    }
}
