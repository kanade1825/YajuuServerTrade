package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.tradesystem.sell;

import jp.jyn.jecon.Jecon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class SellInstantlyClickEvent implements Listener {
    private static final Path DB_CONFIG_PATH = Paths.get("plugins/Tradesystem/Config/database_config.json");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9A-FK-ORa-fk-or]");
    private final Jecon jecon;
    private String dbURL;
    private String dbUser;
    private String dbPassword;

    public SellInstantlyClickEvent(Jecon jecon) {
        if (jecon == null) {
            throw new IllegalArgumentException("Jecon instance cannot be null");
        }
        this.jecon = jecon;
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

        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§a§lSell Instantly")) {
                    Inventory playerInventory = player.getInventory();

                    // Get the item in the center of the top row
                    ItemStack targetItem = clickedInventory.getItem(13);  // Slot 13 is the center of the top row
                    if (targetItem == null || !targetItem.hasItemMeta()) {
                        player.sendMessage("No item selected for selling.");
                        return;
                    }

                    String targetDisplayName = COLOR_CODE_PATTERN.matcher(targetItem.getItemMeta().getDisplayName()).replaceAll("");

                    try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
                        if (connection == null) {
                            player.sendMessage("Could not establish a database connection.");
                            return;
                        }

                        String query = "SELECT * FROM trades WHERE LOWER(ItemName) = LOWER(?) AND LOWER(BuyOrSell)='buy' ORDER BY UnitPrice ASC";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            preparedStatement.setString(1, targetDisplayName);

                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (!resultSet.next()) {
                                player.sendMessage("No buying offers available for selected item.");
                                return;
                            }

                            int bestPrice = resultSet.getInt("UnitPrice");
                            int itemCount = 0;

                            // Count the number of target items in the player's inventory
                            for (ItemStack item : playerInventory.getContents()) {
                                if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(targetDisplayName)) {
                                    itemCount += item.getAmount();
                                }
                            }

                            if (itemCount == 0) {
                                player.sendMessage("You do not have the selected item in your inventory.");
                                return;
                            }

                            // Calculate total sell price and proceed with the selling process
                            int totalSellPrice = bestPrice * itemCount;
                            UUID playerUUID = player.getUniqueId();
                            Optional<BigDecimal> optionalBalance = jecon.getRepository().getDecimal(playerUUID);

                            if (!optionalBalance.isPresent()) {
                                player.sendMessage("Could not retrieve your balance.");
                                return;
                            }

                            BigDecimal newBalance = optionalBalance.get().add(BigDecimal.valueOf(totalSellPrice));
                            jecon.getRepository().set(playerUUID, newBalance);

                            // Deduct items from the database inventory
                            try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE trades SET Quantity=? WHERE LOWER(ItemName)=LOWER(?) AND LOWER(BuyOrSell)='buy' AND UnitPrice=?")) {
                                int remainingItemsToSell = itemCount;
                                resultSet.beforeFirst(); // reset the cursor
                                while (resultSet.next() && remainingItemsToSell > 0) {
                                    int currentQuantity = resultSet.getInt("Quantity");
                                    int itemsToSell = Math.min(remainingItemsToSell, currentQuantity);

                                    // Update item quantity in the database
                                    int newQuantity = currentQuantity - itemsToSell;
                                    updateStatement.setInt(1, newQuantity);
                                    updateStatement.setString(2, targetDisplayName);
                                    updateStatement.setInt(3, resultSet.getInt("UnitPrice"));
                                    updateStatement.executeUpdate();

                                    remainingItemsToSell -= itemsToSell;
                                }

                                if (remainingItemsToSell > 0) {
                                    player.sendMessage("Not enough items in stock to sell the specified amount.");
                                    return;
                                }
                            }

                            // Remove items from player's inventory
                            for (ItemStack item : playerInventory.getContents()) {
                                if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(targetDisplayName)) {
                                    int amount = item.getAmount() - itemCount;
                                    if (amount <= 0) {
                                        playerInventory.remove(item);
                                    } else {
                                        item.setAmount(amount);
                                    }
                                }
                            }

                            player.sendMessage("You sold " + itemCount + " items for " + totalSellPrice + " units. New balance: " + newBalance);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendMessage("An error occurred: " + e.getMessage());
                    }
                }
            }
        }
    }
}
