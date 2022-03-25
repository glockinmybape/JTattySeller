package org.glockinmybape.jtattyseller;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private static FileConfiguration config;
    private static FileConfiguration data;

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void init() {
        data = create("data.yml");
        config = create("config.yml");
    }

    public static FileConfiguration get(String name) {
        File f = new File(TattySeller.inst.getDataFolder(), name);
        if (TattySeller.inst.getResource(name) == null) {
            return save(YamlConfiguration.loadConfiguration(f), name);
        } else {
            if (!f.exists()) {
                TattySeller.inst.saveResource(name, false);
            }

            return YamlConfiguration.loadConfiguration(f);
        }
    }

    private static FileConfiguration create(String name) {
        File file = new File(TattySeller.inst.getDataFolder(), name);
        if (TattySeller.inst.getResource(name) == null) {
            return save(YamlConfiguration.loadConfiguration(file), name);
        } else {
            if (!file.exists()) {
                TattySeller.inst.saveResource(name, false);
            }

            return YamlConfiguration.loadConfiguration(file);
        }
    }

    public static FileConfiguration save(FileConfiguration config, String name) {
        try {
            config.save(new File(TattySeller.inst.getDataFolder(), name));
        } catch (IOException var3) {
            Bukkit.getConsoleSender().sendMessage(var3.getMessage());
        }

        return config;
    }
}
