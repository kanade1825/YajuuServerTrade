package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeBrowserCombatInventoryGUI {

    public void setupTradeMenu(Inventory inv) {
        // 全スロットを黒のガラス板で埋める
        ItemStack redGlass = createCustomItem(Material.RED_STAINED_GLASS_PANE, " ", Collections.singletonList(""));


        //くわ
        ItemStack Golden_hoe = createCustomItem(Material.GOLDEN_HOE, "§e§lFarming", Collections.singletonList("§fCategory"));

        inv.setItem(0, Golden_hoe);


        //ぴっける
        ItemStack diamond_pickaxe = createCustomItem(Material.DIAMOND_PICKAXE, "§b§lMining", Collections.singletonList("§fCategory"));
        //てつけん
        ItemStack iron_sowrd = createCustomItem(Material.IRON_SWORD, "§c§lCombat", Collections.singletonList("§fCategory"));

        iron_sowrd.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta itemMeta = iron_sowrd.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        iron_sowrd.setItemMeta(itemMeta);


        //つりざお
        ItemStack fishing_rod = createCustomItem(Material.FISHING_ROD, "§6§lWoods＆Fishes", Collections.singletonList("§fCategory"));
        //エンチャントテーブル
        ItemStack enchanted_table = createCustomItem(Material.ENCHANTING_TABLE, "§d§lOddities", Collections.singletonList("§fCategory"));
        //かんばん
        ItemStack kanban = createCustomItem(Material.OAK_SIGN, "§a§lSearch", Collections.singletonList("§fComming soon..."));
        //ちぇすと　
        ItemStack chest = createCustomItem(Material.CHEST, "§a§lSell Inventory Now", Collections.singletonList("§fComming soon..."));


        // 特定のスロットに特別なアイテムを配置
        //二列目空白
        // 一列目: 全てを埋める
        for (int i = 1; i < 9; i++) {
            inv.setItem(i, redGlass);
        }

        // 二列目: 左端と右端のみを埋める
        inv.setItem(9, diamond_pickaxe);
        inv.setItem(10, redGlass);
        inv.setItem(17, redGlass);


        // 三列目: 左端と右端のみを埋める
        inv.setItem(18, iron_sowrd);
        inv.setItem(19, redGlass);
        inv.setItem(26, redGlass);

        //四列目:
        inv.setItem(27, fishing_rod);
        inv.setItem(28, redGlass);
        inv.setItem(35, redGlass);

        //五列目:

        inv.setItem(36, enchanted_table);
        inv.setItem(37, redGlass);
        inv.setItem(44, redGlass);

        //六列目：
        inv.setItem(45, kanban);
        inv.setItem(46, redGlass);
        inv.setItem(47, chest);
        inv.setItem(48, redGlass);
        //もどるぼたん
        inv.setItem(49, createCustomItem(Material.ARROW, "§a§lGo Back", Collections.singletonList("")));
        inv.setItem(50, redGlass);
        inv.setItem(51, redGlass);
        inv.setItem(52, redGlass);
        inv.setItem(53, redGlass);

        // JSONデータからアイテムを作成して配置
        // JSONからのアイテムの配置
        //動的アイテムの設定部分
        List<JSONObject> jsonList = loadJsonFilesFromDirectory("plugins/Tradesystem/Category/Combat");
        int[] startSlots = {11, 20, 29, 38};
        int slotIndex = 0;
        int counter = 0;

        for (JSONObject jsonObject : jsonList) {
            if (slotIndex >= startSlots.length || counter >= 6) {
                counter = 0;
                slotIndex++;
            }

            if (slotIndex < startSlots.length) {
                ItemStack customItem = createItemFromJson(jsonObject);
                inv.setItem(startSlots[slotIndex] + counter, customItem);
                counter++;
            }
        }


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

    //{
    //  "itemID": "IRON_SWORD",
    //  "displayName": "Sword of Legends",
    //  "isCategoryItem": false,
    //  "initialValue": 100
    //}

    //{
    //  "itemID": "IRON_SWORD",
    //  "displayName": "Sword of Legends",
    //  "isCategoryItem": 2
    //}
    public ItemStack createItemFromJson(JSONObject jsonObject) {
        String itemID = jsonObject.getString("itemID");
        String displayName = "§c§l" + jsonObject.getString("displayName");
        Material material = Material.valueOf(itemID);
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        Object categoryObject = jsonObject.get("isCategoryItem");
        if (categoryObject instanceof Boolean && !((Boolean) categoryObject)) {
            int initialValue = jsonObject.getInt("initialValue");
            // 必要に応じて、初期値を使用してアイテムのメタデータや他の属性を設定します
        } else if (categoryObject instanceof Number) {
            int categoryValue = ((Number) categoryObject).intValue();
            // カテゴリー値に基づいてアイテムをカスタマイズする処理をここに書く
        }

        item.setItemMeta(meta);
        return item;
    }

    private List<JSONObject> loadJsonFilesFromDirectory(String directoryPath) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        try {
                            String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                            jsonObjects.add(new JSONObject(content));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return jsonObjects;
    }


}
