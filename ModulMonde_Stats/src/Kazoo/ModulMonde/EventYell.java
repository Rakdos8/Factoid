package Kazoo.ModulMonde;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class EventYell {
    
    private Map<String,String> EventList = new HashMap<String,String>();
    private boolean IsRunning = true;
    private JavaPlugin Plugin = null;
    private Mysql mysql;
    
    public EventYell(JavaPlugin plugin, Mysql Mysql){
        super();
        Plugin = plugin;
        mysql = Mysql;
    }
    
    
    public void addList(String time,String name){
        EventList.put(time,name);
    }
    
    public void removeList(String time,String name){
        EventList.remove(time);
    }
    
    public void doCheck(){
        
            long firstDelay = 18000; //  15 min
            long period = 18000; // 15 min
            int RepTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin, new Runnable() {

            @Override
            public void run() {
                try{
                    ResultSet data = mysql.Query("SELECT title FROM evenement WHERE jour='"+Dates.date()+"'");
                    
                        while(data.next()){
                                if(!EventList.containsKey(data.getString("heure"))){
                                    addList(data.getString("heure"),data.getString("title")); 
                                }
    
                        }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for(Map.Entry<String,String> entry : EventList.entrySet()){
                    String[] time = entry.getKey().split(":");
                    int hour = Integer.parseInt(time[0]);
                    String[] time_now = Dates.time().split(":");
                    int hour_now = Integer.parseInt(time_now[0]);
                    if((hour-hour_now) <= 2){
                        for(Player player : Bukkit.getOnlinePlayers()){
                            player.sendMessage(ChatColor.DARK_PURPLE+"[Event] "+ChatColor.YELLOW+"l'événement '"+entry.getValue()+"' auras lieux à "+entry.getKey());
                        }
                    }
                  
                }
            }

            }, firstDelay, period);
        
    }
}
