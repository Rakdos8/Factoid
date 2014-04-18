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
import me.tabinol.factoid.economy.PlayerMoney;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.permissions.PermissionType;
import org.bukkit.ChatColor;

public class CommandMoney extends CommandExec {

    private final PlayerMoney playerMoney;

    public CommandMoney(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
        playerMoney = Factoid.getPlayerMoney();
    }

    public void commandExecute() throws FactoidCommandException {

        if (playerMoney == null) {

            throw new FactoidCommandException("Economy not avalaible", entity.player, "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        checkSelections(true, null);

        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("balance")) {
            balance();
        } else if (curArg.equalsIgnoreCase("deposit")) {
            deposit();
        } else if (curArg.equalsIgnoreCase("withdraw")) {
            withdraw();
        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    private void balance() throws FactoidCommandException {

        checkPermission(true, false, PermissionType.MONEY_BALANCE, null);
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDBALANCE",
                land.getName(), playerMoney.toFormat(land.getMoney())));
    }

    private void deposit() throws FactoidCommandException {

        checkPermission(true, false, PermissionType.MONEY_DEPOSIT, null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > playerMoney.getPlayerBalance(entity.playerName, land.getWorldName())) {
            throw new FactoidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        playerMoney.getFromPlayer(entity.playerName, land.getWorldName(), amount);
        land.addMoney(amount);
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDDEPOSIT",
                playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    private void withdraw() throws FactoidCommandException {

        checkPermission(true, false, PermissionType.MONEY_WITHDRAW, null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > land.getMoney()) {
            throw new FactoidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        land.substractMoney(amount);
        playerMoney.giveToPlayer(entity.playerName, land.getWorldName(), amount);
        entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDWITHDRAW",
                playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    private double getAmountFromCommandLine() throws FactoidCommandException {

        double ret = 0;
        boolean err = false;

        try {
            ret = Double.parseDouble(entity.argList.getNext());
            if (ret <= 0) {
                // Amount is 0 or less
                err = true;
            }
        } catch (NullPointerException ex) {
            // Amount is null
            err = true;
        } catch (NumberFormatException ex) {
            // Amount is unreadable
            err = true;
        }

        if (err) {
            throw new FactoidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        return ret;
    }
}
