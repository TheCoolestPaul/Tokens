---
title: Commands
parent: References
has_children: false
nav_order: 3
---

### Tokens Core Commands

> These commands are the backbone to Tokens.

| Command | Permission | Description |
|:-------------|:-------------|:------------------|
| `/tokens` | `tokens.use` | Displays your Token balance. |
| `/tokens <username>` | `tokens.others` | Displays another user's Token balance. |
| `/tokens buy <number>` | `tokens.buy` | Purchases a number of Tokens. |
| `/tokens give <player> <number>` | `tokens.give` | Gives a number of your Tokens to another player. |
| `/tokens set <username> <number>` | `tokens.set` | Set another user's Token balance. |
| `/tokens add <username> <number>` | `tokens.add` | Adds Tokens to another user's balance. |
| `/tokens remove <username> <number>` | `tokens.remove` | Removes Tokens from another user's balance. |
| `/tokens reload` | `tokens.reload` | Reloads the plugin's configuration files. |
---
### Tokens Redeem Commands

>#### **ATTENTION:**
> Each redeem command needs it's dependency loaded onto the server. 
> You can learn more about that [here](TODO:ADD THIS).

| Command | Permission | Description |
|:------------------|:----------------|:---------------------|
| `/redeem` | `tokens.redeem` | Displays very helpful text on how to redeem your Tokens. |
| `/redeem mcmmo <skill_name> <number>` | `tokens.redeem.mcmmo` | Redeems a number of Tokens to boost a [mcMMO level](https://mcmmo.org/wiki/Primary_Skills) of your choosing. |
| `/redeem factions <number>` | `tokens.redeem.factions` | Redeems a number of Tokens to boost a player's faction power. |
| `/redeem money <number>` | `tokens.redeem.sell` | Redeems Tokens for Vault Money. |
