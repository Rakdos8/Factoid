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
package me.tabinol.factoid.lands.types;

import me.tabinol.factoidapi.lands.types.IType;

public class Type implements IType, Comparable<Type> {

	private final String typeName;

	protected Type(final String typeName) {
		this.typeName = typeName;
	}

	@Override
	public int compareTo(final Type arg0) {
		return typeName.compareTo(arg0.typeName);
	}

	@Override
	public String getName() {
		return typeName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return typeName != null ? typeName.hashCode() : 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return typeName != null ? typeName.equalsIgnoreCase(String.valueOf(obj)) : false;
	}

}
