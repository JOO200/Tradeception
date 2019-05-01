package de.terraconia.tradeception.trades;

import de.terraconia.tradeception.TradeceptionPlugin;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.DelegatePersistence;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.persistence.PersistenceLoader;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class TraitTradeCollection extends Trait {
    static {
        PersistenceLoader.registerPersistDelegate(MerchantHolder.class, TraderNPCPersister.class);
    }

    public TraitTradeCollection() {
        super("tradeception");
    }

    @Persist
    @DelegatePersistence(TraderNPCPersister.class)
    private MerchantHolder trades;

    public MerchantHolder getTrades() {
        return trades;
    }

    public void setTrades(MerchantHolder trades) {
        this.trades = trades;
    }

    @EventHandler
    public void onNpcClickEvent(NPCRightClickEvent event) {
        if(this.getNPC() != event.getNPC()) return;
        if(trades != null) trades.open(event.getClicker());
        else {
            TradeceptionPlugin.getInstance().getLogger().severe(
                    "Spieler " + event.getClicker().getName() + " hat NPC " + event.getNPC().getId() + " mit ungültigem Trait angeklickt.");
        }
    }
}
