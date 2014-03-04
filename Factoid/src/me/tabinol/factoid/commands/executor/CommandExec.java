package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.ChatColor;

public abstract class CommandExec implements CommandInterface {

    protected final CommandEntities entity;
    protected final Land land;
    private boolean isExecutable = true;

    protected CommandExec(CommandEntities entity,
            boolean canFromConsole, boolean needsMoreParameter) throws FactoidCommandException {

        this.entity = entity;

        // Null Entity for an action without command, but don't ask to have information!
        if (entity == null) {
            land = null;
            return;

        } else {

            // get the land Selected or null
            land = entity.playerConf.getLandSelected();
        }

        if (entity.player == null && canFromConsole) {

            // Send a message if this command is player only
            throw new FactoidCommandException("Impossible to do from console", entity.sender, "CONSOLE");
        }

        // Show help if there is no more parameter and the command needs one
        if (needsMoreParameter && entity.argList != null && entity.argList.isLast()) {
            new CommandHelp(entity.sender, entity.command.name()).commandExecute();
            isExecutable = false;
        }
    }

    public boolean isExecutable() {

        return isExecutable;
    }

    // Check for needed selection and not needed (null for no verification)
    protected void checkSelections(Boolean mustBeExpandMode, Boolean mustBeFlagMode,
            Boolean mustBeSelectMode, Boolean mustBeAreaSelected) throws FactoidCommandException {

        // No check if entity is null (if it is not from a command)
        if (entity == null) {
            return;
        }

        // "If" is not in checkSelection to save CPU
        if (mustBeExpandMode != null) {
            checkSelection(entity.playerConf.getExpendingLand() != null, mustBeExpandMode, "GENERAL.QUIT.EXPANDMODE", null);
        }

        if (mustBeExpandMode != null) {
            checkSelection(entity.playerConf.getSetFlagUI() != null, mustBeFlagMode, "GENERAL.QUIT.FLAGMODE", null);
        }
        if (mustBeSelectMode != null) {
            checkSelection(entity.playerConf.getLandSelected() != null, mustBeSelectMode, null, "COMMAND.SELECT.JOIN.SELECTMODE");
        }
        if (mustBeAreaSelected != null) {
            checkSelection(entity.playerConf.getAreaSelection() != null, mustBeAreaSelected, null, "COMMAND.GENERAL.SELECTAREA");
        }
    }

    // Check selection for per type
    private void checkSelection(boolean result, boolean neededResult, String messageTrue, String messageFalse)
            throws FactoidCommandException {

        if (result != neededResult) {
            if (result = true) {
                throw new FactoidCommandException("Player Select", entity.player, messageTrue);
            } else {
                throw new FactoidCommandException("Player Select", entity.player, messageFalse);
            }
        }
    }

    // Check if the player has permission
    protected void checkPermission(boolean mustBeAdminMod, boolean mustBeOwner,
            PermissionType neededPerm, String bukkitPermission) throws FactoidCommandException {

        boolean canDo = false;

        if (mustBeAdminMod && entity.playerConf.isAdminMod()) {
            canDo = true;
        }
        if (mustBeOwner && land.getOwner().hasAccess(entity.playerName)) {
            canDo = true;
        }
        if (neededPerm != null && land.checkPermissionAndInherit(entity.playerName, neededPerm)) {
            canDo = true;
        }
        if (bukkitPermission != null && entity.sender.hasPermission(bukkitPermission)) {
            canDo = true;
        }

        // No permission, this is an exception
        if (canDo == false) {
            throw new FactoidCommandException("No permission to do this action", entity.player, "GENERAL.MISSINGPERMISSION");
        }
    }

    // Why Land paramater? The land can be an other land, not the land stored here.
    protected boolean checkCollision(String landName, Land land, Collisions.LandAction action,
            int removeId, CuboidArea newArea, Land parent, boolean addForApprove) throws FactoidCommandException {

        // allowApprove: false: The command can absolutely not be done if there is error!
        Collisions coll = new Collisions(landName, land, action, removeId, newArea, parent, !addForApprove);
        boolean allowApprove = coll.getAllowApprove();

        if (coll.hasCollisions()) {
            entity.sender.sendMessage(coll.getPrints());

            if (addForApprove) {
                if (Factoid.getConf().getAllowCollision() == Config.AllowCollisionType.APPROVE && allowApprove == true) {
                    entity.sender.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.NEEDAPPROVE", landName));
                    Factoid.getLog().write("land " + landName + " has collision and needs approval.");
                    Factoid.getLands().getApproveList().addApprove(new Approve(landName, action, removeId, newArea,
                            new PlayerContainerPlayer(entity.playerName), parent));
                    return true;
                } else if (Factoid.getConf().getAllowCollision() == Config.AllowCollisionType.FALSE || allowApprove == false) {
                    throw new FactoidCommandException("Land collision", entity.sender, "COLLISION.GENERAL.CANNOTDONE");
                }
            }
        }
        return false;
    }
}
