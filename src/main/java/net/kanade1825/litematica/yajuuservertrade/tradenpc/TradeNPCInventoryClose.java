package net.kanade1825.litematica.yajuuservertrade.tradenpc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Set;


public class TradeNPCInventoryClose implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Set<String> UserTags = event.getPlayer().getScoreboardTags();
        if (UserTags.contains("TradeMenuOpen")) {
            event.getPlayer().removeScoreboardTag("TradeMenuOpen");
        }
    }
}
