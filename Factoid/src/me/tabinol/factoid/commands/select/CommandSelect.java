package me.tabinol.factoid.commands.select;

import java.util.ArrayList;
import java.util.List;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.permissions.PermissionType;
import me.tabinol.factoid.lands.selection.LandMakeSquare;
import me.tabinol.factoid.lands.selection.LandSelection;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/* Note pour Kaz00 : J'ai fait la classe "FactoidCommandException" qui permet de "trower"
 * les erreurs sans aller plus loin et le joueur ressoit le message en rouge.
 * Donc, au lieu de mettre un if(CeciEstCorrect), et d'avoir à aller 500 lignes plus bas
 * pour savoir ce qui se passe quand c'est pas correct, et d'avoir des if indentés jusque chez ton voisin d'à coté,
 * tu fait simplement un if inversé, ton programme n'ira pas plus loin:
 * if(!CeciEstCorrect) {
 *   throw new FactoidCommandException("PATH.DU.MESSAGE.ERREUR");
 * }
 */
public class CommandSelect extends Thread {

    private Player player;

    // Location is only of the selection is with an item
    public CommandSelect(Player player, ArgList argList, Location location) throws FactoidCommandException {

        this.player = player;
        String curArg;

        if (OnCommand.getPlayerSetFlagUI().containsKey(player)) {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.QUIT.FLAGSMODE");
        }
        if (OnCommand.getPlayerExpandingLand().containsKey(player)) {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.QUIT.EXPENDMODE");
        }

        if (!OnCommand.getPlayerSelectingLand().containsKey(player) /* && !OnCommand.getPlayerSelectingWorldEdit().containsKey(player.getName().toLowerCase()) */) {

            Factoid.getLog().write(player.getName() + " join select mode");
            if (!argList.isLast()) {
                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Factoid.getDependPlugin().getWorldEdit() == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new WorldEditSelect(player);
                } else {
                    Land landtest;
                    if (curArg.equalsIgnoreCase("here")) {
                        // add select Here to select the the cuboid
                        if (location != null) {
                            // With an item
                            landtest = Factoid.getLands().getLand(location);
                        } else {
                            // Player location
                            landtest = Factoid.getLands().getLand(player.getLocation());
                        }
                    } else {
                        landtest = Factoid.getLands().getLand(curArg.toString());
                    }
                    if (landtest == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");
                    }
                    PlayerContainer owner = landtest.getOwner();
                    if (!owner.hasAccess(player.getName()) && !Factoid.getPlayerConf().isAdminMod(player)
                            && !landtest.checkPermissionAndInherit(player.getName(), PermissionType.RESIDENT_MANAGER)) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.MISSINGPERMISSION");
                    }
                    if (!OnCommand.getLandSelectioned().containsKey(player)) {
                        OnCommand.getLandSelectioned().put(player, landtest);
                        List<LandMakeSquare> listdummy = new ArrayList<LandMakeSquare>();
                        for (CuboidArea area : landtest.getAreas()) {
                            LandMakeSquare landmake = new LandMakeSquare(player, null,
                                    area.getX1(), area.getX2(), area.getY1(), area.getY2(), area.getZ1(), area.getZ2(), true);
                            landmake.makeSquare();
                            listdummy.add(landmake);
                        }
                        OnCommand.getLandSelectionedUI().put(player, listdummy);

                        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTIONEDLAND", landtest.getName()));
                    } else {
                        player.sendMessage(ChatColor.RED + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANNOTMPODIFY", landtest.getName()));
                    }
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                LandSelection select = new LandSelection(player);
                OnCommand.getPlayerSelectingLand().put(player, select);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {
            if (true /* !OnCommand.getPlayerSelectingWorldEdit().containsKey(player.getName().toLowerCase()) */) {
                if (OnCommand.getLandSelectioned().containsKey(player)) {
                    throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
                }
                // if (!Factoid.getConf().CanMakeCollision) {
                //    if (!select.getCollision()) {
                //        select.setSelected();
                //        player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + "You have selected a new Land.");
                //        // Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                //    } else {
                //        player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                //    }

                //} else {
                //    select.setSelected();
                //    Factoid.getLog().write(Factoid.getLanguage().getMessage("LOG.COMMAND.SELECT.SELECTWITHCOLISSION", player.getName()));
                //    player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.NEWLAND"));
                doSelectDone();
                //}
            } else {
                player.sendMessage(ChatColor.RED + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANTDONEWORLDEDIT"));
            }
        } else {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    private void doSelectDone() throws FactoidCommandException {

        LandSelection select = OnCommand.getPlayerSelectingLand().get(player);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
            select.setSelected();
        } else {
            player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
            select.setSelected();
        }
    }

}
