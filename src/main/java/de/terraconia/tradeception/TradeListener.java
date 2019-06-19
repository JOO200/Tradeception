package de.terraconia.tradeception;

import de.joo.itemmanagment.AsyncItemManagmentApi;
import de.joo.itemmanagment.ItemManagmentPlugin;
import de.terraconia.tradeception.db.TradeModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeListener implements Listener {
    private Map<Player, Merchant> playerMerchantMap = new HashMap<>();

    public void open(Player player, Merchant merchant) {
        playerMerchantMap.put(player, merchant);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getType() != InventoryType.MERCHANT) return;
        MerchantInventory inv = (MerchantInventory)event.getInventory();
        HumanEntity player = event.getPlayer();
        if(!(player instanceof Player)) {
            TradeceptionPlugin.getInstance().getLogger()
                    .warning("[Tradeception] HumanEntity hat ein Händlerinventar geschlossen, ist aber kein Player. UUID: " + event.getPlayer().getUniqueId());
            return;
        }
        if(playerMerchantMap.containsKey(player)) {
            Merchant m = playerMerchantMap.get(player);
            List<TradeModel> changed = new ArrayList<>();
            m.getRecipes().forEach(r -> {
                if(r.getUses() == 0) return; // Nur benutzte beachten.
                AsyncItemManagmentApi asyncApi = ItemManagmentPlugin.getAsyncApi();
                int first = asyncApi.getItemIdSync(r.getIngredients().get(0));
                int second = r.getIngredients().size() == 2 ? asyncApi.getItemIdSync(r.getIngredients().get(1)) : 0;
                int result = asyncApi.getItemIdSync(r.getResult());
                changed.add(new TradeModel(player.getUniqueId(), r.getUses(), first, second, result));

                r.setUses(0);
            });
            TradeceptionPlugin.getInstance().getDb().insertTrades(changed);
        }
    }
}
