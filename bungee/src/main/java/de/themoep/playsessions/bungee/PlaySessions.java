package de.themoep.playsessions.bungee;

import de.themoep.playsessions.core.PlaySession;
import de.themoep.playsessions.core.PlaySessionsCommand;
import de.themoep.playsessions.core.PlaySessionsConfig;
import de.themoep.playsessions.core.PlaySessionsPlugin;
import de.themoep.playsessions.core.SessionManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.UUIDDB.core.UUIDDBPlugin;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

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

public final class PlaySessions extends Plugin implements PlaySessionsPlugin {

    private SessionManager manager;
    private FileConfiguration config;
    private boolean enabled = false;
    private UUIDDBPlugin uuiddb;
    private boolean logSwitches;

    @Override
    public void onEnable() {
        manager = new SessionManager(this);
        if (loadConfig()) {
            getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

            if (getProxy().getPluginManager().getPlugin("UUIDDB") != null) {
                uuiddb = (UUIDDBPlugin) getProxy().getPluginManager().getPlugin("UUIDDB");
            }

            PlaySessions instance = this;
            getProxy().getPluginManager().registerCommand(this, new Command("playsessions", "playsessions.command", "playsession", "ps") {
                @Override
                public void execute(CommandSender sender, String[] args) {
                    if (!PlaySessionsCommand.execute(instance, sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : null, args)) {
                        sender.sendMessage("Wrong Usage: /playsessions [list [<player> <#page>]|reload]");
                    }
                }
            });

            enabled = true;
        }
    }

    public boolean loadConfig() {
        try {
            config = new FileConfiguration(this, "config.yml");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error while loading config", e);
            return false;
        }
        logSwitches = getConfig().getBoolean("log-switch");
        return manager.setupDatabase();
    }

    @Override
    public UUID getPlayerId(String playerName) {
        ProxiedPlayer player = getProxy().getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        if (uuiddb != null) {
            String uuidStr = uuiddb.getStorage().getUUIDByName(playerName, false);
            if (uuidStr != null) {
                return UUID.fromString(uuidStr);
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (!isEnabled())
            return;

        manager.disable();
    }

    public PlaySession startSession(ProxiedPlayer player) {
        return manager.startSession(player.getUniqueId(), player.getName(), player.getServer() != null ? player.getServer().getInfo().getName() : null);
    }

    /**
     * Stops the active session and saves it to the database
     * @param player    The player to stop the session for
     * @return          The active session; <tt>null</tt> if he didn't have one
     */
    public PlaySession stopSession(ProxiedPlayer player) {
        return manager.stopSession(player.getUniqueId());
    }

    public boolean hasActiveSession(ProxiedPlayer player) {
        return manager.hasActiveSession(player.getUniqueId());
    }

    /**
     * Get the active session of a player
     * @param player    The player to get the session for
     * @return          The active PlaySession, <tt>null</tt> if he doesn't have one
     */
    public PlaySession getActiveSession(ProxiedPlayer player) {
        return manager.getActiveSession(player.getUniqueId());
    }

    @Override
    public SessionManager getManager() {
        return manager;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public PlaySessionsConfig getConfig() {
        return config;
    }

    @Override
    public int runAsync(Runnable runnable) {
        return getProxy().getScheduler().runAsync(this, runnable).getId();
    }

    @Override
    public void sendMessage(UUID playerId, String message) {
        sendMessage(playerId, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(UUID playerId, BaseComponent[] message) {
        if (playerId != null) {
            ProxiedPlayer player = getProxy().getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        } else {
            getProxy().getConsole().sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(UUID playerId, String permission) {
        if (playerId != null) {
            ProxiedPlayer player = getProxy().getPlayer(playerId);
            if (player != null) {
                return player.hasPermission(permission);
            }
            return false;
        }
        return true;
    }

    public boolean shouldLogSwitches() {
        return logSwitches;
    }
}
