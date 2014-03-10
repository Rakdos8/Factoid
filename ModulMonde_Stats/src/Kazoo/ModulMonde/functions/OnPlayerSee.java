package Kazoo.ModulMonde.functions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import Kazoo.ModulMonde.Mysql;

public class OnPlayerSee extends Thread implements Runnable{
	private Mysql mysql;
	private Player sender;
	private OfflinePlayer target;
	private String group;

	public OnPlayerSee(OfflinePlayer target,Player sender,String group,Mysql mysql){
		this.target = target;
		this.sender = sender;
		this.mysql = mysql;
		this.group = group;
	}
	
	public void run(){
		try {
			ResultSet datat = mysql.Query("SELECT user_id FROM phpbb_users WHERE username_clean='"+target.getName().toLowerCase()+"'");
			
			if(datat != null)
			{
				if(datat.next()){
					ResultSet data = mysql.Query("SELECT pf_lureglements,pf_prenom,pf_ncompteminecraft,pf_pays,pf_anneedenaissance,pf_combiendetemps,pf_jouerautreserveur,pf_entendparler,pf_aaporter,pf_autrecomentaires,pf_questionrobot FROM phpbb_profile_fields_data WHERE user_id='"+datat.getInt(1)+"'");
					sender.sendMessage(ChatColor.GOLD+"[========Profils======]");
					if(data != null){
						while(data.next()){
							sender.sendMessage(ChatColor.YELLOW+"[Username]: "+ChatColor.WHITE+target.getName()+"");
							if(data.getInt(11) >= 100 && data.getInt(11) <= 999){
								if(data.getBoolean(1)){
									sender.sendMessage(ChatColor.YELLOW+"[Reglement]: "+ChatColor.GREEN+"Vu");
								}else{
									sender.sendMessage(ChatColor.YELLOW+"[Reglement]: "+ChatColor.RED+"Non-Vu");
								}
								if(target.hasPlayedBefore() && group.equalsIgnoreCase("nouveaux"))
								{
									sender.sendMessage(ChatColor.YELLOW+"[Statut]: "+ChatColor.GREEN+"S'est déjà connecter et à fait les questions.");
								}else if(target.hasPlayedBefore()){
									sender.sendMessage(ChatColor.YELLOW+"[Statut]: "+ChatColor.GOLD+"S'est déjà connecter mais n'a pas fait les questions.");
								}else if(group.equalsIgnoreCase("nouveaux")){
									sender.sendMessage(ChatColor.YELLOW+"[Statut]: "+ChatColor.GOLD+"À fait les questions mais se n'est jamais connecter.");
								}else {
									sender.sendMessage(ChatColor.YELLOW+"[Statut]: "+ChatColor.RED+"Ne s'est jamais connecter et n'a pas fait les questions.");
								}
								sender.sendMessage(ChatColor.YELLOW+"[Prenom]: "+ChatColor.WHITE+data.getString(2)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Compte]: "+ChatColor.WHITE+data.getString(3)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Pays]: "+ChatColor.WHITE+data.getString(4)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Naissance]: "+ChatColor.WHITE+data.getString(5)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Temps]: "+ChatColor.WHITE+data.getString(6)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Autre serveur]: "+ChatColor.WHITE+data.getString(7)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Entendu parler]: "+ChatColor.WHITE+data.getString(8)+"");
								sender.sendMessage(ChatColor.YELLOW+"[A apporter]: "+ChatColor.WHITE+data.getString(9)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Commentaire]: "+ChatColor.WHITE+data.getString(10)+"");
								sender.sendMessage(ChatColor.YELLOW+"[Question Robot]: "+ChatColor.GREEN+"Ok");
							}else{
								sender.sendMessage(ChatColor.YELLOW+"[Question Robot]: "+ChatColor.RED+"Erreur");
							}
						}
					}
				}
				sender.sendMessage(ChatColor.GOLD+"[=========================]");
			}else{
				sender.sendMessage(ChatColor.DARK_RED+"[ModulMonde:erreur]"+ChatColor.YELLOW+" Ce joueur n'existe pas.");
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
