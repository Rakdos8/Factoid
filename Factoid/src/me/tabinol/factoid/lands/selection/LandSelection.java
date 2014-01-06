package me.tabinol.factoid.lands.selection;

import java.util.Map;
import java.util.HashMap;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.CuboidArea;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class LandSelection extends Thread implements Listener {

    private Player player;
    private World world;
    private byte by = 0;
    private boolean isSelected = false;
    private Map<Location, Material> BlockList = new HashMap<>();
    private Map<String, Location> CornerList = new HashMap<>();
    private Location LandPos;
    private boolean IsCollision;
    private int trueY1;
    private int trueY2;

    public LandSelection(Player player) {

        LandSelection(player, player.getLocation(), 0, 0, 
                Factoid.getConf().MinLandHigh, Factoid.getConf().MaxLandHigh, 0, 0, false);
    }

    // public LandSelection(Player player, Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {

    //    LandSelection(player, loc, x1, x2, y1, y2, z1, z2, loc == null);
    // }

    // For WorldEdit
    public LandSelection(Player player, int x1, int x2, int y1, int y2, int z1, int z2) {

        LandSelection(player, null, x1, x2, y1, y2, z1, z2, false);
    }
    private void LandSelection(Player player, Location loc, 
            int x1, int x2, int y1, int y2, int z1, int z2, boolean isSelection) {
        this.trueY1 = y1;
        this.trueY2 = y2;
        Factoid.getThisPlugin().getServer().getPluginManager().registerEvents(this, Factoid.getThisPlugin());
        this.player = player;
        this.world = player.getWorld();
        LandMakeSquare landmake = new LandMakeSquare(player, loc, x1, x2, y1, y2, z1, z2, isSelection);
        this.BlockList = landmake.makeSquare();
        this.CornerList = landmake.getCorner();
        this.LandPos = player.getLocation();
        this.IsCollision = landmake.getCollision();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!this.isSelected) {
            if (event.getFrom() != event.getTo()) {
                if (event.getPlayer().getName().equals(this.player.getName())) {
                    if (!this.BlockList.isEmpty() && !this.CornerList.isEmpty()) {
                        boolean done = new LandResetSelection(this.BlockList, this.CornerList, this.player).Reset();
                        if (done) {
                            this.BlockList.clear();
                            LandMakeSquare landmake = new LandMakeSquare(this.player, event.getTo(), 0, 0, 0, 0, 0, 0, false);
                            this.BlockList = landmake.makeSquare();
                            this.CornerList = landmake.getCorner();
                            this.LandPos = event.getTo();
                            this.IsCollision = landmake.getCollision();
                        }
                    }
                }
            }
        }
    }

    public Map<Location, Material> getSquare() {
        return this.BlockList;
    }

    public Player getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public void setSelected() {
        this.isSelected = true;
    }

    public Location getSelection() {
        return this.LandPos;
    }

    public Map<String, Location> getCorner() {
        return this.CornerList;
    }

    public CuboidArea toCuboidArea() {

        int x1 = CornerList.get("FrontCornerLeft").getBlockX();
        int x2 = CornerList.get("BackCornerRigth").getBlockX();
        int y1 = trueY1;
        int y2 = trueY2;
        int z1 = CornerList.get("FrontCornerLeft").getBlockZ();
        int z2 = CornerList.get("BackCornerRigth").getBlockZ();
        
        return new CuboidArea(player.getWorld().getName(), x1, y1, z1, x2, y2, z2);
    }

    public void resetSelection() {
        if (!this.BlockList.isEmpty() && !this.CornerList.isEmpty()) {
            boolean done = new LandResetSelection(this.BlockList, this.CornerList, this.player).Reset();
            if (done) {
                this.BlockList.clear();
            }
        }
    }

    public boolean getCollision() {
        return IsCollision;
    }
}
