package net.kanade1825.litematica.yajuuservertrade.tradenpc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class TradeNPCInventoryClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Set<String> UserTags = event.getWhoClicked().getScoreboardTags();
        if (UserTags.contains("TradeMenuOpen")) {
            event.setCancelled(true);
        }
    }
}


