package me.tabinol.factoid.scoreboard;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import me.tabinol.factoid.Factoid;

public class ScoreBoard extends Thread{
    
    private ScoreboardManager manager;
    private Scoreboard scoreboard;
    private Player player;
    private String LandName;
    
    public ScoreBoard(Player player,String LandName){
        this.manager = Factoid.getThisPlugin().getServer().getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.LandName = LandName;
        scoreboard.registerNewObjective("land", "dummy");
        scoreboard.getObjective("land").setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective("land").setDisplayName(LandName);
        this.player = player;
        scoreboard.getObjective("land").getScore(player).setScore(1);
        player.setScoreboard(this.scoreboard);
    }
    
    public Scoreboard getScoreboard(){
        return scoreboard;
    }
    
    public ScoreboardManager getScoreboardManager(){
        return manager;
    }
    
    public Player getPlayer(){
        return player;
    }
}
