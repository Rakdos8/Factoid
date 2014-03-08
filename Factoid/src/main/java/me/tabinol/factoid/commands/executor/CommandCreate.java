package me.tabinol.factoid.commands.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.Land.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.ChatColor;

public class CommandCreate extends CommandExec {

    public CommandCreate(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, null, true, null);
        checkPermission(true, false, null, null);

        LandSelection select = entity.playerConf.getLandSelection();

        CuboidArea area = select.toCuboidArea();

        // Quit select mod
        entity.playerConf.setAreaSelection(null);
        entity.playerConf.setLandSelected(null);
        select.resetSelection();

        String curArg = entity.argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.HINTUSE");
        }

        Land parent;

        // Check for parent
        if ((parent = entity.playerConf.getLandSelected()) == null && !entity.argList.isLast()) {

            parent = Factoid.getLands().getLand(entity.argList.getNext());

            if (parent == null) {
                throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.PARENTNOTEXIST");
            }
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!entity.playerConf.isAdminMod()
                && !((parent == null && Factoid.getLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(entity.playerName, PermissionType.LAND_CREATE))
                || (parent != null && parent.checkPermissionAndInherit(entity.playerName, PermissionType.LAND_CREATE)))) {
            throw new FactoidCommandException("CommandCreate", entity.player, "GENERAL.MISSINGPERMISSION");
        }

        // Check for collision
        if (checkCollision(curArg, null, LandAction.LAND_ADD, 0, area, parent, true)) {
            return;
        }

        // Create Land
        Land land = null;
        try {
            land = Factoid.getLands().createLand(curArg, new PlayerContainerPlayer(entity.playerName), area, parent);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, "On land create", ex);
        }

        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
        Factoid.getLog().write(entity.playerName + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
        
        // Cancel et select the land
        new CommandCancel(entity.playerConf, true).commandExecute();
        new CommandSelect(entity.player, new ArgList(new String[] {land.getName()}, 
                entity.player), null).commandExecute();
    }
}
