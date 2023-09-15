package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class TradeBrowserCreateBuyOrderInventoryGUI {

    private final String displayNameFromJson;
    private final String itemIDFromJson;

    public TradeBrowserCreateBuyOrderInventoryGUI(String displayNameFromJson, String itemIDFromJson) {
        this.displayNameFromJson = displayNameFromJson;
        this.itemIDFromJson = itemIDFromJson;
    }

    public void setupTradeMenu(Inventory inv) {
        // 全スロットを黒のガラス板で埋める
        ItemStack blackGlass = createCustomItem(Material.BLACK_STAINED_GLASS_PANE, " ", Collections.singletonList(""));

        // 特定のスロットに特別なアイテムを配置
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
        }

        inv.setItem(9, blackGlass);

        ItemStack itemFor10thSlot = createItem(Material.valueOf(itemIDFromJson), "§a§lBuy a stack!", Collections.singletonList(""));
        itemFor10thSlot.setAmount(64);
        inv.setItem(10, itemFor10thSlot);

        inv.setItem(11, blackGlass);

        // 12番目のスロットに2つのチェストを設定
        ItemStack chestFor12thSlot = createItem(Material.CHEST, "§a§lBuy a big stack!", Collections.singletonList(""));
        chestFor12thSlot.setAmount(2);
        inv.setItem(12, chestFor12thSlot);

        inv.setItem(13, blackGlass);

        // 14番目のスロットに16つのチェストを設定
        ItemStack chestFor14thSlot = createItem(Material.CHEST, "§a§lBuy a thousand!", Collections.singletonList(""));
        chestFor14thSlot.setAmount(16);
        inv.setItem(14, chestFor14thSlot);

        inv.setItem(15, blackGlass);
        inv.setItem(16, createCustomItem(Material.OAK_SIGN, "§a§lCustom Amount", Collections.singletonList("§fComming soon")));
        inv.setItem(17, blackGlass);

        for (int i = 18; i < 27; i++) {
            inv.setItem(i, blackGlass);
        }

        for (int i = 27; i < 36; i++) {
            inv.setItem(i, blackGlass);
        }

        inv.setItem(31, createCustomItem(Material.ARROW, "§a§lGo Back", Collections.singletonList("")));
    }

    public ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public Inventory setupAndReturnTradeMenu() {
        Inventory inv = Bukkit.createInventory(null, 36, "TradeMenu");
        setupTradeMenu(inv);
        return inv;
    }

    public ItemStack createCustomItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
