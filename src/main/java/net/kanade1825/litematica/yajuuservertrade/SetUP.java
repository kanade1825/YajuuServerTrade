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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SetUP implements CommandExecutor {
    private final YajuuServerTrade yajuuServerTrade;
    private final Path configDirectoryPath = Paths.get("plugins/Tradesystem/Config");
    private final Path dbConfigFilePath = configDirectoryPath.resolve("database_config.json");
    // Default database connection parameters
    private String databaseURL = "jdbc:postgresql://localhost:5432/mydatabase";
    private String databaseUser = "username";
    private String databasePassword = "password";

    // Constructor for setting up the command
    public SetUP(YajuuServerTrade yajuuServerTrade) {
        this.yajuuServerTrade = yajuuServerTrade;
    }

    // Handle the /setup command
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // Check if user provided correct number of arguments
        if (args.length == 3) {
            // Update the database connection parameters
            databaseUser = args[0];
            databasePassword = args[1];
            databaseURL = args[2] + "?sslmode=disable";

            // Call the setup method
            setup(commandSender);

            // Save the database credentials to the JSON file after setup
            saveDatabaseCredentialsToJSON(databaseUser, databasePassword, databaseURL);

            return true;
        } else {
            commandSender.sendMessage("使用法: /setup ユーザ名 password URL");
            return false;
        }
    }

    // Setup method for creating necessary directories and database tables
    private void setup(CommandSender commandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(yajuuServerTrade, () -> {
            // Create necessary directories
            createDirectory(Paths.get("plugins/Tradesystem/CharacterData"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Farming"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Mining"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Combat"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/WoodsAndFishes"), commandSender);
            createDirectory(Paths.get("plugins/Tradesystem/Category/Oddities"), commandSender);

            // Create the database and table
            try {
                createDatabaseAndTable(commandSender);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Sample character data for testing purposes
            String CharacterNama = "Mr.Tadokoro.json";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SkinName", "9eef7ed2");
            // ... [remaining character data here] ...

            // Write the character data to a JSON file
            Path jsonFilePath = Paths.get("plugins/Tradesystem/CharacterData/" + CharacterNama);
            try {
                Files.write(jsonFilePath, jsonObject.toString().getBytes());
                commandSender.sendMessage("Successfully written to " + jsonFilePath);
            } catch (IOException e) {
                commandSender.sendMessage("Unable to write to " + jsonFilePath);
                e.printStackTrace();
            }
        });
    }

    // Method to create a directory at the given path
    private void createDirectory(Path path, CommandSender commandSender) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                if (commandSender != null) {
                    commandSender.sendMessage("Directory created at " + path);
                }
            } else {
                if (commandSender != null) {
                    commandSender.sendMessage("Directory already exists at " + path);
                }
            }
        } catch (IOException e) {
            if (commandSender != null) {
                commandSender.sendMessage("Unable to create directory at " + path);
            }
            e.printStackTrace();
        }
    }

    // Method to create the database and associated table
    private void createDatabaseAndTable(CommandSender commandSender) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
             Statement stmt = connection.createStatement()) {
            // Create a new database named "tradingsystem"
            String createDatabaseSQL = "CREATE DATABASE tradingsystem";
            stmt.executeUpdate(createDatabaseSQL);
            try (Connection newConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tradingsystem", databaseUser, databasePassword);
                 Statement newStmt = newConnection.createStatement()) {
                // Create a new table named "trades" inside the "tradingsystem" database
                String createTableSQL = "CREATE TABLE trades (" +
                        "MCID VARCHAR(255)," +
                        "BuyOrSell VARCHAR(255)," +
                        "Quantity INT," +
                        "UnitPrice INT," +
                        "ItemName VARCHAR(255)" +
                        ")";
                newStmt.executeUpdate(createTableSQL);
                commandSender.sendMessage("データベースとテーブルが正常に作成されました。");
            }
        } catch (Exception e) {
            commandSender.sendMessage("データベースまたはテーブルの作成中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    private void saveDatabaseCredentialsToJSON(String user, String password, String url) {
        createDirectory(configDirectoryPath, null);  // Ensure the config directory exists

        // Update the URL to point to the "tradingsystem" database
        url = "jdbc:postgresql://localhost:5432/tradingsystem";

        JSONObject dbConfig = new JSONObject();
        dbConfig.put("user", user);
        dbConfig.put("password", password);
        dbConfig.put("url", url);

        try {
            Files.write(dbConfigFilePath, dbConfig.toJSONString().getBytes());
        } catch (IOException e) {
            System.out.println("Error writing to database config file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
