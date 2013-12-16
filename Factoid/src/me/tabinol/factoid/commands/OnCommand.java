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
import me.tabinol.factoid.utilities.StringChanges;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.playercontainer.PlayerContainerGroup;
import me.tabinol.factoid.playercontainer.PlayerContainerFaction;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainerType;

import me.tabinol.factoid.commands.select.Select;

public class OnCommand extends Thread implements CommandExecutor {

    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private static Map<String, LandSelection> PlayerSelectingLand = new HashMap();
    private static Map<String, CuboidArea> PlayerSelectingWorldEdit = new HashMap();
    private static Map<String, Land> LandSelectioned = new HashMap();
    private static Map<String, List<LandMakeSquare>> LandSelectionedUI = new HashMap();
    private static Map<String, LandExpansion> PlayerExpanding = new HashMap();
    private static Map<String, LandSetFlag> PlayerSetFlagUI = new HashMap();
    private static List<String> BannedWord = new ArrayList<String>();
    private static List<String> AdminMod = new ArrayList<String>();
    private static Map<String, String> RemoveList = new HashMap();

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
                        new Select(player,arg);
                    } else if (arg[0].equalsIgnoreCase("expand")) {
                        if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
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
                        if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                            if (this.PlayerSelectingLand.containsKey(player.getName().toLowerCase()) && !PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                                    if (arg[1] != null) {
                                        if (!BannedWord.contains(arg[1])) {
                                            if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                                Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                                if (landtest == null) {
                                                    LandSelection select = this.PlayerSelectingLand.get(player.getName().toLowerCase());
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
                                                    this.PlayerSelectingLand.remove(player.getName().toLowerCase());
                                                    select.resetSelection();
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                                }
                                            } else {
                                                Land landtest = LandSelectioned.get(player.getName().toLowerCase());
                                                if (landtest == null) {
                                                    LandSelection select = this.PlayerSelectingLand.get(player.getName().toLowerCase());
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
                                                    this.PlayerSelectingLand.remove(player.getName().toLowerCase());
                                                    select.resetSelection();
                                                } else {
                                                    DummyLand dummyland = Factoid.getLands().getLand(player.getLocation());
                                                    if (dummyland.checkPermissionAndInherit(player.getName(), PermissionType.LAND_CREATE) != PermissionType.LAND_CREATE.baseValue()) {
                                                        LandSelection select = this.PlayerSelectingLand.get(player.getName().toLowerCase());
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
                                                        this.PlayerSelectingLand.remove(player.getName().toLowerCase());
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
                                if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (player.getName().equalsIgnoreCase(land.getOwner().getName()) || AdminMod.contains(player.getName().toLowerCase())) {
                                        if (arg.length < 2 && false) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.JOINMODE", player.getName()));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
                                            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                            LandSetFlag setting = new LandSetFlag(player, area);
                                            this.PlayerSetFlagUI.put(player.getName().toLowerCase(), setting);
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
                                                        } else {
                                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.PLAYERCONTAINERTYPENULL"));
                                                        }
                                                        if (landflag != null) {
                                                            land.addFlag(landflag);
                                                            // land.forceSave();
                                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", type, arg[3]));
                                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.ISDONE", type, arg[3]));
                                                        }
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.FLAGNULL"));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LAND.INVALIDE"));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.MISSINGINFO"));
                                            }
                                        } else if (arg[1].equalsIgnoreCase("list")) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTSTART"));
                                            if (!land.getFlags().isEmpty()) {
                                                for (LandFlag flag : land.getFlags()) {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROW", flag.getFlagType().name(), flag.getValueString()));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROWNULL"));
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
                                if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (player.getName().equalsIgnoreCase(land.getOwner().getName()) || AdminMod.contains(player.getName().toLowerCase())) {
                                        if (arg.length < 2 && false) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.JOINMODE"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.JOINMODE", player.getName()));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.HINT"));
                                            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                            LandSetFlag setting = new LandSetFlag(player, area);
                                            this.PlayerSetFlagUI.put(player.getName().toLowerCase(), setting);
                                        } else if (arg[1].equalsIgnoreCase("set")) {
                                            //factoid PERMISSION set PlayerContainerType PlayerContainer join true
                                            if (arg.length >= 5) {
                                                String Landname = LandSelectioned.get(player.getName().toLowerCase()).getName();
                                                if (Factoid.getLands().getLand(Landname) != null) {
                                                    String PlayerContainerTypevalue = arg[2];
                                                    PermissionType permtype;
                                                    PlayerContainerType containertype = PlayerContainerType.getFromString(PlayerContainerTypevalue);
                                                    String PlayerContainerNamevalue;
                                                    String PermissionValue;
                                                    String PermissionHeritable = null;
                                                    if (containertype != null && containertype.hasParameter()) {
                                                        PlayerContainerNamevalue = arg[3];
                                                        permtype = PermissionType.getFromString(arg[4]);
                                                        PermissionValue = arg[5];
                                                        if(arg.length > 6) {
                                                            PermissionHeritable = arg[6];
                                                        }
                                                    } else {
                                                        PlayerContainerNamevalue = "";
                                                        permtype = PermissionType.getFromString(arg[3]);
                                                        PermissionValue = arg[4];
                                                        if(arg.length > 5) {
                                                            PermissionHeritable = arg[5];
                                                        }
                                                    }
                                                    if (permtype != null) {
                                                        PlayerContainer pc;
                                                        if (containertype != null) {
                                                            pc = PlayerContainer.create(land, containertype, PlayerContainerNamevalue);
                                                            // if (PlayerContainerTypevalue.equalsIgnoreCase("player")) {
                                                            //    pc = new PlayerContainerPlayer(PlayerContainerNamevalue);
                                                            // } else if (PlayerContainerTypevalue.equalsIgnoreCase("group")) {
                                                            //    pc = new PlayerContainerGroup(PlayerContainerNamevalue);
                                                            // } else if (PlayerContainerTypevalue.equalsIgnoreCase("faction")) {
                                                            //    Faction faction = Factoid.getFactions().getFaction(PlayerContainerNamevalue);
                                                            //    pc = new PlayerContainerFaction(faction);
                                                            // } else if (PlayerContainerTypevalue.equalsIgnoreCase("resident")) {
                                                            //    pc = new PlayerContainerResident(land);
                                                            // } else if (PlayerContainerTypevalue.equalsIgnoreCase("everybody")) {
                                                            //     pc = new PlayerContainerEverybody();
                                                            // }else{
                                                            //    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.PLAYERCONTAINERTYPENULL"));
                                                            // }

                                                            if (pc != null) {
                                                                // land.removePermission(pc, permtype); - Inutile, ça l'écrase de toute manière
                                                                boolean PermissionValueB = Boolean.parseBoolean(PermissionValue);
                                                                boolean PermissionHeritableB;
                                                                    if(PermissionHeritable == null) {
                                                                        PermissionHeritableB = true;
                                                                    } else {
                                                                        PermissionHeritableB = Boolean.parseBoolean(PermissionHeritable);
                                                                    }
                                                                    Permission permission = new Permission(permtype, PermissionValueB, PermissionHeritableB);
                                                                    land.addPermission(pc, permission);
                                                                // Pas besoin de faire un forcesave, sauf si tu désactive le save automatique sur le land.
                                                                // Il y avait un bug à l'époque ou tu as programmé ça. Il est réglé.
                                                                // land.forceSave();
                                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", PlayerContainerTypevalue, PermissionValue, PlayerContainerNamevalue));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.ISDONE", PlayerContainerTypevalue, PermissionValue, PlayerContainerNamevalue));
                                                            }
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
                                        } else if (arg[1].equalsIgnoreCase("list")) {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTSTART"));
                                            if (!land.getResidents().isEmpty()) {
                                                for (PlayerContainer pc : land.getSetPCHavePermission()) {
                                                    String dummypermlist = null;
                                                    for (Permission perm : land.getPermissionsForPC(pc)) {
                                                        if (dummypermlist != null) {
                                                            dummypermlist = dummypermlist + " [" + perm.getPermType().name() + ":" + perm.getValue() + "]";
                                                        } else {
                                                            dummypermlist = "[" + perm.getPermType().name() + ":" + perm.getValue() + "]";
                                                        }
                                                    }
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROW", pc.getName(), dummypermlist));
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROWNULL"));
                                            }
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTEND"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.MISSINGPERMISSION"));
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
                    } else if (arg[0].equalsIgnoreCase("resident")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (land.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER) || AdminMod.contains(player.getName().toLowerCase())) {

                                        if (arg.length > 2 || (arg.length == 2 && arg[1].equalsIgnoreCase("list"))) {
                                            //factoid resident add [PlayerContainerType] [Name]
                                            if (arg[1].equalsIgnoreCase("add")) {
                                                String PlayerContainerNamevalue;
                                                if(arg.length > 3) {
                                                    PlayerContainerNamevalue = arg[3];
                                                } else {
                                                    PlayerContainerNamevalue = "";
                                                }
                                                String PlayerContainerTypevalue = arg[2];
                                                PlayerContainer pc = null;
                                                if (PlayerContainerTypevalue != null) {
                                                    PlayerContainerType containertype = PlayerContainerType.getFromString(PlayerContainerTypevalue);
                                                    if(containertype == null) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERTYPENULL"));
                                                    } else if(containertype == PlayerContainerType.RESIDENT) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERRESIDENT"));
                                                    } else if(containertype == PlayerContainerType.EVERYBODY) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINEREVERYBODY"));
                                                    } else {
                                                        pc = PlayerContainer.create(land, containertype, PlayerContainerNamevalue);
                                                    }

                                                    if (pc != null) {
                                                        land.addResident(pc);
                                                        // land.forceSave();
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", PlayerContainerNamevalue, land.getName()));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.RESIDENT.ISDONE", PlayerContainerNamevalue, land.getName()));
                                                    }


                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERTYPENULL"));
                                                }
                                            } else if (arg[1].equalsIgnoreCase("remove")) {
                                                String PlayerContainerNamevalue = arg[3];
                                                String PlayerContainerTypevalue = arg[2];
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
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERRESIDENT"));
                                                    } else if (PlayerContainerTypevalue.equalsIgnoreCase("everybody")) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINEREVERYBODY"));
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERTYPENULL"));
                                                    }

                                                    if (pc != null) {
                                                        land.removeResident(pc);
                                                        land.forceSave();
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONEREMOVE", PlayerContainerNamevalue, land.getName()));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.RESIDENT.ISDONEREMOVE", PlayerContainerNamevalue, land.getName()));
                                                    }


                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.PLAYERCONTAINERTYPENULL"));
                                                }
                                            } else if (arg[1].equalsIgnoreCase("list")) {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTSTART"));
                                                if (!land.getResidents().isEmpty()) {
                                                    for (PlayerContainer pc : land.getResidents()) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROW", pc.getName()));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
                                                }
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTEND"));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.MISSINGINFO"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.MISSINGPERMISSION"));
                                    }

                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.QUIT.EXPANDMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("banned")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                                    Land land = LandSelectioned.get(player.getName().toLowerCase());
                                    if (player.getName().equalsIgnoreCase(land.getOwner().getName()) || AdminMod.contains(player.getName().toLowerCase())) {

                                        if (arg.length > 3) {
                                            //factoid banned add [PlayerContainerType] [Name]
                                            if (arg[1].equalsIgnoreCase("add")) {
                                                String PlayerContainerNamevalue = arg[3];
                                                String PlayerContainerTypevalue = arg[2];
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
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERRESIDENT"));
                                                    } else if (PlayerContainerTypevalue.equalsIgnoreCase("everybody")) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINEREVERYBODY"));
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERTYPENULL"));
                                                    }

                                                    if (pc != null) {
                                                        land.addBanned(pc);
                                                        land.forceSave();
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ISDONE", PlayerContainerNamevalue, land.getName()));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.BANNED.ISDONE", PlayerContainerNamevalue, land.getName()));
                                                    }


                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERTYPENULL"));
                                                }
                                                //factoid banned remove [PlayerContainerType] [Name]
                                            } else if (arg[1].equalsIgnoreCase("remove")) {
                                                String PlayerContainerNamevalue = arg[3];
                                                String PlayerContainerTypevalue = arg[2];
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
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERRESIDENT"));
                                                    } else if (PlayerContainerTypevalue.equalsIgnoreCase("everybody")) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINEREVERYBODY"));
                                                    } else {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERTYPENULL"));
                                                    }

                                                    if (pc != null) {
                                                        land.removeBanned(pc);
                                                        land.forceSave();
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ISDONEREMOVE", PlayerContainerNamevalue, land.getName()));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.BANNED.ISDONEREMOVE", PlayerContainerNamevalue, land.getName()));
                                                    }


                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.PLAYERCONTAINERTYPENULL"));
                                                }
                                            } else if (arg[1].equalsIgnoreCase("list")) {
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTSTART"));
                                                if (!land.getBanneds().isEmpty()) {
                                                    for (PlayerContainer pc : land.getBanneds()) {
                                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTROW", pc.getName()));
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
                                                }
                                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTEND"));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.MISSINGINFO"));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.MISSINGPERMISSION"));
                                    }

                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ALREADY"));
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.JOIN.SELECTMODE"));
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.QUIT.EXPANDMODE"));
                        }
                    } else if (arg[0].equalsIgnoreCase("remove")) {
                        if (!this.PlayerExpanding.containsKey(player.getName().toLowerCase())) {
                            if (this.LandSelectioned.containsKey(player.getName().toLowerCase())) {
                                if (!this.PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
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
                        } else if (PlayerSelectingLand.containsKey(player.getName().toLowerCase())) {
                            LandSelection select = this.PlayerSelectingLand.get(player.getName().toLowerCase());
                            this.PlayerSelectingLand.remove(player.getName().toLowerCase());
                            select.resetSelection();
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.CANCEL", player.getName()));
                        } else if (PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));
                            PlayerSetFlagUI.remove(player.getName().toLowerCase());
                        } else if (LandSelectioned.containsKey(player.getName().toLowerCase())) {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
                            LandSelectioned.remove(player.getName().toLowerCase());
                            if (LandSelectionedUI.containsKey(player.getName().toLowerCase())) {
                                LandSelectionedUI.remove(player.getName().toLowerCase());
                            }
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
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.NOPERMISSION"));
                        }
                    } else if (arg[0].equalsIgnoreCase("reload")) {
                        if (player.hasPermission("factoid.reload")) {
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.START"));
                            Factoid.getThisPlugin().reload();
                            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.COMPLETE"));
                        } else {
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.NOPERMISSION"));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.NOTEXIST"));
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

    public static Map<String, LandSelection> getPlayerSelectingLand(){
        return PlayerSelectingLand;
    }
    
    public static Map<String, CuboidArea> getPlayerSelectingWorldEdit(){
        return PlayerSelectingWorldEdit;
    }
    
    public static Map<String, Land> getLandSelectioned(){
        return LandSelectioned;
    }
    
    public static Map<String, List<LandMakeSquare>> getLandSelectionedUI(){
        return LandSelectionedUI;
    }
    
    public static Map<String, LandExpansion> getPlayerExpanding(){
        return PlayerExpanding;
    }
    
    public static Map<String, LandSetFlag> getPlayerSetFlagUI(){
        return PlayerSetFlagUI;
    }
    
    public static List<String> getAdminMod(){
        return AdminMod;
    }
    
    public static Map<String, String> getRemoveList(){
        return RemoveList;
    }
}
