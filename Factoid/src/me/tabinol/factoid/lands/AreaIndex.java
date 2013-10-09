package me.tabinol.factoid.lands;

import me.tabinol.factoid.lands.CuboidArea;

public class AreaIndex implements Comparable<AreaIndex> {
    
    protected int indexNb;
    protected CuboidArea area;
    
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
