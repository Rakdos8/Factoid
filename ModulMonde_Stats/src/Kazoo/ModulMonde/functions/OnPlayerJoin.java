package Kazoo.ModulMonde.functions;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerJoin extends Thread implements Runnable{
	private Mysql mysql;
	private String username;
	private String group;
	
	public OnPlayerJoin(String username,String group,Mysql mysql){
		this.username = username;
		this.group = group;
		this.mysql = mysql;
	}
	
	public void run(){
		try {
			boolean data = mysql.SearchQuery("SELECT username FROM OnlinePlayers WHERE username='"+username+"'");
		
			if(!data)
			{
				 int ds = mysql.UpdateQuery("INSERT INTO OnlinePlayers (username,groupe) VALUES ('"+username+"','"+group+"')");
				
				if(ds==1)
				{
					System.out.println("[ModulMonde:Stats](info) Add a new Player to the Who's Online list.");
				}
			}
	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.currentThread().interrupt();
 		return;
	}
}
