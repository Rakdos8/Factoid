package me.tabinol.factoid.utilities;

import java.util.ArrayList;
import java.util.List;

public class StringChanges {

    public static List<String> toLower(List<String> list) {

        if (list == null) {
            return null;
        }

        ArrayList<String> listLower = new ArrayList<>();

        for (String str : list) {
            listLower.add(str.toLowerCase());
        }

        return listLower;
    }

    public static String toQuote(String str) {

        String strRet;

        if (isStartQuote(str) && isEndQuote(str)) {
            strRet = (new StringBuffer(str).deleteCharAt(str.length() - 1).deleteCharAt(0)).toString();
        } else {
            strRet = str;
        }

        return "'" + strRet.replaceAll("'", "''") + "'";
    }

    public static String fromQuote(String str) {

        if (isStartQuote(str) && isEndQuote(str)) {
            return (new StringBuffer(str).deleteCharAt(str.length() - 1).deleteCharAt(0)).toString().replaceAll("''", "'");
        } else {
            return str;
        }
    }

    public static String[] splitKeepQuote(String str, String split) {

        String[] strs = str.split(split);
        ArrayList<String> strl = new ArrayList<>();
        StringBuffer sb = null;

        for (String strv : strs) {
            if (sb == null) {
                if (isStartQuote(strv)) {
                    sb = new StringBuffer(strv);
                } else {
                    strl.add(strv);
                }
            } else {
                sb.append(split).append(strv);
            }
            if(sb != null && isEndQuote(strv)) {
                strl.add(sb.toString());
                sb = null;
            }
        }
        
        return strl.toArray(new String[0]);
    }

    private static boolean isStartQuote(String str) {

        if (str.startsWith("'") || str.startsWith("\"")) {
            return true;
        }
        return false;
    }

    private static boolean isEndQuote(String str) {

        if (str.endsWith("'") || str.endsWith("\"")) {
            return true;
        }
        return false;
    }
}
