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
package me.tabinol.factoid;

import java.util.EnumSet;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * This class is for compatibility to BK 1.7.x
 */
public class BKVersion {

	private static boolean existPlayerInteractAtEntityEvent = false;

	private static GameMode spectatorMode = null;

	private static final EnumSet<Material> doors = EnumSet.noneOf(Material.class);
	private static final EnumSet<Material> buttons = EnumSet.noneOf(Material.class);

	private static Material armorStand = null;

	private static EntityType armorStandEntity = null;

	static void initVersion() {
		// org.bukkit.event.player.PlayerInteractAtEntityEvent (for ArmorStand)
		try {
			final Class<?> plInAtEnEv = Class.forName("org.bukkit.event.player.PlayerInteractAtEntityEvent");
			if (plInAtEnEv != null) {
				existPlayerInteractAtEntityEvent = true;
			}
		} catch (final ClassNotFoundException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.WARNING, "Event (PlayerInteractAtEntityEvent) not found : " + ex.getMessage(), ex);
		}

		// Spectator mode
		try {
			spectatorMode = GameMode.valueOf("SPECTATOR");
		} catch (final IllegalArgumentException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.WARNING, "Invalid GameMode: " + ex.getMessage(), ex);
		}

		// Doors
		try {
			doors.add(Material.SPRUCE_DOOR);
			doors.add(Material.SPRUCE_TRAPDOOR);
			doors.add(Material.SPRUCE_FENCE_GATE);
			doors.add(Material.BIRCH_DOOR);
			doors.add(Material.BIRCH_TRAPDOOR);
			doors.add(Material.BIRCH_FENCE_GATE);
			doors.add(Material.JUNGLE_DOOR);
			doors.add(Material.JUNGLE_TRAPDOOR);
			doors.add(Material.JUNGLE_FENCE_GATE);
			doors.add(Material.ACACIA_DOOR);
			doors.add(Material.ACACIA_TRAPDOOR);
			doors.add(Material.ACACIA_FENCE_GATE);
			doors.add(Material.DARK_OAK_DOOR);
			doors.add(Material.DARK_OAK_TRAPDOOR);
			doors.add(Material.DARK_OAK_FENCE_GATE);
			doors.add(Material.OAK_DOOR);
			doors.add(Material.OAK_TRAPDOOR);
			doors.add(Material.OAK_FENCE_GATE);
			doors.add(Material.IRON_DOOR);
			doors.add(Material.IRON_TRAPDOOR);
		} catch (final IllegalArgumentException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.WARNING, "Invalid door: " + ex.getMessage(), ex);
		}

		// Buttons
		try {
			buttons.add(Material.SPRUCE_BUTTON);
			buttons.add(Material.BIRCH_BUTTON);
			buttons.add(Material.JUNGLE_BUTTON);
			buttons.add(Material.ACACIA_BUTTON);
			buttons.add(Material.DARK_OAK_BUTTON);
			buttons.add(Material.OAK_BUTTON);
		} catch (final IllegalArgumentException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.WARNING, "Invalid button: " + ex.getMessage(), ex);
		}

		// ArmorStand
		try {
			armorStand = Material.ARMOR_STAND;
			armorStandEntity = EntityType.valueOf("ARMOR_STAND");
		} catch (final IllegalArgumentException ex) {
			Factoid.getThisPlugin().getLogger().log(Level.WARNING, "Invalid Material/Entity: " + ex.getMessage(), ex);
		}
	}

	public static boolean isPlayerInteractAtEntityEventExist() {
		return existPlayerInteractAtEntityEvent;
	}

	public static boolean isSpectatorMode(final Player player) {
		return player.getGameMode() == spectatorMode;
	}

	public static boolean isDoor(final Material material) {
		return doors.contains(material);
	}

	public static boolean isButton(final Material material) {
		return buttons.contains(material);
	}

	public static boolean isArmorStand(final Material material) {
		return armorStand != null && material == armorStand;
	}

	public static boolean isArmorStand(final EntityType entityType) {
		return armorStandEntity != null && entityType == armorStandEntity;
	}

}
