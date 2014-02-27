package me.tabinol.factoid.commands;

import me.tabinol.factoid.exceptions.FactoidCommandException;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.create.CommandCreate;
import me.tabinol.factoid.commands.create.CommandCreate.CreateType;

import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.PermissionType;

import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.PlayerConfig;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.Location;

public class OnCommand extends Thread implements CommandExecutor {

    // Represent a Entry for a "/factoid confirm"
    public enum ConfirmType {

        REMOVE_LAND,
        REMOVE_AREA,
        LAND_DEFAULT;
    }

    public static class ConfirmEntry {

        public final ConfirmType confirmType;
        public final Land land;
        public final int areaNb;

        public ConfirmEntry(ConfirmType confirmType, Land land, int areaNb) {

            this.confirmType = confirmType;
            this.land = land;
            this.areaNb = areaNb;
        }
    }

    private static Log log;
    private static JavaPlugin plugin;
    public static final String NEWLINE = System.getProperty("line.separator");
    private static final Map<Player, LandSelection> PlayerSelectingLand = new HashMap<>();
    // private static Map<String, CuboidArea> PlayerSelectingWorldEdit = new HashMap();
    private static final Map<Player, Land> LandSelectioned = new HashMap<>();
    private static final Map<Player, List<LandMakeSquare>> LandSelectionedUI = new HashMap<>();
    private static final Map<Player, LandExpansion> PlayerExpandingLand = new HashMap<>();
    private static final Map<Player, LandSetFlag> PlayerSetFlagUI = new HashMap<>();
    private static final List<String> BannedWord = new ArrayList<>();
    private static final Map<Player, ConfirmEntry> ConfirmList = new HashMap<>();
    private static final Map<Player, ChatPage> chatPageList = new HashMap<>();

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
        BannedWord.add("default");
        BannedWord.add("priority");
        BannedWord.add("null");
        BannedWord.add("approve");
        BannedWord.add("rename");
        log = Factoid.getLog();
        plugin = Factoid.getThisPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        if (cmd.getName().equalsIgnoreCase("factoid") || cmd.getName().equalsIgnoreCase("claim")) {

            ArgList argList = new ArgList(arg, sender);
            try {
                // Check the command to send
                CommandList.getCommand(sender, argList);
                return true;
                // If error on command, send the message to the player
            } catch (FactoidCommandException ex) {
                return true;
            }
        }

