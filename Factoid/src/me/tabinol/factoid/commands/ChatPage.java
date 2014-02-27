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
        getPage(1);
    }

    public final void getPage(int pageNumber) throws FactoidCommandException {

        ChatPaginator.ChatPage page = ChatPaginator.paginate(text, pageNumber, pageWidth, pageHeight);
        totalPages = page.getTotalPages();

        if (pageNumber > totalPages) {
            throw new FactoidCommandException("Page error", sender, "COMMAND.PAGE.INVALID");
        }
        if (param != null) {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage(header,
                    ChatColor.GREEN + param + ChatColor.GRAY));
        } else {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage(header));
        }
        sender.sendMessage(page.getLines());
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.MULTIPAGE",
                    "" + pageNumber, "" + totalPages));
        } else {
            sender.sendMessage(ChatColor.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.ONEPAGE"));
        }
    }

    public final int getTotalPages() {

        return totalPages;
    }
}
