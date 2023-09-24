package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserBuyMenuInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class TradeBrowserBuyInstantlyClickEvent implements Listener {
    public static HashMap<UUID, String> lastDisplayNameMap = new HashMap<>();  // Shared map to pass the displayName

    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();

        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§6§lBuy Instantly")) {
                    // 14番目のスロットのアイテムを取得
                    ItemStack itemIn14thSlot = clickedInventory.getItem(13);
                    Player player = (Player) event.getWhoClicked();
                    UUID playerUUID = player.getUniqueId();
                    lastDisplayNameMap.put(playerUUID, itemIn14thSlot.getItemMeta().getDisplayName());  // Save to the shared map
                    String displayNameFromJson = itemIn14thSlot.getItemMeta().getDisplayName();
                    String itemIDFromJson = itemIn14thSlot.getType().name();  // アイテムのMaterial名を取得

                    Inventory inventory = new TradeBrowserBuyMenuInventoryGUI(displayNameFromJson, itemIDFromJson).setupAndReturnTradeMenu();

                    Player Clicker = (Player) event.getWhoClicked();
                    Clicker.openInventory(inventory);
                    Clicker.addScoreboardTag("TradeMenuOpen");
                }
            }
        }
    }
}
