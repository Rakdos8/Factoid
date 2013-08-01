package me.tabinol.factoid.commands;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.LandSelection;

public class OnCommand extends Thread implements CommandExecutor{
    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private Map<String,LandSelection> PlayerSelecting = new HashMap<String,LandSelection>();
    private Location PlayerSelectLocation;
    private Map<String,LandSelection> PlayerExpanding = new HashMap<String,LandSelection>();
    
    public OnCommand(Lang lang,Log log,JavaPlugin plugin){
        this.language = lang;
        this.log = log;
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console.");
            return false;
	}else{
            if(cmd.getName().equalsIgnoreCase("factoid") || cmd.getName().equalsIgnoreCase("claim")){
                Player player = (Player) sender;
                World world = player.getWorld();
                Location loc = player.getLocation();
                
                if(arg.length > 0){
                    if(arg[0].equalsIgnoreCase("select")){
                        if(!PlayerSelecting.containsKey(player.getName().toLowerCase())){
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Select Mode.");
                            player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid select done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                          LandSelection select =  new LandSelection(player,player.getServer(),PlayerSelectLocation,plugin);
                            PlayerSelecting.put(player.getName().toLowerCase(),select);
                        }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                            player.sendMessage(ChatColor.GREEN+"[Factoid] You have selectioned your land.");
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                            LandSelection select = PlayerSelecting.get(player.getName().toLowerCase());
                            select.isSelected();
                            PlayerSelectLocation = select.getSelection();
                            select.interrupt();
                            PlayerSelecting.remove(player.getName().toLowerCase());
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Select Mode.");
                        }
                    }else if(arg[0].equalsIgnoreCase("expand")){
                        if(!PlayerExpanding.containsKey(player.getName().toLowerCase())){
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Expand Mode.");
                            player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid select done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                          LandSelection select =  new LandSelection(player,player.getServer(),PlayerSelectLocation,plugin);
                            PlayerExpanding.put(player.getName().toLowerCase(),select);
                        }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                            player.sendMessage(ChatColor.GREEN+"[Factoid] You have Expand your land.");
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Expand Mode.");
                            LandSelection select = PlayerExpanding.get(player.getName().toLowerCase());
                            select.isSelected();
                            PlayerSelectLocation = select.getSelection();
                            select.interrupt();
                            PlayerExpanding.remove(player.getName().toLowerCase());
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Expand Mode.");
                        }
                    }
                    //log.write("Supposly block changed");
                    //player.sendMessage(ChatColor.GRAY+"[Factoid] Claimed.");
                return true;
                }
            }
        }
        
        return false;
    }
}
