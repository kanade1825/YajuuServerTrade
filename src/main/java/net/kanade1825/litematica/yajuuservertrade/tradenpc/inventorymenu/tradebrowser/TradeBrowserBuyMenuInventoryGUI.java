package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class TradeBrowserBuyMenuInventoryGUI {

    private final String displayNameFromJson;
    private final String itemIDFromJson;

    public TradeBrowserBuyMenuInventoryGUI(String displayNameFromJson, String itemIDFromJson) {
        this.displayNameFromJson = displayNameFromJson;
        this.itemIDFromJson = itemIDFromJson;
    }


    public void setupTradeMenu(Inventory inv) {
        // 全スロットを黒のガラス板で埋める
        ItemStack blackGlass = createCustomItem(Material.BLACK_STAINED_GLASS_PANE, " ", Collections.singletonList(""));


        // 特定のスロットに特別なアイテムを配置
        //二列目空白
        // 一列目: 全てを埋める
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
        }

        // 二列目: 左端と右端のみを埋める
        inv.setItem(9, blackGlass);

        // 10番目のスロットにdisplayNameFromJsonとitemIDFromJsonを使用してアイテムを設定
        inv.setItem(10, createItem(Material.valueOf(itemIDFromJson), "§a§lBuy only one!", Collections.singletonList("")));
        inv.setItem(11, blackGlass);

        // 12番目のスロットにdisplayNameFromJsonとitemIDFromJsonを使用して64個のアイテムを設定
        ItemStack itemFor12thSlot = createItem(Material.valueOf(itemIDFromJson), "§a§lBuy a stack!", Collections.singletonList(""));
        itemFor12thSlot.setAmount(64);
        inv.setItem(12, itemFor12thSlot);
        inv.setItem(13, blackGlass);

        inv.setItem(14, createCustomItem(Material.CHEST, "§a§lFill My Inventory!", Collections.singletonList("§fSorry Comming soon.....")));
        inv.setItem(15, blackGlass);
        inv.setItem(16, createCustomItem(Material.OAK_SIGN, "§a§lCustom Amount", Collections.singletonList("§fComming soon")));
        inv.setItem(17, blackGlass);


        // 三列目: 全てを埋める
        for (int i = 18; i < 27; i++) {
            inv.setItem(i, blackGlass);
        }

        // 四列目: 全てを埋める
        for (int i = 27; i < 36; i++) {
            inv.setItem(i, blackGlass);
        }


        //もどるぼたん
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
