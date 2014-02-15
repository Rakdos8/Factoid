package me.tabinol.factoid.commands.create;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.Log;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandCreate {

    private Player player;
    private CreateType createType;
    private ArgList argList;
    private CuboidArea area = null;

    public enum CreateType {

        LAND(),
        AREA(),
    }
    Log log = Factoid.getLog();

    public CommandCreate(CreateType createType, Player player, ArgList argList) throws FactoidCommandException {

        this.player = player;
        this.createType = createType;
        this.argList = argList;

        if (OnCommand.getPlayerSetFlagUI().containsKey(player)) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.QUIT.FLAGMODE");
        }
        

        // TEMPORAIRE seulement AdminMod pour le moment
        if (!Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.NOPERMISSION");
        }
        LandSelection select = OnCommand.getPlayerSelectingLand().get(player);
        area = select.toCuboidArea();

        if (createType == CreateType.LAND) {
            if (!OnCommand.getPlayerSelectingLand().containsKey(player) /* && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase()) */) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.SELECTMODE");
            }
            doLand();
        } else if (createType == CreateType.AREA) {
            if (!OnCommand.getPlayerSelectingLand().containsKey(player) /* && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase()) */) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.AREA.SELECTMODE");
            }
            doArea();
        }

        // Quit select mod
        OnCommand.getPlayerSelectingLand().remove(player);
        select.resetSelection();

        log.write(player.getName() + " have quit SelectMode.");
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));

        /* 
         if (!OnCommand.getLandSelectioned().containsKey(player.getName().toLowerCase())) {
         Land landtest = Factoid.getLands().getLand(curArg);
         if (landtest == null) {
         CuboidArea area;
         LandSelection select;
         if (false /*(area = PlayerSelectingWorldEdit.get(player.getName().toLowerCase())) != null) {
         // take selection from WorldEdit
         select = new LandSelection(player, 
         null, area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2());
         } else {
         select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
         area = select.toCuboidArea();
         }
         Land land = new Land(curArg, new PlayerContainerPlayer(player.getName()), area);
                
         if (!Factoid.getConf().CanMakeCollision) {
         if (!select.getCollision()) {
         if (Factoid.getLands().createLand(land)) {
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         log.write(player.getName() + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
         log.write(player.getName() + " have quit SelectMode.");
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         log.write(player.getName() + " have quit SelectMode.");
         }
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
         }
         } else {
         if (Factoid.getLands().createLand(land)) {
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
         log.write(player.getName() + " have quit SelectMode.");
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
         log.write(player.getName() + " have quit SelectMode.");
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         }
         }
         OnCommand.getPlayerSelectingLand().remove(player.getName().toLowerCase());
         // PlayerSelectingWorldEdit.remove(player.getName().toLowerCase());
         select.resetSelection();
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
         }
         } else {
         Land landtest = OnCommand.getLandSelectioned().get(player.getName().toLowerCase());
         if (landtest == null) {
         LandSelection select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
         CuboidArea cuboidarea = select.toCuboidArea();
         Land land = new Land(curArg, new PlayerContainerPlayer(player.getName()), cuboidarea);
         if (!Factoid.getConf().CanMakeCollision) {
         if (!select.getCollision()) {
         if (Factoid.getLands().createLand(land)) {
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         log.write(player.getName() + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
         log.write(player.getName() + " have quit SelectMode.");
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         log.write(player.getName() + " have quit SelectMode.");
         }
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
         }
         } else {
         if (Factoid.getLands().createLand(land)) {
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
         log.write(player.getName() + " have quit SelectMode.");
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
         log.write(player.getName() + " have quit SelectMode.");
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         }
         }
         OnCommand.getPlayerSelectingLand().remove(player.getName().toLowerCase());
         select.resetSelection();
         } else {
         DummyLand dummyland = Factoid.getLands().getLand(player.getLocation());
         if (dummyland.checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE) != PermissionType.LAND_CREATE.baseValue()) {
         LandSelection select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
         CuboidArea cuboidarea = select.toCuboidArea();
         Land land = Factoid.getLands().getLand(player.getLocation());
         if (!Factoid.getConf().CanMakeCollision) {
         if (!select.getCollision()) {
         land.addArea(cuboidarea);
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA", land.getName()));
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         log.write(player.getName() + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
         log.write(player.getName() + " have quit SelectMode.");

         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
         }
         } else {
         land.addArea(cuboidarea);
         player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA"));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
         log.write(player.getName() + " have quit SelectMode.");
         player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
         }
         OnCommand.getPlayerSelectingLand().remove(player.getName().toLowerCase());
         select.resetSelection();
         //player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
         //log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
         //player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
         } else {
         player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.NOPERMISSION", player.getName()));
         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.NOPERMISSION", player.getName()));
         }
         }
         } */
    }

    private void doLand() throws FactoidCommandException {

        if (argList.isLast()) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.NEEDNAME");
        }
        String curArg = argList.getNext();
        if (OnCommand.getBannedWord().contains(curArg.toLowerCase())) {
            throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.HINTUSE");
        }

        Land parent = null;

        // Check for parent
        if (!argList.isLast()) {

            parent = Factoid.getLands().getLand(argList.getNext());

            if (parent == null) {
                throw new FactoidCommandException("CommandCreate", player, "COMMAND.CREATE.PARENTNOTEXIST");
            }
        }

        // Create Land
        Land land = Factoid.getLands().createLand(curArg, new PlayerContainerPlayer(player.getName()), area, parent);

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

        // Add area
        land.addArea(area);

        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
        log.write(player.getName() + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
    }
}
