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

import net.md_5.bungee.api.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlaySessionsCommand {

    private static final int PAGE_SIZE = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final String NO_PERMISSION = ChatColor.RED + "You don't have the permission to use this!";

    /**
     * Execute the command
     * @param senderId  The UUID of the sender or null if it is the console
     * @param args      The command arguments
     * @return          Whether or not it executed successfully
     */
    public static boolean execute(PlaySessionsPlugin plugin, UUID senderId, String[] args) {
        if (args.length > 0) {
            try {
                SubCommands subCommand = SubCommands.valueOf(args[0].toUpperCase());
                if (!plugin.hasPermission(senderId, "playsessions.command." + subCommand.toString().toLowerCase())) {
                    plugin.sendMessage(senderId, NO_PERMISSION);
                    return true;
                }

                switch (subCommand) {
                    case RELOAD:
                        if (plugin.loadConfig()) {
                            plugin.sendMessage(senderId, ChatColor.GREEN + "Config reloaded!");
                        } else {
                            plugin.sendMessage(senderId, ChatColor.RED + "Error while reloading config!");
                        }
                        break;
                    case LIST:
                        if (args.length > 1) {
                            plugin.runAsync(() -> {
                                UUID playerId = null;
                                try {
                                    playerId = UUID.fromString(args[1]);
                                } catch (IllegalArgumentException e) {
                                    playerId = plugin.getPlayerId(args[1]);
                                }
                                int page = 0;
                                if (args.length > 2) {
                                    try {
                                        page = Integer.parseInt(args[2]) - 1;
                                    } catch (NumberFormatException e) {
                                        plugin.sendMessage(senderId, ChatColor.YELLOW + args[2] + ChatColor.RED + " is not a valid page number! Syntax: /playsessions list <player> <#page>");
                                        return;
                                    }
                                }
                                if (page < 0) {
                                    page = 0;
                                }
                                if (playerId != null) {
                                    plugin.sendMessage(senderId, ChatColor.YELLOW + args[1] + ChatColor.GREEN + "'s sessions:");

                                    if (plugin.getManager().hasActiveSession(playerId)) {
                                        PlaySession activeSession = plugin.getManager().getActiveSession(playerId);
                                        plugin.sendMessage(senderId, ChatColor.YELLOW + " Online" +
                                                ChatColor.GREEN + " for " + ChatColor.YELLOW + activeSession.getFormattedDuration() +
                                                ChatColor.GREEN + " since " + ChatColor.YELLOW + DATE_FORMAT.format(new Date(activeSession.getStart())) +
                                                (activeSession.getLocation() != null ? ChatColor.GREEN + " at " + ChatColor.YELLOW + activeSession.getLocation() : "")
                                        );
                                    }

                                    List<PlaySession> sessions = plugin.getManager().getSessions(playerId);
                                    for (int i = page * PAGE_SIZE; i < (page + 1) * PAGE_SIZE && i < sessions.size(); i++) {
                                        PlaySession session = sessions.get(i);
                                        plugin.sendMessage(senderId, ChatColor.YELLOW + " " + DATE_FORMAT.format(new Date(session.getStart())) +
                                                ChatColor.GREEN + " for " + ChatColor.YELLOW + session.getFormattedDuration() +
                                                (session.getLocation() != null ? ChatColor.GREEN + " at " + ChatColor.YELLOW + session.getLocation() : "")
                                        );
                                    }
                                    if (sessions.size() == 0) {
                                        plugin.sendMessage(senderId, ChatColor.RED + " No past sessions found!");
                                    }

                                } else {
                                    plugin.sendMessage(senderId, ChatColor.RED + "No player with the name " + ChatColor.YELLOW + args[1] + ChatColor.RED + " found!");
                                }
                            });
                        } else {
                            plugin.sendMessage(senderId, ChatColor.GREEN + "Active sessions:");
                            for (PlaySession session : plugin.getManager().getActiveSessions()) {
                                plugin.sendMessage(senderId, ChatColor.YELLOW + " " + session.getPlayerName() +
                                        ChatColor.GREEN + " since " + ChatColor.YELLOW + DATE_FORMAT.format(new Date(session.getStart())) +
                                        (session.getLocation() != null ? ChatColor.GREEN + " at " + ChatColor.YELLOW + session.getLocation() : "")
                                );
                            }
                        }
                        break;
                }
                return true;
            } catch (IllegalArgumentException ignored) {}
        }
        return false;
    }

    private enum SubCommands {
        RELOAD,
        LIST;
    }
}
