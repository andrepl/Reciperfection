package com.norcode.bukkit.reciperfection;

import com.norcode.bukkit.reciperfection.command.ReciperfectionCommand;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Reciperfection extends JavaPlugin implements Listener, InventoryHolder {
	public static final String META_KEY_TYPE = "Reciperfection-recipe-type";
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		new ReciperfectionCommand(this);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		loadRecipes();
	}

	TreeMap<String, Recipe> loadedRecipes = new TreeMap<String, Recipe>();
	List<ItemStack> recipesToRemove = new ArrayList<ItemStack>();

	static final List<Integer> GRID_SLOTS = Arrays.asList(new Integer[]{0, 1, 2, 9, 10, 11, 18, 19, 20});
	static final int RESULT_SLOT = 13;
	static final int CANCEL_SLOT = 15;
	static final int CREATE_SLOT = 16;

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (event.getInventory().getHolder().equals(this)) {
				debug(player.getName() + " Clicked in RecipeCreator");
				debug("RawSlot: " + event.getRawSlot());
				debug("Slot: " + event.getSlot());
				debug("CurrentItem: " + event.getCurrentItem());
				debug("Cursor: " + event.getCursor());
				debug("Action: " + event.getAction());
				if (event.getSlot() >= 0 && event.getSlot() == event.getRawSlot()) {
					if (GRID_SLOTS.contains(event.getSlot())) {
						debug("Grid Click");
					} else if (RESULT_SLOT == event.getSlot()) {
						debug("Result Click");
					} else {
						event.setCancelled(true);
						if (event.getSlot() == CANCEL_SLOT) {
							debug("Cancel");
							closeView(event.getView());
						} else if (event.getSlot() == CREATE_SLOT) {
							debug("Create");
							new RecipeCreator(this, event.getView()).runTaskLater(this, 0);
						}
					}
				}
			}
		}
	}

	boolean recipesEqual(Recipe r1, Recipe r2) {
		if (r1 instanceof ShapelessRecipe && r2 instanceof ShapelessRecipe) {
			if (r1.getResult().equals(r2.getResult())) {
				Set<ItemStack> r1Ing = new HashSet<ItemStack>(((ShapelessRecipe) r1).getIngredientList());
				Set<ItemStack> r2Ing = new HashSet<ItemStack>(((ShapelessRecipe) r1).getIngredientList());
				return (r1Ing.equals(r2Ing));
			}
		} else if (r1 instanceof ShapedRecipe && r2 instanceof ShapedRecipe) {
			if (r1.getResult().equals(r2.getResult())) {
				String[] s1 = ((ShapedRecipe) r1).getShape();
				String[] s2 = ((ShapedRecipe) r2).getShape();
				Map<Character, ItemStack> m1 = ((ShapedRecipe) r1).getIngredientMap();
				Map<Character, ItemStack> m2 = ((ShapedRecipe) r2).getIngredientMap();
				if (s1.length == s2.length && s1[0].length() == s2[0].length()) {
					for (int y=0;y<s1.length;y++) {
						for (int x=0;x<s1[0].length();x++) {
							ItemStack i1 = m1.get(s1[y].charAt(x));
							if (i1 == null) {
								i1 = new ItemStack(Material.AIR);
							}
							ItemStack i2 = m2.get(s2[y].charAt(x));
							if (i2 == null) {
								i2 = new ItemStack(Material.AIR);
							}
							if (!i1.equals(i2)) {
								return false;
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	void closeView(InventoryView view) {
		for (int i: GRID_SLOTS) {
			ItemStack s = view.getTopInventory().getItem(i);
			if (s != null) {
				view.getPlayer().getInventory().addItem(s);
				view.getTopInventory().setItem(i, null);
			}
		}
		ItemStack s = view.getTopInventory().getItem(RESULT_SLOT);
		if (s != null) {
			view.getTopInventory().setItem(RESULT_SLOT, null);
			view.getPlayer().getInventory().addItem(s);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			if (player.hasMetadata(META_KEY_TYPE)) {
				player.removeMetadata(META_KEY_TYPE, this);
			}
		}
	}

	void debug(String s) {
		if (getConfig().getBoolean("debug-mode", true)) {
			getLogger().info(s);
		}
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	public void loadRecipes() {
		// if we have any recipes loaded, remove them.
		// Also Remove any vanilla recipes that should be removed
		recipesToRemove = (List<ItemStack>) getConfig().getList("remove");
		if (recipesToRemove == null) {
			recipesToRemove = new ArrayList<ItemStack>();
		}
		Iterator<Recipe> recipeIterator = getServer().recipeIterator();
		while (recipeIterator.hasNext()) {
			Recipe r = recipeIterator.next();
			boolean removed = false;
			for (Recipe r2: loadedRecipes.values()) {
				if (recipesEqual(r, r2)) {
					debug("Removing recipe for " + r.getResult());
					recipeIterator.remove();
					removed = true;
					break;
				}
			}
			if (!removed && recipesToRemove.contains(r.getResult())) {
				debug("Removing recipe for " + r.getResult());
				recipeIterator.remove();
			}
		}

		loadedRecipes.clear();
		// add all our recipes.
		ConfigurationSection cfg = getConfig().getConfigurationSection("recipes");
		if (cfg != null) {
			for (String key: cfg.getKeys(false)) {
				ConfigurationSection recipe = cfg.getConfigurationSection(key);
				Recipe r;
				if (recipe.contains("shape")) {
					r = new ShapedRecipe(recipe.getItemStack("result"));
					((ShapedRecipe) r).shape(recipe.getStringList("shape").toArray(new String[0]));
					ConfigurationSection ing = recipe.getConfigurationSection("ingredients");
					for (String s: ing.getKeys(false)) {
						((ShapedRecipe) r).setIngredient(s.charAt(0), ing.getItemStack(s).getData());
					}
				} else {
					r = new ShapelessRecipe(recipe.getItemStack("result"));
					for (ItemStack ingredient: (List<ItemStack>) cfg.getList("ingredients")) {
						((ShapelessRecipe) r).addIngredient(ingredient.getData());
					}
				}
				debug("Adding recipe for " + r.getResult());
				loadedRecipes.put(key, r);
				getServer().addRecipe(r);
			}
		}
	}

	public void saveRecipe(Recipe recipe) {
		ConfigurationSection rootConfig = getConfig().getConfigurationSection("recipes");
		if (rootConfig == null) {
			rootConfig = getConfig().createSection("recipes");
		}
		String baseRecipeName = recipe.getResult().getType().name();
		if (recipe.getResult().getData().getData() != 0) {
			baseRecipeName += ":" + recipe.getResult().getData().getData();
		}
		int i=2;
		String recipeName = baseRecipeName;
		while (rootConfig.contains(recipeName)) {
			recipeName = baseRecipeName + "-" + (i++);
		}
		loadedRecipes.put(recipeName, recipe);
		getServer().addRecipe(recipe);
		ConfigurationSection cfg = rootConfig.getConfigurationSection(recipeName);
		if (cfg == null) {
			cfg = rootConfig.createSection(recipeName);
		}
		cfg.set("result", recipe.getResult());
		if (recipe instanceof ShapedRecipe) {
			cfg.set("shape", Arrays.asList(((ShapedRecipe) recipe).getShape()));
			for (Map.Entry<Character, ItemStack> entry: ((ShapedRecipe) recipe).getIngredientMap().entrySet()) {
				cfg.set("ingredients." + entry.getKey(), entry.getValue());
			}
		} else if (recipe instanceof ShapelessRecipe) {
			cfg.set("shape", null);
			cfg.set("ingredients", ((ShapelessRecipe) recipe).getIngredientList());
		}
		saveConfig();
	}

	public Map<String, Recipe> getLoadedRecipes() {
		return loadedRecipes;
	}

	public void deleteRecipe(String k) {
		debug("Deleting recipe: " + k);
		Recipe recipe = loadedRecipes.remove(k);
		Iterator<Recipe> recipes = getServer().recipeIterator();
		while (recipes.hasNext()) {
			Recipe r = recipes.next();
			if (recipesEqual(r, recipe)) {
				recipes.remove();
			}
		}
		getConfig().set("recipes." + k, null);
		saveConfig();
	}

	public List<ItemStack> getRecipesToRemove() {
		return recipesToRemove;
	}
}
