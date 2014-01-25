package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class DeleteVanillaRecipeCommand extends BaseCommand {
	public DeleteVanillaRecipeCommand(Reciperfection plugin) {
		super(plugin, "deletevanilla", new String[] {"removevanilla"}, "reciperfection.command.delete",
				new String[] {"Remove all crafting recipes for the item in your hand."});
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		if (!(commandSender instanceof Player)) {
			throw new CommandError("This command is only available from in-game.");
		}
		Player p = (Player) commandSender;
		ItemStack item = p.getItemInHand();
		if (item == null) {
			throw new CommandError("You aren't holding anything.");
		}
		plugin.getRecipesToRemove().add(item.clone());
		plugin.getConfig().set("remove", plugin.getRecipesToRemove());
		plugin.saveConfig();
		plugin.loadRecipes();
		p.sendMessage("All vanilla recipes for " + item.getType().name() + " have been removed.");
	}
}
