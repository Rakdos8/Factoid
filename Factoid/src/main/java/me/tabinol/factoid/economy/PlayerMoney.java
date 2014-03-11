package me.tabinol.factoid.economy;

import me.tabinol.factoid.Factoid;
import net.milkbowl.vault.economy.Economy;

/**
 * Money from players
 *
 * @author Tabinol
 */
public class PlayerMoney {

    private final Economy economy;

    public PlayerMoney() {

        economy = Factoid.getDependPlugin().getEconomy();
    }
    
    public Double getPlayerBalance(String playerName, String worldName) {
        
        return economy.getBalance(playerName, worldName);
    }
    
    public boolean giveToPlayer(String playerName, String worldName, Double amount) {
        
        return economy.depositPlayer(playerName, worldName, amount).transactionSuccess();
    }

    public boolean getFromToPlayer(String playerName, String worldName, Double amount) {
        
        return economy.withdrawPlayer(playerName, worldName, amount).transactionSuccess();
    }
    
    public String toFormat(Double amount) {
        
        return economy.format(amount);
    }
}
