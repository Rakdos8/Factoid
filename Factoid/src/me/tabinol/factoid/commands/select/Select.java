package me.tabinol.factoid.commands.select;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/* Note pour Kaz00 : J'ai fait la classe "FactoidCommandException" qui permet de "trower"
 * les erreurs sans aller plus loin et le joueur ressoit le message en rouge.
 * Donc, au lieu de mettre un if(CeciEstCorrect), et d'avoir à aller 500 lignes plus bas
 * pour savoir ce qui se passe quand c'est pas correct, et d'avoir des if indentés jusque chez ton voisin d'à coté,
 * tu fait simplement un if inversé, ton programme n'ira pas plus loin:
 * if(!CeciEstCorrect) {
 *   throw new FactoidCommandException("PATH.DU.MESSAGE.ERREUR");
 * }
 */

public class Select extends Thread {
    
    public Select(Player player,String[] arg) throws FactoidCommandException {
        if (!OnCommand.getPlayerSetFlagUI().containsKey(player.getName().toLowerCase())) {
            if (!OnCommand.getPlayerExpanding().containsKey(player.getName().toLowerCase())) {
                if (!OnCommand.getPlayerSelectingLand().containsKey(player.getName().toLowerCase()) && !OnCommand.getPlayerSelectingWorldEdit().containsKey(player.getName().toLowerCase())) {
                    Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.JOIN", player.getName()));
                    if (arg.length == 2) {
                        if (arg[1].equalsIgnoreCase("worldedit")) {
                            if (Factoid.getDependPlugin().getWorldEdit() == null) {
                                player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOTLOAD"));
                                Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOTLOAD"));
                            } else {
                                LocalSession session = ((WorldEditPlugin) Factoid.getDependPlugin().getWorldEdit()).getSession(player);
                                try {
                                    Region sel;
                                    if (session.getSelectionWorld() != null
                                            && (sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion) {

                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
                                        Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
                                        CuboidArea area = new CuboidArea(sel.getWorld().getName(),
                                                sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(), sel.getMinimumPoint().getBlockZ(),
                                                sel.getMaximumPoint().getBlockX(), sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ());
                                        OnCommand.getPlayerSelectingWorldEdit().put(player.getName().toLowerCase(), area);
                                    } else {
                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED"));
                                        Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED"));
                                    }
                                } catch (Exception ex) {
                                    if (ex instanceof IncompleteRegionException) {
                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET"));
                                        Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET"));
                                    } else {
                                        Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        } else {
                            Land landtest = Factoid.getLands().getLand(arg[1].toString());
                            if (landtest != null) {
                                PlayerContainer owner = landtest.getOwner();
                                if (owner.hasAccess(player.getName()) || OnCommand.isAdminMod(player)) {
                                    if (!OnCommand.getLandSelectioned().containsKey(player.getName().toLowerCase())) {
                                        OnCommand.getLandSelectioned().put(player.getName().toLowerCase(), landtest);
                                        List<LandMakeSquare> listdummy = new ArrayList<LandMakeSquare>();
                                        for (CuboidArea area : landtest.getAreas()) {
                                            LandMakeSquare landmake = new LandMakeSquare(player, null, area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2());
                                            landmake.makeSquare();
                                            listdummy.add(landmake);
                                        }
                                        OnCommand.getLandSelectionedUI().put(player.getName().toLowerCase(), listdummy);
                                        //new ScoreBoard(player, landtest.getName());

                                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTIONEDLAND", landtest.getName()));
                                    } else {
                                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.MISSINGPERMISSION"));
                                    Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.MISSINGPERMISSION", player.getName()));
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.NOLAND"));
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                        player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                        LandSelection select = new LandSelection(player, player.getServer());
                        OnCommand.getPlayerSelectingLand().put(player.getName().toLowerCase(), select);
                    }
                } else if (arg.length > 1 && arg[1].equalsIgnoreCase("done")) {
                    if (!OnCommand.getPlayerSelectingWorldEdit().containsKey(player.getName().toLowerCase())) {
                        if (!OnCommand.getLandSelectioned().containsKey(player.getName().toLowerCase())) {
                            LandSelection select = OnCommand.getPlayerSelectingLand().get(player.getName().toLowerCase());
                            if (!Factoid.getConf().CanMakeCollision) {
                                if (!select.getCollision()) {
                                    select.setSelected();
                                    player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + "You have selected a new Land.");
                                    Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                                } else {
                                    player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                                }
                            } else {
                                select.setSelected();
                                Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                                player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.NEWLAND"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONE"));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONEWORLDEDIT"));
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.EXPENDMODE"));
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.QUIT.FLAGSMODE"));
        }
    }
    
}
