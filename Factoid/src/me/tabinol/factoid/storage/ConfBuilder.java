package me.tabinol.factoid.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfBuilder {

    private String newline;
    private StringBuffer sb;

    public ConfBuilder(String name) {

        sb = new StringBuffer();
        newline = System.getProperty("line.separator");
        writeName(name);
    }

    private void writeName(String name) {

        writeParam("Name", name);
    }

    public void writeln(String string) {

        sb.append(string).append(newline);
    }

    public void writeParam(String paramName, String param) {

        if (param == null) {
            writeln(paramName + ":-null-");
        } else {
            writeln(paramName + ":" + param);
        }
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
        writeln(ParamName + "{");
        for (String param : params) {
            writeln("  " + param);
        }
        writeln("}");
    }

    public void writeParam(String paramName, ConfBuilder[] cfs) {

        if (cfs == null) {
            return;
        }
        writeln(paramName + ":conflist{");
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

    public void save(File file) {
        try {
            FileWriter fr = new FileWriter(file, false);
            try (BufferedWriter br = new BufferedWriter(fr)) {
                br.write(sb.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
