package me.tabinol.factoid.commands;

import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import java.util.Map;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;

public class OnCommand extends Thread implements CommandExecutor{
    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private Map<String,LandSelection> PlayerSelecting = new HashMap<String,LandSelection>();
    private Map<String,LandExpansion> PlayerExpanding = new HashMap<String,LandExpansion>();
    private Map<String,LandExpansion> PlayerFlags = new HashMap<String,LandExpansion>();
    
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
                        if(!this.PlayerFlags.containsKey(player.getName().toLowerCase())){
                            if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                if(arg.length > 1 && !arg[1].equalsIgnoreCase("done")){
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Select Mode.");
                                    player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Trying to select '"+arg[1]+"'");
                                }else{
                                    if(!this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Select Mode.");
                                        player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid select done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                                        LandSelection select =  new LandSelection(player,player.getServer(),plugin);
                                        this.PlayerSelecting.put(player.getName().toLowerCase(),select);
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                        player.sendMessage(ChatColor.GREEN+"[Factoid] You have selectioned your land.");
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                       select.setSelected();
                                       Map<String,Location> corner = select.getCorner();
                                       int x1 = corner.get("FrontCornerLeft").getBlockX();
                                       int x2 = corner.get("BackCornerRigth").getBlockX();
                                       int y1 = corner.get("FrontCornerLeft").getBlockY();
                                       int y2 = corner.get("BackCornerRigth").getBlockY();
                                       int z1 = corner.get("FrontCornerLeft").getBlockZ();
                                       int z2 = corner.get("BackCornerRigth").getBlockZ();
                                       CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(),x1,y1,z1,x2,y2,z2);
                                       Land land  = new Land("",new PlayerContainerPlayer(player.getName()),cuboidarea);
                                       this.PlayerSelecting.remove(player.getName().toLowerCase());
                                       select.resetSelection();
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Select Mode.");
                                    }
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Expand Mode before.");
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Flags Mode before.");
                        }
                    }else if(arg[0].equalsIgnoreCase("expand")){
                        if(!this.PlayerFlags.containsKey(player.getName().toLowerCase())){
                            if(!this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Expand Mode.");
                                    player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid expand done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                                    LandExpansion expand =  new LandExpansion(player,player.getServer(),plugin);
                                    this.PlayerExpanding.put(player.getName().toLowerCase(),expand);
                                }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                    player.sendMessage(ChatColor.GREEN+"[Factoid] You have Expanded your land.");
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Expand Mode.");
                                    LandExpansion expand = this.PlayerExpanding.get(player.getName().toLowerCase());
                                    expand.setSelected();
                                   this.PlayerExpanding.remove(player.getName().toLowerCase());
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Expand Mode.");
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Select Mode before.");
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Flags Mode before.");
                        }
                        }else if(arg[0].equalsIgnoreCase("create")){
                        if(!this.PlayerFlags.containsKey(player.getName().toLowerCase())){
                            if(this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Create Mode.");
                                   
                                    player.sendMessage(ChatColor.GREEN+"[Factoid] You have Create your land.");
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Create Mode.");
                                    LandExpansion expand = this.PlayerExpanding.get(player.getName().toLowerCase());
                                 //  this.PlayerCreate.remove(player.getName().toLowerCase());
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Create Mode.");
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] You must be in Select Mode before.");
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Flags Mode before.");
                        }
                    }else if(arg[0].equalsIgnoreCase("flags")){
                        if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                            if(!this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Flags Mode.");
                                    player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid flags done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                                    LandExpansion expand =  new LandExpansion(player,player.getServer(),plugin);
                                    this.PlayerExpanding.put(player.getName().toLowerCase(),expand);
                                }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                    player.sendMessage(ChatColor.GREEN+"[Factoid] You have modified your landFlags.");
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Flags Mode.");
                                    LandExpansion expand = this.PlayerExpanding.get(player.getName().toLowerCase());
                                    expand.setSelected();
                                   this.PlayerExpanding.remove(player.getName().toLowerCase());
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Flags Mode.");
                                }
                        }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Select Mode before.");
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Expand Mode before.");
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
