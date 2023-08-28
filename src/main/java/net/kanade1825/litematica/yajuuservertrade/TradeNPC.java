package net.kanade1825.litematica.yajuuservertrade;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.Set;

public class TradeNPC implements Listener {
    @EventHandler
    public void onTadokoroClick(NPCRightClickEvent event){
        String NPCname = event.getNPC().getName();



        if (NPCname.equals("Mr.Tadokoro")){


            Set<String> UserTags = event.getClicker().getScoreboardTags();

            if (UserTags.contains("TradeMenuOpen")) {
                event.setCancelled(true);
            }else {

                Inventory inventory = Bukkit.createInventory(null,9,"TradeMenu");
                inventory.setItem(0,new ViewItems().ViewItemsMain());

                Player Clicker = event.getClicker();



                Clicker.openInventory(inventory);

                Clicker.addScoreboardTag("TradeMenuOpen");



            }




        }
    }
}
