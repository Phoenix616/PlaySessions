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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        this.table = plugin.getConfig().getString("storage.table");

        String host = plugin.getConfig().getString("storage.host");
        int port = plugin.getConfig().getInt("storage.port");
        String database = plugin.getConfig().getString("storage.database");

        ds = new HikariDataSource();
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        ds.setUsername(plugin.getConfig().getString("storage.username"));
        ds.setPassword(plugin.getConfig().getString("storage.password"));
        ds.setConnectionTimeout(5000);

        initializeTable();
    }

    private void initializeTable() throws SQLException {
        // create table
        try (Statement stat = getConn().createStatement()){
            String tableSql = "CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                    "id MEDIUMINT NOT NULL AUTO_INCREMENT, " +
                    "playerid CHAR(36) NOT NULL PRIMARY KEY, " +
                    "playername CHAR(16) NOT NULL, " +
                    "location VARCHAR, " +
                    "starttime DATETIME, " +
                    "endtime DATETIME" +
                    ")";
            stat.execute(tableSql);
        }
    }

    @Override
    public boolean saveSession(PlaySession session) {
        String insertSql = "INSERT INTO `" + table + "` " +
                "(playerid, playername, server, starttime, endtime) " +
                "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stat = getConn().prepareStatement(insertSql)) {
            stat.setString(1, session.getPlayerId().toString());
            stat.setString(2, session.getPlayerName());
            stat.setString(3, session.getLocation());
            stat.setDate(4, new Date(session.getStart()));
            stat.setDate(5, new Date(session.getStart()));
            stat.execute();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to save session to MySQL! (" + session + ")", e);
            return false;
        }
    }

    @Override
    public List<PlaySession> getSessions(UUID playerId) {
        List<PlaySession> sessions = new ArrayList<>();
        String selectSql = "SELECT * FROM `" + table + "` " +
                "WHERE playerid=? " +
                "ORDER BY id";
        try (PreparedStatement stat = getConn().prepareStatement(selectSql)) {
            stat.setString(1, playerId.toString());
            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    sessions.add(new PlaySession(
                            playerId,
                            rs.getString("playername"),
                            rs.getString("location"),
                            rs.getDate("start").getTime(),
                            rs.getDate("end").getTime()
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
