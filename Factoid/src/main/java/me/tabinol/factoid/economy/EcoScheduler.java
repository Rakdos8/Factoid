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
package me.tabinol.factoid.economy;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

public class EcoScheduler extends BukkitRunnable {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		final Calendar now = Calendar.getInstance();
		// Check for rent renew
		for (final ILand land : Factoid.getThisPlugin().iLands().getForRent()) {
			final long nextPaymentTime = land.getLastPaymentTime().getTime() + (86400000L * land.getRentRenew());
			if (land.isRented() && nextPaymentTime < now.getTimeInMillis()) {
				//Check if the tenant has enough money or time limit whit no auto renew
				if (Factoid.getThisPlugin().iPlayerMoney().getPlayerBalance(land.getTenant().getOfflinePlayer(), land.getWorldName()) < land.getRentPrice()
						|| !land.getRentAutoRenew()) {
					// Unrent
					((Land) land).unSetRented();
					try {
						new EcoSign(
							land,
							land.getRentSignLoc()
						).createSignForRent(
							land.getRentPrice(),
							land.getRentRenew(),
							land.getRentAutoRenew(),
							null
						);
					} catch (final SignException ex) {
						Factoid.getThisPlugin().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
				// Renew rent
				else {
					Factoid.getThisPlugin().iPlayerMoney()
							.getFromPlayer(
									land.getTenant().getOfflinePlayer(),
									land.getWorldName(),
									land.getRentPrice()
							);
					if (land.getOwner() instanceof IPlayerContainerPlayer) {
						Factoid.getThisPlugin().iPlayerMoney()
								.giveToPlayer(
										((IPlayerContainerPlayer)land.getOwner()).getOfflinePlayer(),
										land.getWorldName(),
										land.getRentPrice()
								);
					}
					((Land) land).setLastPaymentTime(new Timestamp(now.getTime().getTime()));
				}
			}
		}
	}
}
