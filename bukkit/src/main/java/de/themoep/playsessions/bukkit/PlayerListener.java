package de.themoep.playsessions.bukkit;


/*
 * PlaySessions - bukkit - $project.description
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
