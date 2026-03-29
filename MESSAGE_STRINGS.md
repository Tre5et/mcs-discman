# Message String Reference

The yaml configuration supports configuring all output stings. These strings may contain certain placeholders, denoted as `{placeholder}`. Different strings have different message types that support different placeholders.

## Message types and placeholders

### `DATE_TIME`

Available for all message types.

- `{date_iso}`: Current date in `yyyy-MM-dd` format.
- `{date_dmy}`: Current date in `dd.MM.yyyy` format.
- `{date_mdy}`: Current date in `M/d/yyyy` format.
- `{time_12}`: Current time in 12-hour format.
- `{time_24}`: Current time in 24-hour format.

### `PLAYER`

- `{name}`: Player name.
- `{uuid}`: Player UUID.

### `ADVANCEMENT`

- `{player.name}`: Name of the related player.
- `{player.uuid}`: UUID of the related player.
- `{message}`: Event-provided message text.
- `{identifier}`: Advancement identifier.
- `{title}`: Advancement title text.
- `{description}`: Advancement description text.
- `{toast}`: Advancement toast text.

### `DEATH`

- `{player.name}`: Name of the related player.
- `{player.uuid}`: UUID of the related player.
- `{message}`: Event-provided death message text.

### `UPLOAD_SERVICE`

- `{service}`: Upload service name.

### `DURATION`

- `{duration}`: Human-readable duration value.

### `DETAILS`

- `{version}`: Minecraft server version.

### `JOIN`

- `{url}`: Join address or URL.

### `BACKUPS`

- `{location}`: Backup location string.
- `{admin}`: Admin contact text.

### `MEMBERS`

- `{list}`: Rendered list output.
- `{admin}`: Admin contact text.
- `{count}`: Numeric item count.

### `ONLINE`

- `{list}`: Rendered online-player list.
- `{count}`: Numeric player count.

### `RUN_COMMAND`

- `{command}`: Command input string.
- `{response}`: Command response text.

## String config options and message type

## `commands` section

### Global command messages

- `commands.messageUnknown`: `DATE_TIME`
- `commands.messageDenied`: `DATE_TIME`
- `commands.messageDisabled`: `DATE_TIME`

### Shared per-command overrides

- `commands.<command>.messageDisabled`: `DATE_TIME`
- `commands.<command>.messageDenied`: `DATE_TIME`

`<command>` is one of:
`active`, `backups`, `connection`, `createBackup`, `details`, `join`, `members`, `online`, `ping`, `reload`, `restart`, `runCommand`, `say`, `start`, `stop`.

### Command-specific messages

