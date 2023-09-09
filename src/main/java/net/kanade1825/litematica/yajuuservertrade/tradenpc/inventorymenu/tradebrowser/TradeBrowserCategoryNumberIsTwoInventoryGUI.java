package net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TradeBrowserCategoryNumberIsTwoInventoryGUI {
    private final Map<String, String> directoryColorMap = Map.of(
            "plugins/Tradesystem/Category/Mining", "§b",
            "plugins/Tradesystem/Category/Farming", "§e",
            "plugins/Tradesystem/Category/Combat", "§c",
            "plugins/Tradesystem/Category/WoodsAndFishes", "§6",
            "plugins/Tradesystem/Category/Oddities", "§d"
    );


    public void setupTradeMenu(Inventory inv, String filePath) {
        // 全スロットを黒のガラス板で埋める
        ItemStack blackGlass = createCustomItem(Material.BLACK_STAINED_GLASS_PANE, " ", Collections.singletonList(""));


        // 特定のスロットに特別なアイテムを配置
        //二列目空白
        // 一列目: 全てを埋める
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
        }

        // 二列目: 穴あけ
        inv.setItem(9, blackGlass);
        inv.setItem(10, blackGlass);
        inv.setItem(11, blackGlass);
        inv.setItem(13, blackGlass);
        inv.setItem(15, blackGlass);
        inv.setItem(16, blackGlass);
        inv.setItem(17, blackGlass);

        //三列目：全て埋める
        for (int i = 18; i < 27; i++) {
            inv.setItem(i, blackGlass);
        }


        //四列目：
        inv.setItem(27, blackGlass);
        inv.setItem(28, blackGlass);
        inv.setItem(29, blackGlass);
        inv.setItem(30, blackGlass);
        //もどるぼたん
        inv.setItem(31, createCustomItem(Material.ARROW, "§a§lGo Back", Collections.singletonList("")));
        inv.setItem(32, blackGlass);
        inv.setItem(33, blackGlass);
        inv.setItem(34, blackGlass);
        inv.setItem(35, blackGlass);


        //こっからした分からん

        // JSONデータからアイテムを作成して配置
        // JSONからのアイテムの配置
        //動的アイテムの設定部分
        // JSONからのアイテムの配置
        List<JSONObject> jsonList = loadJsonFilesFromFilePath(filePath);

        for (JSONObject jsonObject : jsonList) {
            Object categoryObject = jsonObject.get("isCategoryItem");
            if (categoryObject instanceof Number && ((Number) categoryObject).intValue() == 2) {
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                if (itemsArray != null && itemsArray.length() == 2) {
                    JSONObject firstItem = itemsArray.getJSONObject(0);
                    JSONObject secondItem = itemsArray.getJSONObject(1);

                    inv.setItem(12, createItemFromJson(firstItem, filePath));
                    inv.setItem(14, createItemFromJson(secondItem, filePath));
                }
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

    public Inventory setupAndReturnTradeMenu(String filePath) {
        Inventory inv = Bukkit.createInventory(null, 36, "TradeMenu");
        setupTradeMenu(inv, filePath);
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
    public ItemStack createItemFromJson(JSONObject jsonObject, String directoryPath) {
        String itemID = jsonObject.getString("itemID");
        String colorCode = getColorForDirectory(directoryPath);
        String displayName = colorCode + "§l" + jsonObject.getString("displayName");
        Material material = Material.valueOf(itemID);
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        Object categoryObject = jsonObject.opt("isCategoryItem");
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

    private String getColorForDirectory(String directoryPath) {
        return directoryColorMap.getOrDefault(directoryPath, "§f");  // デフォルトは白色
    }

    private List<JSONObject> loadJsonFilesFromFilePath(String filePath) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        File file = new File(filePath);
        if (file.exists() && file.getName().endsWith(".json")) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                jsonObjects.add(new JSONObject(content));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObjects;
    }
}
