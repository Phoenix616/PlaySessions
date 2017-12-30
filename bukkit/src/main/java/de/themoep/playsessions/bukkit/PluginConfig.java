package de.themoep.playsessions.bukkit;

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
