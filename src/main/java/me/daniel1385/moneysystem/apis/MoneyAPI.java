package me.daniel1385.moneysystem.apis;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class MoneyAPI {

    public static Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        return rsp.getProvider();
    }

    public static boolean has(UUID uuid, double value) {
        return getEconomy().has(Bukkit.getOfflinePlayer(uuid), value);
    }

    public static double get(UUID uuid) {
        return getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public static void addMoney(UUID uuid, double value, String reason) {
        Economy econ = getEconomy();
        if(econ.getName().equals("MoneySystem")) {
            me.daniel1385.moneysystem.apis.CustomEconomy becon = (me.daniel1385.moneysystem.apis.CustomEconomy) econ;
            becon.depositPlayer(uuid, value, reason);
        } else {
            econ.depositPlayer(Bukkit.getOfflinePlayer(uuid), value);
        }
    }

    public static boolean removeMoney(UUID uuid, double value, String reason) {
        Economy econ = getEconomy();
        if(econ.getName().equals("MoneySystem")) {
            me.daniel1385.moneysystem.apis.CustomEconomy becon = (me.daniel1385.moneysystem.apis.CustomEconomy) econ;
            return becon.withdrawPlayer(uuid, value, reason).transactionSuccess();
        } else {
            return econ.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), value).transactionSuccess();
        }
    }

    public static void setMoney(UUID uuid, double value, String reason) {
        removeMoney(uuid, get(uuid), reason);
        addMoney(uuid, value, reason);
    }

}
