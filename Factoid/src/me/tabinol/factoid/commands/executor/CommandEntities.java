package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.CommandList;
import me.tabinol.factoid.config.PlayerStaticConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Contains general information for commandExecutor
public class CommandEntities {
    
    protected final CommandList command;
    protected final CommandSender sender;
    protected final ArgList argList;
    protected final Player player;
    protected final String playerName;
    protected final PlayerStaticConfig.PlayerConfEntry playerConf;

    public CommandEntities(CommandList command, CommandSender sender, ArgList argList) {
        
        this.command = command;
        this.sender = sender;
        this.argList = argList;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }
        
        playerName = sender.getName();
        playerConf = Factoid.getPlayerConf().get(sender);
    }

}
