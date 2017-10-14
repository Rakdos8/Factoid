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
package me.tabinol.factoid.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The Class Dates.
 */
public class Dates {

	/** The date at server startup. */
	private static final Date STARTUP_DATE = new Date();

	/**
	 * Date.
	 *
	 * @return the string
	 */
	public static String date() {
		final String dat = new SimpleDateFormat("yyyy-MM-dd").format(STARTUP_DATE);
		return dat;
	}

	/**
	 * Time.
	 *
	 * @return the string
	 */
	public static String time() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
}
