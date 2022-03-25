package org.glockinmybape.jtattyseller;

import org.bukkit.entity.Player;

public class Econom {
    static void withdraw(Player p, int e) {
        TattySeller.economy.withdrawPlayer(p, (double)e);
    }

    static void deposit(Player p, int e) {
        TattySeller.economy.depositPlayer(p, (double)e);
    }

    static double getBalance(Player p) {
        return TattySeller.economy.getBalance(p);
    }
}
