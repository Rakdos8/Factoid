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
package me.tabinol.factoid.lands.approve;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.FactoidRunnable;

/**
 * The Class ApproveNotif.
 */
public class ApproveNotif extends FactoidRunnable {

	/** The Constant PERM_APPROVE. */
	public static final String PERM_APPROVE = "factoid.collisionapprove";

	/**
	 * Run approve notif later.
	 */
	public void runApproveNotifLater() {
		final long notifyTime = Factoid.getThisPlugin().iConf().getApproveNotifyTime();

		// Start only if notification is activated in configuration
		if (notifyTime > 0) {
			this.runLater(notifyTime, true);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// If there is some notification to done
		if (!Factoid.getThisPlugin().iLands().getApproveList().getApproveList().isEmpty()) {
			notifyListApprove(Factoid.getThisPlugin().iLands().getApproveList().getApproveList().size());
		}
	}

	/**
	 * Notify for approve.
	 *
	 * @param landName the land name
	 * @param playerName the player name
	 */
	public void notifyForApprove(final String landName, final String playerName) {
		notifyPlayer(Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.NOTIFYLAND", landName, playerName + ChatColor.GREEN));
	}

	/**
	 * Notify list approve.
	 *
	 * @param lstCount the lst count
	 */
	private void notifyListApprove(final int lstCount) {
		notifyPlayer(Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.NOTIFY", lstCount + ""));
	}

	/**
	 * Notify player.
	 *
	 * @param message the message
	 */
	private void notifyPlayer(final String message) {
		for (final Player players : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
			if (players.hasPermission(PERM_APPROVE)) {
				players.sendMessage(ChatColor.GREEN + "[Factoid] " + message);
			}
		}

		Factoid.getThisPlugin().getLogger().log(Level.INFO, "[Factoid] " + message);
	}
}
