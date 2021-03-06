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

import org.bukkit.entity.Tameable;
import org.bukkit.inventory.Merchant;

/**
 * The Enum PermissionList.
 *
 * @author Tabinol
 */
public enum PermissionList {

	/** The undefined. */
	UNDEFINED(false),

	/** The build. */
	BUILD(true),

	/** The build place. */
	BUILD_PLACE(true),

	/** The build destroy. */
	BUILD_DESTROY(true),

	/** The drop. */
	DROP(true),

	/** The picketup. */
	PICKETUP(true),

	/** The sleep. */
	SLEEP(true),

	/** The open. */
	OPEN(true),

	/** The open craft. */
	OPEN_CRAFT(true),

	/** The open brew. */
	OPEN_BREW(true),

	/** The open furnace. */
	OPEN_FURNACE(true),

	/** The open chest. */
	OPEN_CHEST(true),

	/** The open trapped chest. */
	OPEN_TRAPPEDCHEST(true),

	/** The open enderchest. */
	OPEN_ENDERCHEST(true),

	/** The open shulker chest. */
	OPEN_SHULKER_BOX(true),

	/** The open beacon. */
	OPEN_BEACON(true),

	/** The open hopper. */
	OPEN_HOPPER(true),

	/** The open dropper. */
	OPEN_DROPPER(true),

	/** The open dispenser. */
	OPEN_DISPENSER(true),

	/** The open jukebox. */
	OPEN_JUKEBOX(true),

	/** The use. */
	USE(true),

	/** The use door. */
	USE_DOOR(true),

	/** The use button. */
	USE_BUTTON(true),

	/** The use lever. */
	USE_LEVER(true),

	/** The use pressureplate. */
	USE_PRESSUREPLATE(true),

	/** The use string. */
	USE_STRING(true),

	/** The use enchanting table **/
	USE_ENCHANTTABLE(true),

	/** The use comparator **/
	USE_COMPARATOR(true),

	/** The use repeater **/
	USE_REPEATER(true),

	/** The use note block **/
	USE_NOTEBLOCK(true),

	/** The use anvil **/
	USE_ANVIL(true),

	/** The mob spawner **/
	USE_MOBSPAWNER(true),

	/** For daylight detector **/
	USE_LIGHTDETECTOR(true),

	/** For {@link Tameable} **/
	USE_TAMEABLE(true),

	/** For trading with a {@link Merchant} **/
	TRADE(true),

	/** The place end crystal **/
	PLACE_END_CRYSTAL(true),

	/** The place end crystal **/
	FROST_WALKER(true),

	/** The animal kill. */
	ANIMAL_KILL(true),

	/** The tamed kill. */
	TAMED_KILL(true),

	/** The mob kill. */
	MOB_KILL(true),

	/** The villager kill. */
	VILLAGER_KILL(true),

	/** The villager golem kill. */
	VILLAGER_GOLEM_KILL(true),

	/** The horse kill. */
	HORSE_KILL(true),

	/** The bucket water. */
	BUCKET_WATER(true),

	/** The bucket lava. */
	BUCKET_LAVA(true),

	/** The fire. */
	FIRE(true),

	/** The auto heal. */
	AUTO_HEAL(false),

	/** The eat. */
	EAT(true),

	/** The food heal. */
	FOOD_HEAL(true),

	/** The potion splash. */
	POTION_SPLASH(true),

	/** The resident manager. */
	RESIDENT_MANAGER(false),

	/** The land create. */
	LAND_CREATE(false),

	/** The land enter. */
	LAND_ENTER(true),

	/** The land remove. */
	LAND_REMOVE(false),

	/** The land kick. */
	LAND_KICK(false),

	/** The land ban. */
	LAND_BAN(false),

	/** The land who. */
	LAND_WHO(false),

	/** The land notify. */
	LAND_NOTIFY(false),

	/** The money deposit. */
	MONEY_DEPOSIT(false),

	/** The money withdraw. */
	MONEY_WITHDRAW(false),

	/** The money balance. */
	MONEY_BALANCE(false),

	/** The eco land for sale. */
	ECO_LAND_FOR_SALE(false),

	/** The eco land buy. */
	ECO_LAND_BUY(false),

	/** The eco land for rent. */
	ECO_LAND_FOR_RENT(false),

	/** The eco land rent. */
	ECO_LAND_RENT(false),

	/**  TP with ender pearl. */
	ENDERPEARL_TP(true),

	/**  The player can teleport itself to the land. */
	TP(false),

	/**  The payer respawn at the land spawn. */
	TP_DEATH(false),

	/** The player is death on enter. */
	LAND_DEATH(false),

	/** The crop trample */
	CROP_TRAMPLE(true),

	/** The god */
	GOD(false);

	/** The base value. */
	final boolean baseValue;

	/** The Permission type. */
	private PermissionType permissionType;

	/**
	 * Instantiates a new permission list.
	 *
	 * @param baseValue the base value
	 */
	private PermissionList(final boolean baseValue) {
		this.baseValue = baseValue;
	}

	/**
	 * Sets the permission type.
	 *
	 * @param permissionType the new permission type
	 */
	void setPermissionType(final PermissionType permissionType) {
		this.permissionType = permissionType;
	}

	/**
	 * Gets the permission type.
	 *
	 * @return the permission type
	 */
	public PermissionType getPermissionType() {
		return permissionType;
	}

}
