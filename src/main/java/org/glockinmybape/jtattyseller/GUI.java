package org.glockinmybape.jtattyseller;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
    public void openGui(Player p) {
        if (TattySeller.shop == null) {
            p.sendMessage(TattySeller.inst.getConfig().getString("messages.menu-null").replace("&", "§"));
        } else {
            p.openInventory(TattySeller.shop);
        }
    }

    public void confirmGui(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, TattySeller.inst.getConfig().getString("Settings.guiconfirm.name"));
        ItemStack glass = new ItemStack(Material.getMaterial(TattySeller.inst.getConfig().getString("Settings.guiconfirm.glass.material")), 1);
        glass.setDurability((short)TattySeller.inst.getConfig().getInt("Settings.guiconfirm.glass.data"));
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(TattySeller.inst.getConfig().getString("Settings.guiconfirm.glass.name"));
        glass.setItemMeta(meta);
        if (TattySeller.inst.getConfig().getBoolean("Settings.gui.glass.enabled")) {
            int i;
            for(i = 0; i < 9; ++i) {
                inv.setItem(i, glass);
            }

            for(i = 36; i < 45; ++i) {
                inv.setItem(i, glass);
            }

            inv.setItem(9, glass);
            inv.setItem(17, glass);
            inv.setItem(18, glass);
            inv.setItem(26, glass);
            inv.setItem(27, glass);
            inv.setItem(35, glass);
        }

        if (TattySeller.inst.getConfig().getBoolean("Settings.gui.glass-closed.enabled")) {
            inv.setItem(36, (new ItemBuilder(Material.getMaterial(TattySeller.inst.getConfig().getString("Settings.gui.glass-closed.material")))).setDurability((short)TattySeller.inst.getConfig().getInt("Settings.gui.glass-closed.data")).setName(TattySeller.inst.getConfig().getString("Settings.gui.glass-closed.name")).toItemStack());
            inv.setItem(44, (new ItemBuilder(Material.getMaterial(TattySeller.inst.getConfig().getString("Settings.gui.glass-closed.material")))).setDurability((short)TattySeller.inst.getConfig().getInt("Settings.gui.glass-closed.data")).setName(TattySeller.inst.getConfig().getString("Settings.gui.glass-closed.name")).toItemStack());
        }

        inv.setItem(22, (ItemStack)EventListener.items.get(p));
        inv.setItem(21, (new ItemBuilder(Material.STAINED_GLASS_PANE, 1)).setDurability((short)5).setName("§a✔").toItemStack());
        inv.setItem(23, (new ItemBuilder(Material.STAINED_GLASS_PANE, 1)).setDurability((short)14).setName("§4✖").toItemStack());
        p.openInventory(inv);
    }
}
