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

public class Calculate {

    public static int greaterInt(int nb1, int nb2) {

        if (nb1 > nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }

    public static int lowerInt(int nb1, int nb2) {

        if (nb1 < nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }
    
    public static boolean isInInterval(int nbSource, int nb1, int nb2) {
        
        return nbSource >= nb1 && nbSource <= nb2;
    }
    
    // -1 before, 0 inside, +1 after
    public static int comparePosition(int nbSource, int nb1, int nb2) {
        
        if(nbSource < nb1) {
            return -1;
        }
        if(nbSource > nb2) {
            return 1;
        }
        return 0;
    }
    
    public static Double AdditionDouble(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    public static int AdditionInt(int a, int b){
        int t = 0;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    public static boolean getRandomYield(float yield) {
        
        return Math.random() < yield;
    }
}
