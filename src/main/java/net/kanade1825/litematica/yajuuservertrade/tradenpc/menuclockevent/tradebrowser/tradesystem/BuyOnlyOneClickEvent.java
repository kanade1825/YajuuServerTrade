package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.tradesystem;

import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.TradeBrowserBuyInstantlyClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class BuyOnlyOneClickEvent implements Listener {
    private static final Path DB_CONFIG_PATH = Paths.get("plugins/Tradesystem/Config/database_config.json");
    private String dbURL;
    private String dbUser;
    private String dbPassword;

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9A-FK-ORa-fk-or]");

    public BuyOnlyOneClickEvent() {
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
                String displayNameFromPreviousEvent = TradeBrowserBuyInstantlyClickEvent.lastDisplayName;

                if (displayName.equals("§a§lBuy only one!")) {
                    String cleanedDisplayNameFromPreviousEvent = COLOR_CODE_PATTERN.matcher(displayNameFromPreviousEvent).replaceAll("");

                    try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
                        if (connection == null) {
                            player.sendMessage("Could not establish a database connection.");
                            return;
                        }

                        String query = "SELECT * FROM trades WHERE LOWER(ItemName) = LOWER(?) AND LOWER(BuyOrSell)='sell' ORDER BY UnitPrice DESC LIMIT 1";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, cleanedDisplayNameFromPreviousEvent);

                            ResultSet resultSet = preparedStatement.executeQuery();

                            if (resultSet.next()) {
                                int unitPrice = resultSet.getInt("UnitPrice");
                                int currentQuantity = resultSet.getInt("Quantity");

                                if (currentQuantity <= 0) {
                                    player.sendMessage("The item with displayName " + cleanedDisplayNameFromPreviousEvent + " is out of stock.");
                                    return;
                                }

                                String playerName = player.getName();

                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jecon take " + playerName + " " + unitPrice);
                                player.sendMessage("Your balance has been reduced by " + unitPrice + " units.");

                                currentQuantity -= 1;

                                try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE trades SET Quantity=? WHERE LOWER(ItemName)=LOWER(?) AND LOWER(BuyOrSell)='sell'")) {
                                    updateStatement.setInt(1, currentQuantity);
                                    updateStatement.setString(2, cleanedDisplayNameFromPreviousEvent);
                                    int updatedRows = updateStatement.executeUpdate();

                                    if (updatedRows > 0) {
                                        player.sendMessage("The item quantity has been reduced by 1.");
                                    } else {
                                        player.sendMessage("Failed to update the item quantity in the database.");
                                    }
                                }

                                Material material = clickedItem.getType();
                                ItemStack itemToGive = new ItemStack(material, 1);
                                ItemMeta meta = itemToGive.getItemMeta();
                                meta.setDisplayName(cleanedDisplayNameFromPreviousEvent);
                                itemToGive.setItemMeta(meta);

                                player.getInventory().addItem(itemToGive);
                                player.sendMessage("You have received the item.");

                            } else {
                                player.sendMessage("The item with displayName " + cleanedDisplayNameFromPreviousEvent + " is not available for purchase.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            player.sendMessage("An SQL error occurred: " + e.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendMessage("An error occurred while connecting to the database: " + e.getMessage());
                    }
                }
            }
        }
    }
}
