package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.MainMenuInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CloseClockEvent implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        // クリックされたインベントリの名前が"TradeMenu"であることを確認
        if ("TradeMenu".equals(clickedInventory.getTitle())) {

            // アイテムがnullでないか、表示名が存在するかを確認
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                // クリックされたアイテムが"Close"という表示名の場合
                if (displayName.equals("§c§lClose")) {
                    player.closeInventory();
                } else if (displayName.equals("§a§lGo Back")) {
                    Inventory inventory = new MainMenuInventoryGUI().setupAndReturnTradeMenu();
                    player.openInventory(inventory);
                    player.addScoreboardTag("TradeMenuOpen");

                    // 新たに追加された3つのタグを削除
                    player.removeScoreboardTag("TradeMenuOpen_CategoryTwo");
                    player.removeScoreboardTag("TradeMenuOpen_CategoryThree");
                    player.removeScoreboardTag("TradeMenuOpen_CategoryFour");
                }
            }
        }
    }
}
