package net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TradeBrowserMoveItemClickEvent implements Listener {

    private final List<String> directories = Arrays.asList(
            "plugins/Tradesystem/Category/Mining",
            "plugins/Tradesystem/Category/Farming",
            "plugins/Tradesystem/Category/Combat",
            "plugins/Tradesystem/Category/Oddities",
            "plugins/Tradesystem/Category/WoodsAndFishes"
            // 他のディレクトリを追加...

    );

    @EventHandler
    public void onViewPositionClick(InventoryClickEvent event) {
        var clickedInventory = event.getView();
        ItemStack clickedItem = event.getCurrentItem();

        if ("TradeMenu".equals(clickedInventory.getTitle())) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                // 指定された色コードを削除
                String displayName = clickedItem.getItemMeta().getDisplayName().replaceAll("§[6debce]§l", "");

                for (String directory : directories) {
                    JSONObject matchedJson = findJsonWithDisplayName(directory, displayName);
                    if (matchedJson != null) {
                        handleMatchingJson((Player) event.getWhoClicked(), matchedJson);
                        break;  // マッチするJSONが見つかった場合、ループを終了
                    }
                }
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
        // ここにマッチしたJSONに対する処理を書く
        // 例:
        player.sendMessage("You clicked on an item with the name: " + jsonObject.get("displayName"));
    }
}
