package de.themoep.playsessions.bukkit;

import de.themoep.playsessions.core.PlaySession;
import de.themoep.playsessions.core.PlaySessionsCommand;
import de.themoep.playsessions.core.PlaySessionsConfig;
import de.themoep.playsessions.core.PlaySessionsPlugin;
import de.themoep.playsessions.core.SessionManager;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.zaiyers.UUIDDB.core.UUIDDBPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/*
 * Copyright 2017 Max Lee (https://github.com/Phoenix616/)
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

public final class PlaySessions extends JavaPlugin implements PlaySessionsPlugin {

    private PluginConfig pluginConfig = new PluginConfig(this);
    private SessionManager manager;
    private UUIDDBPlugin uuiddb;
    private boolean logSwitches;

    @Override
    public void onEnable() {
        manager = new SessionManager(this);
        if (loadConfig()) {
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

            if (getServer().getPluginManager().isPluginEnabled("UUIDDB")) {
                uuiddb = (UUIDDBPlugin) getServer().getPluginManager().getPlugin("UUIDDB");
            }

            PlaySessions instance = this;
            getCommand("playsessions").setExecutor((sender, command, s, args) -> {
                if (!PlaySessionsCommand.execute(instance, sender instanceof Player ? ((Player) sender).getUniqueId() : null, args)) {
                    sender.sendMessage("Wrong Usage: /playsessions [list [<player> <#page>]|reload]");
                }
                return true;
            });
        }
    }

    public boolean loadConfig() {
        reloadConfig();
        logSwitches = getPluginConfig().getBoolean("log-switches");
        return manager.setupDatabase();
    }

    @Override
    public UUID getPlayerId(String playerName) {
        Player player = getServer().getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        if (uuiddb != null) {
            String uuidStr = uuiddb.getStorage().getUUIDByName(playerName, false);
            if (uuidStr != null) {
                return UUID.fromString(uuidStr);
            }
        }
        return manager.getPlayerId(playerName);
    }

    @Override
    public void onDisable() {
        if (!isEnabled())
            return;

        manager.disable();
    }

    public PlaySession startSession(Player player) {
        return manager.startSession(player.getUniqueId(), player.getName(), player.getWorld() != null ? player.getWorld().getName() : null);
    }

    /**
     * Stops the active session and saves it to the database
     * @param player    The player to stop the session for
     * @return          The active session; <tt>null</tt> if he didn't have one
     */
    public PlaySession stopSession(Player player) {
        return manager.stopSession(player.getUniqueId());
    }

    public boolean hasActiveSession(Player player) {
        return manager.hasActiveSession(player.getUniqueId());
    }

    /**
     * Get the active session of a player
     * @param player    The player to get the session for
     * @return          The active PlaySession, <tt>null</tt> if he doesn't have one
     */
    public PlaySession getActiveSession(Player player) {
        return manager.getActiveSession(player.getUniqueId());
    }

    @Override
    public SessionManager getManager() {
        return manager;
    }

    @Override
    public PlaySessionsConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public int runAsync(Runnable runnable) {
        return getServer().getScheduler().runTaskAsynchronously(this, runnable).getTaskId();
    }

    @Override
    public void sendMessage(UUID playerId, String message) {
        sendMessage(playerId, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(UUID playerId, BaseComponent[] message) {
        if (playerId != null) {
            Player player = getServer().getPlayer(playerId);
            if (player != null) {
                player.spigot().sendMessage(message);
            }
        } else {
            getServer().getConsoleSender().spigot().sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(UUID playerId, String permission) {
        if (playerId != null) {
            Player player = getServer().getPlayer(playerId);
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
