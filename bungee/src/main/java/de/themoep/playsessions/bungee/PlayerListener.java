package de.themoep.playsessions.bungee;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/*
 * PlaySessions - bungee - $project.description
 * Copyright (c) 2026 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
                plugin.startSession(event.getPlayer());
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

        plugin.startSession(event.getPlayer());
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
