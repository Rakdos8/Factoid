package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Dates;
import Kazoo.ModulMonde.Mysql;

public class OnCommandEvents extends Thread implements Runnable{
	private Mysql mysql;
	private Player sender;
	private String[] arg;

	public OnCommandEvents(Player sender,String[] arg,Mysql mysql){
		this.sender = sender;
		this.mysql = mysql;
		this.arg = arg;
	}
	
	public void run(){

	if(arg.length < 2 || arg[1].equalsIgnoreCase("help"))
	{
		if(sender.hasPermission("modul.adminevent") || sender.hasPermission("modul.modul.adminevent"))
		{
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event add //Ajoute un événement au calendrier");
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event remove //Supprime un événement au calendrier");
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event modify //Modifie un événement au calendrier");
		}
		sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event list //affiche la liste des événement");
		sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" Pour obtenir de l'aide faite /modul [command] help exemple : /modul event help");
	}else if(arg[1].equalsIgnoreCase("add") && (sender.hasPermission("modul.adminevent") || sender.hasPermission("modul.modul.adminevent")))
	{
		if(arg.length < 4 || arg[2].equalsIgnoreCase("help"))
		{
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event add [titre] [date] [heure]");
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+"Exemple: /modul event add UNevent 02-05-2013 15:10");
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+"Pour obtenir la date d'aujourd'hui écriver 'today' à la place du champs 'date'");
			sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" Pour obtenir de l'aide faite /modul [command] help exemple : /modul event help");
		}else{
				String title = arg[2];
				String jour = arg[3];
				if(jour.equalsIgnoreCase("today"))
				{
					jour = Dates.date();
				}
				String heure = arg[4];
				try {
					boolean data = mysql.SearchQuery("SELECT title FROM evenement WHERE title='"+title+"' AND jour='"+jour+"' AND heure='"+heure+"'");
				
					if(!data)
					{
						int ds = mysql.UpdateQuery("INSERT INTO evenement (title,jour,heure) VALUES ('"+title+"','"+jour+"','"+heure+"')");
						if(ds==1)
						{
							System.out.println(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez ajouter un événement avec succès.");
							sender.sendMessage(ChatColor.GREEN+"[ModulMonde:Evenement] Vous avez ajouter un Événement avec succès.");
						}else
						{
							System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'ajout de l'événement à échouer.");
							sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'ajout de l'événement à échouer.");
						}
					}else
					{
						System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Un événement de ce nom existe déjà.");
						sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Un événement de ce nom existe déjà.");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(arg[1].equalsIgnoreCase("remove") && (sender.hasPermission("modul.adminevent") || sender.hasPermission("modul.modul.adminevent")))
		{
			if(arg.length < 3 || arg[2].equalsIgnoreCase("help"))
			{
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event remove [titre]");
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+"Exemple: /modul event remove UNevent");
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" Pour obtenir de l'aide faite /modul [command] help exemple : /modul event help");

			}else{
				String title = arg[2];
				try {
					boolean data = mysql.SearchQuery("SELECT title FROM evenement WHERE title='"+title+"'");
				
					if(data)
					{
						int ds = mysql.UpdateQuery("DELETE FROM evenement WHERE title='"+title+"'");
	    				if(ds==1)
	    				{
	    					
	    					System.out.println(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez supprimer un événement avec succès.");
	    					sender.sendMessage(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez supprimer un événement avec succès.");
	    				}else{
	    					
	    					System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La suppresion de l'événement à échouer.");
	    					sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La suppresion de l'événement à échouer.");
	    				}
					}else{
						
						System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
						sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(arg[1].equalsIgnoreCase("modify") && (sender.hasPermission("modul.adminevent") || sender.hasPermission("modul.modul.adminevent")))
		{
			if(arg.length < 5 || arg[2].equalsIgnoreCase("help"))
			{
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" /modul event modify [champ] [titre] [modification] ");
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+"Exemple: /modul event modify titre UNevent uneventmodifier");
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+"Pour obtenir la date d'aujourd'hui faite 'today' à la place du champs 'date'");
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Help]"+ChatColor.YELLOW+" Pour obtenir de l'aide faite /modul [command] help exemple : /modul event help");

			}else{
				if(arg[2].equalsIgnoreCase("title"))
				{
					String title = arg[3];
					String ntitle = arg[4];
					try {
						boolean data = mysql.SearchQuery("SELECT title FROM evenement WHERE title='"+title+"'");
					
						if(data)
						{
							int ds = mysql.UpdateQuery("UPDATE evenement SET title='"+ntitle+"' WHERE title='"+title+"'");
		    				if(ds==1)
		    				{
		    					System.out.println(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un événement avec succès.");
		    					sender.sendMessage(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un événement avec succès.");
		    				}else
		    				{
		    					System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    					sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    				}
						}else
						{
							System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
							sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if(arg[2].equalsIgnoreCase("jour"))
				{
					String title = arg[3];
					String jour = arg[4];
					if(jour.equalsIgnoreCase("today"))
					{
						jour = Dates.date();
					}
					try {
						boolean data = mysql.SearchQuery("SELECT title FROM evenement WHERE title='"+title+"'");
					
						if(data)
						{
							int ds = mysql.UpdateQuery("UPDATE evenement SET jour='"+jour+"' WHERE title='"+title+"'");
		    				if(ds==1)
		    				{
		    					System.out.println(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un événement avec succès.");
		    					sender.sendMessage(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un événement avec succès.");
		    				}else
		    				{
		    					System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    					sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    				}
						}else
						{
							System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
							sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(arg[2].equalsIgnoreCase("heure"))
				{
					String title = arg[3];
					String heure = arg[4];
					try {
						boolean data = mysql.SearchQuery("SELECT title FROM evenement WHERE title='"+title+"'");
					
						if(data)
						{
							int ds = mysql.UpdateQuery("UPDATE evenement SET heure='"+heure+"' WHERE title='"+title+"'");
		    				if(ds==1)
		    				{
		    					System.out.println(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un évenement avec succès.");
		    					sender.sendMessage(ChatColor.GREEN+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" Vous avez modifier un évenement avec succès.");
		    				}else
		    				{
		    					System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    					sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" La modification de l'événement à échouer.");
		    				}
						}else
						{
							System.out.println(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
							sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:Evenement]"+ChatColor.YELLOW+" L'événement n'existe pas ou plus.");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else if(arg[1].equalsIgnoreCase("list") && (sender.hasPermission("modul.use") || sender.hasPermission("modul.modul.use")))
		{
			try {
				boolean datat = mysql.SearchQuery("SELECT title FROM evenement WHERE jour='"+Dates.date()+"'");
				
				if(datat)
				{
					ResultSet data = mysql.Query("SELECT title,heure FROM evenement WHERE jour='"+Dates.date()+"'");
					sender.sendMessage(ChatColor.GOLD+"[=======Événement(s)======]");
					while(data.next())
					{
						sender.sendMessage(ChatColor.YELLOW+"["+data.getString(2)+"] "+data.getString(1)+"");
					}
					sender.sendMessage(ChatColor.GOLD+"[=========================]");
				}else
				{
					sender.sendMessage(ChatColor.GOLD+"[=======Événement(s)======]");
					sender.sendMessage(ChatColor.YELLOW+"Aucun Événement aujourd'hui.");
					sender.sendMessage(ChatColor.GOLD+"[=========================]");
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Thread.currentThread().interrupt();
		return;
	}
}