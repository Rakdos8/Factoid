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
import me.tabinol.factoid.commands.create.Create;
import me.tabinol.factoid.commands.create.Create.CreateType;

import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.flags.FlagType;
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
    public static final String NEWLINE = System.getProperty("line.separator");
    private static Map<String, LandSelection> PlayerSelectingLand = new HashMap();
    // private static Map<String, CuboidArea> PlayerSelectingWorldEdit = new HashMap();
    private static Map<String, Land> LandSelectioned = new HashMap();
    private static Map<String, List<LandMakeSquare>> LandSelectionedUI = new HashMap();
    private static Map<String, LandExpansion> PlayerExpanding = new HashMap();
    private static Map<String, LandSetFlag> PlayerSetFlagUI = new HashMap();
    private static List<String> BannedWord = new ArrayList<>();
    private static List<String> AdminMod = new ArrayList<>();
    private static Map<String, String> RemoveList = new HashMap();
    private static Map<Player, ChatPage> chatPageList = new HashMap();
    private static Map<String, Land> landSelectConfig = new HashMap(); //Select for flags/permisson/ban/etc. Not for resize

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
        BannedWord.add("config");
        BannedWord.add("area");
        BannedWord.add("set");
        BannedWord.add("unset");
        BannedWord.add("list");
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
                    new Select(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("expand")) {
                    doCommandExpand(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("create")) {
                    new Create(CreateType.LAND, player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("area")) {
                    doCommandArea(player, argList);
                    return true;
                }

                if (curArg.equalsIgnoreCase("owner")) {
                    doCommandOwner(player, argList);
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
                if (Factoid.getLanguage().isMessageExist("LOG." + ex.getLangMsg())) {
                    log.write(Factoid.getLanguage().getMessage("LOG." + ex.getLangMsg()));
                }
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

    private Land getLandSelected(Player player) throws FactoidCommandException {

        Land land;
        String playerNameLower = player.getName().toLowerCase();

        if (PlayerExpanding.containsKey(playerNameLower)) {
            throw new FactoidCommandException("COMMAND.GENERAL.QUIT.EXPANDMODE");

        }
        if (PlayerSetFlagUI.containsKey(playerNameLower)) {
            throw new FactoidCommandException("COMMAND.GENERAL.ALREADY");
        }
        if (LandSelectioned.containsKey(playerNameLower)) {
            land = LandSelectioned.get(playerNameLower);
        } else if (landSelectConfig.containsKey(playerNameLower)) {
            land = landSelectConfig.get(playerNameLower);
        } else {
            throw new FactoidCommandException("COMMAND.GENERAL.JOIN.SELECTMODE");
        }

        return land;
    }

    private void doCommandArea(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {

            new Create(CreateType.AREA, player, argList);

        } else if (curArg.equalsIgnoreCase("remove")) {

            curArg = argList.getNext();
            int areaNb;
            if (curArg == null) {
                throw new FactoidCommandException("COMMAND.REMOVE.AREA.EMPTY");
            }
            try {
                areaNb = Integer.parseInt(curArg);
            } catch (NumberFormatException ex) {
                throw new FactoidCommandException("COMMAND.REMOVE.AREA.INVALID");
            }

            // Remove area
            if (land.removeArea(areaNb) == false) {
                throw new FactoidCommandException("COMMAND.REMOVE.AREA.INVALID");
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", land.getName()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.DONE.AREA", land.getName()));
            
        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            for (Map.Entry<Integer, CuboidArea> entry : land.getIdsAndAreas().entrySet()) {
                stList.append("ID: " + entry.getKey() + ", " + entry.getValue().getPrint() + NEWLINE);
            }
            try {
                createPage("COMMAND.CURRENT.LAND.AREA", stList.toString(), player, land.getName());
            } catch (FactoidCommandException ex) {
                Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new FactoidCommandException("COMMAND.REMOVE.AREA.INVALID");
        }
    }

    private void doCommandOwner(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !isAdminMod(player)) {
            throw new FactoidCommandException("COMMAND.OWNER.MISSINGPERMISSION");
        }

        PlayerContainer pc = argList.getPlayerContainerFromArg(land);
        land.setOwner(pc);
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.OWNER.ISDONE", pc.getPrint(), land.getName()));
        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.OWNER.ISDONE", pc.getPrint(), land.getName()));
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
            LandFlag landFlag = argList.getFlagFromArg(isAdminMod(player), land.isOwner(player.getName()));
            land.addFlag(landFlag);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), landFlag.getValueString()));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), landFlag.getValueString()));
        } else if (curArg.equalsIgnoreCase("unset")) {
            FlagType flagType = argList.getFlagTypeFromArg(isAdminMod(player), land.isOwner(player.getName()));
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
                    stList.append(ChatColor.YELLOW).append(flag.getFlagType().toString()).append(":").append(flag.getValuePrint());
                }
                stList.append(NEWLINE);
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.LISTROWNULL"));
            }
            createPage("COMMAND.FLAGS.LISTSTART", stList.toString(), player, land.getName());
        }
    }

    private void doCommandPermission(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("set")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            Permission perm = argList.getPermissionFromArg(isAdminMod(player), land.isOwner(player.getName()));
            land.addPermission(pc, perm);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().toString(), perm.getValue() + ""));
            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.PERMISSION.ISDONE", perm.getPermType().toString(), perm.getValue() + ""));
        } else if (curArg.equalsIgnoreCase("unset")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land);
            PermissionType pt = argList.getPermissionTypeFromArg(isAdminMod(player), land.isOwner(player.getName()));
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
                        stList.append(" ").append(ChatColor.YELLOW).append(perm.getPermType().toString()).append(":").append(perm.getValuePrint());
                    }
                    stList.append(NEWLINE);
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.LISTROWNULL"));
            }
            createPage("COMMAND.PERMISSION.LISTSTART", stList.toString(), player, land.getName());
        }
    }

    private void doCommandResident(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !isAdminMod(player)
                && !land.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
            throw new FactoidCommandException("COMMAND.RESIDENT.MISSINGPERMISSION");
        }

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
                stList.append(NEWLINE);
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
            }
            createPage("COMMAND.RESIDENT.LISTSTART", stList.toString(), player, land.getName());
        }
    }

    private void doCommandBanned(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !isAdminMod(player)
                && !land.checkPermissionAndInherit(player.getName(), PermissionType.LAND_BAN)) {
            throw new FactoidCommandException("COMMAND.BANNED.MISSINGPERMISSION");
        }

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
                stList.append(NEWLINE);
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
            }
            createPage("COMMAND.BANNED.LISTSTART", stList.toString(), player, land.getName());
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

    private static void createPage(String header, String text, Player player, String param) throws FactoidCommandException {

        ChatPage chatPage = new ChatPage(header, text, player, param);

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
            landSelectConfig.put(player.getName().toLowerCase(), land);
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", ChatColor.GREEN + land.getName() + ChatColor.YELLOW) + NEWLINE);
            if (land.getParent() != null) {
                stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.PARENT", land.getParent().getName()) + NEWLINE);
            }
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER", land.getOwner().getPrint()) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.MAINPERMISSION",
                    getPermissionInColForPl(land, player, PermissionType.BUILD) + " "
                    + getPermissionInColForPl(land, player, PermissionType.USE) + " "
                    + getPermissionInColForPl(land, player, PermissionType.OPEN)) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.ACTIVEAREA",
                    "ID: " + area.getKey() + ", " + area.getPrint()) + NEWLINE);
            try {
                createPage("COMMAND.CURRENT.LAND.LISTSTART", stList.toString(), player, land.getName());
            } catch (FactoidCommandException ex) {
                Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
        }

    }

    public static String getPermissionInColForPl(Land land, Player player, PermissionType pt) {

        boolean result = land.checkPermissionAndInherit(player.getName(), pt);

        if (result) {
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

    public static List<String> getBannedWord() {

        return BannedWord;
    }

    public static Map<String, LandSelection> getPlayerSelectingLand() {
        return PlayerSelectingLand;
    }

    // public static Map<String, CuboidArea> getPlayerSelectingWorldEdit() {
    //    return PlayerSelectingWorldEdit;
    // }
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

    public static Map<Player, ChatPage> getChatPageList() {
        return chatPageList;
    }

    public static Map<String, Land> getLandSelectConfig() {
        return landSelectConfig;
    }
}
