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
package me.tabinol.factoid.config;

import java.util.logging.Level;
import me.tabinol.factoid.Factoid;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DependPlugin {

    private Plugin worldEdit = null;
    private Plugin essentials = null;
    private Plugin vanishNoPacket = null;
    private Permission permission = null;
    private Economy economy = null;
    private Chat chat = null;

    public DependPlugin() {

        worldEdit = getPlugin("WorldEdit");
        essentials = getPlugin("Essentials");
        vanishNoPacket = getPlugin("VanishNoPacket");
        setupPermissions();
        setupChat();
        setupEconomy();
    }

    private Plugin getPlugin(String pluginName) {

        Plugin plugin = Factoid.getThisPlugin().getServer().getPluginManager().getPlugin(pluginName);

        if (plugin != null) {
            Factoid.getThisPlugin().getServer().getPluginManager().enablePlugin(plugin);
            Factoid.getLog().write(pluginName + " detected!");
            Factoid.getThisPlugin().getLogger().log(Level.INFO, pluginName + " detected!");
        } else {
            Factoid.getLog().write(pluginName + " NOT detected!");
            Factoid.getThisPlugin().getLogger().log(Level.INFO, pluginName + " IS NOT Detected!");
        }

        return plugin;
    }

    public Plugin getWorldEdit() {

        return worldEdit;
    }

    public Plugin getEssentials() {
        
        return essentials;
    }
    
    public Plugin getVanishNoPacket() {
        
        return vanishNoPacket;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public Permission getPermission() {
        
        return permission;
    }
    
    public Economy getEconomy() {
        
        return economy;
    }
    
    public Chat getChat() {
        
        return chat;
    }
}
