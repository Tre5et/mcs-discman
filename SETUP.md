# Setup

## Content
Required Steps:
- [Download](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#download)
- [Setup the Discord bot](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-the-discord-bot)
- [Edit the Config](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#edit-the-config)
- [Start the Manager](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#start-the-manager)

Optional Steps:
- [Setup the client mod](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-the-client-mod)
- [Setup automatic backup uplaods to Google Drive](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-automatic-backup-uplaods-to-google-drive)
- [Create a service for easy start of the discord bot](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#create-a-service-for-easy-start-of-the-manager)
- [Setup a Minecraft server service](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-a-minecraft-server-service)

# Required Steps
## Download
- Download the latest ```mcsdiscman-[X.X.X].zip``` file from [releases](https://github.com/Tre5et/mcs-discman/releases).
- Extract the files and put them together into a folder.

## Enable developer mode in discord
You are going to need this later.
- Open the discord app or the web app.
- Open your user settings.
- Under "Advanced" enable "Developer Mode".

## Setup the Discord bot
- Go to the [discord developer console](https://discord.com/developers/home).
- In the "Applications" tab click on "New Application".
- Enter the name for your bot and click "Create".
- In the "Bot" tab click "Add bot" and confirm by clicking "Yes, do it".
- Add any information like picture, description etc. if you want.
- I recommend turning the option "Public Bot" off
- In the tab "OAuth2" -> "URL Generator" select "bot" and "applications.commands" in the top tab.
- In the lower tab select "Read Messages/View Channels", "Moderate Members", "Send Messages" and "Manage Messages".
- Copy the link at the bottom of the page and open it in your browser.
- Select the Discord server you want the bot to be active in and click "Continue" and then "Authorize".
- If you want the bot to be active in a private channel (which I recommend), make sure to give the newly created role for your bot permissions to access it.

## Edit the config
The configuration takes place in the `mcsdiscman.yaml` file. Don't worry if you don't know YAML, it's very easy! The file included by default contains placeholders for all required options.

### `discord`
This includes information on how to connect to discord.

- `token` contains the token to your discord bot. You can find it in the [discord developer console](https://discord.com/developers/home).
  - Select the "Applications" tab on the left and click on the application you created.
  - Open the "Bot" tab on the left and find "token".
  - Click copy to copy your token. If you do not see a copy option, click "Reset token" to generate a new one. This will cause all previously configured applications to no longer work.
  - Paste the token after the `:` in `discord.token`.
  - Make sure not to share your token and config with anyone, as it will give them access to you bot (you don't want that).
- `guild` configures the server the bot operates in.
  - Make sure you have developer mode enabled.
  - Right-click your servers title and select "Copy ID".
  - Paste the value after the `:` in `discord.guild`
- `channels` allows you to define channels your bot is active in. For now, you only need to configure a default channel.
  - Right-click the channel you want your bot to operate in and click "Copy ID".
  - Paste the value after the `:` in `discord.channels.default`
- `roles` allows you to define roles to gate access to commands. For now, you only need to configure the moderator role.
  - Open your servers settings by clicking on the name in the top left and selecting "Server Settings".
  - Click on the roles tab and create a role that all people with moderator access should have. If you already have a role like that, leave it.
  - Right-click the role and select "Copy ID".
  - Paste the value after the `:` in `discord.roles.moderator`.

You're done configuring the discord integration!

### `server`
This includes information on where to connect to your server.

The functionality of the discord bot relies on the [Minecraft Server Management Protocol](https://minecraft.wiki/w/Minecraft_Server_Management_Protocol) introduced in Minecraft 1.21.9. Open you servers `server.properties` to configure it and change the following options:
- `management-server-enabled=true`
- `management-server-host=localhost` (if you are configuring anything else except `localhost`, you already know what you are doing)
- `management-server-port=25569` (this can be almost any number, I like 25569 because it contains the funny number)
- `management-server-secret` should already have a value, which you can leave. If no value is present, start your server once and one will be generated. You will need this value later.
- `management-server-tls-enabled=false`. Technically, this is not recommended, but if you are running the server on the same computer as the discord bot, it is totally fine. If you want or need to enable TLS, you need to also supply a `management-server-tls-keystore` and `management-server-tls-keystore-password`.

You have successfully configured your server. Make sure to restart it to apply the changes. Now on to configuring the discord bot. Open `mcsdiscman.yaml` and go to the `server` section.
- `host` configures the host of your server. If you are running the server on the same computer, configure `localhost`.
  - If you configured TLS in your server, add the option `ssl: true`.
- `port` configures the port your management server is running in. Use the value you configured in `management-server-port`, default `25569`.
- `secret` contains the secret of your management server. Copy the value from `management-server-secret`. Again, make sure not to share this value or the config file with anyone as they might get access to your server.
- `worldPath` configures the path, at which your servers world is saved. This will be individual based on your setup, so you need to figure it out yourself. It may be a relative path, but an absolute path is recommended.
- `startCommand` configures how to start your server. You should provide the command as a list of arguments. This will be individual based on your setup. Generally, you should use the command that you used to start your server manually before, split after each space. E.g. if you used `java -jar server.jar`, you should now configure `["java", "-jar", "server.jar"]`.
  - If you need to run the command in a different directory, you can do so by adding the option `commandDirectory: {your directory path}`.

You have now configured all the necessary options and can start the discord bot!

## Starting the Server Manager
To start the Manager, simply run the `mcsdiscman.jar` file using java. For example using `java -jar mcsdiscman.jar`. The manager should now start and connect to discord. You should see the message "Hi, I'm online now!" in the channel you configured as `default`.

If that succeeds, check that interaction with the server is working correctly. Ensure your server is not running and type the command `/startserver` in the discord channel. If everything works correctly, you should see the message "Starting server..." and after the server has started, you should see "Server started.".

Finally, you can test interaction with the server. Simply join the server, and you should see the message "{playername} joined the game." in discord. Type `/online` in discord, and you should see a list of online player containing your name as a response. 

If any of these steps don't work as expected, refer to the [troubleshooting guide](#troubleshooting-the-server-manager).

If all of that works, you are done here and may move on to the optional steps.

# Recommended steps:
## Setup the companion mod
The companion mod enables advanced functionality like death and advancement logging or running commands on the server. The mod only works on fabric loader for versions 26.1 and above.
- Download a version of the companion mod that supports your Minecraft version from [GitHub](https://github.com/Tre5et/mcs-discman-client/releases).
- Download a version of the MC Server Management Extender Library that supports your Minecraft version from [GitHub](https://github.com/Tre5et/mc-server-management-extender/releases).
- Place both mods into the mods folder of your fabric server.
- Restart your server.

The advanced functionality is now enabled.

## Configure backups
### General options

Paste the following configuration at the end of your `mcsdiscman.yaml`:
```yaml
backup:
  path:
```
Change the `path` to the directory you want your backups to be saved.

After restarting the server or running `/reloadconfig`, you should be able to execute `/createbackup` with two modes:
- while running: creates a backup while the server is running.
- restart: stops the server before creating the backup and restarts it after

### Notifications
You can choose to notify players before creating the backup. Two options in the `backup` block enable this:
- `notifyIf` controls when players are notified. The following options are available:
  - `always`: notifications are always sent
  - `never`: notifications are never sent
  - `notEmpty`: notifications are only sent when there are players on the server
  - `restart`: notifications are always set if the backup mode is 'restart'
  - `notRestart`: notifications are always sent if the backup mode is 'while running'
  - `restartAndNotEmpty`: notification are sent if the backup mode is 'restart' and there are players on the server (default)
  - `notRestartAndNotEmpty`: notification are sent if the backup mode is 'while running' and there are players on the server
- `notifiyAt` controls at what times players are notified. The times are provided as a list of seconds, from longest before to shortest before.
  - The default configuration is:
  ```yaml
  notifyAt:
    - 300
    - 60
    - 30
    - 15
    - 10
    - 5
    - 4
    - 3
    - 2
    - 1
  ```
  which notifies players 5 minutes, 1 minute, 30, 15, 10, 5, 4, 3, 2 and 1 seconds before the backup.

  Note: These notifications will be sent after running the `/createbackup` command and will thus delay the backup. If an auto backup is configured, they will be sent so that the auto backup is created at the configured time.

### Automatic backups
The manager can create automatic backups this is done by adding the `auto` option to the `backup` block.

Two options control in which way the backup is created:
- `restartMode` controls in which case the server is restarted. The following options are available:
  - `always`: the backup is always performed using a restart
  - `never`: the backup is always performed while the server is running
  - `empty`: the backup is created while running if there are players on the server, if the server is empty, it is restarted (default)
- `createIf` controls in which case a backup will be created. The following options are available:
  - `always`: the backup is always created
  - `never`: the backup is never created (default)
  - `event`: the backup is created if an event occurred on the server since the last backup. Events include players joining or leaving the server or the server being restarted

Multiple options are available to specify when a backup is created:
- `hour`: defines the hours of the day (24 hour format) at which the backup is created. A list of values is configured, where the backup is created at each of these hours. If not configured, hour 0 (midnight) is assumed.
- `minute`: defines the minutes of the hour to at which the backup is created. A list of values is configured, where the backup is created at each of these minutes. If not configured, minute 0 (start of the hour) is assumed.
- `day`: defines a day of the week for the backup to be created. Available values are: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`. A list of values is configured, where the backup is created on each of the configured days. If not configured, a backups is created every day.
- `dayOfMonth`: defines a day of the month for a backup to be created. A list of values is configured, where the backup is created on each of the configured days. If not configured, a backup is created every day.
- `dayOfYear`: defines a day of the year for a backup to be created. A list of values is configured, where the backup is created on each of the configured days. If not configured, a backup is configured every day.
- `dayInterval`: defines an interval of days on which a backup is created. E.g. if 5 is configured, a backup is created every 5th day. If not configured or 0, a backup is created every day.
These options are combined using AND logic, such that e.g. `day: ["MONDAY"]` and `hour: [3]` will create a backup at 3 AM every Monday. This also means that e.g. configuring `day: ["MONDAY"]` and `dayOfMonth: [1]` will only create a backup on every 1st of the month that is also a Monday. Since this is likely not desirable, `day`, `dayOfMonth`, `dayOfYear` and `dayInterval` should be treated as mutually exclusive.

Example configuration for creating an automatic backup at 3:30 AM every other day, if something changed and restarting the server if no one is online:
```yaml
backup:
  path: ~/backups
  notifyIf: restartAndNotEmpty
  auto:
    hour:
      - 3
    minute:
      - 30
    dayInterval: 2
    restartMode: empty
    createIf: event
```


### Uploading the backup to the Google Drive cloud
Backups can be automatically uploaded to the Google Drive cloud.

To do this, some initial configuration is required:
- Open the [Google Cloud Developers Console](https://console.cloud.google.com/).
- Login with your Google account.
- At the top left click on "Select Project" then click "New Project".
- Enter a nice name and click "Create".
- Select the newly created Project.
- In the search bar at the top search for "google drive api" and click on the entry "Google Drive Api" under marketplace.
- Click "Enable".
- Click on "Credentials" on the left.
- Click "Create Credentials" -> "OAuth client ID".
- Select "OAuth consent screen" on the left.
- Select "External" and click "Create".
- Enter the information in the fileds marked with a red *.
- Click "Save and Continue".
- Click "Add or Remove Scopes".
- Search for "Google Drive API" and select the scopes ".../auth/drive.appdata", ".../auth/drive.file" and ".../auth/drive.install" and click "Update".
- Click on "Save and Continue".
- Leave the next page blank and click "Save and Continue".
- Click "Back to Dashboard".
- Click "Publish App" and click "Confirm".
- Go to the tab "Credentials" on the left.
- Click "Create Credentials" -> "OAuth client ID" .
- Select "Desktop App" from the dropdown, enter a name and click "Create".
- On the popup click "Download JSON", then click "OK".
- If you want to download the file again, click on the pen icon to the right of the created credentials and click "Download JSON" at the top.
- Move the downloaded file into the manager's directory and rename it to something like `credentials.json`.

I know, that was quite involved. But we're almost done now. You just need to configure access in `mcsdiscman.yaml`:
- Add the following configuration into the end of the `backup` block:
  ```yaml
  upload:
    googleDrive:
      folderId:
      credentialsFile:
  ```
- Open [Google Drive](https://drive.google.com/).
- Open or create the folder into which the backups should be uploaded.
- From the URL copy the string after "/folders/".
- Paste the string into `folderId`.
- Set the value of `credentialsFile` to the file you downloaded, for example `credentials.json`.

Finally, you need to grant access via the interactive permission manager:
- Start the Manager.
- A URL will be printed into the program output, open it and follow the instructions to grant access to your Drive.
  - If you can use a browser on the system you are running the server on, the authorization should take place automatically.
  - Otherwise, the process is a little more involved. Open the link in a browser on a different device and follow the instructions. You should be redirected to an unreachable page, the URL of which should start with `http://localhost`. Copy that url. Open a terminal / command prompt and type `curl ` and paste the copied link. Replace the `http://localhost` with `http://{ip-adress}` where `ip-adress` is the address of the server that the manger is running on. Then press enter. The authorization should now continue and the manager should start correctly.

You have now configured automatic upload to Google Drive. Creat a backup using `/creatbackup` and check whether upload succeeds (the discord bot will tell you) and whether the backup is actually uploaded.

If you want to provide access to the backups for your members, you may configure the `publicLocation` field in the `backup` block with a URL to the backups.

Your backup configuration may now look something like this:
```yaml
backup:
  path: ~/backups
  publicLocation: https://drive.google.com/drive/folders/abcdefg12345
  upload:
    googleDrive:
      folderId: abcdefg12345
      credentialsFile: credentials.json
```

> If you wish to be able to upload to another cloud provider, open a [GitHub issue](https://github.com/Tre5et/mcs-discman/issues) and I might look into it. Or of course implement it yourself and create a pull request. The codebase is set up so that it is easy to add multiple providers. Reference `config.backup.UploadConfig`.

# Troubleshooting the server manager

## I am unable to start the manager or I am not seeing the message "Hi, I'm online now!"
This is likely caused by an incorrect connection to the discord bot.
- Double check all the configuration in [Edit the config / discord](#discord).
- Make sure your bot is invited to your server.
- Make sure your bot has the `applications.commands` permission in the "OAuth2" configuration in the [discord developer console](https://discord.com/developers/home).
- Check that your bot has the correct roles or permissions to access the channel configured as `discord.channels.default` in `discman.yaml`.

This may also be caused by an incorrect configuration. In this case, the manager outputs a descriptive error message. Check your logs at `logs/mcsdiscman.log`, whether you can find one. Otherwise, copy the configuration from [discman.required.yaml](discman.required.yaml) into `mcsdiscman.yaml` and redo the steps in [Edit the config](#edit-the-config).

## I can't run the `/startserver` command

### I do not see the `/startserver` command in discord
The commands are not configured correctly.

- Restart your discord client.
- Wait 5 minutes. Updating commands on the server can sometimes take some time. Then restart your discord client.
- Make sure your bot has the `applications.commands` permission in the "OAuth2" configuration in the [discord developer console](https://discord.com/developers/home).
- Ensure that there is no configuration for `commands.start` in `mcsdiscman.yaml`.

### I am getting the response "You are not allowed to do that"
You probably do not have the correct role in the discord server.

- Make sure you have the moderator role configured in `discord.roles.moderator` in `mcsdiscman.yaml`.
    - If you can't give yourself that role, you can temporarily disable this check. Add the following at the end of `mcsdiscman.yaml`:
      ```yaml
      commands:
          start:
            allowedRoles: null
      ```
      After you have done so, restart the manager.

      Note: This will allow anyone in that channel to start the server. You should probably configure custom roles later.

## I am not getting the message "Server started." or I am getting the message "Failed to start server."
### I am getting the failure message immediately or the server does not start
The configuration of `server.startCommand` in `mcsdiscman.yaml` is probably incorrect.
- Double check the command.
- The command is run in the managers directory by default. You can configure a different directory by adding `commandDirectory: {your directory path}` to the `server` block.

### I am getting the failure message, but the server starts afterward
The timeout for waiting for the server to start is probably too short.
- Configure a custom start timeout by adding `startTimeout: {time}` to the `server` block. `time` is in seconds and `600` (5 minutes) by default.

### I am getting the failure message but the server has already started or I am not getting the message "Server started."
The server manager is likely failing to connect to the server.
- Double check the configuration from [Edit the config / server](#server).
- Make sure the port configure in `management-server-port` and `server.port` is not used by any other program. If in doubt, choose another port.

## I am having a different problem or I can not solve my problem using these steps
- Open a [GitHub issue](https://github.com/Tre5et/mcs-discman/issues) and describe your problem (preferred).
- If you can't create an issue or do not want to create a GitHub account, contact @treset on discord.
