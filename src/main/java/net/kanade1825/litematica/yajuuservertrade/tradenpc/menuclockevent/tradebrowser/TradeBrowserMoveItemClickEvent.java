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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TradeBrowserMoveItemClickEvent implements Listener {

    private static final Path DB_CONFIG_PATH = Paths.get("plugins/Tradesystem/Config/database_config.json");
    private final List<String> directories = Arrays.asList(
            "plugins/Tradesystem/Category/Mining",
            "plugins/Tradesystem/Category/Farming",
            "plugins/Tradesystem/Category/Combat",
            "plugins/Tradesystem/Category/Oddities",
            "plugins/Tradesystem/Category/WoodsAndFishes"
    );
    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private List<Map<String, Object>> items_1;  // For category 2
    private List<Map<String, Object>> items_2;  // For category 3
    private List<Map<String, Object>> items_3;  // For category 4

    public TradeBrowserMoveItemClickEvent() {
        try {
            Class.forName("org.postgresql.Driver");

            JSONObject dbConfig = (JSONObject) new JSONParser().parse(Files.newBufferedReader(DB_CONFIG_PATH));
            dbURL = (String) dbConfig.get("url");
            dbUser = (String) dbConfig.get("user");
            dbPassword = (String) dbConfig.get("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        // If the player has the TradeMenuOpen tag, cancel any inventory interactions to prevent items from being taken out or moved.
        if (player.getScoreboardTags().contains("TradeMenuOpen")) {
            event.setCancelled(true);
        }

        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && !clickedItem.getItemMeta().getDisplayName().equals("DisplayItem")) {
                String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§.", "");

                for (String directory : directories) {
                    JSONObject matchedJson = findJsonWithDisplayName(directory, displayName);
                    if (matchedJson != null) {
                        handleMatchingJson(player, matchedJson);
                        break;
                    }
                }
            }

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
                                jsonObject.put("filePath", file.getAbsolutePath());
                                if (jsonObject.containsKey("items")) {
                                    jsonObject.put("subItems", jsonObject.get("items"));
                                }
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
            if (!((Boolean) categoryItemObj)) {
                String displayNameFromJson = (String) jsonObject.get("displayName");
                String itemIdFromJson = (String) jsonObject.get("itemID");
                addOrUpdateInDatabase(player, displayNameFromJson, itemIdFromJson, jsonObject);

                Inventory inventory = new TradeBrowserTradeControlInventoryGUI(displayNameFromJson, itemIdFromJson).setupAndReturnTradeMenu();
                player.openInventory(inventory);
                player.addScoreboardTag("TradeMenuOpen");
            }
        } else if (categoryItemObj instanceof Number) {
            int categoryValue = ((Number) categoryItemObj).intValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) jsonObject.get("subItems");

            switch (categoryValue) {
                case 2:
                    items_1 = items;  // Set the items_1 list
                    break;
                case 3:
                    items_2 = items;  // Set the items_2 list
                    break;
                case 4:
                    items_3 = items;  // Set the items_3 list
                    break;
            }

            for (Map<String, Object> item : items) {
                String itemName = (String) item.get("itemID");
                JSONObject itemJsonObject = new JSONObject(item);  // サブアイテムの JSON オブジェクトを作成
                addOrUpdateInDatabase(player, (String) item.get("displayName"), itemName, itemJsonObject);  // サブアイテムの JSON オブジェクトを渡す
            }

            String filePath = (String) jsonObject.get("filePath");
            Inventory inventory;
            switch (categoryValue) {
                case 2:
                    inventory = new TradeBrowserCategoryNumberIsTwoInventoryGUI().setupAndReturnTradeMenu(filePath);
                    player.openInventory(inventory);
                    player.addScoreboardTag("TradeMenuOpen_CategoryTwo");
                    player.addScoreboardTag("TradeMenuOpen");
                    break;
                case 3:
                    inventory = new TradeBrowserCategoryNumberIsThreeInventoryGUI().setupAndReturnTradeMenu(filePath);
                    player.openInventory(inventory);
                    player.addScoreboardTag("TradeMenuOpen_CategoryThree");
                    player.addScoreboardTag("TradeMenuOpen");
                    break;
                case 4:
                    inventory = new TradeBrowserCategoryNumberIsFourInventoryGUI().setupAndReturnTradeMenu(filePath);
                    player.openInventory(inventory);
                    player.addScoreboardTag("TradeMenuOpen_CategoryFour");
                    player.addScoreboardTag("TradeMenuOpen");
                    break;
                default:
                    player.sendMessage("Invalid category number.");
            }
        }
    }


    private void addOrUpdateInDatabase(Player player, String displayName, String itemId, JSONObject jsonObject) {
        int initialValue = getInitialValueFromJson(jsonObject);
        if (!isInDatabase(player.getName(), displayName)) {
            insertIntoDatabase(player.getName(), "Buy", 1024, initialValue, displayName);
            insertIntoDatabase(player.getName(), "Sell", 1024, initialValue, displayName);
        }
    }

    private boolean isInDatabase(String McId, String displayName) {
        try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             Statement stmt = connection.createStatement()) {
            String query = String.format("SELECT COUNT(*) FROM trades WHERE MCID = '%s' AND itemname = '%s'", McId, displayName);
            ResultSet rs = stmt.executeQuery(query);
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openTradeControlGUI(Player player, String displayName, String itemId) {
        Inventory inventory = new TradeBrowserTradeControlInventoryGUI(displayName, itemId).setupAndReturnTradeMenu();
        player.openInventory(inventory);
        player.addScoreboardTag("TradeMenuOpen");
    }

    private void handleCategoryTwoItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (isValidItem(clickedItem)) {
            String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§.", "");
            for (Map<String, Object> item : items_1) {
                if (displayName.equals(item.get("displayName"))) {
                    String itemId = (String) item.get("itemID");
                    openTradeControlGUI(player, displayName, itemId);
                }
            }
        }
    }

    private void handleCategoryThreeItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (isValidItem(clickedItem)) {
            String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§.", "");
            for (Map<String, Object> item : items_2) {
                if (displayName.equals(item.get("displayName"))) {
                    String itemId = (String) item.get("itemID");
                    openTradeControlGUI(player, displayName, itemId);
                }
            }
        }
    }

    private void handleCategoryFourItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (isValidItem(clickedItem)) {
            String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§.", "");
            for (Map<String, Object> item : items_3) {
                if (displayName.equals(item.get("displayName"))) {
                    String itemId = (String) item.get("itemID");
                    openTradeControlGUI(player, displayName, itemId);
                }
            }
        }
    }

    private boolean isValidItem(ItemStack clickedItem) {
        return clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName();
    }

    private void insertIntoDatabase(String McId, String BuyOrSell, int Quantity, int UnitPrice, String displayName) {
        try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             Statement stmt = connection.createStatement()) {
            String query = String.format("INSERT INTO trades (MCID, BuyOrSell, Quantity, UnitPrice, itemname) VALUES ('%s', '%s', %d, %d, '%s')",
                    McId, BuyOrSell, Quantity, UnitPrice, displayName);
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getInitialValueFromJson(JSONObject jsonObject) {
        Object initialValueObj = jsonObject.get("initialValue");
        if (initialValueObj instanceof Number) {
            return ((Number) initialValueObj).intValue();
        }
        return 0;
    }

}

