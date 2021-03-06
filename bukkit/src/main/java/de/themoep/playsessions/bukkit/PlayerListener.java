package de.themoep.playsessions.bukkit;


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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final PlaySessions plugin;

    public PlayerListener(PlaySessions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(PlayerJoinEvent event) {
        if (!plugin.isEnabled())
            return;

        if (event.getPlayer().hasPermission("playsessions.record"))
            return;

        plugin.startSession(event.getPlayer());
    }

    @EventHandler
    public void onServerSwitch(PlayerChangedWorldEvent event) {
        if (!plugin.isEnabled() || !plugin.shouldLogSwitches())
            return;

        if (!event.getPlayer().hasPermission("playsessions.record"))
            return;

        plugin.startSession(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.isEnabled())
            return;

        if (!event.getPlayer().hasPermission("playsessions.record"))
            return;

        plugin.stopSession(event.getPlayer());
    }
}
