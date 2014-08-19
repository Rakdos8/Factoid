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
package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.economy.EcoSign;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.parameters.PermissionList;

public class CommandSale extends CommandExec {

    public CommandSale(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {
    	
        checkSelections(true, null);
        checkPermission(true, true, null, null);
        if(!entity.playerConf.isAdminMod()) {
        	// If the player not adminmod, he must be owner && permission true
        	checkPermission(false, false, PermissionList.ECO_LAND_FOR_SALE.getPermissonType(), null);
        }
        
        String curArg = entity.argList.getNext();
        double salePrice = 0;
        EcoSign ecoSign = null;
        
        // get price
        try {
            salePrice = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            // ********* EXCEPTION
        }
        
        // Create Sign
        try {
			ecoSign = new EcoSign(land, entity.player);
		} catch (SignException e) {
			// ********* EXCEPTION
		}
        ecoSign.createSignForSale(salePrice);
        land.setForSale(true, salePrice, ecoSign.getLocation());
        
        // ******************** ECRIRE DONE
    }


}
