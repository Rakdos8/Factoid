package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.collisions.Collisions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandApprove extends CommandExec {

    public CommandApprove(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();
        ApproveList approveList = Factoid.getLands().getApproveList();
        boolean isApprover = entity.sender.hasPermission("factoid.collisionapprove");

        if (curArg.equalsIgnoreCase("list")) {

            // List of Approve
            StringBuilder stList = new StringBuilder();
            int t = 0;
            for (String approveName : approveList.getApproveList()) {
                Approve app = approveList.getApprove(approveName);
                if (isApprover || app.getOwner().hasAccess(entity.playerName)) {
                    stList.append(ChatColor.WHITE + Factoid.getLanguage().getMessage("COLLISION.SHOW.LIST",
                            ChatColor.BLUE + app.getLandName() + ChatColor.WHITE,
                            app.getOwner().getPrint() + ChatColor.WHITE,
                            ChatColor.BLUE + app.getAction().toString() + ChatColor.WHITE));
                    stList.append(Config.NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                new ChatPage("COLLISION.SHOW.LISTSTART", stList.toString(), entity.sender, null).getPage(1);
            }
        } else if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm") || curArg.equalsIgnoreCase("cancel")) {

            String param = entity.argList.getNext();

            if (param == null) {
                throw new FactoidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            Approve approve = approveList.getApprove(param);

            // Check permission
            if (approve == null || (curArg.equalsIgnoreCase("confirm") && !isApprover)
                    || ((curArg.equalsIgnoreCase("cancel") || curArg.equalsIgnoreCase("info"))
                    && !(isApprover || approve.getOwner().hasAccess(entity.playerName)))) {
                throw new FactoidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            Land land = Factoid.getLands().getLand(param);
            Collisions.LandAction action = approve.getAction();
            int removeId = approve.getRemovedAreaId();
            CuboidArea newArea = approve.getNewArea();
            Land parent = approve.getParent();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {

                // Info on the specified land (Collision)
                checkCollision(param, land, action, removeId, newArea, parent, false);

                if (curArg.equalsIgnoreCase("confirm")) {

                    // Create the action (if it is possible)
                    approveList.removeApprove(approve);
                    approve.createAction();
                    entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.DONE"));
                }
            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approveList.removeApprove(approve);
                entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
        }
    }
}