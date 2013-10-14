package me.tabinol.factoid.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;

public class ConfLoader {

    private String name;
    private String param = null;
    private String value = null;
    private BufferedReader br;
    private ConfLoader child;

    public ConfLoader(BufferedReader br) {

        this.br = br;
        readName();
    }

    private void readName() {

        readParam();
        name = value;

    }

    public String readln() {

        String lrt;

        try {
            String lr = br.readLine();
            if (lr == null) {
                return null;
            }
            lrt = lr.trim();
            if (lrt.equals("") || lrt.equals("}")) {
                return null;
            }
            Factoid.getLog().write("Readline: " + lrt);
            return lrt;
        } catch (IOException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean readParam() {

        String str = readln();

        if (str == null) {
            return false;
        }
        if (str.endsWith(":conflist{")) {
            param = str.replaceAll(":conflist\\{", "");
            value = null;
        } else if (str.endsWith("\\{")) {
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

    public int getValueInt() {

        return Integer.parseInt(value);
    }

    public short getValueShort() {

        return Short.parseShort(value);
    }

    public String getNextString() {

        return readln();
    }

    public String getName() {

        return name;
    }

    public void startChild() {

        child = new ConfLoader(br);
    }

    public ConfLoader getChild() {

        return child;
    }
}
