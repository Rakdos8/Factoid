package me.tabinol.factoid.storage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfBuilder {

    String newline;
    StringBuffer sb;

    public ConfBuilder(String name) {

        sb = new StringBuffer();
        newline = System.getProperty("line.separator");
        writeParam("Name", name);
    }

    public void writeln(String string) {

        sb.append(string).append(newline);
    }

    public void writeParam(String paramName, String param) {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String paramName, int param) {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String paramName, short param) {

        writeln(paramName + ":" + param);
    }

    public void writeParam(String ParamName, String[] params) {

        if (params == null) {
            return;
        }
        writeln(ParamName + ": {");
        for (String param : params) {
            writeln("  " + param);
        }
        writeln("}");
    }

    public void writeParam(String paramName, ConfBuilder[] cfs) {

        if (cfs == null) {
            return;
        }
        writeln(paramName + ": {");
        for (ConfBuilder cf : cfs) {
            writeln("  {");
            for (String line : cf.getConf().split(newline)) {
                writeln("    " + line);
            }
            writeln("  }");
        }
        writeln("}");
    }

    public String getConf() {

        return sb.toString();
    }

    public void save(String fileName) {
        try {
            FileWriter fr = new FileWriter(fileName, false);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(sb.toString());
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
