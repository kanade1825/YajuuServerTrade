package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserOdditiesInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeBrowserOdditiesClickEvent implements Listener {
    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();

        // クリックされたインベントリの名前が"TradeMenu"であることを確認
        if ("TradeMenu".equals(clickedInventory.getTitle())) {

            // アイテムがnullでないか、表示名が存在するかを確認
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                // クリックされたアイテムが"Manage Trades"という表示名の場合
                if (displayName.equals("§d§lOddities")) {

                    Inventory inventory = new TradeBrowserOdditiesInventoryGUI().setupAndReturnTradeMenu();


                    Player Clicker = (Player) event.getWhoClicked();


                    Clicker.openInventory(inventory);

                    Clicker.addScoreboardTag("TradeMenuOpen");


                }
            }
        }
    }


}
