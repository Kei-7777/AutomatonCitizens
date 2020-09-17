package me.kei.citizens.automaton.item;

import me.kei.citizens.automaton.AutomatonCitizens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AutomatonItemFactory {

    public static ItemStack transport_npc_egg;

    public static void init(AutomatonCitizens plugin){

        transport_npc_egg = new ItemStack(Material.PUFFERFISH_SPAWN_EGG);
        ItemMeta meta = transport_npc_egg.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + "運搬奴隷の卵");
        meta.setLore(Arrays.asList(ChatColor.RESET + "チェストからアイテムを取り、一番近いトラップチェストに", ChatColor.RESET + "アイテムを運搬する奴隷をスポーンする卵", ChatColor.RESET + "行動範囲: 10ブロック"));
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        transport_npc_egg.setItemMeta(meta);
        Bukkit.resetRecipes();
        NamespacedKey key = new NamespacedKey(plugin, "transport_npc_spawn_egg");
        ShapedRecipe recipe = new ShapedRecipe(key, transport_npc_egg);
        recipe.shape("DID", "IGI", "DID");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        Bukkit.addRecipe(recipe);
    }

}
