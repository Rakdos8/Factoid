package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.exceptions.FactoidCommandException;

public interface CommandInterface {

    public void commandExecute() throws FactoidCommandException;
    
    
}
