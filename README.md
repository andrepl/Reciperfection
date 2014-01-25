# Reciperfection #


Reciperfection is a bukkit plugin that allows complete control over crafting recipes.

Using simple commands, and a GUI interface, admins can create new crafting recipes and remove built-in ones.

## Commands ##

### /reciperfection create ###

[permission: reciperfection.command.create]

Create a new crafting recipe.

This command will open a GUI with a crafting grid, a result slot, and cancel/create buttons.

If you are holding an item when you run this command, the item in your hand will be set as the result of the new recipe.

Define the recipe by placing items in the grid and hit the green CREATE button.  Your recipe will be immediately available for crafting.

This command accepts 1 optional argument. running `/reciperfection create shapeless` will create a shapeless recipe instead of the default shaped recipe.


### /reciperfection list ###

[permission: reciperfection.command.list]

List all custom crafting recipes currently in effect, the recipes are named by the item-type of the recipe's result.  These names can be modified by directly editing the config file.

### /reciperfection reload ###

[permission: reciperfection.command.reload]

Reload the config file from disk.

### /reciperfection delete ###

[permission: reciperfection.command.delete]

Delete a custom crafting recipe by name.

### /reciperfection deletevanilla ###

[permission: reciperfection.command.delete]

Delete a vanilla crafting recipe.  This command will remove all crafting recipes matching the item currently in your hand.









