package de.themoep.playsessions.bungee;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

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

public class PlayerListener implements Listener {
    private final PlaySessions plugin;

    public PlayerListener(PlaySessions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        if (event.isCancelled() || !plugin.isEnabled())
            return;

        if (!event.getPlayer().hasPermission("playsessions.record"))
            return;

        if (event.getPlayer().getServer() == null) {
            // player wasn't connected -> new join
            if (!plugin.shouldLogSwitches()) {
                plugin.startSession(event.getPlayer(), event.getTarget().getName());
            }
        } else if (plugin.shouldLogSwitches()){
            // player was connected to server before
            plugin.stopSession(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if (!plugin.isEnabled() || !plugin.shouldLogSwitches())
            return;

        if (!event.getPlayer().hasPermission("playsessions.record"))
            return;

        plugin.startSession(event.getPlayer(), event.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        if (!plugin.isEnabled())
            return;

        if (!event.getPlayer().hasPermission("playsessions.record"))
            return;

        plugin.stopSession(event.getPlayer());
    }
}
