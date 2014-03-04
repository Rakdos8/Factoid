package me.tabinol.factoid.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.expansion.LandExpansion;
import me.tabinol.factoid.lands.flags.LandSetFlag;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.selection.LandSelection;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Contain lists for player (selection, ect, ...)
public class PlayerStaticConfig {

    private final Map<CommandSender, PlayerConfEntry> playerConfList;

    public PlayerStaticConfig() {

        playerConfList = new HashMap<>();

        // Add the consle in the list
        add(Factoid.getThisPlugin().getServer().getConsoleSender());
    }

    // Entries for each player
    public class PlayerConfEntry {

        private final CommandSender sender; // The player (or sender)
        private final Player player; // The player (if is not console)
        private boolean adminMod = false; // If the player is in Admin Mod
        private LandSelection areaSelection = null; // For area selection
        private Land landSelected = null; // When a land is selected
        private List<LandMakeSquare> landSelectedUI = null; // UI selection
        private LandExpansion expandingLand = null; // Land expand
        private LandSetFlag setFlagUI = null; // Land selection for UI flags
        private ConfirmEntry confirm = null; // "/factoid confirm" command
        private ChatPage chatPage = null; // pages for "/factoid page" command
        private long lastMoveUpdate = 0; // Time of lastupdate for PlayerEvents
        private Land lastLand = null; // Last Land for player
        private Location lastLoc = null; // Present location
        private boolean tpCancel = false; // If the player has a teleportation cacelled

        PlayerConfEntry(CommandSender sender) {

            this.sender = sender;
            if(sender instanceof Player) {
                player = (Player) sender;
            } else {
                player = null;
            }
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

        public LandSelection getAreaSelection() {

            return areaSelection;
        }

        public void setAreaSelection(LandSelection landSelection) {

            areaSelection = landSelection;
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
    }

    // Methods for geting a player static config
    public PlayerConfEntry add(CommandSender sender) {

        PlayerConfEntry entry = new PlayerConfEntry(sender);
        playerConfList.put(sender, entry);
        
        return entry;
    }

    public void remove(CommandSender sender) {

        playerConfList.remove(sender);
    }

    public PlayerConfEntry get(CommandSender sender) {

        return playerConfList.get(sender);
    }
}
