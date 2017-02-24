/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.tabinol.factoid.Factoid;


/**
 * The Class ScoreBoard.
 */
public class ScoreBoard extends Thread{

	/** The manager. */
	private final ScoreboardManager manager;

	/** The Scoreboard list. */
	private final Map<Player,Scoreboard> scoreboardList = new HashMap<>();

	/**
	 * Instantiates a new score board.
	 */
	public ScoreBoard(){
		super();
		this.manager = Factoid.getThisPlugin().getServer().getScoreboardManager();
	}

	/**
	 * Send scoreboard.
	 *
	 * @param playerlist the playerlist
	 * @param player the player
	 */
	public void sendScoreboard(final HashSet<Player> playerlist, final Player player){
		resetScoreboard(player);
		final Scoreboard scoreboard = manager.getNewScoreboard();
		scoreboardList.put(player,scoreboard);
		scoreboard.registerNewObjective("land", "dummy");
		scoreboard.getObjective("land").setDisplaySlot(DisplaySlot.SIDEBAR);
		scoreboard.getObjective("land").setDisplayName(Factoid.getThisPlugin().iLanguage().getMessage("SCOREBOARD.LANDINFO"));
		for (final Player p : playerlist){
			scoreboard.getObjective("land").getScore(p).setScore(0);
		}
		scoreboard.getObjective("land").getScore(player).setScore(0);// Note: A voir si preferable de se voir soi meme ou non dans le land.
		player.setScoreboard(scoreboard);
	}

	/**
	 * Gets the scoreboard.
	 *
	 * @param player the player
	 * @return the scoreboard
	 */
	public Scoreboard getScoreboard(final Player player){
			return scoreboardList.get(player);
	}

	/**
	 * Gets the scoreboard manager.
	 *
	 * @return the scoreboard manager
	 */
	public ScoreboardManager getScoreboardManager(){
		return manager;
	}


	/**
	 * Reset scoreboard.
	 *
	 * @param player the player
	 */
	@SuppressWarnings("deprecation")
	public void resetScoreboard(final Player player){
		if (scoreboardList.containsKey(player)){
			scoreboardList.get(player).getObjective("land").unregister();
			scoreboardList.get(player).resetScores(player);
			scoreboardList.remove(player);
		}
	}
}
