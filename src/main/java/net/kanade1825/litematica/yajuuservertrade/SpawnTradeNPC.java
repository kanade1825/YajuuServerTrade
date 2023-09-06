package net.kanade1825.litematica.yajuuservertrade;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SpawnTradeNPC implements CommandExecutor {

    private static YajuuServerTrade yajuuServerTrade;

    public SpawnTradeNPC(YajuuServerTrade yajuuServerTrade) {
        SpawnTradeNPC.yajuuServerTrade = yajuuServerTrade;
    }


    public static void spawnNPC(CommandSender sender, String commandname) {
        if (CitizensAPI.hasImplementation()) {
            String NPCname = commandname;

            String jsonFilePath = "plugins/Tradesystem/CharacterData/" + NPCname + ".json";

            String data = "";
            String skinname = "";
            String signeture = "";

            try {
                String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
                JSONObject json = new JSONObject(content);
                data = json.getString("TextureValue");
                skinname = json.getString("SkinName");
                signeture = json.getString("Signeture");
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage("ファイルの読み込みに失敗しました。");
                return;
            }

            //プレイヤーの位置を取得
            Player player = (Player) sender;
            Location location = player.getLocation();

            // CitizensAPIを使用してNPCを作成する
            net.citizensnpcs.api.npc.NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, NPCname);
            SkinTrait SkinTrait = npc.getOrAddTrait(net.citizensnpcs.trait.SkinTrait.class);

            SkinTrait.setSkinPersistent(skinname, signeture, data);

            // NPCをプレイヤーの位置にスポーンさせる
            npc.spawn(location);
            player.sendMessage("NPCが出現しました。");

        } else {
            sender.sendMessage("CitizensAPIが見つかりません。");
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
