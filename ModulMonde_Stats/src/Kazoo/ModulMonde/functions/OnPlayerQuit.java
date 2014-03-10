package Kazoo.ModulMonde.functions;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerQuit extends Thread implements Runnable{
	private String username;
	private Mysql mysql;
	
	public OnPlayerQuit(String username,Mysql mysql){
		this.username = username;
		this.mysql = mysql;
	}
	
	public void run(){
		try {
			boolean data = mysql.SearchQuery("SELECT username FROM OnlinePlayers WHERE username='"+username+"'");
		
			if(data)
			{
				 int ds = mysql.UpdateQuery("DELETE FROM OnlinePlayers WHERE username='"+username+"'");
				
				if(ds==1)
				{
					System.out.println("[ModulMonde:Stats](info) Delete a Player of the Who's Online list.");
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
