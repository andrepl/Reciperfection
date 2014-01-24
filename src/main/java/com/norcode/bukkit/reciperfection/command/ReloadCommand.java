package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

public class ReloadCommand extends BaseCommand {
	public ReloadCommand(Reciperfection plugin) {
		super(plugin, "reload", null, "reciperfection.command.reload", new String[]{"Reload all custom recipes"});
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		plugin.reloadConfig();
	}
}
