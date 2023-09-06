package net.kanade1825.litematica.yajuuservertrade;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.Set;

public class TradeNPC implements Listener {

    private final YajuuServerTrade yajuuServerTrade;

    public TradeNPC(YajuuServerTrade yajuuServerTrade) {
        this.yajuuServerTrade = yajuuServerTrade;
    }


    @EventHandler
    public void onTadokoroClick(NPCRightClickEvent event) {
        // 非同期
        Bukkit.getScheduler().runTaskAsynchronously(yajuuServerTrade, () -> {
            String NPCname = event.getNPC().getName();


            if (NPCname.equals("Mr.Tadokoro")) {


                Set<String> UserTags = event.getClicker().getScoreboardTags();

                if (UserTags.contains("TradeMenuOpen")) {
                    event.setCancelled(true);
                } else {

                    Inventory inventory = new ViewItems().setupAndReturnTradeMenu();


                    Player Clicker = event.getClicker();


                    Clicker.openInventory(inventory);

                    Clicker.addScoreboardTag("TradeMenuOpen");


                }
            }


        });


    }
}

