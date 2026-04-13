# PlaySessions

This is a simple Minecraft plugin which tracks when players start and stop playing.

It supports BungeeCord proxy networks to track server switches as well. 

Please note that only sessions of players with the permission `playsessions.record` are recorded.
This permission is not granted by default!

## Commands

- `/playsessions list` - `playsessions.command.list` - List the current active sessions
- `/playsessions list <username/uuid> [<#page>]` - `playsessions.command.list` - List a user's sessions
- `/playsessions reload` - `playsessions.command.reload` - Reload the config

## Config

The config basically only includes the settings for your mysql database. (Only storage method supported right now)

```yaml
log-switches: false
storage:
  type: mysql
  username: username
  password: password1
  database: mydatabase
  host: localhost
  port: 3306
  table: playsessions
```

## Downloads

You can get builds from the latest commits from the [Minebench.de Jenkins](https://ci.minebench.de/job/PlaySessions/).

## Is it open source?

Yes, it is open source! Unless noted otherwise in the source it's licensed under AGPLv3.

```
 PlaySessions
 Copyright (c) 2026 Max Lee aka Phoenix616 (max@themoep.de)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published
 by the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
