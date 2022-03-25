package org.glockinmybape.jtattyseller;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.security.SecureRandom;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSeller implements CommandExecutor {
    private GUI gui = new GUI();
    private String CHAR_LIST = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            this.gui.openGui((Player)sender);
            return true;
        } else if (!sender.hasPermission("tattyseller.admin")) {
            sender.sendMessage(TattySeller.inst.getConfig().getString("messages.noperm").replace("&", "§"));
            return true;
        } else if (args.length == 1) {
            Player p = (Player)sender;
            final Hologram nh;
            if (args[0].equalsIgnoreCase("spawn")) {
                nh = HologramsAPI.createHologram(TattySeller.inst, p.getLocation());
                double d_millis = (double)Cooldowns.getCooldown("MAIN");
                double d_min = d_millis / 1000.0D / 60.0D;
                int min = (int)Cooldowns.getCooldown("MAIN") / 1000 / 60;
                int one = min * 60;
                double two = d_min * 60.0D;
                int three = (int)two - one;
                nh.insertTextLine(0, TattySeller.inst.getConfig().getString("Hologram-text").replace("%min%", String.valueOf(Cooldowns.getCooldown("MAIN") / 60000L)).replace("%sec%", String.valueOf(three)));
                nh.teleport(p.getLocation());
                TattySeller.holo = nh;
                TattySeller.data.set("hologram.world", TattySeller.holo.getWorld().getName());
                TattySeller.data.set("hologram.x", TattySeller.holo.getX());
                TattySeller.data.set("hologram.y", TattySeller.holo.getY());
                TattySeller.data.set("hologram.z", TattySeller.holo.getZ());
                Config.save(TattySeller.data, "data.yml");
                Bukkit.getScheduler().runTaskTimer(TattySeller.inst, new Runnable() {
                    public void run() {
                        double d_millis = (double)Cooldowns.getCooldown("MAIN");
                        double d_min = d_millis / 1000.0D / 60.0D;
                        int min = (int)Cooldowns.getCooldown("MAIN") / 1000 / 60;
                        int one = min * 60;
                        double two = d_min * 60.0D;
                        int three = (int)two - one;
                        nh.clearLines();
                        nh.insertTextLine(0, TattySeller.inst.getConfig().getString("Hologram-text").replace("%min%", String.valueOf(Cooldowns.getCooldown("MAIN") / 60000L)).replace("%sec%", String.valueOf(three)));
                        TattySeller.holo = nh;
                    }
                }, 20L, 20L);
                return true;
            } else if (!args[0].equals("move")) {
                sender.sendMessage(TattySeller.inst.getConfig().getString("messages.usage-create").replace("&", "§"));
                return true;
            } else {
                nh = TattySeller.holo;
                if (nh == null) {
                    sender.sendMessage(TattySeller.inst.getConfig().getString("messages.holo-not-exists").replace("&", "§"));
                    return true;
                } else {
                    nh.teleport(p.getLocation());
                    TattySeller.data.set("hologram.world", TattySeller.holo.getWorld().getName());
                    TattySeller.data.set("hologram.x", TattySeller.holo.getX());
                    TattySeller.data.set("hologram.y", TattySeller.holo.getY());
                    TattySeller.data.set("hologram.z", TattySeller.holo.getZ());
                    Config.save(TattySeller.data, "data.yml");
                    return true;
                }
            }
        } else if (args.length == 2) {
            sender.sendMessage(TattySeller.inst.getConfig().getString("messages.usage-create").replace("&", "§"));
            return true;
        } else {
            if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                if (!TattySeller.isInt(args[2])) {
                    sender.sendMessage(TattySeller.inst.getConfig().getString("messages.not-integer").replace("&", "§"));
                    return true;
                }

                if (name.equals("--rnd")) {
                    Random rnd = new Random();
                    int randomNum = rnd.nextInt(10) + 1;
                    String confname = this.generateRandomStringUsingSecureRandom(randomNum);
                    if (TattySeller.data.contains("items." + confname)) {
                        sender.sendMessage(TattySeller.inst.getConfig().getString("messages.name-exists").replace("&", "§"));
                        return true;
                    }

                    Player p2 = (Player)sender;
                    TattySeller.data.set("items." + confname, p2.getInventory().getItemInMainHand());
                    TattySeller.data.set("price-items." + confname, Integer.parseInt(args[2]));
                    TattySeller.data.set("standartprice-items." + confname, Integer.parseInt(args[2]));
                    Config.save(TattySeller.data, "data.yml");
                } else {
                    if (TattySeller.data.contains("items." + name)) {
                        sender.sendMessage(TattySeller.inst.getConfig().getString("messages.name-exists").replace("&", "§"));
                        return true;
                    }

                    Player p3 = (Player)sender;
                    TattySeller.data.set("items." + name, p3.getInventory().getItemInMainHand());
                    TattySeller.data.set("price-items." + name, Integer.parseInt(args[2]));
                    TattySeller.data.set("standartprice-items." + name, Integer.parseInt(args[2]));
                    Config.save(TattySeller.data, "data.yml");
                }
            }

            return false;
        }
    }

    public String generateRandomStringUsingSecureRandom(int length) {
        StringBuffer randStr = new StringBuffer(length);
        SecureRandom secureRandom = new SecureRandom();

        for(int i = 0; i < length; ++i) {
            randStr.append(this.CHAR_LIST.charAt(secureRandom.nextInt(this.CHAR_LIST.length())));
        }

        return randStr.toString();
    }
}
