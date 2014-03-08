package me.tabinol.factoid.lands;

import me.tabinol.factoid.lands.flags.LandFlag;
import java.util.HashSet;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class ContainerFlag {
    
    PlayerContainer pc;
    HashSet <LandFlag> flags;
    
    public ContainerFlag(PlayerContainer pc, HashSet <LandFlag> flags) {
        
        this.pc = pc;
        if(flags == null) {
            this.flags = new HashSet<LandFlag>();
        } else {
            this.flags = flags;
        }
    }
    
    public boolean equals(ContainerFlag cf2) {
        
        return pc.equals(cf2.pc);
    }
}
