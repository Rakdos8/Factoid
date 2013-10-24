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
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.scoreboard.ScoreBoard;


public class OnCommand extends Thread implements CommandExecutor{
    private Lang language;
    private Log log;
    private JavaPlugin plugin;
    private Map<String,LandSelection> PlayerSelecting = new HashMap();
    private Map<String,Land> LandSelectioned = new HashMap();
    private Map<String,LandExpansion> PlayerExpanding = new HashMap();
    private Map<String,LandSetFlag> PlayerSetFlag = new HashMap();
    
    public OnCommand(){
        this.language = Factoid.getLanguage();
        this.plugin = Factoid.getThisPlugin();
        this.log = Factoid.getLog();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Factoid.getLanguage().getMessage("CONSOLE"));
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
                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.JOIN",player.getName()));
                                        if(arg.length == 2){
                                            Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                            if(landtest != null){
                                                PlayerContainer owner = landtest.getOwner();
                                                if(owner.hasAccess(player.getName())){
                                                    if(!LandSelectioned.containsKey(player.getName().toLowerCase())){
                                                        LandSelectioned.put(player.getName().toLowerCase(), landtest);
                                                        for(CuboidArea area : landtest.getAreas()){
                                                            LandMakeSquare landmake = new LandMakeSquare(player,null,area.getX1(),area.getX2(),area.getY1(),area.getY2(),area.getZ1(),area.getZ2());
                                                            landmake.makeSquare();
                                                        }
                                                        new ScoreBoard(player,landtest.getName());
                                                        
                                                        player.sendMessage(ChatColor.GREEN+"[Factoid] "+ChatColor.DARK_GRAY+Factoid.getLanguage().getMessage("COMMAND.SELECT.MISSINGPERMISSION",landtest.getName()));
                                                    }else{
                                                        player.sendMessage(ChatColor.RED+"[Factoid] "+ChatColor.DARK_GRAY+Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY",landtest.getName()));
                                                    }
                                                }else{
                                                    player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.MISSINGPERMISSION"));
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.MISSINGPERMISSION",player.getName()));
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.NOLAND"));
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                                            player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT",ChatColor.ITALIC.toString(),ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                                            LandSelection select =  new LandSelection(player,player.getServer(),plugin);
                                            this.PlayerSelecting.put(player.getName().toLowerCase(),select);
                                        }
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                        if(!LandSelectioned.containsKey(player.getName().toLowerCase())){
                                            LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                            if(!Factoid.getConf().CanMakeCollision){
                                                if(!select.getCollision()){
                                                    select.setSelected();
                                                    player.sendMessage(ChatColor.GREEN+"[Factoid] "+ChatColor.DARK_GRAY+"You have selected a new Land.");
                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION",player.getName()));
                                                }else{
                                                    player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                                                }
                                            }else{
                                                select.setSelected();
                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION",player.getName()));
                                                player.sendMessage(ChatColor.GREEN+"[Factoid] "+ChatColor.DARK_GRAY+Factoid.getLanguage().getMessage("COMMAND.SELECT.NEWLAND"));
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONE"));
                                        }
                                        
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("cancel")){
                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                        this.PlayerSelecting.remove(player.getName().toLowerCase());
                                        select.resetSelection();
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.CANCEL",player.getName()));
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                                    }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.EXPENDMODE"));
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.FLAGSMODE"));
                        }
                    }else if(arg[0].equalsIgnoreCase("expand")){
                            if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                                if(this.LandSelectioned.containsKey(player.getName().toLowerCase())){
                                    if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
                                        player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] " +Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT",ChatColor.ITALIC.toString(),ChatColor.RESET.toString(),ChatColor.DARK_GRAY.toString()));
                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.JOINMODE",player.getName()));
                                        LandExpansion expand =  new LandExpansion(player,player.getServer(),plugin);
                                        this.PlayerExpanding.put(player.getName().toLowerCase(),expand);
                                    }else if(arg.length > 1 && arg[1].equalsIgnoreCase("done")){
                                        player.sendMessage(ChatColor.GREEN+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.EXPAND.QUITMODE",player.getName()));
                                        LandExpansion expand = this.PlayerExpanding.get(player.getName().toLowerCase());
                                        expand.setSelected();
                                       this.PlayerExpanding.remove(player.getName().toLowerCase());
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.ALREADY"));
                                    }
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOIN.SELECTMODE"));
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUIT.FLAGMODE"));
                            }
                        }else if(arg[0].equalsIgnoreCase("create")){
                            if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                                if(this.PlayerSelecting.containsKey(player.getName().toLowerCase())){
                                    if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                                        if(arg[1] != null){
                                            if(!arg[1].equalsIgnoreCase("cancel") && !arg[1].equalsIgnoreCase("done")){
                                                if(!LandSelectioned.containsKey(player.getName().toLowerCase())){
                                                    Land landtest = Factoid.getLands().getLand(arg[1].toString());
                                                    if(landtest == null){
                                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                                        Map<String,Location> corner = select.getCorner();
                                                        int x1 = corner.get("FrontCornerLeft").getBlockX();
                                                        int x2 = corner.get("BackCornerRigth").getBlockX();
                                                        //int y1 = corner.get("FrontCornerLeft").getBlockY();
                                                        //int y2 = corner.get("BackCornerRigth").getBlockY();
                                                        int y1 = Factoid.getConf().MinLandHigh;
                                                        int y2 = Factoid.getConf().MaxLandHigh;
                                                        int z1 = corner.get("FrontCornerLeft").getBlockZ();
                                                        int z2 = corner.get("BackCornerRigth").getBlockZ();

                                                        CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(),x1,y1,z1,x2,y2,z2);
                                                        Land land  = new Land(arg[1].toString(),new PlayerContainerPlayer(player.getName()),cuboidarea);
                                                        if(!Factoid.getConf().CanMakeCollision){
                                                            if(!select.getCollision()){
                                                                if(Factoid.getLands().createLand(land)){
                                                                    player.sendMessage(ChatColor.GREEN+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
                                                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE",player.getName(),land.getName(),land.getAreas().toString()));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                }else{
                                                                    player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR",player.getName()));
                                                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                }
                                                            }else{
                                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION",player.getName()));
                                                            }
                                                        }else{
                                                           if(Factoid.getLands().createLand(land)){
                                                                player.sendMessage(ChatColor.GREEN+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE",player.getName(),land.getName(),land.toString()));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                            }else{
                                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR",player.getName()));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                            }
                                                        }
                                                        this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                        select.resetSelection();
                                                    }else{
                                                        player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE",player.getName()));
                                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                                    }
                                                }else{
                                                    Land landtest = LandSelectioned.get(player.getName().toLowerCase());
                                                    if(landtest == null){
                                                        LandSelection select = this.PlayerSelecting.get(player.getName().toLowerCase());
                                                        Map<String,Location> corner = select.getCorner();
                                                        int x1 = corner.get("FrontCornerLeft").getBlockX();
                                                        int x2 = corner.get("BackCornerRigth").getBlockX();
                                                        //int y1 = corner.get("FrontCornerLeft").getBlockY();
                                                        //int y2 = corner.get("BackCornerRigth").getBlockY();
                                                        int y1 = Factoid.getConf().MinLandHigh;
                                                        int y2 = Factoid.getConf().MaxLandHigh;
                                                        int z1 = corner.get("FrontCornerLeft").getBlockZ();
                                                        int z2 = corner.get("BackCornerRigth").getBlockZ();

                                                        CuboidArea cuboidarea = new CuboidArea(player.getWorld().getName(),x1,y1,z1,x2,y2,z2);
                                                        Land land  = new Land(arg[1].toString(),new PlayerContainerPlayer(player.getName()),cuboidarea);
                                                        if(!Factoid.getConf().CanMakeCollision){
                                                            if(!select.getCollision()){
                                                                if(Factoid.getLands().createLand(land)){
                                                                    player.sendMessage(ChatColor.GREEN+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.LAND"));
                                                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE",player.getName(),land.getName(),land.getAreas().toString()));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                }else{
                                                                    player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR",player.getName()));
                                                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                }
                                                            }else{
                                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.COLLISION"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.COLLISION",player.getName()));
                                                            }
                                                        }else{
                                                           if(Factoid.getLands().createLand(land)){
                                                                player.sendMessage(ChatColor.GREEN+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.DONE",player.getName(),land.getName(),land.toString()));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                            }else{
                                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ERROR"));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ERROR",player.getName()));
                                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.QUITMODE",player.getName()));
                                                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.SELECTMODE"));
                                                            }
                                                        }
                                                        this.PlayerSelecting.remove(player.getName().toLowerCase());
                                                        select.resetSelection();
                                                    }else{
                                                        player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                                        log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE",player.getName()));
                                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                                    }
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.NEEDNAME"));
                                                log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.NEEDNAME",player.getName()));
                                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.ALREADYUSE"));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.CREATE.ALREADYUSE",player.getName()));
                                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.HINTUSE"));
                                        }
                                    }else{
                                        player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.SELECTMODE"));
                                    }
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.SELECTMODE"));
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CREATE.QUIT.FLAGMODE"));
                            }
                    }else if(arg[0].equalsIgnoreCase("flags")){
                        if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                            if(this.LandSelectioned.containsKey(player.getName().toLowerCase())){
                                if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
                                    log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.FLAGS.JOINMODE",player.getName()));
                                    player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
                                    CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                    LandSetFlag setting =  new LandSetFlag(player,area);
                                    this.PlayerSetFlag.put(player.getName().toLowerCase(),setting);
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.ALREADY"));
                                }
                            }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOIN.SELECTMODE"));
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.QUIT.EXPANDMODE"));
                        }
                    }else if(arg[0].equalsIgnoreCase("remove")){
                        if(!this.PlayerExpanding.containsKey(player.getName().toLowerCase())){
                            if(this.LandSelectioned.containsKey(player.getName().toLowerCase())){
                                if(!this.PlayerSetFlag.containsKey(player.getName().toLowerCase())){
                                   if(arg[1] != null){
                                       if(arg[1].equalsIgnoreCase("land")){
                                            Land land = Factoid.getLands().getLand(player.getLocation());
                                            int i = 0;
                                            for(CuboidArea area : land.getAreas()){
                                                //land.removeArea(area);
                                                i++;
                                            }
                                            player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND",land.getName(),i+""));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.DONE.LAND",player.getName(),land.getName()));
                                       }else if(arg[1].equalsIgnoreCase("area")){
                                           CuboidArea area = Factoid.getLands().getCuboidArea(player.getLocation());
                                            player.sendMessage(ChatColor.DARK_GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA",area.getLand().getName()));
                                            log.write(Factoid.getLanguage().getMessage("LOG.COMMAND.REMOVE.DONE.AREA",player.getName(),area.getWorldName(),area.getLand().getName()));
                                       }
                                   }else{
                                       
                                   }
                                }else{
                                    player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.QUIT.FLAGSMODE"));
                                }
                            }else{
                                player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOIN.SELECTMODE"));
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.FLAGS.QUIT.EXPANDMODE"));
                        }
                    }else if(arg[0].equalsIgnoreCase("here") || arg[0].equalsIgnoreCase("current")){
                        Location playerloc = player.getLocation();
                        Land land = Factoid.getLands().getLand(playerloc);
                        if(land != null){
                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.NAME",land.getName()));
                            player.getPlayer().sendMessage(ChatColor.GRAY+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.OWNER",land.getOwner().getContainerType().name(),land.getOwner().getName()));
                            player.getPlayer().sendMessage(ChatColor.GRAY+Factoid.getLanguage().getMessage("COMMAND.CURRENT.LAND.AREA"));
                            for(CuboidArea area : land.getAreas()) {
                                player.getPlayer().sendMessage(ChatColor.GRAY+area.toString());
                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY+"[Factoid] "+Factoid.getLanguage().getMessage("COMMAND.CURRENT.NOLAND"));
                        }
                    }
                    return true;
                }
            }
        }
        
        return false;
    }
}
