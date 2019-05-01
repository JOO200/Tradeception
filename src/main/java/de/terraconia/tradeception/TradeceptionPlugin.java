package de.terraconia.tradeception;

import de.baba43.lib.plugin.BabaJavaPlugin;
import de.terraconia.commands.TerraPaperCommandManager;
import de.terraconia.commands.acf.InvalidCommandArgument;
import de.terraconia.tradeception.commands.TradeCommands;
import de.terraconia.tradeception.trades.TraitTradeCollection;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.Player;

public class TradeceptionPlugin extends BabaJavaPlugin {
    private static TradeceptionPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitTradeCollection.class).withName("tradeception"));
        getLogger().info("trait registriert.");
        commands();
    }

    private void commands() {
        TerraPaperCommandManager manager = new TerraPaperCommandManager(this);
        manager.getCommandContexts().registerIssuerOnlyContext(NPC.class, c -> {
            Player player = c.getIssuer().getPlayer();
            if(player == null) throw new InvalidCommandArgument("Not a player.");
            NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(player);
            if(selected == null) throw new InvalidCommandArgument("No npc selected.");
            return selected;
        });
        manager.registerCommand(new TradeCommands());
    }

    public static TradeceptionPlugin getInstance() {
        return instance;
    }
}
