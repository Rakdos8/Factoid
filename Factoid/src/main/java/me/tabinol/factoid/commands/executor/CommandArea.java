package me.tabinol.factoid.commands.executor;

import java.util.Map;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.commands.ConfirmEntry.ConfirmType;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import org.bukkit.ChatColor;

public class CommandArea extends CommandExec {

    public CommandArea(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {
        
        String curArg = entity.argList.getNext();
        
        if (curArg.equalsIgnoreCase("create")) {

            checkPermission(true, true, null, null);
            checkSelections(false, false, true, null, true);

            CuboidArea area = entity.playerConf.getAreaSelection().toCuboidArea();

            // Check for collision
            if (checkCollision(land.getName(), land, LandAction.AREA_ADD, 0, area, land.getParent(), true)) {
                return;
            }

            // Add Area
            land.addArea(area);

            entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
            Factoid.getLog().write(entity.playerName + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
            new CommandCancel(entity.playerConf, false).commandExecute();

        } else if (curArg.equalsIgnoreCase("remove") || curArg.equalsIgnoreCase("replace")) {

            checkPermission(true, true, null, null);

            String areaNbStr = entity.argList.getNext();
            int areaNb;

            if (areaNbStr == null) {
                throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.EMPTY");
            }
            try {
                areaNb = Integer.parseInt(areaNbStr);
            } catch (NumberFormatException ex) {
                throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
            }
            if (land.getArea(areaNb) == null) {
                throw new FactoidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
            }

            // Only for a remove
            if (curArg.equalsIgnoreCase("remove")) {

                checkSelections(false, false, true, null, true);

                // Check for collision
                if (checkCollision(curArg, land, LandAction.AREA_REMOVE, areaNb, null, land.getParent(), true)) {
                    return;
                }

                // Check if exist
                if (land.getArea(areaNb) == null) {
                    throw new FactoidCommandException("Area", entity.sender, "COMMAND.REMOVE.AREA.INVALID");
                }

                entity.playerConf.setConfirm(new ConfirmEntry(
                        ConfirmType.REMOVE_AREA, land, areaNb));
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CONFIRM"));

            } else {

                //Only for a replace
                checkSelections(false, false, true, true , true);

                CuboidArea area = entity.playerConf.getAreaSelection().toCuboidArea();

                // Check for collision
                if (checkCollision(land.getName(), land, LandAction.AREA_MODIFY, areaNb, area, land.getParent(), true)) {
                    return;
                }

                // Replace Area
                land.replaceArea(areaNb, area);

                entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
                Factoid.getLog().write(entity.playerName + " have create an area named " + land.getName() + " at position " + land.getAreas().toString());
                new CommandCancel(entity.playerConf, false).commandExecute();
            }

        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            for (Map.Entry<Integer, CuboidArea> entry : land.getIdsAndAreas().entrySet()) {
                stList.append("ID: " + entry.getKey() + ", " + entry.getValue().getPrint() + Factoid.getConf().NEWLINE);
            }
            new ChatPage("COMMAND.CURRENT.LAND.AREA", stList.toString(), entity.sender, land.getName()).getPage(1);
        } else {
            throw new FactoidCommandException("Area", entity.sender, "COMMAND.REMOVE.AREA.INVALID");
        }
    }

}