package net.kanade1825.litematica.yajuuservertrade;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.TradeNPC;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.TradeNPCInventoryClick;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.TradeNPCInventoryClose;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.CloseClockEvent;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.ManageTradesClockEvent;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.tradebrowser.*;
import net.kanade1825.litematica.yajuuservertrade.tradenpc.menuclockevent.ViewPositionClockEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        pluginManager.registerEvents(new TradeNPC(), this);
        pluginManager.registerEvents(new TradeNPCInventoryClose(), this);
        pluginManager.registerEvents(new TradeNPCInventoryClick(), this);
        pluginManager.registerEvents(new CloseClockEvent(), this);
        pluginManager.registerEvents(new ManageTradesClockEvent(),this);
        pluginManager.registerEvents(new ViewPositionClockEvent(),this);
        pluginManager.registerEvents(new TradeBrowserClickEvent(),this);
        pluginManager.registerEvents(new TradeBrowserCombatClockEvent(),this);
        pluginManager.registerEvents(new TradeBrowserFarmingClickEvent(),this);
        pluginManager.registerEvents(new TradeBrowserMiningClickEvent(),this);
        pluginManager.registerEvents(new TradeBrowserWoodsAndFishesClickEvent(),this);
        pluginManager.registerEvents(new TradeBrowserOdditiesClickEvent(),this);



        new SpawnTradeNPC(this);


        getLogger().info(
                "　　　　　 /＼　　　 ／ヽ\n" +
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboardTags().contains("TradeMenuOpen")) {
                player.removeScoreboardTag("TradeMenuOpen");
            }
        }

        getLogger().info("system stopped !");
        // Plugin shutdown logic
    }
}
