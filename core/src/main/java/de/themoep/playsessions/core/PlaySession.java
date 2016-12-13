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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlaySession {
    private final UUID playerId;
    private final String playerName;
    private final String location;
    private final long start;
    private long end;

    public PlaySession(UUID playerId, String playerName, String location) {
        this(playerId, playerName, location, System.currentTimeMillis(), -1);
    }

    public PlaySession(UUID playerId, String playerName, String location, long start, long end) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.location = location;
        this.start = start;
        this.end = end;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getLocation() {
        return location;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    /**
     * End this session (currently only sets the end field to the currentTimeMillis)
     */
    public void end() {
        this.end = System.currentTimeMillis();
    }

    /**
     * Get the duration of this play session
     * @return The duration in milliseconds
     */
    public long getDuration() {
        return end > 0 ? end - start : System.currentTimeMillis() - start;
    }

    public String getFormattedDuration() {
        long duration = getDuration() / 1000;

        int seconds = (int) (duration % 60);
        int minutes = (int) (TimeUnit.SECONDS.toMinutes(duration) % 60);
        int hours = (int) (TimeUnit.SECONDS.toHours(duration) % 24);
        int days = (int) (TimeUnit.SECONDS.toDays(duration) % 30);
        int months = (int) ((TimeUnit.SECONDS.toDays(duration) / 30) % 365);
        int years = (int) (TimeUnit.SECONDS.toDays(duration) / 30 / 365);

        StringBuilder sb = new StringBuilder();

        if(years > 0)
            sb.append(years).append("y");

        if(months > 0)
            sb.append(months).append("mo");

        if(days > 0)
            sb.append(days).append("d");

        if(hours > 0)
            sb.append(hours).append("h");

        if(minutes > 0)
            sb.append(minutes).append("m");

        if(seconds > 0)
            sb.append(seconds).append("s");

        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{playerId=" + playerId + ",playerName=" + playerName + ",location=" + location + ",start=" + start + ",end=" + end + "}";
    }
}
