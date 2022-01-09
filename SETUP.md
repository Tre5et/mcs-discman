# Setup

## Content
Required Steps:
- [Download](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#download)
- [Setup the Discord bot](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-the-discord-bot)
- [Edit the Config](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#edit-the-config)
- [Start the Manager](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#start-the-manager)

Optional Steps:
- [Setup automatic backup uplaods to Google Drive](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-automatic-backup-uplaods-to-google-drive)
- [Create a service for easy start of the discord bot](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#create-a-service-for-easy-start-of-the-manager)
- [Setup a Minecraft server service](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-a-minecraft-server-service)

# Required Steps
## Download
- Download the latest ```mcsdiscman-[X.X.X].tar.gz``` file from [releases](https://github.com/Tre5et/mcs-discman/releases).
- Extract the files and put them together into a folder.

## Setup the Discord bot
- Go to [discord developers](https://discord.com/developers/).
- In the "Applications" tab click on "New Application".
- Enter the name for your bot and click "Create".
- In the "Bot" tab click "Add bot" and confirm by clicking "Yes, do it".
- Add any information like pictue, description etc if you want.
- I recommend turning the option "Public Bot" off
- In the tab "OAuth2" -> "URL Generator" select "bot" and "applications.commands" in the top tab.
- In the lower tab select "Read Messages/View Channels", "Moderate Members", "Send Messages" and "Manage Messages".
- Copy the link at the bottom of the page and open it in your browser.
- Select the Discord server you want the bot to be active in and click "Continue" and then "Authorize".
- If you want the bot to be active in a private channel (which I recommend), make sure to give the newly created role for your bot permissions to access it.

## Edit the config
- In the folder you have extracted, you will find two .conf files.
- #### Open ```discordbot.conf```.
- Edit the options how you need them, they're rellatively self explanatory, here are a few notes though:
- Semicolons ```;``` in the config represent comments, anything after them in the same line isn't interpreted
- Some options may be left empty, most can't be though, if you don't know what to enter try leaving it empty
- Spaces directly after the ```=``` and directly before the ```;``` or the end of the line are ignored, other spaces are counted
- Finding the Discord bot token:
  - Go to [discord developers](https://discord.com/developers/).
  - In the tab "Applications" select the bot application you just created.
  - In the tab "Bot" find the field "Token" and click copy (make sure not to share this with anyone)
  - Paste it into the ```token``` field
- Finding Guild ID, Message Channel ID and Moderator Role ID
  - Open your Discord App or the Web App
  - Open your user settings
  - Under "Advanced" enable "Developer Mode"
  - Right click on your server's title, click "Copy ID" and paste it into ```guild_id```
  - Right click the channel you want the bot to send messages in, click "Copy ID" and pastte it into ```message_channel_id```
  - Go to the server settings
  - Under "Roles" right click the role you want to be moderator (able to start/stop the server etc.), click "Copy ID" and pastte it into ```moderator_role_id```
- The paths can be absolute or relative paths to the files location, thought absolute is recommended
- The section ```minecraft-server-console-commands``` is setup for runnig a server as described in [Setup a Minecraft server service](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#setup-a-minecraft-server-service), you need to figure out the things to put in there yourself if you are running the server differently
- Leave the section ```google-drive-details``` as default for now
- #### Open ```details.conf``` 
- Edit the values to your liking
- ```Server``` should be "vanilla", "fabric-loader", "bukkit" etc.
- ```mods``` may be left empty.
- ```members``` should reflect your whitelist.

## Start the manager
- Start the manager by runnig ```mcsdiscman-deploy.jar``` in the folder.
- In the terminal do this by using ```java -jar /path/to/your/folder/mcsdiscman-deploy.jar```.
- If you want a more convenient way to start the bot, follow [Create a service for easy start of the discord bot](https://github.com/Tre5et/mcs-discman/blob/main/SETUP.md#create-a-service-for-easy-start-of-the-manager).

# Optional Steps:
## Setup automatic backup uplaods to Google Drive
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
- Move the downloaded file into the Manager's folder and rename it to ```credentials.json```.
- Open [Google Drive](https://drive.google.com/).
- Open or create the folder where backups should be uploaded to.
- From the URL copy the string after "/folders/" into the field ```upload_folder_id``` in ```discordbot.conf```.
- In discordbot.conf set ```auto_upload_enabled``` to ```true``` if you want to automatically upload backups.
- Start the Manager.
- A URL will be printed into the output of the programm, open it and follow the instructions to grant access to your Drive.
- Drive is now authorized, the next time you start the manager you won't have to do this again.

## Create a service for easy start of the Manager
- This might not work the same on every Linux Distro (tested on Raspberry Pi 4B with Raspberry Pi OS Lite x64).
- Open the folder ```/etc/systemd/system```.
- Create a new file called ```discordbot.service``` (discordbot can be anything you want).
- Copy [this code](https://github.com/Tre5et/mcs-discman/blob/main/services/discordbot.service) into your file, edit the path and save it.
- In the terminal run ```sudo service discordbot start``` to start the service and ```sudo journalctl -u discordbot -f``` to see it's output.

## Setup a Minecraft server service
- This might not work the same on every Linux Distro (tested on Raspberry Pi 4B with Raspberry Pi OS Lite x64).
- Open the folder ```/etc/systemd/system```.
- Create a new file called ```minecraft.service``` (minecraft can be anything you want).
- Copy [this code](https://github.com/Tre5et/mcs-discman/blob/main/services/minecraft.service) into your file, edit the path and save it.
- Create a new file called ```minecraft.socket``` (minecraft can be anything you want but should be the same as specified in ```minecraft.service```).
- Copy [this code](https://github.com/Tre5et/mcs-discman/blob/main/services/minecraft.socket) into your file, edit the ```PartOf``` field if required and save it.
- In the terminal run ```sudo service minecraft start``` to start the service and ```sudo journalctl -u minecraft -f``` to see it's output.
- In the terminal run ```echo "[your command]" > /run/minecraft.stdin``` to run a command in the server.
