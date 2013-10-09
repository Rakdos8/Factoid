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
import me.tabinol.factoid.Factoid;

import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.factions.*;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.playercontainer.PlayerContainerType;

public class OnCommand extends Thread implements CommandExecutor{
    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private Map<String,LandSelection> PlayerSelecting = new HashMap<String,LandSelection>();
    private Map<String,LandExpansion> PlayerExpanding = new HashMap<String,LandExpansion>();
    private Map<String,LandSetFlag> PlayerSetFlag = new HashMap<String,LandSetFlag>();
    
    public OnCommand(){
        this.language = Factoid.getLanguage();
        this.plugin = Factoid.getThisPlugin();
        this.log = Factoid.getLog();
    }
    
    @Override
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
                        if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                            if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                    if(!this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You are now in Select Mode.");
                                        player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Type "+ChatColor.ITALIC+"'/factoid select done'"+ChatColor.RESET+ChatColor.DARK_GRAY+" to confirm your choice.");
                                        LandSelection select =  new LandSelection(player,player.getServer(),plugin);
                                        this.PlayerSelecting.put(player.getName().toLowerCase(),select);
                                        if(arg.length == 2){
                                            Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                            if(landtest != null){
                                                PlayerContainer owner = landtest.getOwner();
                                                if(owner.hasAccess(player.getName())){
                                        
                                                }else{
                                                    player.sendMessage(ChatColor.RED+"[Factoid] You must the permission to execute this action.");
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] This land doesn't Exist.");
                                            }
                                        }
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                        if(!Factoid.getConf().CanMakeCollision){
                                            if(!select.getCollision()){
                                                select.setSelected();
                                            }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] Your selection have an collision with an another Land.");
                                            }
                                        }else{
                                            select.setSelected();
                                        }
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("cancel")){
                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                        this.PlayerSelecting.remove(player.getName().toLowerCase());
                                        select.resetSelection();
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You have cancelled your selection.");
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You are already in Select Mode.");
                                    }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Expand Mode before.");
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] Quit the Flags Mode before.");
                        }
                    }else if(arg[0].equalsIgnoreCase("expand")){
                        if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
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
                            if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                                if(this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                    if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                        if(arg[1] != null){
                                            if(!arg[1].equalsIgnoreCase("cancel") && !arg[1].equalsIgnoreCase("done")){
                                                Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                                if(landtest == null){
                                                    LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                                    Map<String,Location> corner = select.getCorner();
                                                    int x1 = corner.get("FrontCornerLeft").getBlockX();
                                                    int x2 = corner.get("BackCornerRigth").getBlockX();
                                                    int y1 = corner.get("FrontCornerLeft").getBlockY();
                                                    int y2 = corner.get("BackCornerRigth").getBlockY();
                                                    int z1 = corner.get("FrontCornerLeft").getBlockZ();
                                                    int z2 = corner.get("BackCornerRigth").getBlockZ();

                                                    CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(),x1,y1,z1,x2,y2,z2);
                                                    Land land  = new Land(arg[1].toString(),new PlayerContainerPlayer(player.getName()),cuboidarea);
                                                    if(!Factoid.getConf().CanMakeCollision){
                                                        if(!select.getCollision()){
                                                            if(Factoid.getLands().createLand(land)){
                                                                player.sendMessage(ChatColor.GREEN+"[Factoid] You have created your land.");
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                                                            }else{
                                                                player.sendMessage(ChatColor.RED+"[Factoid] An Error Occur Please contact an Administrator.");
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                                                            }
                                                        }else{
                                                            player.sendMessage(ChatColor.RED+"[Factoid] Your selection have an collision with an another Land.");
                                                        }
                                                    }else{
                                                       if(Factoid.getLands().createLand(land)){
                                                            player.sendMessage(ChatColor.GREEN+"[Factoid] You have created your land.");
                                                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                                                        }else{
                                                            player.sendMessage(ChatColor.RED+"[Factoid] An Error Occur Please contact an Administrator.");
                                                            player.sendMessage(ChatColor.GRAY+"[Factoid] You are no longer in Select Mode.");
                                                        }
                                                    }
                                                    this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                    select.resetSelection();
                                                }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] This Land name is already used.");
                                                player.sendMessage(ChatColor.GRAY+"[Factoid] /factoid create [land Name]");
                                            }
                                            }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] You have to provide a Land Name.");
                                                player.sendMessage(ChatColor.GRAY+"[Factoid] /factoid create [land Name]");
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED+"[Factoid] This Land name is already used.");
                                            player.sendMessage(ChatColor.GRAY+"[Factoid] /factoid create [land Name]");
                                        }
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] You must be in Select Mode before.");
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
                                    player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] Your modification will be instantany effective.");
                                    CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                    LandSetFlag setting =  new LandSetFlag(player,area);
                                    this.PlayerSetFlag.put(player.getName().toLowerCase(),setting);
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
