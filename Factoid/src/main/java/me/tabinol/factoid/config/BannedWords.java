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
package me.tabinol.factoid.config;

// TODO: Auto-generated Javadoc
// Banned words for land creation
/**
 * The Enum BannedWords.
 */
public enum BannedWords {

    /** The done. */
    DONE,
    
    /** The worldedit. */
    WORLDEDIT,
    
    /** The expand. */
    EXPAND,
    
    /** The select. */
    SELECT,
    
    /** The remove. */
    REMOVE,
    
    /** The here. */
    HERE,
    
    /** The current. */
    CURRENT,
    
    /** The adminmod. */
    ADMINMOD,
    
    /** The factoid. */
    FACTOID,
    
    /** The console. */
    CONSOLE,
    
    /** The claim. */
    CLAIM,
    
    /** The page. */
    PAGE,
    
    /** The config. */
    CONFIG,
    
    /** The area. */
    AREA,
    
    /** The set. */
    SET,
    
    /** The unset. */
    UNSET,
    
    /** The list. */
    LIST,
    
    /** The default. */
    DEFAULT,
    
    /** The priority. */
    PRIORITY,
    
    /** The null. */
    NULL,
    
    /** The approve. */
    APPROVE,
    
    /** The rename. */
    RENAME;

    // Check if it is a banned word
    /**
     * Checks if is banned word.
     *
     * @param bannedWord the banned word
     * @return true, if is banned word
     */
    public static boolean isBannedWord(String bannedWord) {

        try {
            valueOf(bannedWord.toUpperCase());
        } catch (IllegalArgumentException ex) {

            // If this is not a banned word, the chatch will be throws
            return false;
        }

        // A banned word, return true
        return true;
    }
}
