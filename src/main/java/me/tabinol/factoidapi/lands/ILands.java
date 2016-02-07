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
package me.tabinol.factoidapi.lands;

import java.util.Collection;
import java.util.UUID;

import me.tabinol.factoidapi.exceptions.AFactoidLandException;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;

import org.bukkit.Location;

/**
 * The Interface ILands. Can give access to all lands.
 */
public interface ILands {

    /**
     * Creates the land. (without parent)
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @return the land
     * @throws AFactoidLandException the factoid land exception
     */
    public ILand createLand(String landName, IPlayerContainer owner, ICuboidArea area)
            throws AFactoidLandException;
    // For Land with parent
    /**
     * Creates the child land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @param parent the parent
     * @return the land
     * @throws AFactoidLandException the factoid land exception
     */
    public ILand createLand(String landName, IPlayerContainer owner, ICuboidArea area, ILand parent)
            throws AFactoidLandException;

    /**
     * Checks if is name exist.
     *
     * @param landName the land name
     * @return true, if is name exist
     */
    public boolean isNameExist(String landName);
    
    /**
     * Removes the land.
     *
     * @param land the land
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean removeLand(ILand land) throws AFactoidLandException;

    /**
     * Removes the land.
     *
     * @param landName the land name
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean removeLand(String landName) throws AFactoidLandException;
    
    /**
     * Removes the land.
     *
     * @param uuid the uuid
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean removeLand(UUID uuid) throws AFactoidLandException;

    /**
     * Rename land.
     *
     * @param landName the land name
     * @param newName the new name
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean renameLand(String landName, String newName) throws AFactoidLandException;
    
    /**
     * Rename land.
     *
     * @param uuid the uuid
     * @param newName the new name
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean renameLand(UUID uuid, String newName) throws AFactoidLandException;

    /**
     * Rename land.
     *
     * @param land the land
     * @param newName the new name
     * @return true, if successful
     * @throws AFactoidLandException the factoid land exception
     */
    public boolean renameLand(ILand land, String newName) throws AFactoidLandException;
    
    /**
     * Gets the land from name.
     *
     * @param landName the land name
     * @return the land
     */
    public ILand getLand(String landName);

    /**
     * Gets the land from UUID.
     *
     * @param uuid the uuid
     * @return the land
     */
    public ILand getLand(UUID uuid);

    /**
     * Gets the land from location.
     *
     * @param loc the loc
     * @return the land
     */
    public ILand getLand(Location loc);
    
    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public Collection<ILand> getLands();
    
    /**
     * Gets the land or outside area (Dummy Land World).
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public IDummyLand getLandOrOutsideArea(Location loc);

    /**
     * Gets the outside area (Dummy Land World).
     *
     * @param loc the loc
     * @return the outside area
     */
    public IDummyLand getOutsideArea(Location loc);

    /**
     * Gets the outside area (Dummy Land World).
     *
     * @param worldName the world name
     * @return the outside area
     */
    public IDummyLand getOutsideArea(String worldName);

    /**
     * Gets the lands (Dummy Land World).
     *
     * @param loc the loc
     * @return the lands
     */
    public Collection<ILand> getLands(Location loc);

    /**
     * Gets the lands.
     *
     * @param owner the owner
     * @return the lands
     */
    public Collection<ILand> getLands(IPlayerContainer owner);


    /**
     * Gets the lands from type.
     *
     * @param type the type
     * @return the lands
     */
    public Collection<ILand> getLands(IType type);
    
    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public Collection<ICuboidArea> getCuboidAreas(Location loc);
    
    /**
     * Gets the cuboid area.
     *
     * @param loc the loc
     * @return the cuboid area
     */
    public ICuboidArea getCuboidArea(Location loc);
    
    /**
     * Gets the lands for sale.
     *
     * @return the lands for sale
     */
    public Collection<ILand> getForSale();

    
    /**
     * Gets the lands for rent.
     *
     * @return the lands for rent
     */
    public Collection<ILand> getForRent();
}
