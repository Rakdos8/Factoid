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
package me.tabinol.factoid.economy;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Represent the economy sign
 *
 * @author Tabinol
 */
public class EcoSign {

    Land land;
    Location location;
    BlockFace facing;
    boolean isWallSign;

    // Create from player position
    public EcoSign(Land land, Player player) {

        // ************* TO DO ***************
        
    }
    
    
    // Create from configuration
    public EcoSign(Land land, Location location, BlockFace facing, boolean isWallSign) {

        this.location = location;
        this.facing = facing;
        this.isWallSign = isWallSign;
    }

    public Location getLocation() {

        return location;
    }

    public void setLocation(Location location) {

        this.location = location;
    }

    public BlockFace getFacing() {

        return facing;
    }

    public void setFacing(BlockFace facing) {

        this.facing = facing;
    }

    public boolean isWallSign() {

        return isWallSign;
    }

    public void setIsWallSign(boolean isWallSign) {

        this.isWallSign = isWallSign;
    }

    public boolean createSignForSale(double price) {

        String[] lines = new String[4];
        lines[0] = ChatColor.GREEN + Factoid.getLanguage().getMessage("SIGN.SALE.FORSALE");
        lines[1] = "";
        lines[2] = "";
        lines[3] = ChatColor.BLUE + Factoid.getPlayerMoney().toFormat(price);

        return createSign(lines);
    }

    public boolean createSignForRent(double price, int renew, boolean autoRenew, String tenantName) {

        String[] lines = new String[4];

        if (tenantName != null) {
            lines[0] = ChatColor.RED + Factoid.getLanguage().getMessage("SIGN.RENT.RENTED");
            lines[1] = ChatColor.RED + tenantName;
        } else {
            lines[0] = ChatColor.GREEN + Factoid.getLanguage().getMessage("SIGN.RENT.FORRENT");
            lines[1] = "";
        }

        if (autoRenew) {
            lines[2] = ChatColor.BLUE + Factoid.getLanguage().getMessage("SIGN.RENT.AUTORENEW");
        } else {
            lines[2] = "";
        }

        lines[3] = ChatColor.BLUE + Factoid.getPlayerMoney().toFormat(price) + "/" + renew;

        return createSign(lines);
    }

    public boolean createSign(String[] lines) {

        World world = land.getWorld();

        // Impossible to create the sign here
        if (Factoid.getLands().getLand(location) != land) {
            return false;
        }

        Material mat;
        if (isWallSign) {

            mat = Material.WALL_SIGN;
        } else {
            mat = Material.SIGN_POST;
        }

        // Create sign
        Block blockPlace = world.getBlockAt(location);
        blockPlace.setType(mat);

        Sign sign = (Sign) blockPlace.getState();

        //Add lines
        for (int t = 0; t <= 3; t++) {
            sign.setLine(t, lines[t]);
        }

        // Set facing
        ((org.bukkit.material.Sign) sign.getData()).setFacingDirection(facing);

        return true;
    }

    public void removeSign() {

        Block block = land.getWorld().getBlockAt(location);

        // Remove only if it is a sign;
        if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
            block.setType(Material.AIR);
        }
    }
}
