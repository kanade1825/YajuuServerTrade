package net.kanade1825.litematica.yajuuservertrade;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class YajuuServerTrade extends JavaPlugin {


    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true)); // Load with verbose output
    }


    @Override
    public void onEnable() {

        CommandAPI.onEnable();


        //トレードNPCスポーンコマンド
        new CommandAPICommand("SpawnTradeNPC")
                .withArguments(
                        new StringArgument("Name")
                                //予測変換の一覧設定
                                .replaceSuggestions(ArgumentSuggestions.strings(
                                        "Mr.Tadokoro"
                                ))
                )
                .executes((sender, args) -> {

                    String name = args.get(0).toString();
                    if (name == null) {
                        sender.sendMessage("引数が有りません。");
                        return;
                    }
                    //argsの0番目（最初）（argsはリストっぽい）に名前が入っている。
                    //senderはコマンド送信者
                    SpawnTradeNPC.spawnNPC(sender, (args.get(0)).toString());
                })
                .withPermission(CommandPermission.OP)
                .register();


        Objects.requireNonNull(getCommand("setup")).setExecutor(new SetUP(this));


        //ほんとはこういうことはしないようにね
        PluginManager pluginManager = Bukkit.getPluginManager();

        // イベントリスナークラスを登録
        pluginManager.registerEvents(new TradeNPC(this), this);
        pluginManager.registerEvents(new InventoryClose(), this);


        new SpawnTradeNPC(this);


        getLogger().info("　　　　　 /＼　　　 ／ヽ\n" +
                "　　 　 　 {／￣￣￣｀ヽ!\n" +
                "　　　　 ∠＿＿╋＿＿ｊ\n" +
                ".ヨシ!　 /　 ①_八①　　ヽ\n" +
                "　　　　｛二ﾆ/(_人_)エ二 |´￣)｀ヽ\n" +
                "　　　　 ＼　{　{_,ﾉ　ﾉ 　 / /⌒ヽ　L\n" +
                "　 ⊂￣ ヽ_＞―――‐'’,〈　 　 (＿_)\n" +
                "　　 └ヘ(＿ ィ r―‐γ 　 !\n" +
                "　　　　 　 ＿,ﾉ j　　　|　　 |\n" +
                "　　　　　｛　　 ｛　 　 ﾉ　　/＼\n" +
                "　　 　 　 ＼ス￣￣￣lしｲ＼　＼\n" +
                "　 　 　 (￣　　） 　 　 j　/ 　 ＼_j＼\n" +
                "　　　　　￣￣　　 　 (　 ｀ヽ　　 ＼__)\n" +
                "　　　　　　　　　　　　 ＼__ﾉ\n");
        // Plugin startup logic

    }

    @Override
    public void onDisable() {


        getLogger().info("system stopped !");
        // Plugin shutdown logic
    }
}
