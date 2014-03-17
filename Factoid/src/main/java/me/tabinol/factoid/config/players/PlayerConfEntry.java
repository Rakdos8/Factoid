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
package me.tabinol.factoid.config.players;

    // Entries for each player
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.land.LandMakeSquare;
import me.tabinol.factoid.lands.selection.area.AreaMakeSquare;
import me.tabinol.factoid.lands.selection.land.LandSelection;
import me.tabinol.factoid.lands.selection.area.AreaSelection;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerConfEntry {
    
    private final CommandSender sender; // The player (or sender)
    private final Player player; // The player (if is not console)
    private boolean adminMod = false; // If the player is in Admin Mod
    private LandSelection landSelection = null; // For all area in an Land selection
    private AreaSelection areaSelection = null; // For all area in an Land selection
    private Land landSelected = null; // When a land is selected
    private CuboidArea areaSelected = null; // When a area is selected
    private List<LandMakeSquare> landSelectedUI = null; // UI selection
    private List<AreaMakeSquare> areaSelectedUI = null; // UI selection
    private LandExpansion expandingLand = null; // Land expand
    private LandSetFlag setFlagUI = null; // Land selection for UI flags
    private ConfirmEntry confirm = null; // "/factoid confirm" command
    private ChatPage chatPage = null; // pages for "/factoid page" command
    private long lastMoveUpdate = 0; // Time of lastupdate for PlayerEvents
    private Land lastLand = null; // Last Land for player
    private Location lastLoc = null; // Present location
    private boolean tpCancel = false; // If the player has a teleportation cacelled
    private PlayerAutoCancelSelect cancelSelect = null; // Auto cancel selection system
    private PlayerContainerPlayer playerContainer; // PlayerContainer represent the player

    PlayerConfEntry(CommandSender sender) {
        
        this.sender = sender;
        if (sender instanceof Player) {
            player = (Player) sender;
            playerContainer = Factoid.getPlayerUUID().getPCPFromString(player.getName(), player.getUniqueId(), player);
            playerContainer.setPlayer(player);
        } else {
            player = null;
        }
    }
    
    public PlayerContainerPlayer getPlayerContainer() {
        
        return playerContainer;
    }
    
    public CommandSender getSender() {
        
        return sender;
    }
    
    public Player getPlayer() {
        
        return player;
    }
    
    public boolean isAdminMod() {

        // Security for adminmod
        if (adminMod == true && !sender.hasPermission("factoid.adminmod")) {
            adminMod = false;
            return false;
        }
        
        return adminMod;
    }
    
    public void setAdminMod(boolean value) {
        
        adminMod = value;
    }
    
    public LandSelection getLandSelection() {
        
        return landSelection;
    }
    
    public void setLandSelection(LandSelection newlandSelection) {
        
        landSelection = newlandSelection;
    }
    
    public AreaSelection getAreaSelection() {
        
        return areaSelection;
    }
    
    public void setAreaSelection(AreaSelection newareaSelection) {
        
        areaSelection = newareaSelection;
    }
    
    public CuboidArea getAreaSelected() {
        
        return areaSelected;
    }
    
    public void setAreaSelected(CuboidArea area) {
        
        areaSelected = area;
    }
    
    public List<AreaMakeSquare> getAreaSelectedUI() {
        
        return areaSelectedUI;
    }
    
    public void setAreaSelectedUI(List<AreaMakeSquare> list) {
        
        areaSelectedUI = list;
    }
    
    public Land getLandSelected() {
        
        return landSelected;
    }
    
    public void setLandSelected(Land land) {
        
        landSelected = land;
    }
    
    public List<LandMakeSquare> getLandSelectedUI() {
        
        return landSelectedUI;
    }
    
    public void setLandSelectedUI(List<LandMakeSquare> list) {
        
        landSelectedUI = list;
    }
    
    public LandExpansion getExpendingLand() {
        
        return expandingLand;
    }
    
    public void setExpandingLand(LandExpansion landExpansion) {
        
        expandingLand = landExpansion;
    }
    
    public LandSetFlag getSetFlagUI() {
        
        return setFlagUI;
    }
    
    public void setSetFlagUI(LandSetFlag setFlag) {
        
        setFlagUI = setFlag;
    }
    
    public ConfirmEntry getConfirm() {
        
        return confirm;
    }
    
    public void setConfirm(ConfirmEntry entry) {
        
        confirm = entry;
    }
    
    public ChatPage getChatPage() {
        
        return chatPage;
    }
    
    public void setChatPage(ChatPage page) {
        
        chatPage = page;
    }
    
    public long getLastMoveUpdate() {
        
        return lastMoveUpdate;
    }
    
    public void setLastMoveUpdate(Long lastMove) {
        
        lastMoveUpdate = lastMove;
    }
    
    public Land getLastLand() {
        
        return lastLand;
    }
    
    public void setLastLand(Land land) {
        
        lastLand = land;
    }
    
    public Location getLastLoc() {
        
        return lastLoc;
    }
    
    public void setLastLoc(Location loc) {
        
        lastLoc = loc;
    }
    
    public boolean hasTpCancel() {
        
        return tpCancel;
    }
    
    public void setTpCancel(boolean tpCancel) {
        
        this.tpCancel = tpCancel;
    }

    // Set auto cancel select
    public void setAutoCancelSelect(boolean value) {
        
        Long timeTick = Factoid.getConf().getSelectAutoCancel();
        
        if (timeTick == 0) {
            return;
        }
        
        if (cancelSelect == null && value == true) {
            cancelSelect = new PlayerAutoCancelSelect(this);
        }
        
        if (cancelSelect == null) {
            return;
        }
        
        if (value == true) {

            // Schedule task
            cancelSelect.runLater(timeTick, false);
        } else {

            // Stop!
            cancelSelect.stopNextRun();
        }
    }
}
