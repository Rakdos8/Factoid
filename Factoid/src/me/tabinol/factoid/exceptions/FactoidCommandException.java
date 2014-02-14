package me.tabinol.factoid.exceptions;

import org.bukkit.command.CommandSender;

public class FactoidCommandException extends FactoidException {
    
    public FactoidCommandException(String logMsg, CommandSender sender, String langMsg, String... param) {
        
        super(logMsg, sender, langMsg, param);
    }
}
