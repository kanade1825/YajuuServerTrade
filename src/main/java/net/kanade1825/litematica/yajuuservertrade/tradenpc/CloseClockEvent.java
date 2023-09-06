package net.kanade1825.litematica.yajuuservertrade.tradenpc;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CloseClockEvent implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();

        // クリックされたインベントリの名前が"TradeMenu"であることを確認
        if (clickedInventory != null && "TradeMenu".equals(clickedInventory.getTitle())) {

            // アイテムがnullでないか、表示名が存在するかを確認
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                // クリックされたアイテムが"Close"という表示名の場合
                if (displayName.equals("§c§lClose")) {
                    event.getWhoClicked().sendMessage("You clicked the gold block!");
                }
            }
        }
    }



}
