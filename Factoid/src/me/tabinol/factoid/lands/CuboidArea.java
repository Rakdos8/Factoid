package me.tabinol.factoid.lands;

import me.tabinol.factoid.utilities.Calculate;

public class CuboidArea {

    String worldName;
    private int x1, y1, z1, x2, y2, z2;

    public CuboidArea(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        this.worldName = worldName;
        this.x1 = Calculate.lowerInt(x1, x2);
        this.x2 = Calculate.greaterInt(x1, x2);
        this.y1 = Calculate.lowerInt(y1, y2);
        this.y2 = Calculate.greaterInt(y1, y2);
        this.z1 = Calculate.lowerInt(z1, z2);
        this.z2 = Calculate.greaterInt(z1, z2);
    }

    public boolean equals(CuboidArea area2) {

        return worldName.equalsIgnoreCase(area2.worldName)
                && x1 == area2.x1 && y1 == area2.y1 && z1 == area2.z1
                && x2 == area2.x2 && y2 == area2.y2 && z2 == area2.z2;
    }

    public CuboidArea copyOf() {

        return new CuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }

    public boolean isCollision(CuboidArea area2) {

        return ((Calculate.isInInterval(x1, area2.x1, area2.x2)
                || Calculate.isInInterval(area2.x1, x1, x2)))
                && ((Calculate.isInInterval(y1, area2.y1, area2.y2)
                || Calculate.isInInterval(area2.y1, y1, y2)))
                && ((Calculate.isInInterval(z1, area2.z1, area2.z2)
                || Calculate.isInInterval(area2.z1, z1, z2)));
    }
}
