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

import de.themoep.playsessions.core.PlaySessionsConfig;

import java.util.List;

class PluginConfig implements PlaySessionsConfig {

    private PlaySessions plugin;

    public PluginConfig(PlaySessions plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

    @Override
    public int getInt(String path, int def) {
        return plugin.getConfig().getInt(path, def);
    }

    @Override
    public String getString(String path) {
        return plugin.getConfig().getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return plugin.getConfig().getString(path, def);
    }

    @Override
    public boolean getBoolean(String path) {
        return plugin.getConfig().getBoolean(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return plugin.getConfig().getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return plugin.getConfig().getStringList(path);
    }
}
