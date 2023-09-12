package net.kanade1825.litematica.yajuuservertrade.tradenpc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.Set;

public class TradeNPCInventoryClose implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Set<String> userTags = event.getPlayer().getScoreboardTags();
        if (userTags.contains("TradeMenuOpen")) {
            event.getPlayer().removeScoreboardTag("TradeMenuOpen");
        }
        if (userTags.contains("TradeMenuOpen_CategoryTwo")) {
            event.getPlayer().removeScoreboardTag("TradeMenuOpen_CategoryTwo");
        }
        if (userTags.contains("TradeMenuOpen_CategoryThree")) {
            event.getPlayer().removeScoreboardTag("TradeMenuOpen_CategoryThree");
        }
        if (userTags.contains("TradeMenuOpen_CategoryFour")) {
            event.getPlayer().removeScoreboardTag("TradeMenuOpen_CategoryFour");
        }
    }
}