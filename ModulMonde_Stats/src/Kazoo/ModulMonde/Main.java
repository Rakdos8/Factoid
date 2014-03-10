package Kazoo.ModulMonde;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;
import org.bukkit.World;

import Kazoo.ModulMonde.functions.OnDisable;
import Kazoo.ModulMonde.functions.OnPlayerJoin;
import Kazoo.ModulMonde.functions.OnPlayerQuit;


public class Main extends JavaPlugin implements Listener{

	private static net.milkbowl.vault.permission.Permission permission = null;
	private Mysql mysql;
	private File configFile;
	private FileConfiguration config;
	private Server Server;
	private World world;
	private List<String> VanishedPlayer = new ArrayList<String>();
	private command commandThread;
        private static EventYell eventyell;
	
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = Server.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
		    permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
 	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
 		Player player = event.getPlayer();
 		new OnPlayerJoin(player.getName(),permission.getPrimaryGroup(player),mysql).start();
	}
 	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		new OnPlayerQuit(event.getPlayer().getName(),mysql).start();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		return commandThread.Command(sender,cmd,label,arg);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)
	{
		Player sender = event.getPlayer();
		String[] message = event.getMessage().split(" ");
		
		if(message[0].equalsIgnoreCase("/dynmap")){
			Player target;
			if(message.length > 2){
				target = Server.getPlayer(message[2]);
			}else{
				target = sender;
			}
			
			if(message[1].equalsIgnoreCase("hide")){
				new OnPlayerQuit(target.getName(),mysql).start();
			}else if(message[1].equalsIgnoreCase("show")){
				new OnPlayerJoin(target.getName(),permission.getPrimaryGroup(target),mysql).start();
			}
		}else if(message[0].equalsIgnoreCase("/vanish")){
			Player target = sender;
			if(message.length > 1){
				if(message[1].equalsIgnoreCase("fakequit")){
					new OnPlayerQuit(target.getName(),mysql).start();
				}else if(message[1].equalsIgnoreCase("fakejoin")){
					new OnPlayerJoin(target.getName(),permission.getPrimaryGroup(target),mysql).start();
				}
			}else{
				if(VanishedPlayer.contains(target.getName())){
					new OnPlayerJoin(target.getName(),permission.getPrimaryGroup(target),mysql).start();
					VanishedPlayer.remove(target.getName());
				}else{
					new OnPlayerQuit(target.getName(),mysql).start();
					VanishedPlayer.add(target.getName());
				}
			}
		}
	}
	
	public void onDisable() 
	{
		if(mysql != null){
			new OnDisable(mysql).start();
			mysql.close();
		}
	}	 
			
	@Override
	public void onEnable() 
	{
		getServer().getPluginManager().registerEvents(this, this);
		configFile = new File(getDataFolder(), "config.yml");
		try
		{
			firstRun();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		config = new YamlConfiguration();
		loadYamls();
		Server = getServer();
		mysql = new Mysql(config.getString("mysql.username"),config.getString("mysql.password"),config.getString("mysql.database"),config.getString("mysql.host"));
		new OnDisable(mysql).start();
		world = Server.getWorld("world");
		setupPermissions();
		commandThread = new command(getServer(),mysql,permission,world);
                eventyell = new EventYell(this,mysql);
	}
		
	private void firstRun() throws Exception 
	{
	    if(!configFile.exists())
	    {
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
		}
	}
				
	private void copy(InputStream in, File file) 
	{
	    try 
	    {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0)
	        {
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
			
	public void saveYamls() 
	{
	    try 
	    {
	        config.save(configFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
		
	public void loadYamls() 
	{
	    try 
	    {
	        config.load(configFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
        
        public static EventYell getEventYell(){
            return eventyell;
        }
}
