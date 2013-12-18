package me.tabinol.factoid.commands;

import java.util.HashMap;
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

import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;

import me.tabinol.factoid.commands.select.Select;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class OnCommand extends Thread implements CommandExecutor {

    private Log log;
    private JavaPlugin plugin;
    private static Map<String, LandSelection> PlayerSelectingLand = new HashMap();
    private static Map<String, CuboidArea> PlayerSelectingWorldEdit = new HashMap();
    private static Map<String, Land> LandSelectioned = new HashMap();
    private static Map<String, List<LandMakeSquare>> LandSelectionedUI = new HashMap();
    private static Map<String, LandExpansion> PlayerExpanding = new HashMap();
    private static Map<String, LandSetFlag> PlayerSetFlagUI = new HashMap();
    private static List<String> BannedWord = new ArrayList<>();
    private static List<String> AdminMod = new ArrayList<>();
    private static Map<String, String> RemoveList = new HashMap();
    private static Map<Player, ChatPage> chatPageList = new HashMap();

    public OnCommand() {

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
        BannedWord.add("page");
        log = Factoid.getLog();
        plugin = Factoid.getThisPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        if (cmd.getName().equalsIgnoreCase("factoid") || cmd.getName().equalsIgnoreCase("claim")) {

            ArgList argList = new ArgList(arg);
            String curArg = argList.getNext();

            if (arg.length == 0) {
                // No parameters print help
                return true;
            }


            try {
                // Commands that can be done from the console
                if (curArg.equalsIgnoreCase("reload")) {
                    doCommandReload(sender);
                    return true;
                }

                // If it is console, do not go forward
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Factoid.getLanguage().getMessage("CONSOLE"));
                    return true;
                }

                Player player = (Player) sender;

                if (curArg.equalsIgnoreCase("select")) {
                    new Select(player, arg);
                    return true;
                }

                if (curArg.equalsIgnoreCase("expand")) {
                    doCommandExpand(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("create")) {
                    doCommandCreate(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("flag")) {
                    doCommandFlag(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("permission")) {
                    doCommandPermission(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("resident")) {
                    doCommandResident(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("ban")) {
                    doCommandBanned(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("remove")) {
                    doCommandRemove(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("confirm")) {
                    doCommandConfirm(player);
                    return true;
                }

                if (curArg.equalsIgnoreCase("cancel")) {
                    doCommandCancel(player);
                    return true;
                }

                if (curArg.equalsIgnoreCase("here") || curArg.equalsIgnoreCase("current")) {
                    doCommandHere(player);
                    return true;
                }

                if (curArg.equalsIgnoreCase("adminmod")) {
                    doCommandAdminmod(player);
                    return true;
                }
                if (curArg.equalsIgnoreCase("page")) {
                    doCommandPage(player, argList);
                    return true;
                }

                // If error on command, send the message to thee player
            } catch (FactoidCommandException ex) {
                sender.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage(ex.getLangMsg()));
                log.write(Factoid.getLanguage().getMessage(ex.getLangMsg()));
                return true;
            }

            // If the command does not exist
            sender.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.NOTEXIST"));
            return true;

        }


        return false;
    }

    private void doCommandReload(CommandSender sender) throws FactoidCommandException {

        checkBukkitPermission(sender, "factoid.reload", "COMMAND.RELOAD.NOPERMISSION");
        sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.START"));
        Factoid.getThisPlugin().reload();
        sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.COMPLETE"));
    }

    private void doCommandExpand(Player player, ArgList argList) throws FactoidCommandException {

        if (PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.EXPAND.QUIT.FLAGMODE");
        }
        if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.EXPAND.JOIN.SELECTMODE");
        }
        String curArg = argList.getNext();

        if (!PlayerExpanding.containsKey(player.getName().toLowerCase())) {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.JOINMODE", player.getName()));
            LandExpansion expand = new LandExpansion(player, player.getServer(), plugin);
            PlayerExpanding.put(player.getName().toLowerCase(), expand);
        } else if (curArg != null && curArg.equalsIgnoreCase("done")) {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.QUITMODE", player.getName()));
            LandExpansion expand = PlayerExpanding.get(player.getName().toLowerCase());
            expand.setSelected();
            PlayerExpanding.remove(player.getName().toLowerCase());
        } else {
            throw new FactoidCommandException("COMMAND.EXPAND.ALREADY");
        }
    }

    private void doCommandCreate(Player player, ArgList argList) throws FactoidCommandException {

        if (PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.CREATE.QUIT.FLAGMODE");
        }
        if (!PlayerSelectingLand.containsKey(player.getName().toLowerCase()) || PlayerSelectingWorldEdit.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.CREATE.SELECTMODE");
        }
        if (argList.length() < 2) {
            throw new FactoidCommandException("COMMAND.CREATE.NEEDNAME");
        }
        String curArg = argList.getNext();
        if (!BannedWord.contains(curArg)) {

            throw new FactoidCommandException("COMMAND.CREATE.HINTUSE");
        }
        if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
            Land landtest = Factoid.getLands().getLand(curArg);
            if (landtest == null) {
                LandSelection select = PlayerSelectingLand.get(player.getName().toLowerCase());
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
                PlayerSelectingLand.remove(player.getName().toLowerCase());
                select.resetSelection();
            } else {
                player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE", player.getName()));
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
            }
        } else {
            Land landtest = LandSelectioned.get(player.getName().toLowerCase());
            if (landtest == null) {
                LandSelection select = PlayerSelectingLand.get(player.getName().toLowerCase());
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
                PlayerSelectingLand.remove(player.getName().toLowerCase());
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
    }

    private Land getLandSelected(Player player) throws FactoidCommandException {

        if (PlayerExpanding.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.FLAGS.QUIT.EXPANDMODE");

        }
        if (!LandSelectioned.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.FLAGS.JOIN.SELECTMODE");
        }

        if (PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.FLAGS.ALREADY");
        }
        Land land = LandSelectioned.get(player.getName().toLowerCase());

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !isAdminMod(player)) {
            throw new FactoidCommandException("COMMAND.FLAGS.MISSINGPERMISSION");
        }

        return land;
    }

    private void doCommandFlag(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.JOINMODE", player.getName()));
            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
            LandSetFlag setting = new LandSetFlag(player, area);
            PlayerSetFlagUI.put(player.getName().toLowerCase(), setting);
        } else if (curArg.equalsIgnoreCase("set")) {
            //factoid flags set lol true
            LandFlag landFlag = argList.getFlagFromArg();
            land.addFlag(landFlag);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), landFlag.getValueString()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), landFlag.getValueString()));
        } else if (curArg.equalsIgnoreCase("unset")) {
            FlagType flagType = argList.getFlagTypeFromArg();
            if (!land.removeFlag(flagType)) {
                throw new FactoidCommandException("COMMAND.FLAGS.REMOVENOTEXIST"); // ****** AJOUTER dans lang (et les 3 suivants) ********
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
        } else if (curArg.equalsIgnoreCase("list")) {
            StringBuilder stList = new StringBuilder();
            if (!land.getFlags().isEmpty()) {
                for (LandFlag flag : land.getFlags()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.YELLOW).append(flag.getFlagType().toString()).append(":").append(ChatColor.GRAY).append(flag.getValuePrint());
                }
                stList.append("\n");
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROWNULL"));
            }
            createPage("COMMAND.FLAGS.LISTSTART", stList.toString(), player);
        }
    }

    private void doCommandPermission(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("set")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            Permission perm = argList.getPermissionFromArg();
            land.addPermission(pc, perm);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().toString(), perm.getValue() + ""));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.ISDONE", perm.getPermType().toString(), perm.getValue() + ""));
        } else if (curArg.equalsIgnoreCase("unset")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            PermissionType pt = argList.getPermissionTypeFromArg();
            if (!land.removePermission(pc, pt)) {
                throw new FactoidCommandException("COMMAND.PERMISSION.REMOVENOTEXIST"); // ****** AJOUTER dans lang (et les 3 suivants) ********
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
        } else if (curArg.equalsIgnoreCase("list")) {
            StringBuilder stList = new StringBuilder();
            if (!land.getSetPCHavePermission().isEmpty()) {
                for (PlayerContainer pc : land.getSetPCHavePermission()) {
                    stList.append(ChatColor.WHITE).append(pc.getPrint()).append(":");
                    for (Permission perm : land.getPermissionsForPC(pc)) {
                        stList.append(" ").append(ChatColor.YELLOW).append(perm.getPermType().toString()).append(":").append(ChatColor.GRAY).append(perm.getValuePrint());
                    }
                    stList.append("\n");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROWNULL"));
            }
            createPage("COMMAND.PERMISSION.LISTSTART", stList.toString(), player);
        }
    }

    private void doCommandResident(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("add")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            land.addResident(pc);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.RESIDENT.ISDONE", pc.getContainerType().toString(), pc.getName()));
        } else if (curArg.equalsIgnoreCase("remove")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            if (!land.removeResident(pc)) {
                throw new FactoidCommandException("COMMAND.RESIDENT.REMOVENOTEXIST"); // ****** AJOUTER dans lang (et les 3 suivants) ********
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.RESIDENT.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
        } else if (curArg.equalsIgnoreCase("list")) {
            StringBuilder stList = new StringBuilder();
            if (!land.getResidents().isEmpty()) {
                for (PlayerContainer pc : land.getResidents()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append("\n");
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
            }
            createPage("COMMAND.RESIDENT.LISTSTART", stList.toString(), player);
        }
    }

    private void doCommandBanned(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("add")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            land.addBanned(pc);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.BANNED.ISDONE", pc.getContainerType().toString(), pc.getName()));
        } else if (curArg.equalsIgnoreCase("remove")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            if (!land.removeBanned(pc)) {
                throw new FactoidCommandException("COMMAND.BANNED.REMOVENOTEXIST"); // ****** AJOUTER dans lang (et les 3 suivants) ********
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.BANNED.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
        } else if (curArg.equalsIgnoreCase("list")) {
            StringBuilder stList = new StringBuilder();
            if (!land.getBanneds().isEmpty()) {
                for (PlayerContainer pc : land.getBanneds()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append("\n");
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
            }
            createPage("COMMAND.BANNED.LISTSTART", stList.toString(), player);
        }
    }

    private void doCommandRemove(Player player, ArgList argList) throws FactoidCommandException {

        if (!PlayerExpanding.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.REMOVE.QUIT.EXPANDMODE");
        }
        if (LandSelectioned.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.REMOVE.JOIN.SELECTMODE");
        }
        if (!PlayerSetFlagUI.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.REMOVE.QUIT.FLAGSMODE");
        }
        if (!RemoveList.containsKey(player.getName().toLowerCase())) {
            throw new FactoidCommandException("COMMAND.REMOVE.DUPLICATION");
        }
        if (argList.length() < 2) {
            throw new FactoidCommandException("COMMAND.REMOVE.REMOVENULL");
        }
        String curArg = argList.getNext();
        if (curArg.equalsIgnoreCase("land")) {
            RemoveList.put(player.getName().toLowerCase(), "removeland");
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CONFIRM"));
        } else if (curArg.equalsIgnoreCase("area")) {
            RemoveList.put(player.getName().toLowerCase(), "removearea");
        }
    }

    private void doCommandConfirm(Player player) throws FactoidCommandException {

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
        }
    }

    private void doCommandCancel(Player player) throws FactoidCommandException {

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
            LandSelection select = OnCommand.PlayerSelectingLand.get(player.getName().toLowerCase());
            OnCommand.PlayerSelectingLand.remove(player.getName().toLowerCase());
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
        }
    }

    private void doCommandHere(Player player) throws FactoidCommandException {

        Location playerloc = player.getLocation();
        CuboidArea area = Factoid.getLands().getCuboidArea(playerloc);
        landInfo(area, player);
    }

    private void doCommandAdminmod(Player player) throws FactoidCommandException {

        checkBukkitPermission(player, "factoid.adminmod", "COMMAND.ADMINMOD.NOPERMISSION");
        if (isAdminMod(player)) {
            AdminMod.remove(player.getName().toLowerCase());
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.QUIT"));
        } else {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.JOIN"));
            AdminMod.add(player.getName().toLowerCase());
        }
    }

    private void doCommandPage(Player player, ArgList argList) throws FactoidCommandException {

        ChatPage chatPage = chatPageList.get(player);
        int pageNumber;

        if (chatPage == null) {
            throw new FactoidCommandException("COMMAND.PAGE.INVALID");
        }

        String curArg = argList.getNext();

        if (curArg == null) {
            throw new FactoidCommandException("COMMAND.PAGE.PAGENULL");
        }
        try {
            pageNumber = Integer.parseInt(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("COMMAND.PAGE.INVALID");
        }
        chatPage.getPage(pageNumber);
    }

    private static void createPage(String header, String text, Player player) throws FactoidCommandException {

        ChatPage chatPage = new ChatPage(header, text, player);

        if (chatPage.getTotalPages() > 1) {
            chatPageList.put(player, chatPage);
        } else {
            chatPageList.remove(player);
        }
    }

    private void checkBukkitPermission(CommandSender sender, String permission, String langMsgNoPermission) throws FactoidCommandException {

        if (!sender.hasPermission(permission)) {
            throw new FactoidCommandException(langMsgNoPermission);
        }

    }

    public static void landInfo(CuboidArea area, Player player) {

        if (area != null) {

            Land land = area.getLand();
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", ChatColor.GREEN + land.getName() + ChatColor.YELLOW) + "\n");
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", land.getOwner().getPrint()) + "\n");
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.MAINPERMISSION",
                    getPermissionInColForPl(land, player, PermissionType.BUILD) + " "
                    + getPermissionInColForPl(land, player, PermissionType.USE) + " "
                    + getPermissionInColForPl(land, player, PermissionType.OPEN)) + "\n");
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.ACTIVEAREA",
                    "ID: " + area.getKey() + ", " + area.toString()) + "\n");
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA") + "\n");
            for (Map.Entry<Integer, CuboidArea> entry : land.getIdsAndAreas().entrySet()) {
                stList.append("ID: " + entry.getKey() + ", " + entry.getValue() + "\n");
                try {
                    createPage("COMMAND.CURRENT.LAND.LISTSTART", stList.toString(), player);
                } catch (FactoidCommandException ex) {
                    Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
        }

    }

    public static String getPermissionInColForPl(Land land, Player player, PermissionType pt) {
        
        boolean result = land.checkPermissionAndInherit(player.getName(), pt);
        
        if(result) {
            return ChatColor.GREEN + pt.toString();
        } else {
            return ChatColor.RED + pt.toString();
        }
    }
    
    public static boolean isAdminMod(Player player) {

        String playerNameLow = player.getName().toLowerCase();

        if (AdminMod.contains(playerNameLow)) {
            // Check if the player losts his permission
            if (!player.hasPermission("factoid.adminmod")) {
                AdminMod.remove(playerNameLow);
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    public static Map<String, LandSelection> getPlayerSelectingLand() {
        return PlayerSelectingLand;
    }

    public static Map<String, CuboidArea> getPlayerSelectingWorldEdit() {
        return PlayerSelectingWorldEdit;
    }

    public static Map<String, Land> getLandSelectioned() {
        return LandSelectioned;
    }

    public static Map<String, List<LandMakeSquare>> getLandSelectionedUI() {
        return LandSelectionedUI;
    }

    public static Map<String, LandExpansion> getPlayerExpanding() {
        return PlayerExpanding;
    }

    public static Map<String, LandSetFlag> getPlayerSetFlagUI() {
        return PlayerSetFlagUI;
    }

    public static List<String> getAdminMod() {
        return AdminMod;
    }

    public static Map<String, String> getRemoveList() {
        return RemoveList;
    }
}
