package me.daniel1385.moneysystem;

import me.daniel1385.moneysystem.apis.CustomEconomy;
import me.daniel1385.moneysystem.apis.MySQL;
import me.daniel1385.moneysystem.commands.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class MoneySystem extends JavaPlugin {
    private MySQL mysql;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        if(!config.contains("server")) {
            config.set("server", "global");
        }
        if(!config.contains("mysql")) {
            config.set("mysql.host", "localhost");
            config.set("mysql.port", 3306);
            config.set("mysql.database", "minecraft");
            config.set("mysql.username", "minecraft");
            config.set("mysql.password", "aA1234Aa");
        }
        saveConfig();
        mysql = new MySQL(config.getString("mysql.host"), config.getInt("mysql.port"), config.getString("mysql.database"), config.getString("mysql.username"), config.getString("mysql.password"), config.getString("server"));
        try {
            mysql.init();
        } catch(SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    mysql.keepAlive();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 20*60L, 20*60L);
        Bukkit.getServicesManager().register(Economy.class, new CustomEconomy(mysql), Bukkit.getPluginManager().getPlugin("Vault"), ServicePriority.Highest);
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("setmoney").setExecutor(new SetmoneyCommand());
        getCommand("getmoney").setExecutor(new GetmoneyCommand());
        getCommand("addmoney").setExecutor(new AddmoneyCommand());
        getCommand("removemoney").setExecutor(new RemovemoneyCommand());
        getCommand("bank").setExecutor(new BankCommand(mysql));
        getCommand("baltop").setExecutor(new BaltopCommand(mysql));
        getCommand("banktop").setExecutor(new BanktopCommand(mysql));
    }

    @Override
    public void onDisable() {
        try {
            mysql.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
