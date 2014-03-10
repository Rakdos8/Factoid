package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerWhois extends Thread implements Runnable{
	private Mysql mysql;
	private Player sender;
	private OfflinePlayer target;
	private Player targetp;
	private String group;

	public OnPlayerWhois(OfflinePlayer target,Player sender,String group,Mysql mysql){
		this.target = target;
		this.sender = sender;
		this.mysql = mysql;
		this.group = group;
		this.targetp = (Player) target;
	}
	
	public void run(){
		sender.sendMessage(ChatColor.GOLD+"[========Profils======]");
		sender.sendMessage(ChatColor.YELLOW+"[Username]: "+ChatColor.WHITE+target.getName()+"");
		sender.sendMessage(ChatColor.YELLOW+"[DisplayName]: "+ChatColor.WHITE+targetp.getDisplayName()+"");
		sender.sendMessage(ChatColor.YELLOW+"[Niveau]: "+ChatColor.WHITE+targetp.getLevel()+"");
		sender.sendMessage(ChatColor.YELLOW+"[Experience]: "+ChatColor.WHITE+targetp.getExp()+"");
		sender.sendMessage(ChatColor.YELLOW+"[Fatigue]: "+ChatColor.WHITE+targetp.getExhaustion()+"");
		sender.sendMessage(ChatColor.YELLOW+"[Saturation]: "+ChatColor.WHITE+targetp.getSaturation()+""); 
		sender.sendMessage(ChatColor.YELLOW+"[Faim]: "+ChatColor.WHITE+targetp.getFoodLevel()+"");
		sender.sendMessage(ChatColor.YELLOW+"[Bed Location]: "+ChatColor.WHITE+targetp.getBedSpawnLocation().getWorld().getName()+" - "+targetp.getBedSpawnLocation().getX()+","+targetp.getBedSpawnLocation().getY()+","+targetp.getBedSpawnLocation().getZ());
		sender.sendMessage(ChatColor.YELLOW+"[Faim]: "+ChatColor.WHITE+targetp.getFoodLevel()+"");
		if(target.isOnline()){
			sender.sendMessage(ChatColor.YELLOW+"[En Ligne]: "+ChatColor.GREEN+"Oui");
		}else{
			sender.sendMessage(ChatColor.YELLOW+"[En Ligne]: "+ChatColor.RED+"Non");
		}
		if(target.isBanned()){
			sender.sendMessage(ChatColor.YELLOW+"[Bannis]: "+ChatColor.GREEN+"Non");
		}else{
			sender.sendMessage(ChatColor.YELLOW+"[Bannis]: "+ChatColor.RED+"Oui");
		}
		Thread.currentThread().interrupt();
 		return;
	}
}
