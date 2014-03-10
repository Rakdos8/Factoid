package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerReload extends Thread implements Runnable{
	private Mysql mysql;
	private World world;
	public net.milkbowl.vault.permission.Permission permission = null;

	public OnPlayerReload(World world,net.milkbowl.vault.permission.Permission permission,Mysql mysql){
		this.world = world;
		this.mysql = mysql;
		this.permission = permission;
	}
	
	public void run(){
		for(Player Player : world.getPlayers()){
     		String Player_Username = Player.getName();
     		String Player_Group = permission.getPrimaryGroup(Player);

     		boolean data;
				try {
					data = mysql.SearchQuery("SELECT username FROM OnlinePlayers WHERE username='"+Player_Username+"'");
				
					if(!data)
					{
						int ds;
						ds = mysql.UpdateQuery("INSERT INTO OnlinePlayers (username,groupe) VALUES ('"+Player_Username+"','"+Player_Group+"')");
						
	    				if(ds==1)
	    				{
	    					System.out.println("[ModulMonde:Stats](info) Add a new Player to the Who's'");
	    				}
					}
     		
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 		}
		Thread.currentThread().interrupt();
 		return;
	}
}
