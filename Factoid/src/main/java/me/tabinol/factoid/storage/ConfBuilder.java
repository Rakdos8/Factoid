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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfBuilder {

    private final BufferedWriter br;

    public ConfBuilder(String name, File file, int version) throws IOException {

        FileWriter fr = new FileWriter(file, false);
        br = new BufferedWriter(fr);
        writeVersion(version);
        writeName(name);
    }

    private void writeVersion(int version) throws IOException {

        writeParam("Version", version);
    }

    private void writeName(String name) throws IOException {

        writeParam("Name", name);
    }

    public void writeln(String string) throws IOException {

        br.write(string);
        br.newLine();
    }

    public void writeParam(String paramName, String param) throws IOException {

        if (param == null) {
            writeln(paramName + ":-null-");
        } else {
            writeln(paramName + ":" + param);
        }
    }

    public void writeParam(String paramName, int param) throws IOException {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String paramName, short param) throws IOException {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String paramName, double param) throws IOException {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String ParamName, String[] params) throws IOException {

        if (params == null) {
            return;
        }
        writeln(ParamName + "{");
        for (String param : params) {
            writeln("  " + param);
        }
        writeln("}");
    }

    public void close() throws IOException {
        
        br.close();
    }
}
