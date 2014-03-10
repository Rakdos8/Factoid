package Kazoo.ModulMonde;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.functions.OnCommandEvents;
import Kazoo.ModulMonde.functions.OnGetNews;
import Kazoo.ModulMonde.functions.OnPlayerAccept;
import Kazoo.ModulMonde.functions.OnPlayerReload;
import Kazoo.ModulMonde.functions.OnPlayerSearch;
import Kazoo.ModulMonde.functions.OnPlayerSee;

public class command extends Thread{
	 private Server Server;
	 private Mysql mysql;
	 private World world;
	 public net.milkbowl.vault.permission.Permission permission = null;
	 private Location locevent;
	 
	  public command(Server server,Mysql mysql,net.milkbowl.vault.permission.Permission permission,World world) {
	    this.Server = server;
	    this.mysql = mysql;
	    this.world = world;
	    this.permission = permission;
	  }
	  
	  public boolean Command(CommandSender sender, Command cmd, String label, String[] arg){
		// TODO Auto-generated method stub
	    	if (sender instanceof Player) {
	            // do something
	         } else {
	            sender.sendMessage("Vous devez etre un joueur.");
	            return false;
	         }
	    	
	    	if (cmd.getName().equalsIgnoreCase("modul")){
	    		if(arg.length < 1){
	    			return false;
	    		}
	    		if(arg[0].equalsIgnoreCase("reload") && (sender.hasPermission("modul.reload") || sender.hasPermission("modul.modul.reload"))){
	    			new OnPlayerReload(world,permission,mysql).start();
	    			return true;
	    		}else if(arg[0].equalsIgnoreCase("event")){
	    			new OnCommandEvents((Player)sender,arg,mysql).start();
					return true;
	    		}else if(arg[0].equalsIgnoreCase("help")){
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul reload //Reload les joueurs en ligne");
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event // Gestion du calendrier des Events");
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul news //Obtenir la liste des Nouvelles");
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul infos [joueur] //Obtenir les informations sur un joueur");
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul accept [joueur] //Accepte un joueur au rang d'Aspirant");
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" Pour obtenir de l'aide faite /modul [command] help exemple : /modul event help");
	    			return true;
	    		}else if(arg[0].equalsIgnoreCase("news") && (sender.hasPermission("modul.use") || sender.hasPermission("modul.modul.use"))){
	    			new OnGetNews((Player)sender,mysql).start();
					return true;
	    		}else if(arg[0].equalsIgnoreCase("see") && (sender.hasPermission("modul.see") || sender.hasPermission("modul.modul.see"))){
	    			if(arg.length > 1){
                                    OfflinePlayer target = Server.getOfflinePlayer(arg[1]);
                                    if(target != null){
                                            new OnPlayerSee(target,(Player)sender, permission.getPrimaryGroup("world",target.getName()),mysql).start();
                                            return true;

                                    }
                                }
	    		}else if(arg[0].equalsIgnoreCase("accept") && (sender.hasPermission("modul.accept") || sender.hasPermission("modul.modul.accept"))){
	    			if(arg.length > 1){
                                    OfflinePlayer target = Server.getOfflinePlayer(arg[1]);
                                    if(target != null){
                                            new OnPlayerAccept(target,(Player)sender,permission.getPrimaryGroup("world",target.getName()),mysql).start();
                                            return true;
                                    }
                                }
	    		}else if(arg[0].equalsIgnoreCase("search") && (sender.hasPermission("modul.search") || sender.hasPermission("modul.modul.search"))){
	    			if(arg.length > 1){
	    				new OnPlayerSearch(arg[1],(Player)sender,permission,mysql).start();
	    				return true;
	    			}else{
	    				new OnPlayerSearch(null,(Player)sender,permission,mysql).start();
	    				return true;
	    			}
	    		}
	    		
	    	}else if (cmd.getName().equalsIgnoreCase("tpevent")){
	    		if(arg.length > 1){
	    			return false;
	    		}
	    		
	    		if(locevent != null){
	    			((Player) sender).teleport(locevent);
	    		}else{
	    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde]"+ChatColor.YELLOW+"L'emplacement de téléportation pour l'event n'a pas été crée.");
	    		}
	    		return true;
	    	}else if (cmd.getName().equalsIgnoreCase("tpsetevent")){
	    		if(arg.length > 2){
	    			return false;
	    		}
	    		if(arg[0].equalsIgnoreCase("reset")){
	    			locevent = null;
	    		}else{
		    		if(locevent == null){
		    			locevent = ((Player) sender).getLocation();
		    		}else{
		    			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde]"+ChatColor.YELLOW+"L'emplacement de téléportation pour l'event à déjà été crée.");
		    		}
	    		}
	    		return true;
	    	}
	    	return false;
	  }
}
