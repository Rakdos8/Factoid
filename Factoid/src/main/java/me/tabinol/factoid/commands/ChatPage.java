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
package me.tabinol.factoid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;


/**
 * The Class ChatPage.
 */
public class ChatPage {

	/** The page height. */
	private final int pageHeight;

	/** The page width. */
	private final int pageWidth;

	/** The header. */
	private final String header;

	/** The text. */
	private final String text;

	/** The sender. */
	private final CommandSender sender;

	/** The param. */
	private final String param;

	/** The total pages. */
	private int totalPages;

	/**
	 * Instantiates a new chat page.
	 *
	 * @param header the header
	 * @param text the text
	 * @param sender the sender
	 * @param param the param
	 * @throws FactoidCommandException the factoid command exception
	 */
	public ChatPage(final String header, final String text, final CommandSender sender, final String param) throws FactoidCommandException {

		pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 2;
		pageWidth = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH;
		this.header = header;
		this.text = text;
		this.sender = sender;
		this.param = param;
	}

	/**
	 * Gets the page.
	 *
	 * @param pageNumber the page number
	 * @throws FactoidCommandException the factoid command exception
	 */
	public void getPage(final int pageNumber) throws FactoidCommandException {

		// Create page with Bukkit paginator
		final ChatPaginator.ChatPage page = ChatPaginator.paginate(text, pageNumber, pageWidth, pageHeight);
		totalPages = page.getTotalPages();

		// If the requested page is more than the last age
		if (pageNumber > totalPages) {
			throw new FactoidCommandException("Page error", sender, "COMMAND.PAGE.INVALID");
		}

		// Check if there is a parameter
		if (param != null) {
			sender.sendMessage(ChatColor.GRAY + Factoid.getThisPlugin().iLanguage().getMessage(header,
					ChatColor.GREEN + param + ChatColor.GRAY));
		} else {
			sender.sendMessage(ChatColor.GRAY + Factoid.getThisPlugin().iLanguage().getMessage(header));
		}

		// Send lines to sender
		sender.sendMessage(page.getLines());

		// If there is one or multiple page, put the number of page at the bottom
		if (totalPages > 1) {
			sender.sendMessage(ChatColor.GRAY + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PAGE.MULTIPAGE",
					"" + pageNumber, "" + totalPages));
			Factoid.getThisPlugin().iPlayerConf().get(sender).setChatPage(this);
		} else {
			sender.sendMessage(ChatColor.GRAY + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PAGE.ONEPAGE"));
			Factoid.getThisPlugin().iPlayerConf().get(sender).setChatPage(null);
		}

	}

	/**
	 * Gets the total pages.
	 *
	 * @return the total pages
	 */
	public final int getTotalPages() {

		return totalPages;
	}
}
