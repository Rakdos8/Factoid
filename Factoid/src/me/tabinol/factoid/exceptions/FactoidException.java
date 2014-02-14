package me.tabinol.factoid.exceptions;

import me.tabinol.factoid.Factoid;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FactoidException extends Exception {

    public FactoidException(String logMsg, CommandSender sender, String langMsg, String... param) {

        super(logMsg);
        if (sender != null) {
            Factoid.getLog().write("Player: " + sender.getName() + ", Lang Msg: " + langMsg + ", " + logMsg);
        } else {
            Factoid.getLog().write(logMsg);
        }
        if (sender != null) {
            sender.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage(langMsg, param));
        }

    }
}
