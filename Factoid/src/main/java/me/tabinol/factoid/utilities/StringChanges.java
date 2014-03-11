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
package me.tabinol.factoid.utilities;

import java.util.ArrayList;
import java.util.List;

public class StringChanges {

    public static List<String> toLower(List<String> list) {

        if (list == null) {
            return null;
        }

        ArrayList<String> listLower = new ArrayList<String>();

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
        ArrayList<String> strl = new ArrayList<String>();
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
    
    public static String[] splitAddVoid(String string, String split) {
        
        String[] tlist = string.split(split);
        String[] result = new String[tlist.length + 1];
        for(int t = 0; t < tlist.length; t ++) {
            result[t] = tlist[t];
        }
        result[tlist.length] = "";
        
        return result;
    }
    
    public static int toInteger(String n){
        try{
           return Integer.parseInt(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    public static boolean isInt(String n)
    {
        try{
            Integer.parseInt(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public static double toDouble(String n){
        try{
           return Double.parseDouble(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    public static boolean isDouble(String n)
    {
        try{
            Double.parseDouble(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public static long toLong(String n){
        try{
           return Long.parseLong(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    public static boolean isLong(String n)
    {
        try{
            Long.parseLong(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    public static String FirstUpperThenLower(String str) {
        
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
