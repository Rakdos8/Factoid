/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.commands;

// Work with command arguments
import me.tabinol.factoid.exceptions.FactoidCommandException;
import java.util.ArrayList;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.flags.FlagValueType;
import me.tabinol.factoid.lands.flags.LandFlag;
import me.tabinol.factoid.lands.permissions.Permission;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.command.CommandSender;

public class ArgList {

    private final String[] arg;
    private int iterator;
    private final CommandSender player;

    public ArgList(String[] arg, CommandSender player) {

        this.arg = arg;
        this.player = player;
        iterator = -1;
    }

    public String getNext() {

        iterator++;
        return getCur();
    }

    public String getCur() {

        if (iterator >= arg.length) {
            return null;
        }
        if (iterator < 0) {
            iterator = 0;
        }

        return arg[iterator];
    }

    public int getPos() {

        return iterator;
    }

    public void setPos(int iterator) {

        this.iterator = iterator;
    }

    public boolean isLast() {

        return iterator == arg.length - 1;
    }

    public int length() {

        return iterator;
    }

    public String getNextToEnd() {

        StringBuilder result = new StringBuilder();
        String cur;

        while ((cur = getNext()) != null) {
            if (result.length() != 0) {
                result.append(" ");
            }
            result.append(cur);
        }

        return result.toString();
    }

    public FlagType getFlagTypeFromArg(boolean isAdminmod, boolean isOwner) throws FactoidCommandException {

        String curArg = getNext();
        FlagType flagType;

        if (curArg == null) {
            throw new FactoidCommandException("Flag error", player, "COMMAND.FLAGS.FLAGNULL");
        }

        try {
            flagType = FlagType.valueOf(curArg.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new FactoidCommandException("Flag error", player, "COMMAND.FLAGS.FLAGNULL");
        }

        if (!isAdminmod && !(isOwner && Factoid.getConf().getOwnerConfigFlag().contains(flagType))) {
            throw new FactoidCommandException("Flag error", player, "GENERAL.MISSINGPERMISSION");
        }

        return flagType;
    }

    public LandFlag getFlagFromArg(boolean isAdminmob, boolean isOwner) throws FactoidCommandException {

        FlagType flagType = getFlagTypeFromArg(isAdminmob, isOwner);

        if (isLast()) {
            throw new FactoidCommandException("Flag error", player, "GENERAL.MISSINGINFO");
        }

        LandFlag landFlag;

        if (flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            boolean BooleanValue = Boolean.parseBoolean(getNext());
            landFlag = new LandFlag(flagType, BooleanValue, true);
        } else if (flagType.getFlagValueType() == FlagValueType.STRING) {
            String StringValue = getNextToEnd();
            landFlag = new LandFlag(flagType, StringValue, true);
        } else if (flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            ArrayList<String> result = new ArrayList<String>();
            String[] strs = StringChanges.splitKeepQuote(getNext(), ";");
            for (String str : strs) {
                result.add(StringChanges.fromQuote(str));
            }
            String[] StringArrayValue = result.toArray(new String[0]);
            landFlag = new LandFlag(flagType, StringArrayValue, true);
        } else {
            landFlag = null;
        }

        return landFlag;
    }

    public PlayerContainer getPlayerContainerFromArg(Land land,
            PlayerContainerType[] bannedPCTList) throws FactoidCommandException {

        String curArg = getNext();
        String param = null;
        PlayerContainer pc;

        if (curArg == null) {
            throw new FactoidCommandException("PlayerContainer Error", player, "COMMAND.CONTAINERTYPE.TYPENULL");
        }

        PlayerContainerType pcType = PlayerContainerType.getFromString(curArg);

        if (pcType == null) {
            // Type player if it is the player directly
            pcType = PlayerContainerType.PLAYER;
            param = curArg;
        }

        if (bannedPCTList != null) {
            for (PlayerContainerType bPCT : bannedPCTList) {
                if (pcType == bPCT) {
                    throw new FactoidCommandException("PlayerContainer Error", player, "COMMAND.CONTAINERTYPE.NOTPERMITTED");
                }
            }
        }

        if (pcType.hasParameter()) {
            if (param == null) {
                param = getNext();
            }
            if (param == null) {
                throw new FactoidCommandException("PlayerContainer Error", player, "COMMAND.CONTAINER.CONTAINERNULL");
            }
            pc = PlayerContainer.create(land, pcType, param);
        } else {
            pc = PlayerContainer.create(land, pcType, "");
        }

        return pc;
    }

    public PermissionType getPermissionTypeFromArg(boolean isAdminmod, boolean isOwner) throws FactoidCommandException {

        String curArg = getNext();
        PermissionType pt;

        if (curArg == null) {
            throw new FactoidCommandException("Permission Error", player, "COMMAND.PERMISSIONTYPE.TYPENULL");
        }

        try {
            pt = PermissionType.valueOf(curArg.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new FactoidCommandException("Permission Error", player, "COMMAND.PERMISSIONTYPE.INVALID");
        }

        if (!isAdminmod && !(isOwner && Factoid.getConf().getOwnerConfigPerm().contains(pt))) {
            throw new FactoidCommandException("Permission Error", player, "GENERAL.MISSINGPERMISSION");
        }

        return pt;
    }

    public Permission getPermissionFromArg(boolean isAdminmod, boolean isOwner) throws FactoidCommandException {

        PermissionType pt = getPermissionTypeFromArg(isAdminmod, isOwner);
        String curArg = getNext();

        if (curArg == null) {
            throw new FactoidCommandException("Permission Error", player, "COMMAND.PERMISSIONVALUE.VALUENULL");
        }

        return new Permission(pt, Boolean.parseBoolean(curArg), true);
    }
}
