package me.tabinol.factoid.scoreboard;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import me.tabinol.factoid.Factoid;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

public class ScoreBoard extends Thread{
    
    private ScoreboardManager manager;
    private Map<String,Scoreboard> ScoreboardList = new HashMap<String,Scoreboard>();
    
    public ScoreBoard(){
        super();
        this.manager = Factoid.getThisPlugin().getServer().getScoreboardManager();
    }
    
    public void sendScoreboard(TreeSet<String> playerlist,Player player, String LandName){
        resetScoreboard(player);
        Scoreboard scoreboard = manager.getNewScoreboard();
        ScoreboardList.put(player.getName().toLowerCase(),scoreboard);
        scoreboard.registerNewObjective("land", "dummy");
        scoreboard.getObjective("land").setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective("land").setDisplayName(Factoid.getLanguage().getMessage("SCOREBOARD.LANDINFO"));
        for(String p : playerlist){
            scoreboard.getObjective("land").getScore(Factoid.getThisPlugin().getServer().getPlayer(p)).setScore(0);
        }
        scoreboard.getObjective("land").getScore(player).setScore(0);// Note: A voir si preferable de se voir soi meme ou non dans le land.
        player.setScoreboard(scoreboard);
    }
    
    public Scoreboard getScoreboard(Player player){
        if(ScoreboardList.containsKey(player.getName().toLowerCase())){
            return ScoreboardList.get(player.getName().toLowerCase());
        }
        
        return null;
    }
    
    public ScoreboardManager getScoreboardManager(){
        return manager;
    }

    
    public void resetScoreboard(Player player){
        if(ScoreboardList.containsKey(player.getName().toLowerCase())){
            ScoreboardList.get(player.getName().toLowerCase()).getObjective("land").unregister();
            ScoreboardList.get(player.getName().toLowerCase()).resetScores(player);
            ScoreboardList.remove(player.getName().toLowerCase());
        }
    }
}
