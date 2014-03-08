package me.tabinol.factoid.scoreboard;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import me.tabinol.factoid.Factoid;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class ScoreBoard extends Thread{
    
    private ScoreboardManager manager;
    private Map<Player,Scoreboard> ScoreboardList = new HashMap<Player,Scoreboard>();
    
    public ScoreBoard(){
        super();
        this.manager = Factoid.getThisPlugin().getServer().getScoreboardManager();
    }
    
    public void sendScoreboard(HashSet<Player> playerlist, Player player, String LandName){
        resetScoreboard(player);
        Scoreboard scoreboard = manager.getNewScoreboard();
        ScoreboardList.put(player,scoreboard);
        scoreboard.registerNewObjective("land", "dummy");
        scoreboard.getObjective("land").setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective("land").setDisplayName(Factoid.getLanguage().getMessage("SCOREBOARD.LANDINFO"));
        for(Player p : playerlist){
            scoreboard.getObjective("land").getScore(p).setScore(0);
        }
        scoreboard.getObjective("land").getScore(player).setScore(0);// Note: A voir si preferable de se voir soi meme ou non dans le land.
        player.setScoreboard(scoreboard);
    }
    
    public Scoreboard getScoreboard(Player player){
            return ScoreboardList.get(player);
    }
    
    public ScoreboardManager getScoreboardManager(){
        return manager;
    }

    
    public void resetScoreboard(Player player){
        if(ScoreboardList.containsKey(player)){
            ScoreboardList.get(player).getObjective("land").unregister();
            ScoreboardList.get(player).resetScores(player);
            ScoreboardList.remove(player);
        }
    }
}
