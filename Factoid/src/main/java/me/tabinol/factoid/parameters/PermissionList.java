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
package me.tabinol.factoid.parameters;

/**
 *
 * @author Tabinol
 */
enum PermissionList {

    UNDEFINED(false),
    BUILD(true),
    BUILD_PLACE(true),
    BUILD_DESTROY(true),
    DROP(true),
    PICKETUP(true),
    SLEEP(true),
    OPEN(true),
    OPEN_CRAFT(true),
    OPEN_BREW(true),
    OPEN_FURNACE(true),
    OPEN_CHEST(true),
    OPEN_ENDERCHEST(true),
    OPEN_BEACON(true),
    OPEN_HOPPER(true),
    OPEN_DROPPER(true),
    USE(true),
    USE_DOOR(true),
    USE_BUTTON(true),
    USE_LEVER(true),
    USE_PRESSUREPLATE(true),
    USE_TRAPPEDCHEST(true),
    USE_STRING(true),
    ANIMAL_KILL(true),
    //ANIMAL_FEED(true),
    //ANIMAL_ACCOUPLE(true),
    TAMED_KILL(true),
    MOB_KILL(true),
    //MOB_HEAL(true),
    VILLAGER_KILL(true),
    VILLAGER_GOLEM_KILL(true),
    HORSE_KILL(true),
    //HORSE_EQUIP(true),
    //HORSE_CLIMB(true),
    BUCKET_WATER(true),
    BUCKET_LAVA(true),
    FIRE(true),
    //TNT(true),
    AUTO_HEAL(false),
    POTION_SPLASH(true),
    RESIDENT_MANAGER(false),
    LAND_CREATE(false),
    LAND_ENTER(true),
    LAND_REMOVE(false),
    LAND_KICK(false),
    LAND_BAN(false),
    LAND_WHO(false),
    LAND_NOTIFY(false),
    MONEY_DEPOSIT(false),
    MONEY_WITHDRAW(false),
    MONEY_BALANCE(false),
    ECO_LAND_FOR_SALE(false),
    ECO_LAND_BUY(false),
    ECO_LAND_FOR_RENT(false),
    ECO_LAND_RENT(false);

    final boolean baseValue;

    private PermissionList(boolean baseValue) {

        this.baseValue = baseValue;
    }
}
