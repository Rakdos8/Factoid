package me.tabinol.factoid.commands.create;

import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.Log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Create {
    
    Log log = Factoid.getLog();
    
        public Create(Player player, ArgList argList) throws FactoidCommandException {

        if (OnCommand.getPlayerSetFlagUI().containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.CREATE.QUIT.FLAGMODE");
        }
        if (!OnCommand.getPlayerSelectingLand().containsKey(player.getName().toLowerCase()) 
                /* && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase()) */) {
            throw new FactoidCommandException("COMMAND.CREATE.SELECTMODE");
        }
        if (argList.length() >= 2) {
            throw new FactoidCommandException("COMMAND.CREATE.NEEDNAME");
        }
        String curArg = argList.getNext();
        if (OnCommand.getBannedWord().contains(curArg.toLowerCase())) {

            throw new FactoidCommandException("COMMAND.CREATE.HINTUSE");
        }
        if (!OnCommand.getLandSelectioned().containsKey(player.getName().toLowerCase())) {
            Land landtest = Factoid.getLands().getLand(curArg);
            if (landtest == null) {
                CuboidArea area;
                LandSelection select;
                if (false /*(area = PlayerSelectingWorldEdit.get(player.getName().toLowerCase())) != null*/) {
                    // take selection from WorldEdit
                    select = new LandSelection(player, 
                            null, area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2());
                } else {
                    select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
                    Map<String, Location> corner = select.getCorner();
                    int x1 = corner.get("FrontCornerLeft").getBlockX();
                    int x2 = corner.get("BackCornerRigth").getBlockX();
                    //int y1 = corner.get("FrontCornerLeft").getBlockY();
                    //int y2 = corner.get("BackCornerRigth").getBlockY();
                    int y1 = Factoid.getConf().MinLandHigh;
                    int y2 = Factoid.getConf().MaxLandHigh;
                    int z1 = corner.get("FrontCornerLeft").getBlockZ();
                    int z2 = corner.get("BackCornerRigth").getBlockZ();

                    area = new CuboidArea(player.getWorld().getName(), x1, y1, z1, x2, y2, z2);
                }
                Land land = new Land(curArg, new PlayerContainerPlayer(player.getName()), area);
                
                if (!Factoid.getConf().CanMakeCollision) {
                    if (!select.getCollision()) {
                        if (Factoid.getLands().createLand(land)) {
                            player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.getAreas().toString()));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        } else {
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
                    }
                } else {
                    if (Factoid.getLands().createLand(land)) {
                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
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
                Map<String, Location> corner = select.getCorner();
                int x1 = corner.get("FrontCornerLeft").getBlockX();
                int x2 = corner.get("BackCornerRigth").getBlockX();
                //int y1 = corner.get("FrontCornerLeft").getBlockY();
                //int y2 = corner.get("BackCornerRigth").getBlockY();
                int y1 = Factoid.getConf().MinLandHigh;
                int y2 = Factoid.getConf().MaxLandHigh;
                int z1 = corner.get("FrontCornerLeft").getBlockZ();
                int z2 = corner.get("BackCornerRigth").getBlockZ();

                CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(), x1, y1, z1, x2, y2, z2);
                Land land = new Land(curArg, new PlayerContainerPlayer(player.getName()), cuboidarea);
                if (!Factoid.getConf().CanMakeCollision) {
                    if (!select.getCollision()) {
                        if (Factoid.getLands().createLand(land)) {
                            player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.getAreas().toString()));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        } else {
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
                    }
                } else {
                    if (Factoid.getLands().createLand(land)) {
                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR", player.getName()));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                    }
                }
                OnCommand.getPlayerSelectingLand().remove(player.getName().toLowerCase());
                select.resetSelection();
            } else {
                DummyLand dummyland = Factoid.getLands().getLand(player.getLocation());
                if (dummyland.checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE) != PermissionType.LAND_CREATE.baseValue()) {
                    LandSelection select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
                    Map<String, Location> corner = select.getCorner();
                    int x1 = corner.get("FrontCornerLeft").getBlockX();
                    int x2 = corner.get("BackCornerRigth").getBlockX();
                    //int y1 = corner.get("FrontCornerLeft").getBlockY();
                    //int y2 = corner.get("BackCornerRigth").getBlockY();
                    int y1 = Factoid.getConf().MinLandHigh;
                    int y2 = Factoid.getConf().MaxLandHigh;
                    int z1 = corner.get("FrontCornerLeft").getBlockZ();
                    int z2 = corner.get("BackCornerRigth").getBlockZ();

                    CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(), x1, y1, z1, x2, y2, z2);
                    Land land = Factoid.getLands().getLand(player.getLocation());
                    if (!Factoid.getConf().CanMakeCollision) {
                        if (!select.getCollision()) {
                            land.addArea(cuboidarea);
                            player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA", land.getName()));
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.getAreas().toString()));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));

                        } else {
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION", player.getName()));
                        }
                    } else {
                        land.addArea(cuboidarea);
                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA"));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE", player.getName(), land.getName(), land.toString()));
                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE", player.getName()));
                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                    }
                    OnCommand.getPlayerSelectingLand().remove(player.getName().toLowerCase());
                    select.resetSelection();
                    /*player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                     log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                     player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));*/
                } else {
                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.NOPERMISSION", player.getName()));
                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.NOPERMISSION", player.getName()));
                }
            }
        }
    }
}