- `commands.active.messageActive`: `DATE_TIME`
- `commands.active.messageInactive`: `DATE_TIME`
- `commands.backups.messageLocationAndAdmin`: `BACKUPS`
- `commands.backups.messageLocation`: `BACKUPS`
- `commands.backups.messageAdmin`: `BACKUPS`
- `commands.backups.messageNone`: `DATE_TIME`
- `commands.connection.messageStatusOpen`: `DATE_TIME`
- `commands.connection.messageStatusClosed`: `DATE_TIME`
- `commands.connection.messageOpenAlreadyOpen`: `DATE_TIME`
- `commands.connection.messageOpenFailed`: `DATE_TIME`
- `commands.connection.messageOpenSuccess`: `DATE_TIME`
- `commands.connection.messageCloseNoConnection`: `DATE_TIME`
- `commands.connection.messageCloseForced`: `DATE_TIME`
- `commands.connection.messageCloseSuccess`: `DATE_TIME`
- `commands.connection.messageCloseFailed`: `DATE_TIME`
- `commands.connection.messageUnknownAction`: `DATE_TIME`
- `commands.createBackup.messageInvalidMode`: `DATE_TIME`
- `commands.details.messageFailed`: `DATE_TIME`
- `commands.details.messageVersion`: `DETAILS`
- `commands.join.messageJoin`: `JOIN`
- `commands.join.messageMissingInfo`: `DATE_TIME`
- `commands.members.messageFailed`: `DATE_TIME`
- `commands.members.messageNoMembers`: `DATE_TIME`
- `commands.members.messageMembers`: `MEMBERS`
- `commands.members.messageContactAdmin`: `MEMBERS`
- `commands.online.messageFailed`: `DATE_TIME`
- `commands.online.messageNoPlayers`: `DATE_TIME`
- `commands.online.messageSinglePlayer`: `ONLINE`
- `commands.online.messageMultiplePlayers`: `ONLINE`
- `commands.ping.messagePong`: `DATE_TIME`
- `commands.reload.messageReloaded`: `DATE_TIME`
- `commands.reload.messageFailed`: `DATE_TIME`
- `commands.restart.messageStopping`: `DATE_TIME`
- `commands.restart.messageStopFailed`: `DATE_TIME`
- `commands.restart.messageStopped`: `DATE_TIME`
- `commands.restart.messageRestarting`: `DATE_TIME`
- `commands.restart.messageRestarted`: `DATE_TIME`
- `commands.restart.messageRestartFailed`: `DATE_TIME`
- `commands.runCommand.messageSuccess`: `RUN_COMMAND`
- `commands.runCommand.messageInvalid`: `RUN_COMMAND`
- `commands.runCommand.messageNoResponse`: `RUN_COMMAND`
- `commands.runCommand.messageFailed`: `DATE_TIME`
- `commands.runCommand.messageRequestFailed`: `DATE_TIME`
- `commands.start.messageAlreadyRunning`: `DATE_TIME`
- `commands.start.messageStarting`: `DATE_TIME`
- `commands.start.messageStarted`: `DATE_TIME`
- `commands.start.messageStartFailed`: `DATE_TIME`
- `commands.stop.messageAlreadyStopped`: `DATE_TIME`
- `commands.stop.messageStopping`: `DATE_TIME`
- `commands.stop.messageStopFailed`: `DATE_TIME`
- `commands.stop.messageStopped`: `DATE_TIME`

## `events` section

### Event message mappings

- `events.online.message`: `DATE_TIME`
- `events.joined.message`: `PLAYER`
- `events.left.message`: `PLAYER`
- `events.advancement.message`: `ADVANCEMENT`
- `events.death.message`: `DEATH`
- `events.started.message`: `DATE_TIME`
- `events.startFailed.message`: `DATE_TIME`
- `events.stopping.message`: `DATE_TIME`
- `events.stopped.message`: `DATE_TIME`
- `events.backupNotConfigured.message`: `DATE_TIME`
- `events.backupPreparationFailed.message`: `DATE_TIME`
- `events.backupPreparationUndoFailed.message`: `DATE_TIME`
- `events.backupStarted.message`: `DATE_TIME`
- `events.backupCompleted.message`: `DATE_TIME`
- `events.backupFailed.message`: `DATE_TIME`
- `events.backupCreatedLocal.message`: `DATE_TIME`
- `events.backupUploading.message`: `UPLOAD_SERVICE`
- `events.backupUploadFailed.message`: `UPLOAD_SERVICE`
- `events.backupWhileRunningAnnouncement.message`: `DURATION`
- `events.backupRestartAnnouncement.message`: `DURATION`
- `events.backupAnnouncing.message`: `DURATION`
- `events.backupAutosaveDisabled.message`: `DATE_TIME`
- `events.backupAutosaveEnabled.message`: `DATE_TIME`
- `events.backupSaving.message`: `DATE_TIME`
- `events.backupSaved.message`: `DATE_TIME`
- `events.backupServerStarting.message`: `DATE_TIME`
- `events.backupServerStarted.message`: `DATE_TIME`
- `events.backupServerStopping.message`: `DATE_TIME`
- `events.backupServerStopped.message`: `DATE_TIME`
- `events.inactive.message`: `DURATION`
- `events.crashDetected.message`: `DATE_TIME`
- `events.crashConfirmed.message`: `DATE_TIME`
- `events.crashTooMany.message`: `DATE_TIME`
- `events.crashRestarting.message`: `DATE_TIME`
- `events.crashRestartFailed.message`: `DATE_TIME`
- `events.crashStarted.message`: `DATE_TIME`






