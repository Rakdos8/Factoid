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

public enum CommandList {

    RELOAD,
    SELECT,
    EXPAND,
    CREATE,
    AREA,
    OWNER,
    FLAG,
    PERMISSION,
    RESIDENT,
    BAN,
    REMOVE,
    CONFIRM,
    CANCEL,
    INFO,
    ADMINMOD,
    PAGE,
    DEFAULT,
    PRIORITY,
    APPROVE,
    RENAME,
    HELP,
    KICK,
    WHO,
    NOTIFY,
    MONEY;

    // If a command has a second name
    public enum SecondName {

        CURRENT(INFO),
        HERE(INFO);

        public final CommandList mainCommand;

        SecondName(CommandList mainCommand) {

            this.mainCommand = mainCommand;
        }
    }

}
