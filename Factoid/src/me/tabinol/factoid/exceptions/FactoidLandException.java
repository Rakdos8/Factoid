package me.tabinol.factoid.exceptions;

import org.bukkit.command.CommandSender;

public class FactoidLandException extends FactoidException {
    
    public FactoidLandException(String logMsg, CommandSender sender, String langMsg, String... param) {
        
        super(logMsg, sender, langMsg, param);
    }
}
