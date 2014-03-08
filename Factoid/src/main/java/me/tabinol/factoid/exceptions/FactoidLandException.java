package me.tabinol.factoid.exceptions;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions;

public class FactoidLandException extends Exception {
    
    public FactoidLandException(String landName, CuboidArea area, Collisions.LandAction action, Collisions.LandError error) {
        
        super("Factoid Land Exception");
        
        StringBuilder bf = new StringBuilder();
        
        bf.append("Error: Land: ").append(landName);
        if(area != null) {
            bf.append(", area: ").append(area.getPrint());
        }
        bf.append(", Action: ").append(action.toString()).append(", Error: ").append(error.toString());

        Factoid.getLog().write(bf.toString());
    }
}
