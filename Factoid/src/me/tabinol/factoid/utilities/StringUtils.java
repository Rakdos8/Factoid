package me.tabinol.factoid.utilities;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    
    public static List<String> toLower(List<String> list) {
        
        if(list == null) {
            return null;
        }
        
        ArrayList<String> listLower = new ArrayList<>();
        
        for(String str : list) {
            listLower.add(str.toLowerCase());
        }
        
        return listLower;
    }
    
}
