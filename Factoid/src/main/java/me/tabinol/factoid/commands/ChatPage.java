package me.tabinol.factoid.commands;

import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.Factoid;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

public class ChatPage {

    private final int pageHeight;
    private final int pageWidth;
    private final String header;
    private final String text;
    private final CommandSender sender;
    private final String param;
    private int totalPages;

    public ChatPage(String header, String text, CommandSender sender, String param) throws FactoidCommandException {

        pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 2;
        pageWidth = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH;
        this.header = header;
        this.text = text;
        this.sender = sender;
        this.param = param;
    }

    public void getPage(int pageNumber) throws FactoidCommandException {

        // Create page with Bukkit paginator
        ChatPaginator.ChatPage page = ChatPaginator.paginate(text, pageNumber, pageWidth, pageHeight);
        totalPages = page.getTotalPages();

        // If the requested page is more than the last age
        if (pageNumber > totalPages) {
            throw new FactoidCommandException("Page error", sender, "COMMAND.PAGE.INVALID");
        }
        
        // Check if there is a parameter
        if (param != null) {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage(header,
                    ChatColor.GREEN + param + ChatColor.GRAY));
        } else {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage(header));
        }
        
        // Send lines to sender
        sender.sendMessage(page.getLines());
        
        // If there is one or multiple page, put the number of page at the bottom
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.MULTIPAGE",
                    "" + pageNumber, "" + totalPages));
            Factoid.getPlayerConf().get(sender).setChatPage(this);
        } else {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.ONEPAGE"));
            Factoid.getPlayerConf().get(sender).setChatPage(null);
        }

    }

    public final int getTotalPages() {

        return totalPages;
    }
}