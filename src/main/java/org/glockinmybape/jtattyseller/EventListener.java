package org.glockinmybape.jtattyseller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {
    private GUI gui = new GUI();
    public static HashMap<Player, ItemStack> items = new HashMap();
    public static HashMap<ItemStack, Integer> clicked = new HashMap();
    private List<Player> bypass_close = new ArrayList();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player p = (Player)e.getWhoClicked();
        ItemStack citem = e.getCurrentItem();
        if (inv != null && inv.getName().equals(TattySeller.inst.getConfig().getString("Settings.gui.name"))) {
            e.setCancelled(true);
            if (citem != null & citem.hasItemMeta() && citem.getItemMeta().hasLore()) {
                items.put(p, citem);
                this.bypass_close.add(p);
                this.gui.confirmGui(p);
                this.bypass_close.remove(p);
            }
        }

        if (inv != null && inv.getName().equals(TattySeller.inst.getConfig().getString("Settings.guiconfirm.name"))) {
            ItemStack citem2 = inv.getItem(22).clone();
            e.setCancelled(true);
            if (citem != null & citem.hasItemMeta() && citem.getItemMeta().hasDisplayName()) {
                String name = citem.getItemMeta().getDisplayName();
                if (name.equals("§a✔")) {
                    ItemStack item = inv.getItem(22).clone();
                    ItemMeta meta1 = item.getItemMeta();
                    meta1.setLore((List)null);
                    item.setItemMeta(meta1);
                    Iterator var9 = TattySeller.data.getConfigurationSection("items").getKeys(false).iterator();

                    label86:
                    while(true) {
                        String s;
                        do {
                            if (!var9.hasNext()) {
                                break label86;
                            }

                            s = (String)var9.next();
                        } while(!TattySeller.data.getItemStack("items." + s).equals(item));

                        int price = TattySeller.data.getInt("price-items." + s);
                        ItemStack find = item.clone();
                        find.setAmount(1);
                        if (!p.getInventory().containsAtLeast(find, item.getAmount())) {
                            p.sendMessage(TattySeller.inst.getConfig().getString("messages.no-items").replace("&", "§").replace("%amount%", String.valueOf(item.getAmount())));
                            return;
                        }

                        p.closeInventory();
                        p.getInventory().removeItem(new ItemStack[]{item});
                        p.updateInventory();
                        Econom.deposit(p, price);
                        if (TattySeller.data.getInt("price-items." + s) - TattySeller.inst.getConfig().getInt("Mitigation") < 0) {
                            TattySeller.data.set("price-items." + s, 0);
                        } else {
                            TattySeller.data.set("price-items." + s, TattySeller.data.getInt("price-items." + s) - TattySeller.inst.getConfig().getInt("Mitigation"));
                        }

                        if (!clicked.containsKey(item)) {
                            clicked.put(item, 1);
                        } else {
                            clicked.put(item, (Integer)clicked.get(item) + 1);
                        }

                        Config.save(TattySeller.data, "data.yml");
                        Inventory shop = TattySeller.shop;
                        ItemStack[] storageContents;
                        int length = (storageContents = shop.getStorageContents()).length;

                        for(int i = 0; i < length; ++i) {
                            ItemStack it = storageContents[i];
                            if (it != null && it.equals(citem2)) {
                                ArrayList<String> nsl = new ArrayList();
                                Iterator var19 = TattySeller.inst.getConfig().getStringList("items.lore").iterator();

                                while(var19.hasNext()) {
                                    String lr = (String)var19.next();
                                    nsl.add(lr.replace("%sells%", String.valueOf(clicked.get(item))).replace("%price_standart%", String.valueOf(TattySeller.data.getInt("standartprice-items." + s))).replace("&", "§").replace("%price%", String.valueOf(TattySeller.data.getInt("price-items." + s))));
                                }

                                ItemMeta cme = it.getItemMeta();
                                cme.setLore(nsl);
                                it.setItemMeta(cme);
                            }
                        }

                        p.sendMessage(TattySeller.inst.getConfig().getString("messages.sell-success").replace("&", "§").replace("%itemname%", citem.getItemMeta().getDisplayName()).replace("%price%", String.valueOf(price)));
                    }
                }

                if (name.equals("§4✖")) {
                    p.closeInventory();
                }
            }
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals(TattySeller.inst.getConfig().getString("Settings.gui.name")) && !this.bypass_close.contains(e.getPlayer())) {
            items.remove(e.getPlayer());
        }

    }
}
