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
package me.tabinol.factoid.config.players;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Entries for each player
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.selection.PlayerSelection;
import me.tabinol.factoidapi.config.players.IPlayerConfEntry;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;


/**
 * The Class PlayerConfEntry.
 */
public class PlayerConfEntry implements IPlayerConfEntry {

	/** The sender. */
	private final CommandSender sender; // The player (or sender)

	/** The player. */
	private final Player player; // The player (if is not console)

	/** The player selection. */
	private final PlayerSelection playerSelection; // Player Lands, areas and visual selections

	/** The admin mod. */
	private boolean adminMod = false; // If the player is in Admin Mod

	/** The confirm. */
	private ConfirmEntry confirm = null; // "/factoid confirm" command

	/** The chat page. */
	private ChatPage chatPage = null; // pages for "/factoid page" command

	/** The last move update. */
	private long lastMoveUpdate = 0; // Time of lastupdate for PlayerEvents

	/** The last land. */
	private IDummyLand lastLand = null; // Last Land for player

	/** The last loc. */
	private Location lastLoc = null; // Present location

	/** The tp cancel. */
	private boolean tpCancel = false; // If the player has a teleportation cacelled

	/** The cancel select. */
	private PlayerAutoCancelSelect cancelSelect = null; // Auto cancel selection system

	/** The pcp. */
	private IPlayerContainerPlayer pcp; // PlayerContainerPlayer for this player

	/**
	 * Instantiates a new player conf entry.
	 *
	 * @param sender the sender
	 */
	PlayerConfEntry(final CommandSender sender) {
		this.sender = sender;
		if (sender instanceof Player) {
			player = (Player) sender;
			playerSelection = new PlayerSelection(this);
			pcp = new PlayerContainerPlayer(player.getUniqueId());
		} else {
			player = null;
			playerSelection = null;
			pcp = null;
		}
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getPlayerContainer()
	 */
	@Override
	public IPlayerContainerPlayer getPlayerContainer() {
		return pcp;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getSender()
	 */
	@Override
	public CommandSender getSender() {
		return sender;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getPlayer()
	 */
	@Override
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the selection.
	 *
	 * @return the selection
	 */
	public PlayerSelection getSelection() {
		return playerSelection;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#isAdminMod()
	 */
	@Override
	public boolean isAdminMod() {
		// Security for adminmod
		if (adminMod == true && !sender.hasPermission("factoid.adminmod")) {
			adminMod = false;
			return false;
		}

		return adminMod;
	}

	/**
	 * Sets the admin mod.
	 *
	 * @param value the new admin mod
	 */
	public void setAdminMod(final boolean value) {
		adminMod = value;
	}

	/**
	 * Gets the confirm.
	 *
	 * @return the confirm
	 */
	public ConfirmEntry getConfirm() {
		return confirm;
	}

	/**
	 * Sets the confirm.
	 *
	 * @param entry the new confirm
	 */
	public void setConfirm(final ConfirmEntry entry) {
		confirm = entry;
	}

	/**
	 * Gets the chat page.
	 *
	 * @return the chat page
	 */
	public ChatPage getChatPage() {
		return chatPage;
	}

	/**
	 * Sets the chat page.
	 *
	 * @param page the new chat page
	 */
	public void setChatPage(final ChatPage page) {
		chatPage = page;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getLastMoveUpdate()
	 */
	@Override
	public long getLastMoveUpdate() {

		return lastMoveUpdate;
	}

	/**
	 * Sets the last move update.
	 *
	 * @param lastMove the new last move update
	 */
	public void setLastMoveUpdate(final Long lastMove) {
		lastMoveUpdate = lastMove;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getLastLand()
	 */
	@Override
	public IDummyLand getLastLand() {
		return lastLand;
	}

	/**
	 * Sets the last land.
	 *
	 * @param land the new last land
	 */
	public void setLastLand(final IDummyLand land) {
		lastLand = land;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.config.players.IPlayerConfEntry#getLastLoc()
	 */
	@Override
	public Location getLastLoc() {
		return lastLoc;
	}

	/**
	 * Sets the last loc.
	 *
	 * @param loc the new last loc
	 */
	public void setLastLoc(final Location loc) {
		lastLoc = loc;
	}

	/**
	 * Checks for tp cancel.
	 *
	 * @return true, if successful
	 */
	public boolean hasTpCancel() {
		return tpCancel;
	}

	/**
	 * Sets the tp cancel.
	 *
	 * @param tpCancel the new tp cancel
	 */
	public void setTpCancel(final boolean tpCancel) {
		this.tpCancel = tpCancel;
	}

	// Set auto cancel select
	/**
	 * Sets the auto cancel select.
	 *
	 * @param value the new auto cancel select
	 */
	public void setAutoCancelSelect(final boolean value) {

		final Long timeTick = Factoid.getThisPlugin().iConf().getSelectAutoCancel();

		if (timeTick == 0) {
			return;
		}

		if (cancelSelect == null && value == true) {
			cancelSelect = new PlayerAutoCancelSelect(this);
		}

		if (cancelSelect == null) {
			return;
		}

		if (value == true) {
			// Schedule task
			cancelSelect.runLater(timeTick, false);
		} else {

			// Stop!
			cancelSelect.stopNextRun();
		}
	}
}
