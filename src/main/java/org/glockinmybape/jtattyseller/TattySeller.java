package org.glockinmybape.jtattyseller;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TattySeller extends JavaPlugin implements Listener {
    public static TattySeller inst;
    public static FileConfiguration config;
    public static FileConfiguration data;
    public static Economy economy;
    public static Inventory shop;
    public static Hologram holo;
    public static HashMap<String, ItemStack> stacks = new HashMap();

    public static TattySeller getInstance() {
        return inst;
    }

    public void onEnable() {
        if (!this.setupEconomy()) {
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            inst = this;
            this.saveDefaultConfig();
            this.getCommand("seller").setExecutor(new CommandSeller());
            this.getServer().getPluginManager().registerEvents(new EventListener(), this);
            data = Config.get("data.yml");
            config = Config.get("config.yml");
            Config.init();
            (new PlaceHolders()).register();
            setInventory();
            Cooldowns.tryCooldown("MAIN", inst.getConfig().getLong("time-to-update-menu"));
            if (data.contains("hologram.world")) {
                this.holoFromFonfig();
            }

            Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                public void run() {
                    if (Cooldowns.containsCooldown("MAIN") && Cooldowns.getCooldown("MAIN") <= 0L) {
                        Cooldowns.tryCooldown("MAIN", TattySeller.inst.getConfig().getLong("time-to-update-menu"));

                        for(Iterator var1 = Bukkit.getOnlinePlayers().iterator(); var1.hasNext(); TattySeller.setInventory()) {
                            Player all = (Player)var1.next();
                            if (all.getOpenInventory() != null && all.getOpenInventory().getTitle().equals(TattySeller.inst.getConfig().getString("Settings.gui.name"))) {
                                EventListener.clicked.clear();
                                TattySeller.data.set("price-items", "standartprice-items");
                                all.closeInventory();
                            }
                        }
                    }

                }
            }, 0L, 20L);
        }

    }

    public void onDisable() {
        if (holo != null) {
            holo.delete();
        }

    }

    private static void setInventory() {
        if (data.getConfigurationSection("items") != null) {
            Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, inst.getConfig().getString("Settings.gui.name"));
            ItemStack glass = new ItemStack(Material.getMaterial(inst.getConfig().getString("Settings.gui.glass.material")), 1);
            glass.setDurability((short)inst.getConfig().getInt("Settings.gui.glass.data"));
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(inst.getConfig().getString("Settings.gui.glass.name"));
            glass.setItemMeta(meta);
            if (inst.getConfig().getBoolean("Settings.gui.glass.enabled")) {
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

            Config.save(data, "data.yml");
            if (inst.getConfig().getBoolean("Settings.gui.glass-closed.enabled")) {
                inv.setItem(36, (new ItemBuilder(Material.getMaterial(inst.getConfig().getString("Settings.gui.glass-closed.material")))).setDurability((short)inst.getConfig().getInt("Settings.gui.glass-closed.data")).setName(inst.getConfig().getString("Settings.gui.glass-closed.name")).toItemStack());
                inv.setItem(44, (new ItemBuilder(Material.getMaterial(inst.getConfig().getString("Settings.gui.glass-closed.material")))).setDurability((short)inst.getConfig().getInt("Settings.gui.glass-closed.data")).setName(inst.getConfig().getString("Settings.gui.glass-closed.name")).toItemStack());
            }

            List<ItemStack> list = new ArrayList();
            Iterator var4 = data.getConfigurationSection("items").getKeys(false).iterator();

            while(var4.hasNext()) {
                String s = (String)var4.next();
                list.add(data.getItemStack("items." + s));
                data.set("price-items." + s, data.getInt("standartprice-items." + s));
            }

            Config.save(data, "data.yml");
            Collections.shuffle(list);
            List<String> ls1 = new ArrayList();
            Iterator var16 = data.getConfigurationSection("items").getKeys(false).iterator();

            String s2;
            while(var16.hasNext()) {
                s2 = (String)var16.next();
                ls1.add(s2);
            }

            Collections.shuffle(ls1);
            var16 = ls1.iterator();

            while(var16.hasNext()) {
                s2 = (String)var16.next();

                for(int j = 1; j < list.size(); ++j) {
                    if (((ItemStack)list.get(j)).equals(data.getItemStack("items." + s2))) {
                        ItemStack item = ((ItemStack)list.get(j)).clone();
                        ItemStack save = item.clone();
                        ItemMeta sam = save.getItemMeta();
                        sam.setLore((List)null);
                        save.setItemMeta(sam);
                        EventListener.clicked.put(save, 0);
                        ArrayList<String> nsl = new ArrayList();
                        Iterator var12 = inst.getConfig().getStringList("items.lore").iterator();

                        while(var12.hasNext()) {
                            String lr = (String)var12.next();
                            nsl.add(lr.replace("%sells%", String.valueOf(EventListener.clicked.get(item))).replace("%price_standart%", String.valueOf(data.getInt("standartprice-items." + s2))).replace("&", "§").replace("%price%", String.valueOf(data.getInt("price-items." + s2))));
                        }

                        ItemMeta meta2 = item.getItemMeta();
                        meta2.setLore(nsl);
                        item.setItemMeta(meta2);
                        inv.addItem(new ItemStack[]{item});
                        stacks.put(s2, list.get(j));
                    }
                }
            }

            shop = inv;
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy)economyProvider.getProvider();
        }

        return economy != null;
    }

    private void holoFromFonfig() {
        double d_millis = (double)Cooldowns.getCooldown("MAIN");
        double d_min = d_millis / 1000.0D / 60.0D;
        int min = (int)Cooldowns.getCooldown("MAIN") / 1000 / 60;
        int one = min * 60;
        double two = d_min * 60.0D;
        int three = (int)two - one;
        Location loc = new Location(Bukkit.getWorld(data.getString("hologram.world")), data.getDouble("hologram.x"), data.getDouble("hologram.y"), data.getDouble("hologram.z"));
        final Hologram nh = HologramsAPI.createHologram(inst, loc);
        String s = inst.getConfig().getString("Hologram-text").replace("%min%", String.valueOf(Cooldowns.getCooldown("MAIN") / 60000L)).replace("%sec%", String.valueOf(three));
        nh.clearLines();
        nh.appendTextLine(s);
        nh.teleport(loc);
        holo = nh;
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                double d_millis = (double)Cooldowns.getCooldown("MAIN");
                double d_min = d_millis / 1000.0D / 60.0D;
                int min = (int)Cooldowns.getCooldown("MAIN") / 1000 / 60;
                int one = min * 60;
                double two = d_min * 60.0D;
                int three = (int)two - one;
                nh.clearLines();
                nh.appendTextLine(TattySeller.inst.getConfig().getString("Hologram-text").replace("%min%", String.valueOf(Cooldowns.getCooldown("MAIN") / 60000L)).replace("%sec%", String.valueOf(three)));
                TattySeller.holo = nh;
            }
        }, 20L, 20L);
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }
}
