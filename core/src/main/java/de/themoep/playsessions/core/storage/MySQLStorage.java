package de.themoep.playsessions.core.storage;
/*
 * Copyright 2016 Max Lee (https://github.com/Phoenix616/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * 
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */

import com.zaxxer.hikari.HikariDataSource;
import de.themoep.playsessions.core.PlaySession;
import de.themoep.playsessions.core.PlaySessionsPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLStorage implements SessionStorage {
    private final PlaySessionsPlugin plugin;
    private final String table;
    private final HikariDataSource ds;

    static {
        @SuppressWarnings("unused")
        Class driverClass = org.mariadb.jdbc.Driver.class;
        // We need to access it for the maven shade plugin to not minimize it away!
    }

    public MySQLStorage(PlaySessionsPlugin plugin) throws SQLException {
        this.plugin = plugin;

        plugin.getLogger().info("Loading MySQLStorage...");
        this.table = plugin.getPluginConfig().getString("storage.table");

        String host = plugin.getPluginConfig().getString("storage.host");
        int port = plugin.getPluginConfig().getInt("storage.port");
        String database = plugin.getPluginConfig().getString("storage.database");

        ds = new HikariDataSource();
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        ds.setUsername(plugin.getPluginConfig().getString("storage.username"));
        ds.setPassword(plugin.getPluginConfig().getString("storage.password"));
        ds.setConnectionTimeout(5000);

        initializeTable();
    }

    private void initializeTable() throws SQLException {
        // create table
        try (Connection conn = getConn(); Statement stat = conn.createStatement()){
            String tableSql = "CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                    "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
                    "playerid CHAR(36) NOT NULL, " +
                    "playername VARCHAR(16) NOT NULL, " +
                    "location VARCHAR(255), " +
                    "starttime TIMESTAMP NOT NULL, " +
                    "endtime TIMESTAMP, " +
                    "INDEX (playerid) " +
                    ") DEFAULT CHARACTER SET=utf8 AUTO_INCREMENT=1;";
            stat.execute(tableSql);
        }
    }

    @Override
    public boolean saveSession(PlaySession... sessions) {
        String insertSql = "INSERT INTO `" + table + "` " +
                "(playerid, playername, location, starttime, endtime) " +
                "VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConn(); PreparedStatement stat = conn.prepareStatement(insertSql)) {
            for (int i = 0; i < sessions.length; i++) {
                stat.setString(1, sessions[i].getPlayerId().toString());
                stat.setString(2, sessions[i].getPlayerName());
                stat.setString(3, sessions[i].getLocation());
                stat.setTimestamp(4, new Timestamp(sessions[i].getStart()));
                stat.setTimestamp(5, new Timestamp(sessions[i].getEnd()));
                stat.addBatch();

                if (i == sessions.length - 1 || i % 1000 == 0) {
                    stat.executeBatch();
                }
            }
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to save sessions to MySQL! (" + sessions.length + " sessions)", e);
            return false;
        }
    }

    @Override
    public List<PlaySession> getSessions(UUID playerId) {
        List<PlaySession> sessions = new ArrayList<>();
        String selectSql = "SELECT * FROM `" + table + "` " +
                "WHERE playerid=? " +
                "ORDER BY endtime DESC";
        try (Connection conn = getConn(); PreparedStatement stat = conn.prepareStatement(selectSql)) {
            stat.setString(1, playerId.toString());
            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    sessions.add(new PlaySession(
                            playerId,
                            rs.getString("playername"),
                            rs.getString("location"),
                            rs.getTimestamp("starttime").getTime(),
                            rs.getTimestamp("endtime").getTime()
                    ));
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while querying statement to get sessions of " + playerId + "!", e);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while preparing statement to get sessions of " + playerId + "!", e);
        }
        return sessions;
    }

    public Connection getConn() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void disable() {
        ds.close();
    }
}
