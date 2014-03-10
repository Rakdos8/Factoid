package Kazoo.ModulMonde.functions;

import Kazoo.ModulMonde.Mysql;

public class OnDisable extends Thread implements Runnable{
	private Mysql mysql;
	
	public OnDisable(Mysql mysql){
		this.mysql = mysql;
	}
	
	public void run(){
		try {
			 int ds = mysql.UpdateQuery("DELETE FROM OnlinePlayers");
			 if(ds==1)
				{
					System.out.println("[ModulMonde:Stats](info) Delete all Players of the Who's Online list.");
				}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