        return false;
    }

    protected static void doCommandReload(CommandSender sender) throws FactoidCommandException {

        checkBukkitPermission(sender, "factoid.reload", "GENERAL.MISSINGPERMISSION");
        sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.START"));
        Factoid.getThisPlugin().reload();
        sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RELOAD.COMPLETE"));
    }

    protected static void doCommandExpand(Player player, ArgList argList) throws FactoidCommandException {

        /**
         * *** TO DO : COLLISIONS! ******
         */
        if (PlayerSetFlagUI.containsKey(player)) {
            throw new FactoidCommandException("Player Select", player, "COMMAND.EXPAND.QUIT.FLAGMODE");
        }
        if (!LandSelectioned.containsKey(player)) {
            throw new FactoidCommandException("Player Select", player, "COMMAND.EXPAND.JOIN.SELECTMODE");
        }
        String curArg = argList.getNext();

        if (!PlayerExpandingLand.containsKey(player)) {
            player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            log.write(player.getName() + " have join ExpandMode.");
            LandExpansion expand = new LandExpansion(player, player.getServer(), plugin);
            PlayerExpandingLand.put(player, expand);
        } else if (curArg != null && curArg.equalsIgnoreCase("done")) {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            log.write(player.getName() + " have quit ExpandMode.");
            LandExpansion expand = PlayerExpandingLand.get(player);
            expand.setDone();
            PlayerExpandingLand.remove(player);
        } else {
            throw new FactoidCommandException("Player Expand", player, "COMMAND.EXPAND.ALREADY");
        }
    }

    private static Land getLandSelected(Player player) throws FactoidCommandException {

        Land land;

        if (PlayerExpandingLand.containsKey(player)) {
            throw new FactoidCommandException("Player Select", player, "COMMAND.SELECT.QUIT.EXPANDMODE");

        }
        if (PlayerSetFlagUI.containsKey(player)) {
            throw new FactoidCommandException("Player Select", player, "COMMAND.SELECT.QUIT.FLAGSMODE");
        }
        if (LandSelectioned.containsKey(player)) {
            land = LandSelectioned.get(player);
        } else {
            throw new FactoidCommandException("Player Select", player, "COMMAND.SELECT.JOIN.SELECTMODE");
        }

        return land;
    }

    protected static void doCommandArea(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {

            if (!Factoid.getPlayerConf().isAdminMod(player) && !land.isOwner(player.getName())) {
                throw new FactoidCommandException("Priority", player, "GENERAL.MISSINGPERMISSION");
            }

            new CommandCreate(CreateType.AREA, player, argList, 0);

        } else if (curArg.equalsIgnoreCase("remove") || curArg.equalsIgnoreCase("replace")) {

            String areaNbStr = argList.getNext();
            int areaNb;
            if (!Factoid.getPlayerConf().isAdminMod(player) && !land.isOwner(player.getName())) {
                throw new FactoidCommandException("Priority", player, "GENERAL.MISSINGPERMISSION");
            }
            if (areaNbStr == null) {
                throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.EMPTY");
            }
            try {
                areaNb = Integer.parseInt(areaNbStr);
            } catch (NumberFormatException ex) {
                throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.INVALID");
            }
            if (land.getArea(areaNb) == null) {
                throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.INVALID");
            }

            // Only for a remove
            if (curArg.equalsIgnoreCase("remove")) {

                if (!Factoid.getPlayerConf().isAdminMod(player) && !land.isOwner(player.getName())) {
                    throw new FactoidCommandException("Priority", player, "GENERAL.MISSINGPERMISSION");
                }

                // Check for collision
                if (OnCommand.checkCollision(player, curArg, land, LandAction.AREA_REMOVE, areaNb, null, land.getParent(), true)) {
                    return;
                }

                // Check if exist
                if (land.getArea(areaNb) == null) {
                    throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.INVALID");
                }

                ConfirmList.put(player, new ConfirmEntry(ConfirmType.REMOVE_AREA, LandSelectioned.get(player), areaNb));
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CONFIRM"));

            } else {

                //Only for a replace
                new CommandCreate(CreateType.AREA_REPLACE, player, argList, areaNb);
            }

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
            throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.INVALID");
        }
    }

    protected static void doCommandPrority(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();
        short newPrio;

        if (!Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("Priority", player, "GENERAL.MISSINGPERMISSION");
        }
        if (land.getParent() != null) {
            throw new FactoidCommandException("Priority", player, "COMMAND.PRIORITY.NOTCHILD");
        }
        if (curArg == null) {
            throw new FactoidCommandException("Priority", player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        try {
            newPrio = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("Priority", player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        if (!Calculate.isInInterval(newPrio, Land.MINIM_PRIORITY, Land.MAXIM_PRIORITY)) {
            throw new FactoidCommandException("Priority", player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        land.setPriority(newPrio);
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage(
                "COMMAND.PRIORITY.DONE", land.getName(), land.getPriority() + ""));
        log.write("Priority for land " + land.getName() + " changed for " + land.getPriority());
    }

    protected static void doCommandOwner(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("Owner", player, "GENERAL.MISSINGPERMISSION");
        }

        PlayerContainer pc = argList.getPlayerContainerFromArg(land,
                new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                    PlayerContainerType.OWNER, PlayerContainerType.VISITOR});
        land.setOwner(pc);
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.OWNER.ISDONE", pc.getPrint(), land.getName()));
        log.write("The land " + land.getName() + "is set to owner: " + pc.getPrint());
    }

    protected static void doCommandDefault(Player player) throws FactoidCommandException {

        Land land = getLandSelected(player);

        if (!Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("Command Default", player, "GENERAL.MISSINGPERMISSION");
        }

        ConfirmList.put(player, new ConfirmEntry(ConfirmType.LAND_DEFAULT, LandSelectioned.get(player), 0));
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SETDEFAULT.CONFIRM"));
    }

    protected static void doCommandFlag(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            log.write("PlayerSetFlagUI for " + player.getName());
            player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
            LandSetFlag setting = new LandSetFlag(player, area);
            PlayerSetFlagUI.put(player, setting);
        } else if (curArg.equalsIgnoreCase("set")) {
            //factoid flags set lol true
            LandFlag landFlag = argList.getFlagFromArg(Factoid.getPlayerConf().isAdminMod(player), land.isOwner(player.getName()));
            land.addFlag(landFlag);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), landFlag.getValueString()));
            log.write("Flag set: " + landFlag.getFlagType().toString() + ", value: " + landFlag.getValueString());
        } else if (curArg.equalsIgnoreCase("unset")) {
            FlagType flagType = argList.getFlagTypeFromArg(Factoid.getPlayerConf().isAdminMod(player), land.isOwner(player.getName()));
            if (!land.removeFlag(flagType)) {
                throw new FactoidCommandException("Flags", player, "COMMAND.FLAGS.REMOVENOTEXIST"); // ****** AJOUTER dans lang (et les 3 suivants) ********
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            log.write("Flag unset: " + flagType.toString());
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

    protected static void doCommandPermission(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("set")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land, null);
            Permission perm = argList.getPermissionFromArg(Factoid.getPlayerConf().isAdminMod(player), land.isOwner(player.getName()));
            if (perm.getPermType() == PermissionType.LAND_ENTER
                    && perm.getValue() != perm.getPermType().baseValue()
                    && land.isLocationInside(land.getWord().getSpawnLocation())) {
                throw new FactoidCommandException("Permission", player, "COMMAND.PERMISSION.NOENTERNOTINSPAWN");
            }
            land.addPermission(pc, perm);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().toString(), perm.getValue() + "", land.getName()));
            log.write("Permission set: " + perm.getPermType().toString() + ", value: " + perm.getValue());

        } else if (curArg.equalsIgnoreCase("unset")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land, null);
            PermissionType pt = argList.getPermissionTypeFromArg(Factoid.getPlayerConf().isAdminMod(player), land.isOwner(player.getName()));
            if (!land.removePermission(pc, pt)) {
                throw new FactoidCommandException("Permission", player, "COMMAND.PERMISSION.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
            log.write("Permission unset: " + pt.toString());
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

    protected static void doCommandRename(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("CommandRename", player, "GENERAL.MISSINGPERMISSION");
        }

        if (argList.isLast()) {
            throw new FactoidCommandException("CommandRename", player, "COMMAND.RENAME.NEEDNAME");
        }
        String curArg = argList.getNext();
        if (OnCommand.getBannedWord().contains(curArg.toLowerCase())) {
            throw new FactoidCommandException("CommandRename", player, "COMMAND.RENAME.HINTUSE");
        }

        // Check for collision
        if (OnCommand.checkCollision(player, curArg, land, LandAction.LAND_RENAME, 0, null, land.getParent(), true)) {
            return;
        }

        String oldName = land.getName();

        try {
            Factoid.getLands().renameLand(oldName, curArg);
        } catch (FactoidLandException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "On land rename", ex);
            throw new FactoidCommandException("On land rename", player, "GENERAL.ERROR");
        }
        player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RENAME.ISDONE", oldName, curArg));
        log.write(player.getName() + " has renamed " + oldName + " to " + curArg);
    }

    protected static void doCommandResident(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !Factoid.getPlayerConf().isAdminMod(player)
                && !land.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
            throw new FactoidCommandException("Resident", player, "GENERAL.MISSINGPERMISSION");
        }

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("add")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                        PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                        PlayerContainerType.RESIDENT});

            land.addResident(pc);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getPrint(), land.getName()));
            log.write("Resident added: " + pc.toString());
        } else if (curArg.equalsIgnoreCase("remove")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land, null);
            if (!land.removeResident(pc)) {
                throw new FactoidCommandException("Resident", player, "COMMAND.RESIDENT.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getPrint(), land.getName()));
            log.write("Resident removed: " + pc.toString());
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

    protected static void doCommandBanned(Player player, ArgList argList) throws FactoidCommandException {

        Land land = getLandSelected(player);
        String curArg = argList.getNext();

        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !Factoid.getPlayerConf().isAdminMod(player)
                && !land.checkPermissionAndInherit(player.getName(), PermissionType.LAND_BAN)) {
            throw new FactoidCommandException("Banned", player, "GENERAL.MISSINGPERMISSION");
        }

        // Temporary desactivated
        if (argList.length() < 2 && false) {
        } else if (curArg.equalsIgnoreCase("add")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                        PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                        PlayerContainerType.RESIDENT});
            if (land.isLocationInside(land.getWord().getSpawnLocation())) {
                throw new FactoidCommandException("Banned", player, "COMMAND.BANNED.NOTINSPAWN");
            }
            land.addBanned(pc);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.ISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write("Ban added: " + pc.toString());
        } else if (curArg.equalsIgnoreCase(
                "remove")) {
            PlayerContainer pc = argList.getPlayerContainerFromArg(land, null);
            if (!land.removeBanned(pc)) {
                throw new FactoidCommandException("Banned", player, "COMMAND.BANNED.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.BANNED.REMOVEISDONE", pc.getContainerType().toString(), pc.getName()));
            log.write("Ban removed: " + pc.toString());
        } else if (curArg.equalsIgnoreCase(
                "list")) {
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

    protected static void doCommandRemove(Player player /*, ArgList argList */) throws FactoidCommandException {

        Land land;

        if (PlayerExpandingLand.containsKey(player)) {
            throw new FactoidCommandException("Land Remove", player, "COMMAND.REMOVE.QUIT.EXPANDMODE");
        }
        if (!LandSelectioned.containsKey(player)) {
            throw new FactoidCommandException("Land Remove", player, "COMMAND.REMOVE.JOIN.SELECTMODE");
        }
        land = LandSelectioned.get(player);
        if (PlayerSetFlagUI.containsKey(player)) {
            throw new FactoidCommandException("Land Remove", player, "COMMAND.REMOVE.QUIT.FLAGSMODE");
        }
        if (ConfirmList.containsKey(player)) {
            throw new FactoidCommandException("Land Remove", player, "COMMAND.REMOVE.DUPLICATION");
        }
        if (!player.getName().equalsIgnoreCase(land.getOwner().getName()) && !Factoid.getPlayerConf().isAdminMod(player)) {
            throw new FactoidCommandException("Land Remove", player, "GENERAL.MISSINGPERMISSION");
        }
        if (!land.getChildren().isEmpty()) {
            throw new FactoidCommandException("Land Remove", player, "COMMAND.REMOVE.ISPARENT");
        }

        // Check for collision
        if (OnCommand.checkCollision(player, land.getName(), land, LandAction.LAND_REMOVE, 0, null, land.getParent(), true)) {
            return;
        }

        ConfirmList.put(player, new ConfirmEntry(ConfirmType.REMOVE_LAND, LandSelectioned.get(player), 0));
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.CONFIRM"));
    }

    protected static void doCommandConfirm(Player player) throws FactoidCommandException {

        ConfirmEntry confirmEntry;

        if ((confirmEntry = ConfirmList.get(player)) != null) {
            if (confirmEntry.confirmType == ConfirmType.REMOVE_LAND) {

                // Remove land
                int i = confirmEntry.land.getAreas().size();
                try {
                    Factoid.getLands().removeLand(confirmEntry.land);
                } catch (FactoidLandException ex) {
                    Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "On land remove", ex);
                    throw new FactoidCommandException("On land remove", player, "GENERAL.ERROR");
                }
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", confirmEntry.land.getName(), i + ""));
                log.write(player.getName() + " confirm for removing " + confirmEntry.land.getName());

            } else if (confirmEntry.confirmType == ConfirmType.REMOVE_AREA) {

                // Remove area
                if (!confirmEntry.land.removeArea(confirmEntry.areaNb)) {
                    throw new FactoidCommandException("Area", player, "COMMAND.REMOVE.AREA.INVALID");
                }
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", confirmEntry.land.getName()));
                log.write("area " + confirmEntry.areaNb + " for land " + confirmEntry.land.getName() + " is removed by " + player.getName());

            } else if (confirmEntry.confirmType == ConfirmType.LAND_DEFAULT) {

                // Set to default
                confirmEntry.land.setDefault();
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SETDEFAULT.ISDONE", confirmEntry.land.getName()));
                log.write("The land " + confirmEntry.land.getName() + "is set to default configuration by " + player.getName());
            }
        }
    }

    public static void doCommandCancel(Player player) throws FactoidCommandException {

        if (ConfirmList.remove(player) != null) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Factoid.getLog().write(player.getName() + " cancel for action");
        } else if (PlayerSelectingLand.containsKey(player)) {
            LandSelection select = OnCommand.PlayerSelectingLand.get(player);
            OnCommand.PlayerSelectingLand.remove(player);
            select.resetSelection();
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getLog().write(player.getName() + ": Select cancel");
        } else if (PlayerSetFlagUI.containsKey(player)) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));
            PlayerSetFlagUI.remove(player);
        } else if (LandSelectioned.containsKey(player)) {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));
            LandSelectioned.remove(player);
            if (LandSelectionedUI.containsKey(player)) {
                LandSelectionedUI.remove(player);
            }
        }
    }

    protected static void doCommandHere(Player player) throws FactoidCommandException {

        Location playerloc = player.getLocation();
        CuboidArea area = Factoid.getLands().getCuboidArea(playerloc);
        landInfo(area, player);
    }

    protected static void doCommandAdminmod(Player player) throws FactoidCommandException {

        PlayerConfig pc = Factoid.getPlayerConf();

        checkBukkitPermission(player, "factoid.adminmod", "GENERAL.MISSINGPERMISSION");
        if (pc.isAdminMod(player)) {
            pc.removeAdminMod(player);
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.QUIT"));
        } else {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ADMINMOD.JOIN"));
            pc.addAdminMod(player);
        }
    }

    protected static void doCommandApprove(Player player, ArgList argList) throws FactoidCommandException {

        String curArg = argList.getNext();
        ApproveList approveList = Factoid.getLands().getApproveList();
        boolean isApprover = player.hasPermission("factoid.collisionapprove");

        if (curArg.equalsIgnoreCase("list")) {

            // List of Approve
            StringBuilder stList = new StringBuilder();
            int t = 0;
            for (String approveName : approveList.getApproveList()) {
                Approve app = approveList.getApprove(approveName);
                if (isApprover || app.getOwner().hasAccess(player.getName())) {
                    stList.append(ChatColor.WHITE + Factoid.getLanguage().getMessage("COLLISION.SHOW.LIST",
                            ChatColor.BLUE + app.getLandName() + ChatColor.WHITE,
                            app.getOwner().getPrint() + ChatColor.WHITE,
                            ChatColor.BLUE + app.getAction().toString() + ChatColor.WHITE));
                    stList.append(NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                createPage("COLLISION.SHOW.LISTSTART", stList.toString(), player, null);
            }
        } else if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm") || curArg.equalsIgnoreCase("cancel")) {

            String param = argList.getNext();

            if (param == null) {
                throw new FactoidCommandException("Approve", player, "COLLISION.SHOW.PARAMNULL");
            }

            Approve approve = approveList.getApprove(param);

            // Check permission
            if (approve == null || (curArg.equalsIgnoreCase("confirm") && !isApprover)
                    || ((curArg.equalsIgnoreCase("cancel") || curArg.equalsIgnoreCase("info"))
                    && (!isApprover || !approve.getOwner().hasAccess(player.getName())))) {
                throw new FactoidCommandException("Approve", player, "COLLISION.SHOW.PARAMNULL");
            }

            Land land = Factoid.getLands().getLand(param);
            LandAction action = approve.getAction();
            int removeId = approve.getRemovedAreaId();
            CuboidArea newArea = approve.getNewArea();
            Land parent = approve.getParent();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {

                // Info on the specified land (Collision)
                checkCollision(player, param, land,
                        action, removeId, newArea, parent, false);

                if (curArg.equalsIgnoreCase("confirm")) {

                    // Create the action (if it is possible)
                    approveList.removeApprove(approve);
                    approve.createAction();
                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.DONE"));
                }
            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approveList.removeApprove(approve);
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new FactoidCommandException("Approve", player, "GENERAL.MISSINGPERMISSION");
            }
        }
    }

    protected static void doCommandPage(Player player, ArgList argList) throws FactoidCommandException {

        ChatPage chatPage = chatPageList.get(player);
        int pageNumber;

        if (chatPage == null) {
            throw new FactoidCommandException("Page", player, "COMMAND.PAGE.INVALID");
        }

        String curArg = argList.getNext();

        if (curArg == null) {
            throw new FactoidCommandException("Page", player, "COMMAND.PAGE.PAGENULL");
        }
        try {
            pageNumber = Integer.parseInt(curArg);
        } catch (NumberFormatException ex) {
            throw new FactoidCommandException("Page", player, "COMMAND.PAGE.INVALID");
        }
        chatPage.getPage(pageNumber);
    }

    protected static void createPage(String header, String text, Player player, String param) throws FactoidCommandException {

        ChatPage chatPage = new ChatPage(header, text, player, param);

        if (chatPage.getTotalPages() > 1) {
            chatPageList.put(player, chatPage);
        } else {
            chatPageList.remove(player);
        }
    }

    protected static void checkBukkitPermission(CommandSender sender, String permission, String langMsgNoPermission) throws FactoidCommandException {

        if (!sender.hasPermission(permission)) {
            throw new FactoidCommandException("checkBukkitPermission: " + permission, sender, langMsgNoPermission);
        }

    }

    public static void landInfo(CuboidArea area, Player player) {

        if (area != null) {

            Land land = area.getLand();
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME", ChatColor.GREEN + land.getName() + ChatColor.YELLOW) + NEWLINE);
            stList.append(ChatColor.YELLOW + Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.PRIORITY", land.getPriority() + ""));
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
                Logger.getLogger(OnCommand.class
                        .getName()).log(Level.SEVERE, null, ex);
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

    public static List<String> getBannedWord() {

        return BannedWord;
    }

    public static Map<Player, LandSelection> getPlayerSelectingLand() {
        return PlayerSelectingLand;
    }

    // public static Map<String, CuboidArea> getPlayerSelectingWorldEdit() {
    //    return PlayerSelectingWorldEdit;
    // }
    public static Map<Player, Land> getLandSelectioned() {
        return LandSelectioned;
    }

    public static Map<Player, List<LandMakeSquare>> getLandSelectionedUI() {
        return LandSelectionedUI;
    }

    public static Map<Player, LandExpansion> getPlayerExpandingLand() {
        return PlayerExpandingLand;
    }

    public static Map<Player, LandSetFlag> getPlayerSetFlagUI() {
        return PlayerSetFlagUI;
    }

    public static Map<Player, ConfirmEntry> getConfirmList() {
        return ConfirmList;
    }

    public static Map<Player, ChatPage> getChatPageList() {
        return chatPageList;
    }

    public static boolean checkCollision(Player player, String landName, Land land,
            LandAction action, int removeId, CuboidArea newArea, Land parent, boolean addForApprove) throws FactoidCommandException {

        // allowApprove: false: The command can absolutely not be done if there is error!
        Collisions coll = new Collisions(landName, land, action, removeId, newArea, parent, !addForApprove);
        boolean allowApprove = coll.getAllowApprove();
        if (coll.hasCollisions()) {
            player.sendMessage(coll.getPrints());

            if (addForApprove) {
                if (Factoid.getConf().AllowCollision == Config.AllowCollisionType.APPROVE && allowApprove == true) {
                    player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.NEEDAPPROVE", landName));
                    Factoid.getLog().write("land " + landName + " has collision and needs approval.");
                    Factoid.getLands().getApproveList().addApprove(new Approve(landName, action, removeId, newArea,
                            new PlayerContainerPlayer(player.getName()), parent));
                    return true;
                } else if (Factoid.getConf().AllowCollision == Config.AllowCollisionType.FALSE || allowApprove == false) {
                    throw new FactoidCommandException("Land collision", player, "COLLISION.GENERAL.CANNOTDONE");
                }
            }
        }

        return false;
    }
}
