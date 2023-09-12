package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserCategoryNumberIsFourInventoryGUI;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserCategoryNumberIsThreeInventoryGUI;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserCategoryNumberIsTwoInventoryGUI;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.inventorymenu.tradebrowser.TradeBrowserTradeControlInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TradeBrowserMoveItemClickEvent implements Listener {

    private final List<String> directories = Arrays.asList(
            "plugins/Tradesystem/Category/Mining",
            "plugins/Tradesystem/Category/Farming",
            "plugins/Tradesystem/Category/Combat",
            "plugins/Tradesystem/Category/Oddities",
            "plugins/Tradesystem/Category/WoodsAndFishes"
            // 他のディレクトリを追加...
    );

    private List<Map<String, Object>> items_1;  // For category 2
    private List<Map<String, Object>> items_2;  // For category 3
    private List<Map<String, Object>> items_3;  // For category 4

    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        // "TradeMenu" タイトルのインベントリがクリックされたときの処理
        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§[6debce]§l", "");

                for (String directory : directories) {
                    JSONObject matchedJson = findJsonWithDisplayName(directory, displayName);
                    if (matchedJson != null) {
                        handleMatchingJson(player, matchedJson);
                        break;
                    }
                }
            }

            // 各カテゴリのGUIでの特定の位置のアイテムがクリックされたときの処理を実行
            if (player.getScoreboardTags().contains("TradeMenuOpen_CategoryTwo")) {
                handleCategoryTwoItemClick(event);
            } else if (player.getScoreboardTags().contains("TradeMenuOpen_CategoryThree")) {
                handleCategoryThreeItemClick(event);
            } else if (player.getScoreboardTags().contains("TradeMenuOpen_CategoryFour")) {
                handleCategoryFourItemClick(event);
            }
        }
    }

    private JSONObject findJsonWithDisplayName(String directoryPath, String displayName) {
        File directory = new File(directoryPath);
        JSONParser parser = new JSONParser();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        try (FileReader reader = new FileReader(file)) {
                            JSONObject jsonObject = (JSONObject) parser.parse(reader);
                            if (displayName.equals(jsonObject.get("displayName"))) {
                                jsonObject.put("filePath", file.getAbsolutePath()); // ファイルの相対パスを追加
                                return jsonObject;
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    private void handleMatchingJson(Player player, JSONObject jsonObject) {
        Object categoryItemObj = jsonObject.get("isCategoryItem");
        if (categoryItemObj instanceof Boolean) {

            // JSONからdisplayNameとitemIDを取得して変数に格納
            String displayNameFromJson = (String) jsonObject.get("displayName");
            String itemIDFromJson = (String) jsonObject.get("itemID");

            Inventory inventory = new TradeBrowserTradeControlInventoryGUI(displayNameFromJson, itemIDFromJson).setupAndReturnTradeMenu(); // ファイルパスを渡す
            player.openInventory(inventory);  // この行を追加して、プレイヤーにインベントリを表示
            player.addScoreboardTag("TradeMenuOpen");


            // 以下に、displayNameFromJsonとitemIDFromJsonを使用するコードを追加できます

            //各メニューごとにタグを着けることにより識別する案

        } else if (categoryItemObj instanceof Number) {
            int categoryValue = ((Number) categoryItemObj).intValue();
            switch (categoryValue) {
                case 2:

                    String filePath = (String) jsonObject.get("filePath");//パスを取得
                    Inventory inventory = new TradeBrowserCategoryNumberIsTwoInventoryGUI().setupAndReturnTradeMenu(filePath); // ファイルパスを渡す
                    player.openInventory(inventory);  // この行を追加して、プレイヤーにインベントリを表示
                    player.addScoreboardTag("TradeMenuOpen");
                    player.addScoreboardTag("TradeMenuOpen_CategoryTwo");
                    break;
                case 3:

                    String filePath_Three = (String) jsonObject.get("filePath");//パスを取得
                    Inventory inventory_Three = new TradeBrowserCategoryNumberIsThreeInventoryGUI().setupAndReturnTradeMenu(filePath_Three); // ファイルパスを渡す
                    player.openInventory(inventory_Three);  // この行を追加して、プレイヤーにインベントリを表示
                    player.addScoreboardTag("TradeMenuOpen_CategoryThree");
                    player.addScoreboardTag("TradeMenuOpen");
                    break;
                case 4:
                    String filePath_Fore = (String) jsonObject.get("filePath");//パスを取得
                    Inventory inventory_Fore = new TradeBrowserCategoryNumberIsFourInventoryGUI().setupAndReturnTradeMenu(filePath_Fore); // ファイルパスを渡す
                    player.openInventory(inventory_Fore);
                    player.addScoreboardTag("TradeMenuOpen_CategoryFour");
                    player.addScoreboardTag("TradeMenuOpen");// この行を追加して、プレイヤーにインベントリを表示
                    break;
                default:
                    player.sendMessage("Invalid category number.");
            }
        }
    }


    // ... (findJsonWithDisplayNameメソッドなどの既存のコード)

    private void handleCategoryTwoItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            for (Map<String, Object> item : items_1) {
                if (displayName.equals("§f" + item.get("displayName"))) {
                    player.sendMessage("You clicked on an item from Category Two with displayName: " + displayName);
                }
            }
        }
    }

    private void handleCategoryThreeItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            for (Map<String, Object> item : items_2) {
                if (displayName.equals("§f" + item.get("displayName"))) {
                    player.sendMessage("You clicked on an item from Category Three with displayName: " + displayName);
                }
            }
        }
    }

    private void handleCategoryFourItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            for (Map<String, Object> item : items_3) {
                if (displayName.equals("§f" + item.get("displayName"))) {
                    player.sendMessage("You clicked on an item from Category Four with displayName: " + displayName);
                }
            }
        }
    }

}