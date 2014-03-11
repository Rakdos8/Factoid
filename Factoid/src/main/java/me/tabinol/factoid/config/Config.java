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
package me.tabinol.factoid.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.flags.FlagType;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.utilities.StringChanges;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    // Global
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String GLOBAL = "_global_";
    private final Factoid thisPlugin;
    private FileConfiguration config;
    // Configuration
    private boolean debug = false;
    public boolean isDebug() { return debug; }
    
    private List<String> worlds = null;
    public List<String> getWorlds() { return worlds; }
    
    private String lang = "english";
    public String getLang() { return lang; }
    
    private boolean useEconomy = false;
    public boolean useEconomy() { return useEconomy; }
    
    private int infoItem = 352;
    public int getInfoItem() { return infoItem; }
    
    private int selectItem = 367;
    public int getSelectItem() { return selectItem; }
    
    public enum AllowCollisionType {

        TRUE,
        APPROVE,
        FALSE;
    }
    private AllowCollisionType allowCollision;
    public AllowCollisionType getAllowCollision() { return allowCollision; }
    
    private long approveNotifyTime = 24002;
    public long getApproveNotifyTime() { return approveNotifyTime; }
    
    private long selectAutoCancel = 12000;
    public long getSelectAutoCancel() {return selectAutoCancel; }
    
    private int maxLand = 1;
    public int getMaxLand() { return maxLand; } 
    
    private int minLandSize = 1;
    public int getMinLandSize() { return minLandSize; }
    
    private int maxLandSize = 1;
    public int getMaxLandSize() { return maxLandSize; }
    
    private int maxAreaPerLand = 1;
    public int getMaxAreaPerLand() { return maxAreaPerLand; }
    
    private int minAreaSize = 1;
    public int getMinAreaSize() { return minAreaSize; }
    
    private int maxAreaSize = 1;
    public int getMaxAreaSize() { return maxAreaSize; }
    
    private int minLandHigh = 0;
    public int getMinLandHigh() { return minLandHigh; }
    
    private int maxLandHigh = 255;
    public int getMaxLandHigh() { return maxLandHigh; }
    
    private double priceByCube = 1;
    public double getPriceByCube() { return priceByCube; }
    
    private double minPriceLocation = 1;
    public double getMinPriceLocation() { return minPriceLocation; }
    
    private double maxPriceLocation = 1;
    public double getMaxPriceLocation() { return maxPriceLocation; }
    
    private double minPriceSell = 1;
    public double getMinPriceSell() { return minPriceSell; }
    
    private double maxPriceSell = 1;
    public double maxPriceSell() { return maxPriceSell; }
    
    private boolean beaconLight = false;
    public boolean isBeaconLight() { return beaconLight; }
    
    private boolean overrideExplosions = true;
    public boolean isOverrideExplosions() { return overrideExplosions; }
    
    private Set<FlagType> ownerConfigFlag; // Flags a owner can set
    public Set<FlagType> getOwnerConfigFlag() { return ownerConfigFlag; }
    
    private Set<PermissionType> ownerConfigPerm; // Permissions a owner can set
    public Set<PermissionType> getOwnerConfigPerm() { return ownerConfigPerm; }

    public Config() {

        thisPlugin = Factoid.getThisPlugin();
        thisPlugin.saveDefaultConfig();

        // Get Bukkit Config for this plugin, not this class!!!
        config = thisPlugin.getConfig();

        reloadConfig();
    }

    public final void reloadConfig() {

        thisPlugin.reloadConfig();
        config = thisPlugin.getConfig();
        getConfig();
    }

    private void getConfig() {

        debug = config.getBoolean("general.debug");
        worlds = StringChanges.toLower(config.getStringList("general.worlds"));
        lang = config.getString("general.lang");
        useEconomy = config.getBoolean("general.UseEconomy");
        infoItem = config.getInt("general.InfoItem");
        selectItem = config.getInt("general.SelectItem");
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        approveNotifyTime = config.getLong("land.ApproveNotifyTime");
        selectAutoCancel = config.getLong("land.SelectAutoCancel");
        maxLand = config.getInt("land.MaxLand");
        minLandSize = config.getInt("land.MinLandSize");
        maxLandSize = config.getInt("land.MaxLandSize");
        maxAreaPerLand = config.getInt("land.area.MaxAreaPerLand");
        minAreaSize = config.getInt("land.area.MinAreaSize");
        maxAreaSize = config.getInt("land.area.MaxAreaSize");
        minLandHigh = config.getInt("land.area.MinLandHigh");
        maxLandHigh = config.getInt("land.area.MaxLandHigh");
        beaconLight = config.getBoolean("land.BeaconLight");
        overrideExplosions = config.getBoolean("general.OverrideExplosions");

        if (useEconomy) {
            priceByCube = config.getDouble("economy.PricebyCube");
            minPriceSell = config.getDouble("economy.MinPriceSell");
            maxPriceSell = config.getDouble("economy.MaxPriceSell");
            minPriceLocation = config.getDouble("economy.MinPriceLocation");
            maxPriceLocation = config.getDouble("economy.MaxPriceLocation");
        }

        ownerConfigFlag = Collections.synchronizedSet(EnumSet.noneOf(FlagType.class));
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(FlagType.valueOf(value.toUpperCase()));
        }
        ownerConfigPerm = Collections.synchronizedSet(EnumSet.noneOf(PermissionType.class));
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(PermissionType.valueOf(value.toUpperCase()));
        }
    }
}
