package com.norcode.bukkit.reciperfection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RecipeCreator extends BukkitRunnable {

	private InventoryView view;
	private Reciperfection plugin;
	Map<ItemStack, Character> charMap = new HashMap<ItemStack, Character>();
	char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
	int charPtr = 0;
	
	private char getNextChar() {
		return chars[charPtr++];
	}
	
	public RecipeCreator(Reciperfection plugin, InventoryView view) {
		this.view = view;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		String recipeType = this.view.getPlayer().getMetadata(Reciperfection.META_KEY_TYPE).get(0).asString();
		ItemStack result = this.view.getTopInventory().getItem(13);
		plugin.debug(this.view.getPlayer().getName() + " creating new " + recipeType + " recipe for " + result);
		Recipe recipe;
		if (recipeType.equals("shaped")) {
			recipe = createdShapedRecipe();
		} else {
			recipe = createShapelessRecipe();
		}
		plugin.saveRecipe(recipe);
		((Player) view.getPlayer()).sendMessage("New " + recipeType + " recipe created");
		plugin.closeView(this.view);
	}


	private ShapedRecipe createdShapedRecipe() {
		
		List<String> lines = new ArrayList<String>();
		String line = "";
		for (int i=0;i<9;i++) {
			if (i == 3 || i == 6) {
				lines.add(line);
				line = "";
			}
			ItemStack s = view.getTopInventory().getItem(Reciperfection.GRID_SLOTS.get(i));
			if (s != null) {
				if (!charMap.containsKey(s)) {
					char c = getNextChar();
					charMap.put(s, c);							
				}
				line += charMap.get(s);
			} else {
				line += ' ';
			}
		}
		lines.add(line);
		Iterator<String> lineIt = lines.iterator();
		List<String> newLines = new ArrayList<String>();
		// trim vertically
		while (lineIt.hasNext()) {
			if (lineIt.next().trim().equals("")) {
				lineIt.remove();
			} else {
				newLines.add("");
			}
		}
		plugin.debug("Vertically Trimmed Recipe");
		plugin.debug("+" + StringUtils.repeat("=", lines.get(0).length()) + "+");
		for (String l: lines) {
			plugin.debug("|" + l + "|");
		}
		plugin.debug("+" + StringUtils.repeat("=", lines.get(0).length()) + "+");

		// trim horizontally
		for (int i=0;i<3;i++) {
			boolean clear = true;
			char c = ' ';
			for (String l: lines) {
				if (l.charAt(i) != ' ') {
					clear = false;
					c = l.charAt(i);
					break;
				}
			}
			if (!clear) {
				for (int j=0;j<newLines.size();j++) {
					newLines.set(j, newLines.get(j) + lines.get(j).charAt(i));
				}
			}
		}
		plugin.debug("Horizontally Trimmed Recipe");
		plugin.debug("+" + StringUtils.repeat("=", newLines.get(0).length()) + "+");
		for (String l: newLines) {
			plugin.debug("|" + l + "|");
		}
		plugin.debug("+" + StringUtils.repeat("=", newLines.get(0).length()) + "+");

		ShapedRecipe r = new ShapedRecipe(this.view.getTopInventory().getItem(13));
		r = r.shape(newLines.toArray(new String[0]));
		for (ItemStack s: charMap.keySet()) {
			r = r.setIngredient(charMap.get(s), s.getData());
		}
		return r;
	}

	private ShapelessRecipe createShapelessRecipe() {
		ShapelessRecipe r = new ShapelessRecipe(view.getTopInventory().getItem(13));
		for (int i=0;i<9;i++) {
			ItemStack s = view.getTopInventory().getItem(Reciperfection.GRID_SLOTS.get(i));
			r.addIngredient(s.getData());
		}
		return r;
	}
}
