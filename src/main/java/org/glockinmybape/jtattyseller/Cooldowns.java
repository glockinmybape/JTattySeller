package org.glockinmybape.jtattyseller;

import java.util.HashMap;
import java.util.Map;

public class Cooldowns {
    private static Map<String, Long> cooldowns2 = new HashMap();

    public static long getCooldown(String key) {
        return calculateRemainder((Long)cooldowns2.get(key));
    }

    public static long setCooldown(String key, long delay) {
        return calculateRemainder((Long)cooldowns2.put(key, System.currentTimeMillis() + delay));
    }

    public static boolean tryCooldown(String key, long delay) {
        if (getCooldown(key) <= 0L) {
            setCooldown(key, delay);
            return true;
        } else {
            return false;
        }
    }

    public static long getTime(String key) {
        return (Long)cooldowns2.get(key);
    }

    public static void removeCooldown(String key) {
        cooldowns2.remove(key);
    }

    public static boolean containsCooldown(String key) {
        return cooldowns2.containsKey(key);
    }

    private static long calculateRemainder(Long expireTime) {
        return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
    }

    public static String getRemainingSec(long millis) {
        long seconds = millis / 1000L;

        long minutes;
        for(minutes = 0L; seconds > 60L; ++minutes) {
            seconds -= 60L;
        }

        while(minutes > 60L) {
            minutes -= 60L;
        }

        return String.valueOf(seconds);
    }

    public static String getRemainingMin(long millis) {
        long seconds = millis / 1000L;

        long minutes;
        for(minutes = 0L; seconds > 60L; ++minutes) {
            seconds -= 60L;
        }

        while(minutes > 60L) {
            minutes -= 60L;
        }

        return String.valueOf(minutes);
    }

    public static String getRemainingHour(long millis) {
        long seconds = millis / 1000L;

        long minutes;
        for(minutes = 0L; seconds > 60L; ++minutes) {
            seconds -= 60L;
        }

        long hours;
        for(hours = 0L; minutes > 60L; ++hours) {
            minutes -= 60L;
        }

        return String.valueOf(hours);
    }
}
