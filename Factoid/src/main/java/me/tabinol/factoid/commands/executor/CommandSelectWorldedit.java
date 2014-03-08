package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.selection.LandSelection;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.tabinol.factoid.config.players.PlayerConfEntry;

// WorldEdit is in a separate class from CommandSelect because if WorldEdit
// is not installed, we don't want to makes error.

public class CommandSelectWorldedit {
    
    Player player;
    PlayerConfEntry entry;
    
    public CommandSelectWorldedit(Player player, PlayerConfEntry entry) throws FactoidCommandException{
        
        this.player = player;
        this.entry = entry;
    }
    
    protected void MakeSelect() throws FactoidCommandException {
        
        if (Factoid.getDependPlugin().getWorldEdit() == null) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
        }
        LocalSession session = ((WorldEditPlugin) Factoid.getDependPlugin().getWorldEdit()).getSession(player);
        
        try {
            Region sel;
            if (session.getSelectionWorld() == null
                    || !((sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion)) {
                throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED");
            }

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            
            LandSelection select = new LandSelection(player,
                    sel.getMinimumPoint().getBlockX(), sel.getMaximumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(),
                    sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ(), sel.getMinimumPoint().getBlockZ());
            
            entry.setAreaSelection(select);
            select.setSelected();
            entry.setAutoCancelSelect(true);

        } catch (IncompleteRegionException ex) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
