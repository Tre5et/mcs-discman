# Minecraft Server Discord Manager by TreSet
A Manager for your existing Minecraft Server on Linux, fully controlled through Discord.

## Features
- Permanent logging of certain server events in Discord
  - Server start
  - Player join / leave
- Automatic backup
  - Completely safe without server restart
  - Togglable using Discord Commands
  - Adjustable backup time
  - Automatic upload to Google Drive
- Server crash detection
  - Automatically restart the server to minimize downtime
- Many Discord commands for server management
  - Start / stop / restart
  - Create Backup
  - Run a command on the server
- Many Discord commands for easy access of server information
  - Is the server online
  - What version and mods the server is running
  - How to join the server
  - Who is allowed to join the server
  - Where to find backups
- An optional fabric mod for your minecraft server to enable advanced functionality
  - Death and Advancement logging
  - Faster and more reliable join and leave logging
  - Getting ingame properties like time through discord
  - Sending messages to discord from an ingame command
- Easy to understand config file for toggling (almost) all features
- Works with most ways of running Minecraft servers on linux, so you can use your existing server

## [Setup](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md)

## Notes
- Validated to work with Minecraft Vanilla and Fabric versions 1.17.0 - 1.19.0, most other minecraft servers will likely work too.
- Thus far Linux only, validated on Raspberry Pi 4B running Raspberry Pi OS Lite x32 and x64, most other Linux distros will likely work too.
- Might also work on macOS maybe, as it's got the same shell as Linux? Can't validate.
- There are plans for Windows compatibility.
- There are no plans for intentional macOS compatibility.

## Licence
This piece of software is available under the GNU GPLv3 license. Feel free to redistribute, modify and incorporate it in your own open-source projects.
