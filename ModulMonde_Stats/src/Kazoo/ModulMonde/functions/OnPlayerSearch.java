package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerSearch extends Thread implements Runnable{
	private Mysql mysql;
	private Player sender;
	private String type;
	private List<String> playertype = new ArrayList<String>();
	private net.milkbowl.vault.permission.Permission permission;

	public OnPlayerSearch(String type,Player sender,net.milkbowl.vault.permission.Permission permission,Mysql mysql){
		this.sender = sender;
		this.mysql = mysql;
		this.type = type;
		this.permission = permission;
	}
	
	public void run(){
		if(type == null || type.equals("nouveaux")){
			sender.sendMessage(ChatColor.GOLD+"[========Nouveaux======]");
			for(OfflinePlayer player : sender.getServer().getOfflinePlayers()){
				if(permission.playerInGroup(player.getPlayer(),"nouveaux")){
					sender.sendMessage(ChatColor.GREEN+player.getName());
				}
			}
			sender.sendMessage(ChatColor.GOLD+"[=========================]");
		}
		Thread.currentThread().interrupt();
 		return;
	}
}
