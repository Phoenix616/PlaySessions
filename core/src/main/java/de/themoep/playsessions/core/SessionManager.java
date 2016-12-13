package de.themoep.playsessions.core;
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

import de.themoep.playsessions.core.storage.MySQLStorage;
import de.themoep.playsessions.core.storage.SessionStorage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SessionManager {
    private final PlaySessionsPlugin plugin;
    private SessionStorage storage = null;
    private Map<UUID, PlaySession> activeSessions = new HashMap<>();

    public SessionManager(PlaySessionsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean setupDatabase() {
        if ("mysql".equalsIgnoreCase(plugin.getConfig().getString("storage.type"))) {
            try {
                storage = new MySQLStorage(plugin);
                return true;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error while initializing MySQLStorage! Will not store non-active sessions!", e);
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "Unknown storage type " + plugin.getConfig().getString("storage.type") + "! Will not store non-active sessions!");
        }
        return false;
    }

    public PlaySession startSession(UUID playerId, String playerName, String serverName) {
        if (hasActiveSession(playerId)) {
            plugin.getLogger().log(Level.WARNING, playerName + " already has an active session? Discarding it! (" + getActiveSession(playerId) + ")");
        }
        PlaySession session = new PlaySession(playerId, playerName, serverName);
        addSession(session);
        return session;
    }

    private void addSession(PlaySession session) {
        activeSessions.put(session.getPlayerId(), session);
    }

    public boolean hasActiveSession(UUID playerId) {
        return getActiveSession(playerId) != null;
    }

    /**
     * Get the active session of a player
     * @param playerId  The uuid of the player to get the session for
     * @return          The active PlaySession, <tt>null</tt> if he doesn't have one
     */
    public PlaySession getActiveSession(UUID playerId) {
        return activeSessions.get(playerId);
    }

    public PlaySession stopSession(UUID playerId) {
        PlaySession session = getActiveSession(playerId);
        if (session != null) {
            activeSessions.remove(playerId);
            if (storage != null) {
                plugin.runAsync(() -> storage.saveSession(session));
            }
        }
        return session;
    }

    public void disable() {
        if (storage != null) {
            storage.disable();
        }
    }

    public Collection<PlaySession> getActiveSessions() {
        return activeSessions.values();
    }

    public List<PlaySession> getSessions(UUID playerId) {
        return storage.getSessions(playerId);
    }
}
