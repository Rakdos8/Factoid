package me.tabinol.factoid.commands;

import me.tabinol.factoid.Factoid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

public class ChatPage {

    private int pageHeight;
    private int pageWidth;
    private String header;
    private String text;
    private Player player;
    private int totalPages;

    public ChatPage(String header, String text, Player player) throws FactoidCommandException {

        pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 2;
        pageWidth = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH;
        this.header = header;
        this.text = text;
        this.player = player;
        getPage(1);
    }

    public final void getPage(int pageNumber) throws FactoidCommandException {

        ChatPaginator.ChatPage page = ChatPaginator.paginate(text, pageNumber, pageWidth, pageHeight);
        totalPages = page.getTotalPages();

        if (pageNumber > totalPages) {
            throw new FactoidCommandException("COMMAND.PAGE.INVALID");
        }
        player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage(header));
        player.sendMessage(page.getLines());
        if (totalPages > 1) {
            player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.MULTIPAGE",
                    "" + pageNumber, "" + totalPages));
        } else {
            player.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.ONEPAGE"));
        }
    }
    
    public final int getTotalPages() {
        
        return totalPages;
    }
}
