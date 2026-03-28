# AGENTS Guide for mcs-discman

## Project Snapshot
- Java + Gradle Discord manager for Minecraft servers, centered on Discord slash commands and an RPC bridge to a server-side management API.
- Entry point is `src/main/java/net/treset/minecraft_server_discord_bot/Main.java`: load config -> init Discord client -> register shutdown disconnect hook.
- Runtime config source is YAML (`discman.yaml`, or `debug/discman.yaml` if present), not the legacy `*.conf` format documented in `README.md`/`SETUP.md`.

## Architecture and Data Flow
- `config/Config.java` is the system orchestrator: it deserializes YAML, validates sections, then performs side effects (`ManagementClient.init()`, `NotificationHandlers.register()`, `AutoBackupScheduler.scheduleNext(...)`, `InactivityScheduler.scheduleNext(...)`).
- Discord ingress: `discord/DiscordBot.java` wires JDA and slash command registration; `SlashCommandHandler.java` dispatches command handlers asynchronously via `Commands.*.handle(event)`.
- Command boundary: `commands/Command.java` provides shared prechecks (feature enabled + role access), while concrete command classes implement action-specific `process(...)` logic.
- Server control boundary: `server/ManagementClient.java` wraps `dev.treset.mcdl:mcdl-server-management`; `server/ServerActions.java` handles start/stop/connect/retry semantics.
- Backup pipeline: `server/BackupHandler.java` executes notifications -> prepare (restart/autosave-off) -> zip world -> optional async upload -> restore state.
- Event-driven schedulers: `schedulers/EventScheduler.java` is a central timestamp bus; notifications call `EventScheduler.eventOccurred()`, which drives auto-backup and inactivity reminders.

## Integration Points
- Discord API via JDA 4 (`build.gradle`, `discord/DiscordBot.java`).
- Minecraft management RPC via `mcdl-server-management` (`server/*`, `notifications/NotificationHandlers.java`).
- Google Drive uploads via OAuth local receiver on port 8888 (`upload/GoogleDriveUploadService.java`); credentials file path comes from YAML.
- Logging via Logback (`src/main/resources/logback.xml`) with app logs in `logs/` and debug logs in `logs/debug/`.

## Codebase Conventions (Project-Specific)
- Most state is global/static (`Config.get()`, `ManagementClient.get()`, static schedulers); avoid introducing conflicting lifecycle ownership.
- Config reload is a full re-initialization path (`commands/ReloadConfigCommand.java` -> `Config.load()`), so new startup side effects must be idempotent and safe to re-run.
- Prefer extending `commands/Command.java` and wiring instances in `commands/Commands.java` instead of duplicating permission/enable checks in each command class.
- Function-level access is now controlled by `functions.<name>.allowedRoles` resolved through `discord.roles`; always validate new role names.
- Message templating is centralized in `config/message/MessageTemplates.java`; prefer typed contexts over ad-hoc string substitution.
- For multi-target notifications, prefer `logging/OutputConsumer` + `OutputType` instead of ad-hoc Discord/server sends.

## Practical Workflows
- Build fat jar: `./gradlew shadowJar` (Windows: `./gradlew.bat shadowJar`), artifact in `build/libs/`.
- Run tests: `./gradlew test` (test coverage appears limited; verify key behavior manually after refactors).
- Explore tasks: `./gradlew tasks --all`.
- Local debug config precedence: if `debug/discman.yaml` exists, it overrides root `discman.yaml` automatically.

## Gotchas to Check Before Editing
- Keep slash command registration and dispatch in sync (`discord/DiscordBot.java` vs `SlashCommandHandler.java` vs `commands/Commands.java`) when adding/removing commands.
- `discord.roles` is a name -> ID map used by function access control; invalid role names now fail validation in `config/function/FunctionConfig.java`.
- If you add message keys, also update the corresponding template in `config/message/MessageTemplates.java` or config validation will fail.
- Backup zipping excludes `session.lock` only (`system/FileHandler.java`); changes to backup behavior should preserve server safety assumptions.
- Crash handling is connection-close driven (`server/CrashHandler.java` + `ManagementClient.onClose`), so edits around disconnect semantics can affect restart loops.
- This repo currently contains real-looking tokens/secrets in `debug/discman.yaml` and `discordbot.conf`; treat all credentials as sensitive and avoid propagating them.
