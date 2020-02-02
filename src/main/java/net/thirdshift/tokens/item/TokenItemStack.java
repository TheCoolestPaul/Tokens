package net.thirdshift.tokens.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class TokenItemStack extends ItemStack {
    private ItemStack tokenItem;

    public TokenItemStack(){
        this.tokenItem = new ItemStack(Material.NETHER_STAR, 1);
        this.tokenItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta tokenMeta = this.tokenItem.getItemMeta();
        assert tokenMeta != null;
        tokenMeta.setDisplayName("Token");
        ArrayList<String> metaLore = new ArrayList<>();
        metaLore.add("Right-Click to redeem");
        tokenMeta.setLore(metaLore);
        tokenMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        tokenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tokenMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        this.tokenItem.setItemMeta(tokenMeta);
        this.tokenItem.getData();
    }

    public ItemStack getTokenItem() {
        return this.tokenItem;
    }
}
