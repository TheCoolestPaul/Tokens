---
title: Permissions
parent: References
has_children: false
nav_order: 2
---

## Tokens Permissions

### Recommended permissions _everyone_ has:
- _tokens.use_ **/tokens** The base command for the plugin.
- _tokens.redeem_ **/redeem** The command that lets players redeem Tokens.
- _tokens.give_ **/tokens give** The command that allows players to give Tokens to eachother.

### Recommended permissions _admins_ have:
- _tokens.others_ **/tokens < username >** Used to see how many Tokens another player has.
- _tokens.set_ **/tokens set** Used to set a player's Token amount.
- _tokens.add_ **/tokens add** Used to add Tokens to a player.
- _tokens.remove_ **/tokens remove** Used to take Tokens from a player.

### Recommended permissions _owners_ have:
- _tokens.reload_ **/tokens reload** Used to reload Tokens configuration files. Found in `/plugins/Tokens/`