package de.terraconia.tradeception.trades;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;

public class MerchantHolder {
    private Merchant merchant;
    private String name;

    public MerchantHolder(Merchant merchant, String name) {
        this.merchant = merchant;
        this.name = name;
    }

    public void open(Player player) {
        player.openMerchant(merchant, true);
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
