package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserCreateSellOfferInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeBrowserCreateSellOfferClickEvent implements Listener {

    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();

        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();


                // クリックされたアイテムが"§a§lCreate Sell Offer"という表示名の場合
                if (displayName.equals("§a§lCreate Sell Offer")) {
                    // 14番目のスロットのアイテムを取得
                    ItemStack itemIn14thSlot = clickedInventory.getItem(13);
                    String displayNameFromJson = itemIn14thSlot.getItemMeta().getDisplayName();
                    String itemIDFromJson = itemIn14thSlot.getType().name();  // アイテムのMaterial名を取得

                    Inventory inventory = new TradeBrowserCreateSellOfferInventoryGUI(displayNameFromJson, itemIDFromJson).setupAndReturnTradeMenu();

                    Player Clicker = (Player) event.getWhoClicked();
                    Clicker.openInventory(inventory);
                    Clicker.addScoreboardTag("TradeMenuOpen");
                }
            }
        }
    }


}
