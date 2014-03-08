package me.tabinol.factoid.config.vanish;

import org.bukkit.entity.Player;

/**
 * Only return false if there is no Vanish plugin
 * 
 * @author michel
 */
public class DummyVanish implements Vanish {

    @Override
    public boolean isVanished(Player player) {
        
        return false;
    }
}
