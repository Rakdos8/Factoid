package me.tabinol.factoid.lands.Areas;

import java.util.Collection;
import java.util.HashSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
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

        return (worldName.equalsIgnoreCase(area2.worldName)
                && (Calculate.isInInterval(x1, area2.x1, area2.x2)
                || Calculate.isInInterval(area2.x1, x1, x2)))
                && ((Calculate.isInInterval(y1, area2.y1, area2.y2)
                || Calculate.isInInterval(area2.y1, y1, y2)))
                && ((Calculate.isInInterval(z1, area2.z1, area2.z2)
                || Calculate.isInInterval(area2.z1, z1, z2)));
    }

    public boolean isLocationInside(Location loc) {

        return loc.getWorld().getName().equalsIgnoreCase(worldName)
                && Calculate.isInInterval(loc.getBlockX(), x1, x2)
                && Calculate.isInInterval(loc.getBlockY(), y1, y2)
                && Calculate.isInInterval(loc.getBlockZ(), z1, z2);
    }

    // Return and create outise area(s) if the area is outside
    public Collection<CuboidArea> getOutside(CuboidArea area2) {

        HashSet<CuboidArea> areaList = new HashSet<>();

        if (!worldName.equalsIgnoreCase(area2.worldName)) {
            return areaList;
        }

        Integer ax1 = null;
        Integer ax2 = null;
        Integer bx1 = null;
        Integer bx2 = null;
        Integer ay1 = null;
        Integer ay2 = null;
        Integer by1 = null;
        Integer by2 = null;
        Integer az1 = null;
        Integer az2 = null;
        Integer bz1 = null;
        Integer bz2 = null;

        // -1 before, 0 inside, +1 after
        int x1pos = Calculate.comparePosition(area2.x1, x1, x2);
        int y1pos = Calculate.comparePosition(area2.y1, y1, y2);
        int z1pos = Calculate.comparePosition(area2.z1, z1, z2);
        int x2pos = Calculate.comparePosition(area2.x2, x1, x2);
        int y2pos = Calculate.comparePosition(area2.y2, y1, y2);
        int z2pos = Calculate.comparePosition(area2.z2, z1, z2);

        // first check if both points are before or after
        if ((x1pos == -1 && x2pos == -1) || (x1pos == 1 && x2pos == 1)) {
            return areaList;
        }
        if ((y1pos == -1 && y2pos == -1) || (y1pos == 1 && y2pos == 1)) {
            return areaList;
        }
        if ((z1pos == -1 && z2pos == -1) || (z1pos == 1 && z2pos == 1)) {
            return areaList;
        }

        // Check positions before
        if (x1pos == -1) {
            ax1 = area2.x1;
            ax2 = x1;
        }
        if (x1pos == 1) {
            bx1 = x2;
            bx2 = area2.x2;
        }
        if (x1pos == -1) {
            ay1 = area2.y1;
            ay2 = y1;
        }
        if (x1pos == 1) {
            by1 = y2;
            by2 = area2.y2;
        }
        if (x1pos == -1) {
            az1 = area2.z1;
            az2 = z1;
        }
        if (x1pos == 1) {
            bz1 = z2;
            bz2 = area2.z2;
        }

        // Create areas
        if (ax1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1, ay1 != null ? ay1 : y2, az1 != null ? az1 : z2,
                    ax2, ay2 != null ? ay2 : y1, az2 != null ? az2 : z1));
        }
        if (bx1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1, by1 != null ? by1 : y2, bz1 != null ? bz1 : z2,
                    bx2, by2 != null ? by2 : y1, bz2 != null ? bz2 : z1));
        }
        if (ay1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1 != null ? ax1 : x2, ay1, az1 != null ? az1 : z2,
                    ax2 != null ? ax2 : x1, ay2, az2 != null ? az2 : z1));
        }
        if (by1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1 != null ? bx1 : x2, by1, bz1 != null ? bz1 : z2,
                    bx2 != null ? bx2 : x1, by2, bz2 != null ? bz2 : z1));
        }
        if (az1 != null) {
            areaList.add(new CuboidArea(worldName,
                    ax1 != null ? ax1 : x2, ay1 != null ? ay1 : y2, az1,
                    ax2 != null ? ax2 : x1, ay2 != null ? ay2 : y1, az2));
        }
        if (bz1 != null) {
            areaList.add(new CuboidArea(worldName,
                    bx1 != null ? bx1 : x2, by1 != null ? by1 : y2, bz1,
                    bx2 != null ? bx2 : x1, by2 != null ? by2 : y1, bz2));
        }

        return areaList;
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

    public static CuboidArea getFromString(String str) {

        String[] multiStr = str.split(":");

        return new CuboidArea(multiStr[0],
                Integer.parseInt(multiStr[1]),
                Integer.parseInt(multiStr[2]),
                Integer.parseInt(multiStr[3]),
                Integer.parseInt(multiStr[4]),
                Integer.parseInt(multiStr[5]),
                Integer.parseInt(multiStr[6]));
    }
}
