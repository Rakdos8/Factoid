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
package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;

public abstract class CommandExec implements CommandInterface {

    protected final CommandEntities entity;
    protected Land land;
    private boolean isExecutable = true;
    public boolean resetSelectCancel = false; // If reset select cancel is done (1 time only)

    protected CommandExec(CommandEntities entity,
            boolean canFromConsole, boolean needsMoreParameter) throws FactoidCommandException {

        this.entity = entity;

        // Null Entity for an action without command, but don't ask to have information!
        if (entity == null) {
            land = null;
            return;

        } else {

            // get the land Selected or null
            land = entity.playerConf.getSelection().getLand();
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
    protected void checkSelections(Boolean mustBeSelectMode, Boolean mustBeAreaSelected) throws FactoidCommandException {

        // No check if entity is null (if it is not from a command)
        if (entity == null) {
            return;
        }

        // "If" is not in checkSelection to save CPU
/*
         if (mustBeExpandMode != null) {
         checkSelection(entity.playerConf.getExpendingLand() != null, mustBeExpandMode, "GENERAL.QUIT.EXPANDMODE", null, true);
         }

         if (mustBeExpandMode != null) {
         checkSelection(entity.playerConf.getSetFlagUI() != null, mustBeFlagMode, "GENERAL.QUIT.FLAGMODE", null, true);
         }
         */
        if (mustBeSelectMode != null) {
            // Pasted to variable land, can take direcly
            checkSelection(land != null, mustBeSelectMode, null, "GENERAL.JOIN.SELECTMODE",
                    entity != null && entity.playerConf.getSelection().getLand() != null);
        }
        if (mustBeAreaSelected != null) {
            checkSelection(entity.playerConf.getSelection().getCuboidArea() != null, mustBeAreaSelected, null, "GENERAL.JOIN.SELECTAREA", true);
        }
    }

    // Check selection for per type
    private void checkSelection(boolean result, boolean neededResult, String messageTrue, String messageFalse,
            boolean startSelectCancel) throws FactoidCommandException {

        if (result != neededResult) {
            if (result == true) {
                throw new FactoidCommandException("Player Select", entity.player, messageTrue);
            } else {
                throw new FactoidCommandException("Player Select", entity.player, messageFalse);
            }
        } else {
            if (startSelectCancel && !resetSelectCancel && result == true) {

                // Reset autocancel if there is a command executed that need it
                entity.playerConf.setAutoCancelSelect(true);
                resetSelectCancel = true;
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
        if (mustBeOwner && land.getOwner().hasAccess(entity.player)) {
            canDo = true;
        }
        if (neededPerm != null && land.checkPermissionAndInherit(entity.player, neededPerm)) {
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
                            entity.playerConf.getPlayerContainer(), parent));
                    return true;
                } else if (Factoid.getConf().getAllowCollision() == Config.AllowCollisionType.FALSE || allowApprove == false) {
                    throw new FactoidCommandException("Land collision", entity.sender, "COLLISION.GENERAL.CANNOTDONE");
                }
            }
        }
        return false;
    }

    // The name says what it does!!!
    protected void getLandFromCommandIfNoLandSelected() {

        if (land == null && !entity.argList.isLast()) {
            land = Factoid.getLands().getLand(entity.argList.getNext());
        }
    }
}
