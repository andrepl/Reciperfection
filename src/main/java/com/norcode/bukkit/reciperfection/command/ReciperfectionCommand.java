package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

public class ReciperfectionCommand extends BaseCommand {
	public ReciperfectionCommand(Reciperfection plugin) {
		super(plugin, "reciperfection", new String[] {"recipe", "rp"}, "reciperfection.command", null);
		plugin.getCommand("reciperfection").setExecutor(this);
		registerSubcommand(new CreateRecipeCommand(plugin));
		registerSubcommand(new ReloadCommand(plugin));
		registerSubcommand(new DeleteRecipeCommand(plugin));
		registerSubcommand(new ListRecipesCommand(plugin));
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		if (args.size() == 0) {
			showHelp(commandSender, label, args);
			return;
		}
	}
}
