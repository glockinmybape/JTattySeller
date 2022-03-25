package org.glockinmybape.jtattyseller;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceHolders extends PlaceholderExpansion {
    public boolean canRegister() {
        return true;
    }

    public String getAuthor() {
        return "glockinmybape";
    }

    public String getIdentifier() {
        return "TattySeller";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        } else if (identifier.equals("time_min")) {
            return String.valueOf(Cooldowns.getCooldown("MAIN") / 60000L);
        } else if (identifier.equals("time_sec")) {
            double d_millis = (double)Cooldowns.getCooldown("MAIN");
            double d_min = d_millis / 1000.0D / 60.0D;
            int min = (int)Cooldowns.getCooldown("MAIN") / 1000 / 60;
            int one = min * 60;
            double two = d_min * 60.0D;
            int three = (int)two - one;
            return String.valueOf(three);
        } else {
            return "Неизвестный плэйсхолдер";
        }
    }
}
