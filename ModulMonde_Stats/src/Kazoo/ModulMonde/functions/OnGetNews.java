package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnGetNews extends Thread implements Runnable{
	private Mysql mysql;
	private Player sender;

	public OnGetNews(Player sender,Mysql mysql){
		this.sender = sender;
		this.mysql = mysql;
	}
	
	public void run(){
		try 
		{
			boolean datat = mysql.SearchQuery("SELECT topic_title FROM phpbb_topics WHERE forum_id='19' ORDER BY topic_id DESC LIMIT 5");
			if(datat)
			{
				ResultSet data = mysql.Query("SELECT topic_title,forum_id,topic_id FROM phpbb_topics WHERE forum_id='19' ORDER BY topic_id DESC LIMIT 5");
				sender.sendMessage(ChatColor.GOLD+"[========Nouvelle(s)======]");
				while(data.next()){
					sender.sendMessage(ChatColor.YELLOW+"["+data.getString(1)+"]");
					sender.sendMessage(ChatColor.WHITE+"http://www.modulmonde.com/forum/viewtopic.php?f="+data.getString(2)+"&t="+data.getString(3));
				}
				sender.sendMessage(ChatColor.GOLD+"[=========================]");
			}else{
				sender.sendMessage(ChatColor.GOLD+"[========Nouvelle(s)======]");
				sender.sendMessage(ChatColor.YELLOW+"Aucune nouvelles.");
				sender.sendMessage(ChatColor.GOLD+"[=========================]");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.currentThread().interrupt();
 		return;
	}
}
