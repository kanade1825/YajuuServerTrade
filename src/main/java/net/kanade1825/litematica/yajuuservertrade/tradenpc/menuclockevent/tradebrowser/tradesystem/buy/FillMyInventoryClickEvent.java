package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.tradesystem.buy;

import jp.jyn.jecon.Jecon;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.TradeBrowserBuyInstantlyClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class FillMyInventoryClickEvent implements Listener {

    private static final Path DB_CONFIG_PATH = Paths.get("plugins/Tradesystem/Config/database_config.json");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9A-FK-ORa-fk-or]");
    private final Jecon jecon;
    private String dbURL;
    private String dbUser;
    private String dbPassword;

    public FillMyInventoryClickEvent(Jecon jecon) {
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

                if (displayName.equals("§a§lFill My Inventory!")) {
                    UUID playerUUID = player.getUniqueId();
                    String lastDisplayName = TradeBrowserBuyInstantlyClickEvent.lastDisplayNameMap.get(playerUUID);

                    if (lastDisplayName == null || lastDisplayName.isEmpty()) {
                        player.sendMessage("No item selected for trading.");
                        return;
                    }

                    String cleanedDisplayNameFromPreviousEvent = COLOR_CODE_PATTERN.matcher(lastDisplayName).replaceAll("");

                    try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
                        if (connection == null) {
                            player.sendMessage("Could not establish a database connection.");
                            return;
                        }

                        int emptySlots = (int) Arrays.stream(player.getInventory().getContents()).filter(item -> item == null || item.getType() == Material.AIR).count();
                        int totalItemsToBuy = emptySlots * 64;

                        String query = "SELECT * FROM trades WHERE LOWER(ItemName) = LOWER(?) AND LOWER(BuyOrSell)='sell' ORDER BY UnitPrice DESC";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            preparedStatement.setString(1, cleanedDisplayNameFromPreviousEvent);
                            ResultSet resultSet = preparedStatement.executeQuery();

                            int totalQuantity = 0;
                            int totalPrice = 0;

                            while (resultSet.next()) {
                                int unitPrice = resultSet.getInt("UnitPrice");
                                int availableQuantity = resultSet.getInt("Quantity");
                                int quantityToBuy = Math.min(totalItemsToBuy - totalQuantity, availableQuantity);

                                totalQuantity += quantityToBuy;
                                totalPrice += unitPrice * quantityToBuy;
                            }

                            if (totalQuantity < totalItemsToBuy) {
                                player.sendMessage("Not enough items in stock to fill your inventory.");
                                return;
                            }

                            Optional<BigDecimal> optionalBalance = jecon.getRepository().getDecimal(playerUUID);
                            if (!optionalBalance.isPresent()) {
                                player.sendMessage("Could not retrieve your balance.");
                                return;
                            }

                            BigDecimal balance = optionalBalance.get();
                            if (balance.compareTo(BigDecimal.valueOf(totalPrice)) < 0) {
                                player.sendMessage("You do not have enough balance to make this purchase.");
                                return;
                            }

                            jecon.getRepository().set(playerUUID, balance.subtract(BigDecimal.valueOf(totalPrice)));
                            player.sendMessage("Your balance has been reduced by " + totalPrice + " units.");

                            resultSet.beforeFirst();
                            int remainingItems = totalItemsToBuy;

                            try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE trades SET Quantity=? WHERE LOWER(ItemName)=LOWER(?) AND LOWER(BuyOrSell)='sell'")) {
                                while (resultSet.next() && remainingItems > 0) {
                                    int currentQuantity = resultSet.getInt("Quantity");
                                    int quantityToBuy = Math.min(remainingItems, currentQuantity);

                                    int newQuantity = currentQuantity - quantityToBuy;
                                    updateStatement.setInt(1, newQuantity);
                                    updateStatement.setString(2, cleanedDisplayNameFromPreviousEvent);
                                    int updatedRows = updateStatement.executeUpdate();

                                    if (updatedRows > 0) {
                                        player.sendMessage("The item quantity has been reduced by " + quantityToBuy + ".");
                                    } else {
                                        player.sendMessage("Failed to update the item quantity in the database.");
                                    }

                                    remainingItems -= quantityToBuy;
                                }
                            }

                            Material material = clickedItem.getType();
                            while (remainingItems > 0) {
                                int quantityToGive = Math.min(64, remainingItems);
                                ItemStack itemToGive = new ItemStack(material, quantityToGive);
                                ItemMeta meta = itemToGive.getItemMeta();
                                meta.setDisplayName(cleanedDisplayNameFromPreviousEvent);
                                itemToGive.setItemMeta(meta);

                                int firstEmptySlot = player.getInventory().firstEmpty();
                                if (firstEmptySlot >= 0) {
                                    player.getInventory().setItem(firstEmptySlot, itemToGive);
                                } else {
                                    boolean added = false;
                                    for (ItemStack item : player.getInventory().getContents()) {
                                        if (item != null && item.isSimilar(itemToGive) && item.getAmount() < item.getMaxStackSize()) {
                                            int newAmount = Math.min(item.getAmount() + quantityToGive, item.getMaxStackSize());
                                            item.setAmount(newAmount);
                                            added = true;
                                            break;
                                        }
                                    }
                                    if (!added) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), itemToGive);
                                        player.sendMessage("Your inventory is full. Some items were dropped on the ground.");
                                    }
                                }

                                remainingItems -= quantityToGive;
                            }

                            player.sendMessage("You have received " + totalItemsToBuy + " items.");
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
