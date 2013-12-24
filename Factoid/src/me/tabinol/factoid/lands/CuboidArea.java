package me.tabinol.factoid.lands;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.Calculate;
import org.bukkit.Location;
import org.bukkit.World;

public class CuboidArea implements Comparable<CuboidArea> {

    private String worldName;
    private int x1, y1, z1, x2, y2, z2;
    private Land land = null;

    public CuboidArea(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {

        this.worldName = worldName.toLowerCase();
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

    @Override
    public int compareTo(CuboidArea t) {

        int worldCompare = worldName.compareTo(t.worldName);
        if (worldCompare != 0) {
            return worldCompare;
        }
        if (x1 < t.x1) {
            return -1;
        }
        if (x1 > t.x1) {
            return 1;
        }
        if (z1 < t.z1) {
            return -1;
        }
        if (z1 > t.z1) {
            return 1;
        }
        if (y1 < t.y1) {
            return -1;
        }
        if (y1 > t.y1) {
            return 1;
        }
        if (x2 < t.x2) {
            return -1;
        }
        if (x2 > t.x2) {
            return 1;
        }
        if (z2 < t.z2) {
            return -1;
        }
        if (z2 > t.z2) {
            return 1;
        }
        if (y2 < t.y2) {
            return -1;
        }
        if (y2 > t.y2) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {

        return worldName + ":" + x1 + ":" + y1 + ":" + z1 + ":" + x2 + ":" + y2 + ":" + z2;
    }

    public String getPrint() {

        return "(" + x1 + ", " + y1 + ", " + z1 + ")-(" + x2 + ", " + y2 + ", " + z2 + ")";
    }

    public Integer getKey() {

        if (land != null) {
            return land.getAreaKey(this);
        }

        return null;
    }

    public boolean isCollision(CuboidArea area2) {

        return ((Calculate.isInInterval(x1, area2.x1, area2.x2)
                || Calculate.isInInterval(area2.x1, x1, x2)))
                && ((Calculate.isInInterval(y1, area2.y1, area2.y2)
                || Calculate.isInInterval(area2.y1, y1, y2)))
                && ((Calculate.isInInterval(z1, area2.z1, area2.z2)
                || Calculate.isInInterval(area2.z1, z1, z2)));
    }

    public boolean isLocationInside(Location loc) {

        return loc.getWorld().getName().equals(worldName)
                && Calculate.isInInterval(loc.getBlockX(), x1, x2)
                && Calculate.isInInterval(loc.getBlockY(), y1, y2)
                && Calculate.isInInterval(loc.getBlockZ(), z1, z2);
    }

    public final void setLand(Land land) {

        this.land = land;
    }

    public void setWorldName(String worldName) {

        this.worldName = worldName.toLowerCase();
    }

    public void setX1(int x1) {

        this.x1 = x1;
    }

    public void setY1(int y1) {

        this.y1 = y1;
    }

    public void setZ1(int z1) {

        this.z1 = z1;
    }

    public void setX2(int x2) {

        this.x2 = x2;
    }

    public void setY2(int y2) {

        this.y2 = y2;
    }

    public void setZ2(int z2) {

        this.z2 = z2;
    }

    public Land getLand() {

        return land;
    }

    public String getWorldName() {

        return worldName;
    }

    public World getWord() {

        return Factoid.getThisPlugin().getServer().getWorld(worldName);
    }

    public int getX1() {

        return x1;
    }

    public int getY1() {

        return y1;
    }

    public int getZ1() {

        return z1;
    }

    public int getX2() {

        return x2;
    }

    public int getY2() {

        return y2;
    }

    public int getZ2() {

        return z2;
    }
}
