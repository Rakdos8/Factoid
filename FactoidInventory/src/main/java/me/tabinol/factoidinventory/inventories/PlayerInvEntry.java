/*
 FactoidInventory: Minecraft plugin for Inventory change (works with Factoid)
 Copyright (C) 2014  Michel Blanchet

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoidinventory.inventories;

public class PlayerInvEntry {

	private InventorySpec actualInv;
	private boolean isCreativeInv;

	public PlayerInvEntry(final InventorySpec actualInv, final boolean isCreativeInv) {
		this.actualInv = actualInv;
		this.isCreativeInv = isCreativeInv;
	}

	public InventorySpec getActualInv() {
		return actualInv;
	}

	public void setActualInv(final InventorySpec actualInv) {
		this.actualInv = actualInv;
	}

	public boolean isCreativeInv() {
		return isCreativeInv;
	}

	public void setCreativeInv(final boolean isCreativeInv) {
		this.isCreativeInv = isCreativeInv;
	}

}
