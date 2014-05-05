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
package me.tabinol.factoid.parameters;

import org.bukkit.ChatColor;

/**
 *
 * @author michel
 */
public class ParameterType implements Comparable<ParameterType> {
    
    private final String name;
    private boolean isRegistered = false;
    
    ParameterType(String name) {
        
        this.name = name;
    }
    
    @Override
    public int compareTo(ParameterType t) {
        
        return name.compareTo(t.name);
    }
    
    public boolean equals(ParameterType t) {
        
        return name.equals(t.name);
    }
    
    public String getName() {
        
        return name;
    }
    
    @Override
    public String toString() {
        
        return name;
    }
    
    public String getPrint() {
        
        if(isRegistered) {
            return ChatColor.YELLOW + name;
        } else {
            return ChatColor.DARK_GRAY + name;
        }
    }
    
    public boolean isRegistered() {
        
        return isRegistered;
    }
    
    void setRegistered() {
        
        isRegistered = true;
    }
}
