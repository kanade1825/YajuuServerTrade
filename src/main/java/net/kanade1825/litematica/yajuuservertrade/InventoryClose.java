package net.kanade1825.litematica.yajuuservertrade;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Set;

public class InventoryClose implements Listener {
    @EventHandler

    public void onInventoryClose(InventoryCloseEvent event){
        Set<String> UserTags = event.getPlayer().getScoreboardTags();
        if(UserTags.contains("TradeMenuOpen")){

            Player string = (Player) event.getPlayer();
            event.getPlayer().removeScoreboardTag("TradeMenuOpen");
        }

    }
}
