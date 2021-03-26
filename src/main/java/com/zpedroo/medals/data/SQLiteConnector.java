package com.zpedroo.medals.data;

import com.zpedroo.medals.Main;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;
import java.sql.*;
import java.util.*;

public class SQLiteConnector {

    Connection connection;
    private Logger logger;

    public SQLiteConnector() {
        connection = null;
        (logger = Main.get().getLogger()).info("Connecting to the SQLite database...");
        long ms = System.currentTimeMillis();
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + loadFile("/players.db").getAbsolutePath();
            connection = DriverManager.getConnection(url);
            logger.info("Connection to SQLite has been established in " + (System.currentTimeMillis() - ms) + "ms.");
            checkTable();
        } catch (SQLException | ClassNotFoundException ex) {
            logger.severe("Failed to connect to the SQLite database:");
            ex.printStackTrace();
        }
    }

    private void checkTable() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='medals';")) {
            Long l = null;
            if (!resultSet.next()) {
                logger.info("Database table does not exist, creating...");
                l = System.currentTimeMillis();
            }
            statement.execute("CREATE TABLE IF NOT EXISTS `medals` (\n  `uuid` VARCHAR(255) NOT NULL,\n  `id` INT NOT NULL,\n  `custom` INT NOT NULL,\n  PRIMARY KEY (`uuid`));");
            if (l != null) {
                logger.info("Created table in " + (System.currentTimeMillis() - l) + "ms.");
            }
        }
    }

    public PlayerData loadPlayer(UUID uuid) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM medals where uuid='" + uuid.toString() + "';");
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int custom = resultSet.getInt("custom");
                return new PlayerData(uuid, id, custom);
            }
            return new PlayerData(uuid, -1, -1);
        } catch (SQLException e) {
            e.printStackTrace();
            return new PlayerData(uuid, -1, -1);
        }
    }

    public boolean savePlayer(PlayerData playerData) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT OR REPLACE INTO medals VALUES (\"" + playerData.getUUID().toString() + "\", " + playerData.getID() + ", " + playerData.getCustom() + ");");
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    private File loadFile(String filen) {
        File file = new File(Main.get().getDataFolder(), filen);
        if (!file.exists()) {
            try {
                if (file.getParent() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
