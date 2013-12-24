package me.tabinol.factoid.commands;

// Work with command arguments
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

public class ArgList {

    private String[] arg;
    private int iterator;

    public ArgList(String[] arg) {

        this.arg = arg;
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

        if (curArg == null) {
            throw new FactoidCommandException("COMMAND.FLAGS.FLAGNULL");
        }

        FlagType flagType = FlagType.getFromString(curArg);

        if (flagType == null) {
            throw new FactoidCommandException("COMMAND.FLAGS.FLAGNULL");
        }

        if (!isAdminmod && !(isOwner && Factoid.getConf().ownerConfigFlag.contains(flagType))) {
            throw new FactoidCommandException("COMMAND.FLAGS.MISSINGPERMISSION");
        }

        return flagType;
    }

    public LandFlag getFlagFromArg(boolean isAdminmob, boolean isOwner) throws FactoidCommandException {

        FlagType flagType = getFlagTypeFromArg(isAdminmob, isOwner);

        if (isLast()) {
            throw new FactoidCommandException("COMMAND.FLAGS.MISSINGINFO");
        }

        LandFlag landFlag;

        if (flagType.getFlagValueType() == FlagValueType.BOOLEAN) {
            boolean BooleanValue = Boolean.parseBoolean(getNext());
            landFlag = new LandFlag(flagType, BooleanValue, true);
        } else if (flagType.getFlagValueType() == FlagValueType.STRING) {
            String StringValue = getNextToEnd();
            landFlag = new LandFlag(flagType, StringValue, true);
        } else if (flagType.getFlagValueType() == FlagValueType.STRING_LIST) {
            ArrayList<String> result = new ArrayList<>();
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
        PlayerContainer pc;

        if (curArg == null) {
            throw new FactoidCommandException("COMMAND.CONTAINERTYPE.NULL");
        }

        PlayerContainerType pcType = PlayerContainerType.getFromString(curArg);

        if (pcType == null) {
            throw new FactoidCommandException("COMMAND.CONTAINERTYPE.INVALID");
        }

        if (bannedPCTList != null) {
            for (PlayerContainerType bPCT : bannedPCTList) {
                if (pcType == bPCT) {
                    throw new FactoidCommandException("COMMAND.CONTAINERTYPE.INVALID");
                }
            }
        }

        if (pcType.hasParameter()) {
            curArg = getNext();
            if (curArg == null) {
                throw new FactoidCommandException("COMMAND.CONTAINER.NULL");
            }
            pc = PlayerContainer.create(land, pcType, curArg);
        } else {
            pc = PlayerContainer.create(land, pcType, "");
        }

        return pc;
    }

    public PermissionType getPermissionTypeFromArg(boolean isAdminmod, boolean isOwner) throws FactoidCommandException {

        String curArg = getNext();

        if (curArg == null) {
            throw new FactoidCommandException("COMMAND.PERMISSIONTYPE.NULL");
        }

        PermissionType pt = PermissionType.getFromString(curArg);

        if (pt == null) {
            throw new FactoidCommandException("COMMAND.PERMISSIONTYPE.INVALID");
        }

        if (!isAdminmod && !(isOwner && Factoid.getConf().ownerConfigPerm.contains(pt))) {
            throw new FactoidCommandException("COMMAND.PERMISSION.MISSINGPERMISSION");
        }

        return pt;
    }

    public Permission getPermissionFromArg(boolean isAdminmod, boolean isOwner) throws FactoidCommandException {

        PermissionType pt = getPermissionTypeFromArg(isAdminmod, isOwner);
        String curArg = getNext();

        if (curArg == null) {
            throw new FactoidCommandException("COMMAND.PERMISSIONVALUE.NULL");
        }

        return new Permission(pt, Boolean.parseBoolean(curArg), true);
    }
}