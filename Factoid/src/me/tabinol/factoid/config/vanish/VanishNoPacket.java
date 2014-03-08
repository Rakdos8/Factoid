package me.tabinol.factoid.config.vanish;

import me.tabinol.factoid.Factoid;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

/**
 * VanishNoPacket Function
 *
 * @author michel
 */
public class VanishNoPacket implements Vanish {

    private final VanishPlugin vanishNoPacket;

    public VanishNoPacket() {

        vanishNoPacket = (VanishPlugin) Factoid.getDependPlugin().getVanishNoPacket();
    }

    @Override
    public boolean isVanished(Player player) {

        return vanishNoPacket.getManager().isVanished(player);
    }
}
