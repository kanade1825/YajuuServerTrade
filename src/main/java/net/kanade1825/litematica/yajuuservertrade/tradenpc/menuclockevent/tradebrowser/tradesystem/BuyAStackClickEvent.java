package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.tradesystem;

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
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuyAStackClickEvent implements Listener {
    private static final Path DB_CONFIG_PATH = Paths.get("plugins/Tradesystem/Config/database_config.json");
    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private Jecon jecon;

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9A-FK-ORa-fk-or]");

    public BuyAStackClickEvent(Jecon jecon) {
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

                if (displayName.equals("§a§lBuy a stack!")) {  // ボタンの表示名を適切に変更
                    UUID playerUUID = player.getUniqueId();
                    String lastDisplayName = TradeBrowserBuyInstantlyClickEvent.lastDisplayNameMap.get(playerUUID);  // Retrieve the last display name

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

                        // スクロール可能な ResultSet を作成
                        // スクロール可能な ResultSet を作成
                        String query = "SELECT * FROM trades WHERE LOWER(ItemName) = LOWER(?) AND LOWER(BuyOrSell)='sell' ORDER BY UnitPrice DESC";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            preparedStatement.setString(1, cleanedDisplayNameFromPreviousEvent);  // 実際の変数を使用

                            ResultSet resultSet = preparedStatement.executeQuery();

                            int totalQuantity = 0;
                            int totalPrice = 0;

                            // 初回のResultSetの走査で、購入する総量と総価格を計算
                            while (resultSet.next()) {
                                int unitPrice = resultSet.getInt("UnitPrice");
                                int availableQuantity = resultSet.getInt("Quantity");
                                totalQuantity += availableQuantity;
                                totalPrice += unitPrice * availableQuantity;
                            }

                            // カーソルをリセットしてデータベースを更新
                            resultSet.beforeFirst();  // これがエラーなく動作するはず

                            // 二回目のResultSetの走査で、在庫を減らす
                            while (resultSet.next()) {
                                int unitPrice = resultSet.getInt("UnitPrice");
                                int availableQuantity = resultSet.getInt("Quantity");
                                int quantityToBuy = Math.min(64 - totalQuantity, availableQuantity);

                                totalQuantity += quantityToBuy;
                                totalPrice += unitPrice * quantityToBuy;
                            }

                            if (totalQuantity < 64) {
                                player.sendMessage("Not enough items in stock to buy a stack.");
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


                            // Deduct balance
                            jecon.getRepository().set(playerUUID, balance.subtract(BigDecimal.valueOf(totalPrice)));
                            player.sendMessage("Your balance has been reduced by " + totalPrice + " units.");

                            // Reset ResultSet cursor and update database
                            resultSet.beforeFirst();
                            int remainingStack = 64;  // 64 items to buy in total

                            try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE trades SET Quantity=? WHERE LOWER(ItemName)=LOWER(?) AND LOWER(BuyOrSell)='sell'")) {
                                while (resultSet.next() && remainingStack > 0) {
                                    int currentQuantity = resultSet.getInt("Quantity");
                                    int quantityToBuy = Math.min(remainingStack, currentQuantity);

                                    // Update item quantity in the database
                                    int newQuantity = currentQuantity - quantityToBuy;
                                    updateStatement.setInt(1, newQuantity);
                                    updateStatement.setString(2, cleanedDisplayNameFromPreviousEvent);
                                    int updatedRows = updateStatement.executeUpdate();

                                    if (updatedRows > 0) {
                                        player.sendMessage("The item quantity has been reduced by " + quantityToBuy + ".");
                                    } else {
                                        player.sendMessage("Failed to update the item quantity in the database.");
                                    }

                                    remainingStack -= quantityToBuy;
                                }
                            }

                            // Give the item to the player
                            Material material = clickedItem.getType();
                            ItemStack itemToGive = new ItemStack(material, 64);  // Giving 64 items
                            ItemMeta meta = itemToGive.getItemMeta();
                            meta.setDisplayName(cleanedDisplayNameFromPreviousEvent);
                            itemToGive.setItemMeta(meta);

                            player.getInventory().addItem(itemToGive);
                            player.sendMessage("You have received a stack of the item.");
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