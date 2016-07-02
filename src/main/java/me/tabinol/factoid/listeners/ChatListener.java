package me.tabinol.factoid.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.lands.Land;

/**
 *
 * Chat listener
 *
 */
public class ChatListener extends CommonListener implements Listener {

	/** The conf. */
	private final Config conf;

	/** The player conf. */
	private final PlayerStaticConfig playerConf;

	/**
	 * Instantiates a new chat listener.
	 */
	public ChatListener() {
		super();
		conf = Factoid.getThisPlugin().iConf();
		playerConf = Factoid.getThisPlugin().iPlayerConf();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if(!conf.isLandChat()) {
			return;
		}

		final String firstChar = event.getMessage().substring(0, 1);
		final Player player = event.getPlayer();

		// Chat in a land
		if(firstChar.equals("=") || firstChar.equals(">") || firstChar.equals("<")) {

			event.setCancelled(true);

			@SuppressWarnings("deprecation")
			final
			Land land = Factoid.getLands().getLand(player.getLocation());

			// The player is not in a land
			if(land == null) {
				player.sendMessage(ChatColor.RED + "[Factoid] "
						+ Factoid.getThisPlugin().iLanguage().getMessage(
								"CHAT.OUTSIDE"));
				return;
			}

			// Return if the player is muted
			if(playerConf.getChat().isMuted(player)) {
				return;
			}

			// Get users list
			Set<Player> playersToMsg;

			if(firstChar.equals("=")) {
				playersToMsg = copyWithSpy(land.getPlayersInLand());
			} else if(firstChar.equals("<")) {
				playersToMsg = copyWithSpy(land.getPlayersInLandAndChildren());
			} else { // ">"
				playersToMsg = copyWithSpy(land.getAncestor(land.getGenealogy()).getPlayersInLandAndChildren());
			}

			final String message = event.getMessage().substring(1);

			// send messages
 			System.out.println(ChatColor.WHITE + "[" + player.getDisplayName()
					+ ChatColor.WHITE + " " + firstChar + " " + "'"
					+ ChatColor.GREEN + land.getName() + ChatColor.WHITE + "'] "
					+ ChatColor.GRAY + message);
			for(final Player playerToMsg : playersToMsg) {
				playerToMsg.sendMessage(ChatColor.WHITE + "[" + player.getDisplayName()
						+ ChatColor.WHITE + " " + firstChar + " " + "'"
						+ ChatColor.GREEN + land.getName() + ChatColor.WHITE + "'] "
						+ ChatColor.GRAY + message);
			}
		}
	}

	private HashSet<Player> copyWithSpy(final Set<Player> a) {

		final HashSet<Player> listSet = new HashSet<Player>();

		for(final Player player : a) {
			listSet.add(player);
		}
		for(final Player player : Bukkit.getOnlinePlayers()) {
			if(playerConf.getChat().isSpy(player)) {
				listSet.add(player);
			}
		}

		return listSet;
	}
}
