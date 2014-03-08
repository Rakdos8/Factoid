package me.tabinol.factoid.config.vanish;

import com.earth2me.essentials.Essentials;
import me.tabinol.factoid.Factoid;
import org.bukkit.entity.Player;

/**
 * Essentials Functions
 * 
 * @author Tabinol
 */
public class VanishEssentials implements Vanish {
    
    private final Essentials essentials;
    
    public VanishEssentials() {
        
        essentials = (Essentials)Factoid.getDependPlugin().getEssentials();
    }
    
    @Override
    public boolean isVanished(Player player) {
        
        return essentials.getUser(player).isVanished();
    }
}
