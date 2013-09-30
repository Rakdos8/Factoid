package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfLoader {
    
    String newline;
    String name;
    String[] configs;
    String param = null;
    String value = null;
    BufferedReader br;

    public ConfLoader(BufferedReader br) {

        this.br = br;
        newline = System.getProperty("line.separator");
        readParam();
        name = value;
    }
    
    public String readln() {
        try {
            return br.readLine().trim();
        } catch (IOException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void readParam() {
        
        String str = readln();
        if()
        
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

    public void close() {
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

    
}
