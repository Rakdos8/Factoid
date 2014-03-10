package Kazoo.ModulMonde.functions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerAccept extends Thread implements Runnable{
	private String group;
	private Mysql mysql;
	private Player sender;
	private OfflinePlayer target;

	public OnPlayerAccept(OfflinePlayer target,Player sender,String group,Mysql mysql){
		this.target = target;
		this.sender = sender;
		this.group = group;
		this.mysql = mysql;
	}
	
	public void run(){
		try 
		{
			if(group.equalsIgnoreCase("Nouveaux"))
			{
				 int datat = mysql.UpdateQuery("UPDATE phpbb_users SET user_type='0', user_inactive_reason='0' WHERE username_clean='"+target.getName().toLowerCase()+"'");
				 if(datat == 1)
				 {
					 if(Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd "+target.getName()+" Aspirants"))
					 {
						 sender.sendMessage(ChatColor.GREEN+"[ModulMonde]"+ChatColor.YELLOW+" La déesse Redstone a changer ce joueur de groupe.");
					 }else
					 {
						 sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde]"+ChatColor.YELLOW+" Le dieu LOL ne reconnais pas ce nouveau joueur !");
					 }
				 }else
				 {
					 sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde]"+ChatColor.YELLOW+" Le dieu LOL ne reconnais pas ce nouveau joueur !");
				 }
			}else
			{
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde]"+ChatColor.YELLOW+" Le dieu LOL ne reconnais pas ce nouveau joueur !");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.currentThread().interrupt();
 		return;
	}
}
