package me.tabinol.factoid.lands.flags;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import me.tabinol.factoid.lands.Areas.CuboidArea;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class LandSetFlag extends Thread implements Listener{
    
    private Player player;
    private CuboidArea area;
    private ItemStack RedstoneTorchOff = new ItemStack(Material.REDSTONE_TORCH_OFF);
    private ItemStack RedstoneTorchOn = new ItemStack(Material.REDSTONE_TORCH_ON);
    private Inventory inventory;
    
    public LandSetFlag(Player player,CuboidArea area){
        this.player = player;
        this.area = area;
        makeMenu();
        //player.openInventory(inventory);
    }
    
    private void makeMenu(){
        
        inventory = player.getServer().createInventory(null,8,"Flag Setting");
        int i = 0;
       /* for(){
            if(){
                ItemStack Torch = RedstoneTorchOn.clone();
                ItemMeta meta = Torch.getItemMeta();
                List<String> lore = new ArrayList<String>();
                
                meta.setDisplayName("Flag");
                lore.add("BLABLABLA");
                meta.setLore(lore);
                
                Torch.setItemMeta(meta);
                inventory.setItem(i, Torch);
            }else{
                ItemStack Torch = RedstoneTorchOff.clone();
                ItemMeta meta = Torch.getItemMeta();
                List<String> lore = new ArrayList<String>();
                
                meta.setDisplayName("Flag");
                lore.add("BLABLABLA");
                meta.setLore(lore);
                
                Torch.setItemMeta(meta);
                inventory.setItem(i, Torch);
            }
                i++;
        }*/
        
    }
    
}
