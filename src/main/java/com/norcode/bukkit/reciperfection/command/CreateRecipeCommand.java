package com.norcode.bukkit.reciperfection.command;

import com.norcode.bukkit.reciperfection.Reciperfection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;
import java.util.LinkedList;

public class CreateRecipeCommand extends BaseCommand {
	public CreateRecipeCommand(Reciperfection plugin) {
		super(plugin, "create", new String[] {"new"}, "reciperfection.command.create", new String[] {"Create a new custom crafting recipe"});
	}

	@Override
	void onCommand(CommandSender sender, String label, LinkedList<String> args) throws CommandError {
		Iterator<String> it = args.iterator();
		String type = "shaped";
		if (args.size() > 0) {
			if (args.peek().equalsIgnoreCase("shapeless")) {
				type = "shapeless";
			}
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;
			p.setMetadata(Reciperfection.META_KEY_TYPE, new FixedMetadataValue(plugin, type));
			Inventory creator = Bukkit.createInventory(plugin, 27, "Create new " + type + " recipe");
			ItemStack nullstack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
			ItemMeta meta = nullstack.getItemMeta();
			meta.setDisplayName(ChatColor.BLACK + "");
			nullstack.setItemMeta(meta);
			ItemStack cancelButton = new ItemStack(Material.REDSTONE_BLOCK);
			meta = cancelButton.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "CANCEL");
			cancelButton.setItemMeta(meta);
			ItemStack createButton = new ItemStack(Material.EMERALD_BLOCK);
			meta = createButton.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "CREATE RECIPE");
			createButton.setItemMeta(meta);
			creator.setItem(3, nullstack);
			creator.setItem(4, nullstack);
			creator.setItem(5, nullstack);
			creator.setItem(6, nullstack);
			creator.setItem(7, nullstack);
			creator.setItem(8, nullstack);
			creator.setItem(12, nullstack);
			creator.setItem(13, p.getItemInHand());
			creator.setItem(14, nullstack);
			creator.setItem(15, cancelButton);
			creator.setItem(16, createButton);
			creator.setItem(17, nullstack);
			creator.setItem(21, nullstack);
			creator.setItem(22, nullstack);
			creator.setItem(23, nullstack);
			creator.setItem(24, nullstack);
			creator.setItem(25, nullstack);
			creator.setItem(26, nullstack);
			p.setItemInHand(null);
			p.openInventory(creator);
		} else {
			throw new CommandError("This command can only be used in game.");
		}
	}

}
