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
package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FileLoadException;

public class ConfLoader {

    private int version;
    private UUID uuid;
    private String name;
    private String param = null;
    private String value = null;
    private final File file;
    private final BufferedReader br;
    private String actLine = null; // Line read
    private int actLineNb = 0; // Line nb

    public ConfLoader(File file) throws FileLoadException {

        this.file = file;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            // Impossible
        }
        br = new BufferedReader(fr);
        readVersion();
        if(version >= 2) {
            readUUID();
        } else {
            uuid = null;
        }
        readName();
    }

    private void readVersion() throws FileLoadException {

        readParam();
        version = getValueInt();
    }
    
    private void readUUID() throws FileLoadException {
        
        readParam();
        try {
        uuid = UUID.fromString(getValueString());
        } catch(IllegalArgumentException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read UUID.");
        }
    }

    private void readName() throws FileLoadException {

        readParam();
        name = value;

    }

    public String readln() throws FileLoadException {

        String lrt;

        actLineNb++;

        try {
            actLine = br.readLine();
        } catch (IOException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the next line.");
        }

        if (actLine == null) {
            return null;
        }
        lrt = actLine.trim();
        if (lrt.equals("") || lrt.equals("}")) {
            return null;
        }
        Factoid.getLog().write("Readline: " + lrt);
        return lrt;
    }

    public boolean readParam() throws FileLoadException {

        String str = readln();

        if (str == null) {
            return false;
        }
        if (str.endsWith("\\{")) {
            param = str.replaceAll("\\{", "");
            value = null;
        } else if (str.contains(":")) {
            String[] chn = str.split(":", 2);
            param = chn[0];
            if (chn[1].equals("-null-")) {
                value = null;
            } else {
                value = chn[1];
            }
        }

        return true;
    }

    public String getParamName() {

        return param;
    }

    public String getValueString() {

        return value;
    }

    public int getValueInt() throws FileLoadException {

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Integer parameter.");
        }
    }

    public short getValueShort() throws FileLoadException {

        try {
        return Short.parseShort(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Short parameter.");
        }
    }

    public double getValueDouble() throws FileLoadException {

        try {
        return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Double parameter.");
        }
    }

    public String getNextString() throws FileLoadException {

        return readln();
    }

    public String getName() {

        return name;
    }

    public int getVersion() {

        return version;
    }
    
    public UUID getUUID() {
        
        return uuid;
    }

    public String getFileName() {

        return file.getName();
    }

    // Used for errors
    public int getLineNb() {

        return actLineNb;
    }

    // Used for errors
    public String getLine() {

        return actLine;
    }

    public void close() throws FileLoadException {
        try {
            br.close();
        } catch (IOException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Impossible to close the file.");
        }
    }
}
