package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;

import java.util.LinkedList;

public class ListRecipesCommand extends BaseCommand {
	public ListRecipesCommand(Reciperfection plugin) {
		super(plugin, "list", null, "reciperfection.command.list", new String[] {"List all custom recipes"});
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		ConfigurationSection recipes = plugin.getConfig().getConfigurationSection("recipes");
		if (recipes == null) {
			throw new CommandError("There are no custom recipes registered.");
		}
		int page = 1;
		int perPage = 7;
		int totalPages = (int) Math.ceil(plugin.getLoadedRecipes().size() / (double) perPage);
		if (args.size() > 0) {
			try {
				page = Integer.parseInt(args.peek());
			} catch (IllegalArgumentException ex) {
				throw new CommandError("Invalid page number: " + args.pop());
			}
			if (page > totalPages) {
				throw new CommandError("There is no page " + page);
			}
		}
		int start = perPage * (page-1);
		int end = start + perPage;
		end = (end > plugin.getLoadedRecipes().size()) ? plugin.getLoadedRecipes().size(): end;
		int i = 0;
		commandSender.sendMessage(ChatColor.BOLD + "Custom Recipes pg. " + page + " of " + totalPages);
		for (String s: plugin.getLoadedRecipes().keySet()) {
			if (i >= start && i < end) {
				Recipe r = plugin.getLoadedRecipes().get(s);
				commandSender.sendMessage((i+1) + ". " + s);
			}
			i++;
		}
	}
}
