package me.tabinol.factoid.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.FlagValueType;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.scoreboard.ScoreBoard;
import me.tabinol.factoid.utilities.StringChanges;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainerGroup;
import me.tabinol.factoid.playercontainer.PlayerContainerFaction;
import me.tabinol.factoid.playercontainer.PlayerContainerResident;
import me.tabinol.factoid.playercontainer.PlayerContainerEverybody;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.permissions.Permission;

public class OnCommand extends Thread implements CommandExecutor {

    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private Map<String, LandSelection> PlayerSelecting = new HashMap();
    private Map<String, CuboidArea> PlayerSelectingWorldEdit = new HashMap();
    private Map<String, Land> LandSelectioned = new HashMap();
    private Map<String, LandExpansion> PlayerExpanding = new HashMap();
    private Map<String, LandSetFlag> PlayerSetFlag = new HashMap();
    private List<String> BannedWord = new ArrayList<String>();
    private static List<String> AdminMod = new ArrayList<String>();
    private Map<String, String> RemoveList = new HashMap();

    public OnCommand() {
        this.language = Factoid.getLanguage();
        this.plugin = Factoid.getThisPlugin();
        this.log = Factoid.getLog();
        BannedWord.add("cancel");
        BannedWord.add("done");
        BannedWord.add("worldedit");
        BannedWord.add("expand");
        BannedWord.add("select");
        BannedWord.add("remove");
        BannedWord.add("here");
        BannedWord.add("current");
        BannedWord.add("adminmod");
        BannedWord.add("factoid");
        BannedWord.add("console");
        BannedWord.add("claim");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Factoid.getLanguage().getMessage("CONSOLE"));
            return false;
        } else {
            if (cmd.getName().equalsIgnoreCase("factoid") || cmd.getName().equalsIgnoreCase("claim")) {
                Player player = (Player) sender;
                World world = player.getWorld();
                Location loc = player.getLocation();

                if (arg.length > 0) {
                    if (arg[0].equalsIgnoreCase("select")) {
                        if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                            if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSelecting.containsKey(player.getName().toLowerCase()) && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase())) {
                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.JOIN", player.getName()));
                                    if (arg.length == 2) {
                                        if (arg[1].equalsIgnoreCase("worldedit")) {
                                            if (Factoid.getDependPlugin().getWorldEdit() == null) {
                                                player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOTLOAD"));
                                                log.write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOTLOAD"));
                                            } else {
                                                LocalSession session = ((WorldEditPlugin) Factoid.getDependPlugin().getWorldEdit()).getSession(player);
                                                try {
                                                    Region sel;
                                                    if (session.getSelectionWorld() != null
                                                            && (sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion) {

                                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
                                                        log.write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
                                                        CuboidArea area = new CuboidArea(sel.getWorld().getName(),
                                                                sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(), sel.getMinimumPoint().getBlockZ(),
                                                                sel.getMaximumPoint().getBlockX(), sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ());
                                                        PlayerSelectingWorldEdit.put(player.getName().toLowerCase(), area);
                                                    } else {
                                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED"));
                                                        log.write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED"));
                                                    }
                                                } catch (Exception ex) {
                                                    if (ex instanceof IncompleteRegionException) {
                                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET"));
                                                        log.write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET"));
                                                    } else {
                                                        Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
                                                }
                                            }
                                        } else {
                                            Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                            if (landtest != null) {
                                                PlayerContainer owner = landtest.getOwner();
                                                if (owner.hasAccess(player.getName()) || AdminMod.contains(player.getName().toLowerCase())) {
                                                    if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                                        LandSelectioned.put(player.getName().toLowerCase(), landtest);
                                                        for (CuboidArea area : landtest.getAreas()) {
                                                            LandMakeSquare landmake = new LandMakeSquare(player, null, area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2());
                                                            landmake.makeSquare();
                                                        }
                                                        new ScoreBoard(player, landtest.getName());

                                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.MISSINGPERMISSION", landtest.getName()));
                                                    } else {
                                                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.MISSINGPERMISSION"));
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.MISSINGPERMISSION", player.getName()));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.NOLAND"));
                                            }
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                                        player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                                        LandSelection select = new LandSelection(player, player.getServer(), plugin);
                                        this.PlayerSelecting.put(player.getName().toLowerCase(), select);
                                    }
                                } else if (arg.length > 1 && arg[1].equalsIgnoreCase("done")) {
                                    if (!PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase())) {
                                        if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                            LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                            if (!Factoid.getConf().CanMakeCollision) {
                                                if (!select.getCollision()) {
                                                    select.setSelected();
                                                    player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + "You have selected a new Land.");
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                                                }
                                            } else {
                                                select.setSelected();
                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                                                player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.NEWLAND"));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONE"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONEWORLDEDIT"));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.EXPENDMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.FLAGSMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("expand")) {
                        if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                                    player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
                                    player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.JOINMODE", player.getName()));
                                    LandExpansion expand = new LandExpansion(player, player.getServer(), plugin);
                                    this.PlayerExpanding.put(player.getName().toLowerCase(), expand);
                                } else if (arg.length > 1 && arg[1].equalsIgnoreCase("done")) {
                                    player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.QUITMODE", player.getName()));
                                    LandExpansion expand = this.PlayerExpanding.get(player.getName().toLowerCase());
                                    expand.setSelected();
                                    this.PlayerExpanding.remove(player.getName().toLowerCase());
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUIT.FLAGMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("create")) {
                        if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                            if (this.PlayerSelecting.containsKey(player.getName().toLowerCase()) && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                                    if (arg[1] != null) {
                                        if (!BannedWord.contains(arg[1])) {
                                            if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                                Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                                if (landtest == null) {
                                                    LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
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
                                                    Land land = new Land(arg[1].toString(), new PlayerContainerPlayer(player.getName()), cuboidarea);
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
                                                    this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                    select.resetSelection();
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                                }
                                            } else {
                                                Land landtest = LandSelectioned.get(player.getName().toLowerCase());
                                                if (landtest == null) {
                                                    LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
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
                                                    Land land = new Land(arg[1].toString(), new PlayerContainerPlayer(player.getName()), cuboidarea);
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
                                                    this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                    select.resetSelection();
                                                } else {
                                                    DummyLand dummyland = Factoid.getLands().getLand(player.getLocation());
                                                    if (dummyland.checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE) != PermissionType.LAND_CREATE.baseValue()) {
                                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
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
                                                        this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                        select.resetSelection();
                                                        /*player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                                         log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                                                         player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));*/
                                                    } else {
                                                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.NOPERMISSION"));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.NOPERMISSION", player.getName()));
                                                    }
                                                }
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.NEEDNAME"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.NEEDNAME", player.getName()));
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.SELECTMODE"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.FLAGMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("flags")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (player.getName().equals(land.getOwner().getName())) {
                                        if (arg.length < 2) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.JOINMODE", player.getName()));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
                                            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                            LandSetFlag setting = new LandSetFlag(player, area);
                                            this.PlayerSetFlag.put(player.getName().toLowerCase(), setting);
                                        } else if (arg[1].equalsIgnoreCase("set")) {
                                            //factoid flags set lol true
                                            if (arg.length == 4) {
                                                String type = arg[2];
                                                String Landname = LandSelectioned.get(player.getName().toLowerCase()).getName();
                                                if (Factoid.getLands().getLand(Landname) != null) {
                                                    if (FlagType.getFromString(type) != null) {
                                                        FlagType flag = FlagType.getFromString(type);
                                                        LandFlag landflag = null;
                                                        land.removeFlag(flag);
                                                        if (flag.getFlagValueType() == FlagValueType.BOOLEAN) {
                                                            boolean BooleanValue = Boolean.parseBoolean(arg[3]);
                                                            landflag = new LandFlag(flag, BooleanValue, true);
                                                        } else if (flag.getFlagValueType() == FlagValueType.STRING) {
                                                            String StringValue = arg[3];
                                                            landflag = new LandFlag(flag, StringValue, true);
                                                        } else if (flag.getFlagValueType() == FlagValueType.STRING_LIST) {
                                                            ArrayList<String> result = new ArrayList<>();
                                                            String[] strs = StringChanges.splitKeepQuote(arg[3], ";");
                                                            for (String str : strs) {
                                                                result.add(StringChanges.fromQuote(str));
                                                            }
                                                            String[] StringArrayValue = result.toArray(new String[0]);
                                                            landflag = new LandFlag(flag, StringArrayValue, true);
                                                        }
                                                        land.addFlag(landflag);
                                                        land.forceSave();
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.FLAGNULL"));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LAND.INVALIDE"));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.MISSINGINFO"));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTSTART"));
                                            for (FlagType ft : FlagType.values()) {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROW", ft.name()));
                                            }
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTEND"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.MISSINGPERMISSION"));
                                    }

                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.QUIT.EXPANDMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("permission")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (player.getName().equals(land.getOwner().getName())) {
                                        if (arg.length < 2) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.JOINMODE"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.JOINMODE", player.getName()));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.HINT"));
                                            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                            LandSetFlag setting = new LandSetFlag(player, area);
                                            this.PlayerSetFlag.put(player.getName().toLowerCase(), setting);
                                        } else if (arg[1].equalsIgnoreCase("set")) {
                                            //factoid PERMISSION set PlayerContainerType PlayerContainer join true
                                            if (arg.length == 6) {
                                                String PlayerContainerNamevalue = arg[3];
                                                String PlayerContainerTypevalue = arg[2];
                                                String PermissionValue = arg[5];
                                                String Landname = LandSelectioned.get(player.getName().toLowerCase()).getName();
                                                boolean value = false;
                                                if (Factoid.getLands().getLand(Landname) != null) {
                                                    PermissionType permtype = PermissionType.getFromString(arg[4]);
                                                    if (permtype != null) {
                                                        PlayerContainer pc = null;
                                                        if (PlayerContainerTypevalue != null) {
                                                            if (PlayerContainerTypevalue.equalsIgnoreCase("player")) {
                                                                pc = new PlayerContainerPlayer(PlayerContainerNamevalue);
                                                            } else if (PlayerContainerTypevalue.equalsIgnoreCase("group")) {
                                                                pc = new PlayerContainerGroup(PlayerContainerNamevalue);
                                                            } else if (PlayerContainerTypevalue.equalsIgnoreCase("faction")) {
                                                                Faction faction = Factoid.getFactions().getFaction(PlayerContainerNamevalue);
                                                                pc = new PlayerContainerFaction(faction);
                                                            } else if (PlayerContainerTypevalue.equalsIgnoreCase("resident")) {
                                                                pc = new PlayerContainerResident(land);
                                                            } else if (PlayerContainerTypevalue.equalsIgnoreCase("everybody")) {
                                                                pc = new PlayerContainerEverybody();
                                                            }

                                                            //if(land.isResident(pc) || land.getOwner() == pc){
                                                            land.removePermission(pc, permtype);
                                                            boolean PermissionValueB = Boolean.parseBoolean(PermissionValue);
                                                            if (PermissionValueB) {
                                                                Permission permission = new Permission(permtype, PermissionValueB, true);
                                                                land.addPermission(pc, permission);
                                                            } else {
                                                                Permission permission = new Permission(permtype, false, true);
                                                                land.addPermission(pc, permission);
                                                            }
                                                            land.forceSave();
                                                            //}
                                                        } else {
                                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.PLAYERCONTAINERTYPENULL"));
                                                        }
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.PERMISSIONNULL"));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LAND.INVALIDE"));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.MISSINGINFO"));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTSTART"));
                                            for (PermissionType pt : PermissionType.values()) {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROW", pt.name()));
                                            }
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTEND"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.MISSINGPERMISSION"));
                                    }

                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.QUIT.EXPANDMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("remove")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                                    if (!this.RemoveList.containsKey(player.getName().toLowerCase())) {
                                        if (arg[1] != null) {
                                            if (arg[1].equalsIgnoreCase("land")) {
                                                RemoveList.put(player.getName().toLowerCase(), "removeland");
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CONFIRM"));
                                            } else if (arg[1].equalsIgnoreCase("area")) {
                                                RemoveList.put(player.getName().toLowerCase(), "removearea");

                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.REMOVENULL"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DUPLICATION"));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.QUIT.FLAGSMODE"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.QUIT.EXPANDMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("confirm")) {
                        Location playerloc = player.getLocation();
                        if (RemoveList.containsKey(player.getName().toLowerCase())) {
                            if (RemoveList.get(player.getName().toLowerCase()).equalsIgnoreCase("land")) {
                                Land land = Factoid.getLands().getLand(player.getLocation());
                                int i = 0;
                                for (int key : land.getAreasKey()) {
                                    land.removeArea(key);
                                    i++;
                                }
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", land.getName(), i + ""));
                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.DONE.LAND", player.getName(), land.getName()));
                            } else if (RemoveList.get(player.getName().toLowerCase()).equalsIgnoreCase("area")) {
                                CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                Land land = Factoid.getLands().getLand(player.getLocation());
                                land.removeArea(area);
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", area.getLand().getName()));
                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.DONE.AREA", player.getName(), area.getKey() + "", area.getLand().getName()));
                            }
                        } else {
                            return false;
                        }

                    } else if (arg[0].equalsIgnoreCase("cancel")) {
                        Location playerloc = player.getLocation();
                        if (RemoveList.containsKey(player.getName().toLowerCase())) {
                            if (RemoveList.get(player.getName().toLowerCase()).equalsIgnoreCase("land")) {
                                RemoveList.remove(player.getName().toLowerCase());
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CANCEL"));
                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.CANCEL", player.getName()));
                            } else if (RemoveList.get(player.getName().toLowerCase()).equalsIgnoreCase("area")) {
                                RemoveList.remove(player.getName().toLowerCase());
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CANCEL"));
                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.CANCEL", player.getName()));
                            }
                        } else if (PlayerSelecting.containsKey(player.getName().toLowerCase())) {
                            LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                            this.PlayerSelecting.remove(player.getName().toLowerCase());
                            select.resetSelection();
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.CANCEL", player.getName()));
                        } else if (PlayerSetFlag.containsKey(player.getName().toLowerCase())) {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));
                            PlayerSetFlag.remove(player.getName().toLowerCase());
                        } else if (LandSelectioned.containsKey(player.getName().toLowerCase())) {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
                            LandSelectioned.remove(player.getName().toLowerCase());
                        } else {
                            return false;
                        }



                    } else if (arg[0].equalsIgnoreCase("here") || arg[0].equalsIgnoreCase("current")) {
                        Location playerloc = player.getLocation();
                        Land land = Factoid.getLands().getLand(playerloc);
                        landInfo(land, player);
                    } else if (arg[0].equalsIgnoreCase("adminmod")) {
                        if (player.hasPermission("factoid.adminmod")) {
                            if (AdminMod.contains(player.getName().toLowerCase())) {
                                AdminMod.remove(player.getName().toLowerCase());
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.QUIT"));
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.JOIN"));
                                AdminMod.add(player.getName().toLowerCase());
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.NOPERMISSION"));
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public static void landInfo(DummyLand land, Player player) {

        if (land != null && land instanceof Land) {

            Land trueLand = (Land) land;
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", trueLand.getName()));
            player.sendMessage(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", trueLand.getOwner().getContainerType().name(), trueLand.getOwner().getName()));
            player.sendMessage(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA"));
            for (Map.Entry<Integer, CuboidArea> entry : trueLand.getIdsAndAreas().entrySet()) {
                player.sendMessage("ID: " + entry.getKey() + ", " + entry.getValue());
            }

        } else {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
        }

    }

    public static boolean isAdminMod(String playerName) {

        return AdminMod.contains(playerName.toLowerCase());
    }
}
