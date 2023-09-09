package net.kanade1825.litematica.yajuuservertrade;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SetUP implements CommandExecutor {
    private final YajuuServerTrade yajuuServerTrade;

    public SetUP(YajuuServerTrade yajuuServerTrade) {
        this.yajuuServerTrade = yajuuServerTrade;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(yajuuServerTrade, () -> {

            createDirectory(Paths.get("plugins/Tradesystem/CharacterData"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Farming"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Mining"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Combat"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/WoodsAndFishes"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Oddities"), commandSender);

            String CharacterNama = "Mr.Tadokoro.json";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SkinName", "9eef7ed2");
            jsonObject.put("Signeture", "lPpKmKKXk4zZlNg+UsNrut5zuLXn9LKUeMDtod1ujdwj5ru/gJCv/mwwe9eJNmcGSypxJCHHVcUTMSWKbsmz4MiK0Mu6327TsFkqUE7eM/Z4wjTh9m/Ma/vexfh7pnPigcP1Zi3bAyOXhU9klla2gutI5mk3vLCWtupDadzFJMvk/DKgK+VA6zU/YHpC6dhs8lVVUhziPPJLuGV4gxPhE0xT7tCnM9Z2H44eJ46hAjQ/BHKGXLz39J7B2baQsU9J3ll5uDBjlaJcNB9Bw8dmu5nB8SPI+QUVM5SxEit6k0OwQmOnXiesXzdeaEhwQBqcSXXZAIvghzy39Aim2cmWqV+ixifwRXbQrqHV5igjE+wEsguegbhqFkMkntPyKwaEt7WOLC9q+yCu6SjyBg04aC4Vs1eSQwBdwLJQG942eVDUphmvdfwRhicujf38CLwX9bt2fnXPidz1mNVcDT6CCcVSfCNXaLYbRXJxRcOwopo0NAp0Dk+SwwmeDkgAN7DukL0a/OUR+g2SwfUmLeXr66zpPPGKAToTkRoWIQAGw8mTk2xZO/PRUaL0RWLkYNoKWAaGHdqvalw8OjgT0E9/XWcWNFlkcFHsdAORJa8ZiNZx9NLki8GTblPdwsP+EnAqNz+0cubuzI7Chff+L2uvjOEt3v0OjiVAb/VNL5LA8Ks=");
            jsonObject.put("TextureValue", "ewogICJ0aW1lc3RhbXAiIDogMTY4NTEwNDk3MDc3NCwKICAicHJvZmlsZUlkIiA6ICJkMWYxODM4YjRlMTU0NTk0OGQwMjUwNzNkMzhjNWNjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJadWthWnVrYW1pIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ0NGQ3MzY3Y2MxMGMyMTQzYzEyYjlhNWQxNGMyNzNjZTI0YTIwZjI0MzViMWQ1ZGVlYTNhMjJjYzFiZDI4ZmMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");

            Path jsonFilePath = Paths.get("plugins/Tradesystem/CharacterData/" + CharacterNama);
            try {
                Files.write(jsonFilePath, jsonObject.toString().getBytes());
                commandSender.sendMessage("Successfully written to " + jsonFilePath);
            } catch (IOException e) {
                commandSender.sendMessage("Unable to write to " + jsonFilePath);
                e.printStackTrace();
            }
        });
        return false;
    }


    private void createDirectory(Path path, CommandSender commandSender) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                commandSender.sendMessage("Directory created at " + path);
            } else {
                commandSender.sendMessage("Directory already exists at " + path);
            }
        } catch (IOException e) {
            commandSender.sendMessage("Unable to create directory at " + path);
            e.printStackTrace();
        }
    }
}
