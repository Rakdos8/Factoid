package me.tabinol.factoid.commands.select;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.selection.LandSelection;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class WorldEditSelect {
    
    Player player;
    
    public WorldEditSelect(Player player) throws FactoidCommandException{
        super();
        this.player = player;
        MakeSelect();
    }
    
    private void MakeSelect() throws FactoidCommandException{
        
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
            //CuboidArea area = new CuboidArea(sel.getWorld().getName(),
            //        sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(), sel.getMinimumPoint().getBlockZ(),
            //        sel.getMaximumPoint().getBlockX(), sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ());
            // OnCommand.getPlayerSelectingWorldEdit().put(player.getName().toLowerCase(), area);
            LandSelection select = new LandSelection(player,
                    sel.getMinimumPoint().getBlockX(), sel.getMaximumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(),
                    sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ(), sel.getMinimumPoint().getBlockZ());
            OnCommand.getPlayerSelectingLand().put(player, select);
            select.setSelected();

        } catch (IncompleteRegionException ex) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
