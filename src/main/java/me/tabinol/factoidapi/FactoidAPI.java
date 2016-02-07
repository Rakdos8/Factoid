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
package me.tabinol.factoidapi;

import java.lang.reflect.InvocationTargetException;

import me.tabinol.factoidapi.config.players.IPlayerStaticConfig;
import me.tabinol.factoidapi.factions.IFactions;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.ILands;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.ITypes;
import me.tabinol.factoidapi.parameters.IParameters;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;

// TODO: Auto-generated Javadoc
/**
 * The Factoid API. This is the static way to access the Factoid API.
 * To use this API, be sure to put this line in your plugin.yml: <br>
 * depend: [Factoid]<br>
 */
public final class FactoidAPI {

    /**  The Factoid Interface. */
	private static IFactoid iFactoid;
	
    /**
     * Inits the factoid plugin access. NOTE: It is not useful to run it.
     */
    public static void initFactoidPluginAccess() {
    	
    	try {
			iFactoid = (IFactoid) Class.forName("me.tabinol.factoid.Factoid")
					.getDeclaredMethod("getThisPlugin").invoke(null);
		
    	} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	
	/*
     * Normal gets
     */
	
    /**
	 * I factions.
	 *
	 * @return the i factions
	 * @see IFactoid#iFactions()
	 */
    public static IFactions iFactions() {
    	
    	return iFactoid.iFactions();
    }

    /**
     * I lands.
     *
     * @return the i lands
     * @see IFactoid#iLands()
     */
    public static ILands iLands() {
    	
    	return iFactoid.iLands();
    }

    /**
     * I types.
     *
     * @return the i types
     * @see IFactoid#iTypes()
     */
    public static ITypes iTypes() {
    	
    	return iFactoid.iTypes();
    }
    
    /**
     * I parameters.
     *
     * @return the i parameters
     * @see IFactoid#iParameters()
     */
    public static IParameters iParameters() {
    	
    	return iFactoid.iParameters();
    }
    
    /**
     * I player conf.
     *
     * @return the i player static config
     * @see IFactoid#iPlayerConf()
     */
    public static IPlayerStaticConfig iPlayerConf() {
    	
    	return iFactoid.iPlayerConf();
    }
    
    /*
     * Creators
     */

    /**
     * Creates the player container.
     *
     * @param land the land
     * @param pct the pct
     * @param name the name
     * @return the i player container
     * @see IFactoid#createPlayerContainer(ILand, EPlayerContainerType, String)
     */
    public static IPlayerContainer createPlayerContainer(ILand land, 
    		EPlayerContainerType pct, String name) {
    	
    	return iFactoid.createPlayerContainer(land, pct, name);
    }
    
    /**
     * Creates the cuboid area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     * @return the i cuboid area
     * @see IFactoid#createCuboidArea(String, int, int, int, int, int, int)
     */
    public static ICuboidArea createCuboidArea(String worldName, int x1, int y1, 
    		int z1, int x2, int y2, int z2) {
    	
    	return iFactoid.createCuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }

}
