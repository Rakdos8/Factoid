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
package me.tabinol.factoid.lands.areas;

public class AreaIndex implements Comparable<AreaIndex> {
    
    private int indexNb;
    private CuboidArea area;
    
    public AreaIndex(int indexNb, CuboidArea area) {
        
        this.indexNb = indexNb;
        this.area = area;
    }

    public boolean equals(AreaIndex index2) {
        
        return indexNb == index2.indexNb && area == index2.area;
    }
    
    public AreaIndex copyOf() {
        
        return new AreaIndex(indexNb, area);
    }
    
    @Override
    public int compareTo(AreaIndex t) {
        if(indexNb < t.indexNb) {
            return -1;
        }
        if(indexNb > t.indexNb) {
            return 1;
        }
        return area.compareTo(t.area);
    }
    
    public int getIndexNb() {
        
        return indexNb;
    }
    
    public CuboidArea getArea() {
        
        return area;
    }
}
