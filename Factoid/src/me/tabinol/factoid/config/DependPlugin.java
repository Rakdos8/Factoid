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
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    
    public DependPlugin() {
        
        if(Factoid.getConf().UseWorldEdit){
            worldEdit = getPlugin("WorldEdit");
        }
        setupPermissions();
        setupChat();
        setupEconomy();
    }

    private Plugin getPlugin(String pluginName) {

        Plugin plugin = Factoid.getThisPlugin().getServer().getPluginManager().getPlugin(pluginName);

        if(plugin != null) {
            Factoid.getThisPlugin().getServer().getPluginManager().enablePlugin(plugin);
            Factoid.getLog().write(pluginName + " detected!");
            Factoid.getThisPlugin().getLogger().log(Level.INFO, pluginName + " detected!");
        } else {
            Factoid.getLog().write(pluginName + " NOT detected!");
            Factoid.getThisPlugin().getLogger().log(Level.INFO, pluginName + " NOT Detected!");
        }
        
        return plugin;
    }
    
    public Plugin getWorldEdit() {
        
        return worldEdit;
    }
    
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }


}
