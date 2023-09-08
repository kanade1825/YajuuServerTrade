package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class TradeBrowserWoodsAndFishesInventoryGUI {

    public void setupTradeMenu(Inventory inv) {
        // 全スロットを黒のガラス板で埋める
        ItemStack orangeGlass = createCustomItem(Material.ORANGE_STAINED_GLASS_PANE, " ", Collections.singletonList(""));

        //くわ
        ItemStack Golden_hoe = createCustomItem(Material.GOLDEN_HOE,"§e§lFarming",Collections.singletonList("§fCategory"));

        inv.setItem(0,Golden_hoe);

        //ぴっける
        ItemStack diamond_pickaxe = createCustomItem(Material.DIAMOND_PICKAXE,"§b§lMining",Collections.singletonList("§fCategory"));
        //てつけん
        ItemStack iron_sowrd = createCustomItem(Material.IRON_SWORD,"§c§lCombat",Collections.singletonList("§fCategory"));
        //つりざお
        ItemStack fishing_rod = createCustomItem(Material.FISHING_ROD,"§6§lWoods＆Fishes",Collections.singletonList("§fCategory"));

        fishing_rod.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        ItemMeta itemMeta = fishing_rod.getItemMeta();
        itemMeta.addItemFlags();
        fishing_rod.setItemMeta(itemMeta);



        //エンチャントテーブル
        ItemStack enchanted_table = createCustomItem(Material.ENCHANTING_TABLE,"§d§lOddities",Collections.singletonList("§fCategory"));
        //かんばん
        ItemStack kanban = createCustomItem(Material.OAK_SIGN,"§a§lSearch",Collections.singletonList("§fComming soon..."));
        //ちぇすと　
        ItemStack chest = createCustomItem(Material.CHEST,"§a§lSell Inventory Now",Collections.singletonList("§fComming soon..."));




        // 特定のスロットに特別なアイテムを配置
        //二列目空白
        // 一列目: 全てを埋める
        for (int i = 1; i < 9; i++) {
            inv.setItem(i, orangeGlass);
        }

        // 二列目: 左端と右端のみを埋める
        inv.setItem(9,diamond_pickaxe);
        inv.setItem(10, orangeGlass);
        inv.setItem(17, orangeGlass);


        // 三列目: 左端と右端のみを埋める
        inv.setItem(18,iron_sowrd);
        inv.setItem(19, orangeGlass);
        inv.setItem(26, orangeGlass);

        //四列目:
        inv.setItem(27,fishing_rod);
        inv.setItem(28,orangeGlass);
        inv.setItem(35,orangeGlass);

        //五列目:

        inv.setItem(36,enchanted_table);
        inv.setItem(37,orangeGlass);
        inv.setItem(44,orangeGlass);

        //六列目：
        inv.setItem(45,kanban);
        inv.setItem(46,orangeGlass);
        inv.setItem(47,chest);
        inv.setItem(48,orangeGlass);
        //もどるぼたん
        inv.setItem(49, createCustomItem(Material.ARROW, "§a§lGo Back", Collections.singletonList("")));
        inv.setItem(50,orangeGlass);
        inv.setItem(51,orangeGlass);
        inv.setItem(52,orangeGlass);
        inv.setItem(53,orangeGlass);

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
        Inventory inv = Bukkit.createInventory(null, 54, "TradeMenu");
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
