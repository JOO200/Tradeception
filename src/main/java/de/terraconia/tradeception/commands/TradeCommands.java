package de.terraconia.tradeception.commands;

import de.joo.itemmanagment.ItemManagmentPlugin;
import de.terraconia.commands.acf.BaseCommand;
import de.terraconia.commands.acf.annotation.*;
import de.terraconia.commands.exceptions.TerraconiaCommandException;
import de.terraconia.tradeception.trades.MerchantHolder;
import de.terraconia.tradeception.trades.TraitTradeCollection;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CommandPermission("tradeception.admin")
@CommandAlias("tradeception")
public class TradeCommands extends BaseCommand {
    @Subcommand("setName")
    public void setName(Player player, NPC npc, String title) {
        TraitTradeCollection trait = npc.getTrait(TraitTradeCollection.class);
        if(trait == null) {
            throw new TerraconiaCommandException(ChatColor.RED + "Nutze zuerst /tradeception createTrade.");
        }
        trait.getTrades().setName(title);
        player.sendMessage("Title geändert, Neustart erforderlich.");
    }

    @Subcommand("createTrade")
    public void createTrade(Player player, NPC npc, String title) {
        Merchant merchant = Bukkit.createMerchant(title);
        MerchantHolder holder = new MerchantHolder(merchant, title);
        TraitTradeCollection traitTradeCollection = new TraitTradeCollection();
        traitTradeCollection.setTrades(holder);
        npc.addTrait(traitTradeCollection);
        player.sendMessage("Trait hinzugefügt.");
        player.sendMessage("Nutze /tradeception removeTrade|addTrade|setTrade für weiteres.");
    }

    @Subcommand("removeTrade")
    public void removeTrade(Player player, NPC npc, int slot) {
        TraitTradeCollection trait = npc.getTrait(TraitTradeCollection.class);
        if(trait == null) {
            throw new TerraconiaCommandException(ChatColor.RED + "Nutze zuerst /tradeception createTrade.");
        }
        List<MerchantRecipe> recipes = new ArrayList<>(trait.getTrades().getMerchant().getRecipes());
        recipes.remove(slot);
        trait.getTrades().getMerchant().setRecipes(recipes);
        player.sendMessage("Handel entfernt.");
    }

    @Subcommand("addTrade")
    @Syntax("<Sell-ItemStack-ID> <Buy-ItemStack-ID1> <Buy-ItemStack-ID2>")
    public void addTrade(Player player, NPC npc, int sell, int buy1, @Default("0") int buy2) throws ExecutionException, InterruptedException {
        TraitTradeCollection trait = npc.getTrait(TraitTradeCollection.class);
        if(trait == null) {
            throw new TerraconiaCommandException(ChatColor.RED + "Nutze zuerst /tradeception createTrade.");
        }
        CompletableFuture<ItemStack> sellIS = ItemManagmentPlugin.getAsyncApi().getItemStack(sell);
        CompletableFuture<ItemStack> buy1IS = ItemManagmentPlugin.getAsyncApi().getItemStack(buy1);
        CompletableFuture<ItemStack> buy2IS;
        if(buy2 != 0) buy2IS = ItemManagmentPlugin.getAsyncApi().getItemStack(buy2);
        else buy2IS = CompletableFuture.completedFuture(null);

        CompletableFuture.allOf(sellIS, buy1IS, buy2IS).thenAccept(nothing -> {
            MerchantRecipe recipe = new MerchantRecipe(sellIS.join(), Integer.MAX_VALUE);
            recipe.addIngredient(buy1IS.join());
            if(buy2IS.join() != null) recipe.addIngredient(buy2IS.join());
            List<MerchantRecipe> recipes = new ArrayList<>(trait.getTrades().getMerchant().getRecipes());
            recipes.add(recipe);
            trait.getTrades().getMerchant().setRecipes(recipes);
            player.sendMessage("Handel hinzugefügt.");
        });
    }

    @Subcommand("setTrade")
    @Syntax("<TradingSlot> <Sell-ItemStack-ID> <Buy-ItemStack-ID1> <Buy-ItemStack-ID2>")
    public void setTrade(Player player, NPC npc, int slot, int sell, int buy1, @Default("0") int buy2) {
        TraitTradeCollection trait = npc.getTrait(TraitTradeCollection.class);
        if(trait == null) {
            throw new TerraconiaCommandException(ChatColor.RED + "Nutze zuerst /tradeception createTrade.");
        }
        CompletableFuture<ItemStack> sellIS = ItemManagmentPlugin.getAsyncApi().getItemStack(sell);
        CompletableFuture<ItemStack> buy1IS = ItemManagmentPlugin.getAsyncApi().getItemStack(buy1);
        CompletableFuture<ItemStack> buy2IS;
        if(buy2 != 0) buy2IS = ItemManagmentPlugin.getAsyncApi().getItemStack(buy2);
        else buy2IS = CompletableFuture.completedFuture(null);
        CompletableFuture.allOf(sellIS, buy1IS, buy2IS).thenAccept(nothing -> {
            MerchantRecipe recipe = new MerchantRecipe(sellIS.join(), Integer.MAX_VALUE);
            recipe.addIngredient(buy1IS.join());
            if(buy2IS.join() != null) recipe.addIngredient(buy2IS.join());
            trait.getTrades().getMerchant().setRecipe(slot, recipe);
            player.sendMessage("Handel ersetzt.");
        });
    }
}
