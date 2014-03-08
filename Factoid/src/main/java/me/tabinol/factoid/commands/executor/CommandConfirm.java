package me.tabinol.factoid.commands.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import org.bukkit.ChatColor;

public class CommandConfirm extends CommandExec {

    public CommandConfirm(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        ConfirmEntry confirmEntry;

        if ((confirmEntry = entity.playerConf.getConfirm()) != null) {
            
            if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.REMOVE_LAND) {

                // Remove land
                int i = confirmEntry.land.getAreas().size();
                try {
                    Factoid.getLands().removeLand(confirmEntry.land);
                } catch (FactoidLandException ex) {
                    Logger.getLogger(CommandConfirm.class.getName()).log(Level.SEVERE, "On land remove", ex);
                    throw new FactoidCommandException("On land remove", entity.player, "GENERAL.ERROR");
                }
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", confirmEntry.land.getName(), i + ""));
                Factoid.getLog().write(entity.playerName + " confirm for removing " + confirmEntry.land.getName());

            } else if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.REMOVE_AREA) {

                // Remove area
                if (!confirmEntry.land.removeArea(confirmEntry.areaNb)) {
                    throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
                }
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", confirmEntry.land.getName()));
                Factoid.getLog().write("area " + confirmEntry.areaNb + " for land " + confirmEntry.land.getName() + " is removed by " + entity.playerName);

            } else if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.LAND_DEFAULT) {

                // Set to default
                confirmEntry.land.setDefault();
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SETDEFAULT.ISDONE", confirmEntry.land.getName()));
                Factoid.getLog().write("The land " + confirmEntry.land.getName() + "is set to default configuration by " + entity.playerName);
            }
            
            // Remove confirm
            entity.playerConf.setConfirm(null);
            
        } else {
            
            throw new FactoidCommandException("Nothing to confirm", entity.player, "COMMAND.NOCONFIRM");
        }
    }
}
