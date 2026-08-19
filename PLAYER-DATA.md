# DungNT RPG - Player Data & Resources

Player data is persisted under `plugins/DungNTRPG/players/<uuid>.yml`.

Stored values include:
- class
- level
- experience
- current health
- current/max mana snapshot
- current/max health snapshot

Data is loaded on first access / join, saved on quit and plugin shutdown.

Commands:
- `/mana` - show current/max mana
- `/mana <number>` - add mana to yourself
- `/mana add <player> <number>` - admin command to add mana to another online player

The action bar continuously shows:
`❤ current/max health   ✦ current/max mana`

Equipment changes refresh RPG max health/max mana and the action bar reflects the new values.
