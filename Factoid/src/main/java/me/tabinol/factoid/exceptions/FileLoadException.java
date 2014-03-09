package me.tabinol.factoid.exceptions;

import java.util.logging.Level;
import me.tabinol.factoid.Factoid;

public class FileLoadException extends Exception {

    public FileLoadException(String FileName, String Line, Integer LineNum, String message) {

        super("File Load Exception in:" + FileName);
        Factoid.getLog().write("Error: file: " + FileName + ", Line: " + Line + ", Line Nb: " + LineNum + ", msg: " + message);
        Factoid.getThisPlugin().getLogger().log(Level.SEVERE, "Error! There is an error in file: " + FileName);
        Factoid.getThisPlugin().getLogger().log(Level.SEVERE, "Line: " + LineNum);
        Factoid.getThisPlugin().getLogger().log(Level.SEVERE, Line);
        Factoid.getThisPlugin().getLogger().log(Level.SEVERE, "Error Message: " + message);
    }
}
