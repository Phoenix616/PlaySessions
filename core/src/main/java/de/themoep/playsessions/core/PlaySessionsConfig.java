package de.themoep.playsessions.core;/*
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

import java.util.List;

public interface PlaySessionsConfig {

    int getInt(String path);

    int getInt(String path, int def);

    String getString(String path);

    String getString(String path, String def);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path);
}
