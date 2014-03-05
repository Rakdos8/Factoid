package me.tabinol.factoid.commands.executor;

import java.util.ArrayList;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class CommandSelect extends CommandExec {

    private final Player player;
    private final Location location;
    private final PlayerConfEntry playerConf;
    private final ArgList argList;

    public CommandSelect(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
        player = entity.player;
        location = null;
        playerConf = entity.playerConf;
        argList = entity.argList;
    }

    // Called from player action, not a command
    public CommandSelect(Player player, ArgList argList, Location location) throws FactoidCommandException {

        super(null, false, false);
        this.player = player;
        this.location = location;
        playerConf = Factoid.getPlayerConf().get(player);
        this.argList = argList;
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(false, false, null, null);

        String curArg;

        if (playerConf.getAreaSelection() == null) {
            Factoid.getLog().write(player.getName() + " join select mode");

            if (!argList.isLast()) {
                
                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Factoid.getDependPlugin().getWorldEdit() == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(player, playerConf).MakeSelect();
                
                } else {
                    
                    Land landtest;
                    if (curArg.equalsIgnoreCase("here")) {
                        
                        // add select Here to select the the cuboid
                        if (location != null) {
                            
                            // With an item
                            landtest = Factoid.getLands().getLand(location);
                        } else {
                            
                            // Player location
                            landtest = Factoid.getLands().getLand(player.getLocation());
                        }
                    
                    } else {
                        
                        landtest = Factoid.getLands().getLand(curArg.toString());
                    }
                    
                    if (landtest == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");
                    
                    }
                    PlayerContainer owner = landtest.getOwner();
                    
                    if (!owner.hasAccess(player.getName()) && !playerConf.isAdminMod()
                            && !landtest.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
                        throw new FactoidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getLandSelected() == null) {
                        
                        playerConf.setLandSelected(landtest);
                        List<LandMakeSquare> listdummy = new ArrayList<>();
                        for (CuboidArea area : landtest.getAreas()) {
                            LandMakeSquare landmake = new LandMakeSquare(player, null,
                                    area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2(), true);
                            landmake.makeSquare();
                            listdummy.add(landmake);
                        }
                        playerConf.setLandSelectedUI(listdummy);

                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTIONEDLAND", landtest.getName()));
                        playerConf.setAutoCancelSelect(true);
                    } else {
                        
                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                    }
                }
            } else {
                
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                LandSelection select = new LandSelection(player);
                playerConf.setAreaSelection(select);
                playerConf.setAutoCancelSelect(true);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {
            
            if (playerConf.getLandSelected() != null) {
                throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
            }
            
            doSelectDone();
        
        } else {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    private void doSelectDone() throws FactoidCommandException {

        checkSelections(null, null, null, true);

        LandSelection select = playerConf.getAreaSelection();
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
            select.setSelected();
        } else {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
            select.setSelected();
        }
    }

}
