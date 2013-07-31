package me.tabinol.factoid.commands;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class OnCommand extends Thread implements Listener{
    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    
    public OnCommand(Lang lang,Log log,JavaPlugin plugin){
        this.language = lang;
        this.log = log;
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console.");
            return false;
	}
        
        return false;
    }
}
