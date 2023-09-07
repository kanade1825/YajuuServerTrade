package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;


//チェストから見えるアイテム
public class MainMenuInventoryGUI {

    public void setupTradeMenu(Inventory inv) {
        // 全スロットを黒のガラス板で埋める
        ItemStack blackGlass = createCustomItem(Material.BLACK_STAINED_GLASS_PANE, " ", Collections.singletonList(""));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, blackGlass);
        }

        // 特定のスロットに特別なアイテムを配置
        inv.setItem(11, createCustomItem(Material.DIAMOND_BLOCK, "§6§lTrade Browser", Collections.singletonList("§fここから様々なアイテムを取引できます")));
        inv.setItem(13, createCustomItem(Material.GOLDEN_CARROT, "§a§lView Position", Collections.singletonList("§f貴方の持つポジションを確認できます")));
        inv.setItem(15, createCustomItem(Material.DIAMOND_HORSE_ARMOR, "§a§lManage Trades", Collections.singletonList("§f購入品などを確認できます")));
        inv.setItem(31, createCustomItem(Material.BARRIER, "§c§lClose", Collections.singletonList("")));
        inv.setItem(32, createCustomItem(Material.MAP, "§a§lTrade Status", Collections.singletonList("§f過去の履歴などを確認できます")));
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