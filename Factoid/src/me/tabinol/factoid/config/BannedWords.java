package me.tabinol.factoid.config;

// Banned words for land creation
public enum BannedWords {

    DONE,
    WORLDEDIT,
    EXPAND,
    SELECT,
    REMOVE,
    HERE,
    CURRENT,
    ADMINMOD,
    FACTOID,
    CONSOLE,
    CLAIM,
    PAGE,
    CONFIG,
    AREA,
    SET,
    UNSET,
    LIST,
    DEFAULT,
    PRIORITY,
    NULL,
    APPROVE,
    RENAME;

    // Check if it is a banned word
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
