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

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.commands.executor.CommandHelp;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoid.playercontainer.PlayerContainerOwner;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.parameters.IPermissionType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;


/**
 * The Class CommandExec.
 */
public abstract class CommandExec {

	/** The entity. */
	protected final CommandEntities entity;

	/** The land. */
	protected ILand land;

	/** The is executable. */
	private boolean isExecutable = true;

	/** The reset select cancel. */
	public boolean resetSelectCancel = false; // If reset select cancel is done (1 time only)

	/**
	 * Instantiates a new command exec.
	 *
	 * @param entity the entity
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected CommandExec(final CommandEntities entity) throws FactoidCommandException {
		this.entity = entity;

		// Null Entity for an action without command, but don't ask to have information!
		if (entity == null) {
			land = null;
			return;
		}

		if (entity.player != null) {
			// get the land Selected or null
			land = entity.playerConf.getSelection().getLand();
		}

		if (entity.player == null && !entity.infoCommand.allowConsole()) {

			// Send a message if this command is player only
			throw new FactoidCommandException("Impossible to do from console", Bukkit.getConsoleSender(), "CONSOLE");
		}

		// Show help if there is no more parameter and the command needs one
		if (entity.infoCommand.forceParameter() && entity.argList != null && entity.argList.isLast()) {
			new CommandHelp(entity.onCommand, entity.sender,
					entity.infoCommand.mainCommand()[0], entity.infoCommand.name()).commandExecute();
			isExecutable = false;
		}
	}

	/**
	 * Command execute.
	 *
	 * @throws FactoidCommandException the factoid command exception
	 */
	public abstract void commandExecute() throws FactoidCommandException;


	/**
	 * Checks if is executable.
	 *
	 * @return true, if is executable
	 */
	public boolean isExecutable() {
		return isExecutable;
	}

	// Check for needed selection and not needed (null for no verification)
	/**
	 * Check selections.
	 *
	 * @param mustBeSelectMode the must be select mode
	 * @param mustBeAreaSelected the must be area selected
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected void checkSelections(final Boolean mustBeSelectMode, final Boolean mustBeAreaSelected) throws FactoidCommandException {
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
	/**
	 * Check selection.
	 *
	 * @param result the result
	 * @param neededResult the needed result
	 * @param messageTrue the message true
	 * @param messageFalse the message false
	 * @param startSelectCancel the start select cancel
	 * @throws FactoidCommandException the factoid command exception
	 */
	private void checkSelection(final boolean result, final boolean neededResult, final String messageTrue, final String messageFalse,
			final boolean startSelectCancel) throws FactoidCommandException {
		if (result != neededResult) {
			if (result == true) {
				throw new FactoidCommandException("Player Select", entity.player, messageTrue);
			}
			throw new FactoidCommandException("Player Select", entity.player, messageFalse);
		}
		if (startSelectCancel && !resetSelectCancel && result == true) {

			// Reset autocancel if there is a command executed that need it
			entity.playerConf.setAutoCancelSelect(true);
			resetSelectCancel = true;
		}
	}

	// Check if the player has permission
	/**
	 * Check permission.
	 *
	 * @param mustBeAdminMod the must be admin mod
	 * @param mustBeOwner the must be owner
	 * @param neededPerm the needed perm
	 * @param bukkitPermission the bukkit permission
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected void checkPermission(final boolean mustBeAdminMod, final boolean mustBeOwner,
			final IPermissionType neededPerm, final String bukkitPermission) throws FactoidCommandException {
		boolean canDo = false;

		if (mustBeAdminMod && entity.playerConf.isAdminMod()) {
			canDo = true;
		}
		if (mustBeOwner && (land == null || (land !=null && new PlayerContainerOwner(land).hasAccess(entity.player)))) {
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
	/**
	 * Check collision.
	 *
	 * @param landName the land name
	 * @param land the land
	 * @param type the type
	 * @param action the action
	 * @param removeId the remove id
	 * @param newArea the new area
	 * @param parent the parent
	 * @param owner the owner of the land (PlayerContainer)
	 * @param price the price
	 * @param addForApprove the add for approve
	 * @return true, if successful
	 * @throws FactoidCommandException the factoid command exception
	 */
	protected boolean checkCollision(
			final String landName,
			final ILand land,
			final IType type,
			final Collisions.LandAction action,
			final int removeId,
			final ICuboidArea newArea,
			final ILand parent,
			final IPlayerContainer owner,
			final double price,
			final boolean addForApprove
	) throws FactoidCommandException {
		// allowApprove: false: The command can absolutely not be done if there is error!
		final Collisions coll = new Collisions(
				landName,
				land,
				action,
				removeId,
				newArea,
				parent,
				owner,
				price,
				!addForApprove
		);
		final boolean allowApprove = coll.getAllowApprove();

		if (coll.hasCollisions()) {
			entity.sender.sendMessage(coll.getPrints());

			if (addForApprove) {
				if (Factoid.getThisPlugin().iConf().getAllowCollision() == Config.AllowCollisionType.APPROVE && allowApprove) {
					entity.sender.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.NEEDAPPROVE", landName));
					Factoid.getThisPlugin().iLog().write("land " + landName + " has collision and needs approval.");
					Factoid.getThisPlugin().iLands().getApproveList().addApprove(new Approve(landName, type, action, removeId, newArea,
							owner, parent, price, Calendar.getInstance()));
					new CommandCancel(entity.playerConf, true).commandExecute();
					return true;
				} else if (Factoid.getThisPlugin().iConf().getAllowCollision() == Config.AllowCollisionType.FALSE || !allowApprove) {
					throw new FactoidCommandException("Land collision", entity.sender, "COLLISION.GENERAL.CANNOTDONE");
				}
			}
		}
		return false;
	}

	/**
	 * Gets the land from command if no land selected.
	 */
	protected void getLandFromCommandIfNoLandSelected() {
		if (land == null && !entity.argList.isLast()) {
			land = Factoid.getThisPlugin().iLands().getLand(entity.argList.getNext());
		}
	}

	/**
	 * Removes the sign from hand.
	 */
	protected void removeSignFromHand() {
		if (entity.player.getGameMode() != GameMode.CREATIVE) {
			if (entity.player.getInventory().getItemInMainHand().getAmount() == 1) {
				entity.player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			} else {
				entity.player.getInventory().getItemInMainHand().setAmount(entity.player.getInventory().getItemInMainHand().getAmount() - 1);
			}
		}
	}
}
