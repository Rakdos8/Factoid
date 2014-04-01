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
    private boolean debug;
    public boolean isDebug() { return debug; }
    
    private List<String> worlds;
    public List<String> getWorlds() { return worlds; }
    
    private String lang;
    public String getLang() { return lang; }
    
    private boolean useEconomy;
    public boolean useEconomy() { return useEconomy; }
    
    private int infoItem;
    public int getInfoItem() { return infoItem; }
    
    private int selectItem;
    public int getSelectItem() { return selectItem; }
    
    public enum AllowCollisionType {

        TRUE,
        APPROVE,
        FALSE;
    }
    private AllowCollisionType allowCollision;
    public AllowCollisionType getAllowCollision() { return allowCollision; }
    
    private long approveNotifyTime;
    public long getApproveNotifyTime() { return approveNotifyTime; }
    
    private long selectAutoCancel;
    public long getSelectAutoCancel() { return selectAutoCancel; }
    
    private int maxVisualSelect;
    public int getMaxVisualSelect() { return maxVisualSelect; }
    
    private int maxVisualSelectFromPlayer;
    public int getMaxVisualSelectFromPlayer() { return maxVisualSelectFromPlayer; }

    private int maxLand;
    public int getMaxLand() { return maxLand; } 
    
    private int minLandSize;
    public int getMinLandSize() { return minLandSize; }
    
    private int maxLandSize;
    public int getMaxLandSize() { return maxLandSize; }
    
    private int maxAreaPerLand;
    public int getMaxAreaPerLand() { return maxAreaPerLand; }
    
    private int minAreaSize;
    public int getMinAreaSize() { return minAreaSize; }
    
    private int maxAreaSize;
    public int getMaxAreaSize() { return maxAreaSize; }
    
    private int minLandHigh;
    public int getMinLandHigh() { return minLandHigh; }
    
    private int maxLandHigh;
    public int getMaxLandHigh() { return maxLandHigh; }
    
    private int defaultXSize;
    public int getDefaultXSize() { return defaultXSize; }
  
    private int defaultZSize;
    public int getDefaultZSize() { return defaultZSize; }
  
    private int defaultBottom;
    public int getDefaultBottom() { return defaultBottom; }
  
    private int defaultTop;
    public int getDefaultTop() { return defaultTop; }

    private double priceByCube;
    public double getPriceByCube() { return priceByCube; }
    
    private double minPriceLocation;
    public double getMinPriceLocation() { return minPriceLocation; }
    
    private double maxPriceLocation;
    public double getMaxPriceLocation() { return maxPriceLocation; }
    
    private double minPriceSell;
    public double getMinPriceSell() { return minPriceSell; }
    
    private double maxPriceSell;
    public double maxPriceSell() { return maxPriceSell; }
    
    private boolean beaconLight;
    public boolean isBeaconLight() { return beaconLight; }
    
    private boolean overrideExplosions;
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

        debug = config.getBoolean("general.debug", false);
        config.addDefault("general.worlds", new String[] {"world", "world_nether", "world_the_end"});
        worlds = StringChanges.toLower(config.getStringList("general.worlds"));
        lang = config.getString("general.lang", "english");
        useEconomy = config.getBoolean("general.UseEconomy", false);
        infoItem = config.getInt("general.InfoItem", 352);
        selectItem = config.getInt("general.SelectItem", 367);
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision", "approve").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        approveNotifyTime = config.getLong("land.ApproveNotifyTime", 24002);
        selectAutoCancel = config.getLong("land.SelectAutoCancel", 12000);
        maxVisualSelect = config.getInt("land.MaxVisualSelect", 256);
        maxVisualSelectFromPlayer = config.getInt("land.MaxVisualSelectFromPlayer", 128);
        maxLand = config.getInt("land.MaxLand", 1);
        minLandSize = config.getInt("land.MinLandSize", 1);
        maxLandSize = config.getInt("land.MaxLandSize", 1);
        defaultXSize = config.getInt("land.defaultXSize", 10);
        defaultZSize = config.getInt("land.defaultZSize", 10);
        defaultBottom = config.getInt("land.defaultBottom", 0);
        defaultTop = config.getInt("land.defaultTop", 255);
        maxAreaPerLand = config.getInt("land.area.MaxAreaPerLand", 1);
        minAreaSize = config.getInt("land.area.MinAreaSize", 1);
        maxAreaSize = config.getInt("land.area.MaxAreaSize", 1);
        minLandHigh = config.getInt("land.area.MinLandHigh", 0);
        maxLandHigh = config.getInt("land.area.MaxLandHigh", 256);
        beaconLight = config.getBoolean("land.BeaconLight", false);
        overrideExplosions = config.getBoolean("general.OverrideExplosions", true);

        if (useEconomy) {
            priceByCube = config.getDouble("economy.PricebyCube", 0.01);
            minPriceSell = config.getDouble("economy.MinPriceSell", 1);
            maxPriceSell = config.getDouble("economy.MaxPriceSell", 1);
            minPriceLocation = config.getDouble("economy.MinPriceLocation", 1);
            maxPriceLocation = config.getDouble("economy.MaxPriceLocation", 1);
        }

        config.addDefault("land.OwnerCanSet.Flags", new String[] {"MESSAGE_JOIN", "MESSAGE_QUIT"});
        ownerConfigFlag = Collections.synchronizedSet(EnumSet.noneOf(FlagType.class));
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(FlagType.valueOf(value.toUpperCase()));
        }
        config.addDefault("land.OwnerCanSet.Permissions", new String[] {"BUILD", "OPEN", "USE"});
        ownerConfigPerm = Collections.synchronizedSet(EnumSet.noneOf(PermissionType.class));
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(PermissionType.valueOf(value.toUpperCase()));
        }
    }
}
