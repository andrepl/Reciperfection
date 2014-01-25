package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeleteRecipeCommand extends BaseCommand {
	public DeleteRecipeCommand(Reciperfection plugin) {
		super(plugin, "deletecustom", new String[] {"removecustom"}, "reciperfection.command.delete",
				new String[] {"Delete a custom recipe", "", "/reciperfection delete <recipe-name>"});
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		if (args.size() == 0) {
			showHelp(commandSender, label, args);
			return;
		}
		String name = args.peek().toLowerCase();
		String deleted = null;
		for (String k: plugin.getLoadedRecipes().keySet()) {
			if (k.equalsIgnoreCase(name)) {
				plugin.deleteRecipe(k);
				deleted = k;
				break;
			}
		}
		if (deleted != null) {
			commandSender.sendMessage("Recipe `" + deleted + "` successfully deleted.");
		} else {
			throw new CommandError("Unknown recipe: " + name);
		}
	}

	@Override
	protected List<String> onTab(CommandSender sender, LinkedList<String> args) {
		List<String> results = new ArrayList<String>();
		for (String key: plugin.getLoadedRecipes().keySet()) {
			if (key.toLowerCase().startsWith(args.peek().toLowerCase())) {
				results.add(key);
			}
		}
		return results;
	}
}
