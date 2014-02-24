package me.tabinol.factoid.commands.create;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.Log;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCreate {

    Log log = Factoid.getLog();
    private final Player player;
    private final CreateType createType;
    private final ArgList argList;
    private final int areaNb;
    private CuboidArea area = null;

    public enum CreateType {

        LAND,
        AREA,
        AREA_REPLACE;
    }

    public CommandCreate(CreateType createType, Player player, ArgList argList, int areaNb) throws FactoidCommandException {

        this.player = player;
        this.createType = createType;
        this.argList = argList;
        this.areaNb = areaNb;

        if (OnCommand.getPlayerSetFlagUI().containsKey(player)) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.QUIT.FLAGMODE");
        }

        LandSelection select = OnCommand.getPlayerSelectingLand().get(player);

        if (createType == CreateType.LAND) {
            if (select == null) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.SELECTMODE");
            }
            area = select.toCuboidArea();
            doLand();
        } else if (createType == CreateType.AREA || createType == CreateType.AREA_REPLACE) {
            if (select == null) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.AREA.SELECTMODE");
            }
            area = select.toCuboidArea();
            doArea();
        }

        // Quit select mod
        OnCommand.getPlayerSelectingLand().remove(player);
        select.resetSelection();

        log.write(player.getName() + " have quit SelectMode.");
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
    }

    private void doLand() throws FactoidCommandException {

        if (argList.isLast()) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.NEEDNAME");
        }
        String curArg = argList.getNext();
        if (OnCommand.getBannedWord().contains(curArg.toLowerCase())) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.HINTUSE");
        }

        Land parent;

        // Check for parent
        if ((parent = OnCommand.getLandSelectioned().get(player)) == null && !argList.isLast()) {

            parent = Factoid.getLands().getLand(argList.getNext());

            if (parent == null) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.PARENTNOTEXIST");
            }
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!Factoid.getPlayerConf().isAdminMod(player)
                && !((parent == null && Factoid.getLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE))
                || (parent != null && parent.checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE)))) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.NOPERMISSION");
        }

        // Check for collision
        if (OnCommand.checkCollision(player, curArg, null, LandAction.LAND_ADD, 0, area, parent, true)) {
            return;
        }

        // Create Land
        Land land = null;

        try {
            land = Factoid.getLands().createLand(curArg, new PlayerContainerPlayer(player.getName()), area, parent);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (land == null) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.ERROR");
        }

        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
        log.write(player.getName() + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
    }

    private void doArea() throws FactoidCommandException {

        Land land = OnCommand.getLandSelectioned().get(player);

        if (land == null) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.AREA.LANDNOTSELECT");
        }

        if (createType == CreateType.AREA) {

            // Check for collision
            if (OnCommand.checkCollision(player, land.getName(), land, LandAction.AREA_ADD, 0, area, land.getParent(), true)) {
                return;
            }
            // Add Area
            land.addArea(area);

        } else {

            // Check for collision
            if (OnCommand.checkCollision(player, land.getName(), land, LandAction.AREA_MODIFY, areaNb, area, land.getParent(), true)) {
                return;
            }
            // Replace Area
            land.replaceArea(areaNb, area);
        }

        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
        log.write(player.getName() + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
    }
}
