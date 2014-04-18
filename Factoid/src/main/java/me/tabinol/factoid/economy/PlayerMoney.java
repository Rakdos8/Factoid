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

import me.tabinol.factoid.Factoid;
import net.milkbowl.vault.economy.Economy;

/**
 * Money from players
 *
 * @author Tabinol
 */
public class PlayerMoney {

    private final Economy economy;

    public PlayerMoney() {

        economy = Factoid.getDependPlugin().getEconomy();
    }
    
    public Double getPlayerBalance(String playerName, String worldName) {
        
        return economy.getBalance(playerName, worldName);
    }
    
    public boolean giveToPlayer(String playerName, String worldName, Double amount) {
        
        return economy.depositPlayer(playerName, worldName, amount).transactionSuccess();
    }

    public boolean getFromPlayer(String playerName, String worldName, Double amount) {
        
        return economy.withdrawPlayer(playerName, worldName, amount).transactionSuccess();
    }
    
    public String toFormat(Double amount) {
        
        return economy.format(amount);
    }
}
