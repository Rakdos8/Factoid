/*
 FactoidFlyCreative: Minecraft Factoid plugin for fly and creative control
 Copyright (C) 2014  Michel Blanchet
 Rebuild from ResCreative and ResFly by Kolorafa

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoidflycreative;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoidflycreative.config.FlyCreativeConfig;
import me.tabinol.factoidflycreative.listeners.PlayerListener;

public class FactoidFlyCreative extends JavaPlugin implements Listener {

	private static PlayerListener playerListener;
	private static FactoidFlyCreative thisPlugin;
	private static FlyCreativeConfig config;

	@Override
	public void onEnable() {
		thisPlugin = this;

		// Config
		config = new FlyCreativeConfig();
		config.loadConfig();

		// Activate listeners
		playerListener = new PlayerListener();
		getServer().getPluginManager().registerEvents(playerListener, this);
	}

	public static FactoidFlyCreative getThisPlugin() {
		return thisPlugin;
	}

	public static FlyCreativeConfig getConf() {
		return config;
	}

	public static PlayerListener getPlayerListener() {
		return playerListener;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("fcreload")) {

			FactoidFlyCreative.getConf().reLoadConfig();
			sender.sendMessage("Configuration reloaded!");

			return true;

		}
		return false;
	}
}
