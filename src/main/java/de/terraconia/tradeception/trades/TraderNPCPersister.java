package de.terraconia.tradeception.trades;

import de.joo.itemmanagment.AsyncItemManagmentApi;
import de.joo.itemmanagment.ItemManagmentPlugin;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class TraderNPCPersister implements Persister<MerchantHolder> {
    @Override
    public MerchantHolder create(DataKey dataKey) {
        AsyncItemManagmentApi api = ItemManagmentPlugin.getAsyncApi();
        String name = dataKey.getString("title", "Handel");
        DataKey tradeKey = dataKey.getRelative("trades");
        Merchant merchant = Bukkit.createMerchant(name);
        List<MerchantRecipe> recipes = new ArrayList<>();
        tradeKey.getSubKeys().forEach(key -> {
            try {
                int slot = key.getInt("slot");
                ItemStack buy1 = api.getItemStackSync(key.getInt("buy1"));
                ItemStack buy2 = key.keyExists("buy2") ? api.getItemStackSync(key.getInt("buy2")) : null;
                ItemStack sell = api.getItemStackSync(key.getInt("sell"));
                var recipe = new MerchantRecipe(sell, Integer.MAX_VALUE);
                recipe.addIngredient(buy1);
                if (buy2 != null) recipe.addIngredient(buy2);
                recipes.add(recipe);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        merchant.setRecipes(recipes);
        return new MerchantHolder(merchant, name);
    }

    @Override
    public void save(MerchantHolder merchantHolder, DataKey dataKey) {
        dataKey.setString("title", merchantHolder.getName());
        DataKey trades = dataKey.getRelative("trades");
        List<MerchantRecipe> recipes = merchantHolder.getMerchant().getRecipes();
        for(int i = 0; i < merchantHolder.getMerchant().getRecipeCount(); i++) {
            DataKey relative = trades.getRelative("slot-" + i);
            relative.setInt("slot", i);
            relative.setInt("sell", ItemManagmentPlugin.getAsyncApi().getItemIdSync(recipes.get(i).getResult()));
            relative.setInt("buy1", ItemManagmentPlugin.getAsyncApi().getItemIdSync(recipes.get(i).getIngredients().get(0)));
            if(recipes.get(i).getIngredients().size() > 1) {
                relative.setInt("buy2", ItemManagmentPlugin.getAsyncApi().getItemIdSync(recipes.get(i).getIngredients().get(1)));
            }
        }
    }
}
