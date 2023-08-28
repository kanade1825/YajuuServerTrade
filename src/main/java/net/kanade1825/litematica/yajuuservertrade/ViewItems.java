package net.kanade1825.litematica.yajuuservertrade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;



//チェストから見えるアイテム
public class ViewItems{
    public ItemStack ViewItemsMain(){
        ItemStack zero = new ItemStack(Material.DIAMOND_SWORD,1);
        ItemMeta zeroMeta = zero.getItemMeta();
        zeroMeta.setLore(Arrays.asList("First line","Second line"));
        zeroMeta.setDisplayName("Example Sword");
        zero.setItemMeta(zeroMeta);
        return zero;
    }
}