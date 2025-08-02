package me.daniel1385.moneysystem;

import me.daniel1385.moneysystem.apis.CustomEconomy;
import me.daniel1385.moneysystem.apis.MySQL;
import me.daniel1385.moneysystem.commands.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneySystem extends JavaPlugin {
    private String prefix;
    private MySQL mysql;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        if(!config.contains("prefix")) {
            config.set("prefix", "&7[&9MoneySystem&7] ");
        }
        if(!config.contains("server")) {
            config.set("server", "global");
        }
        if(!config.contains("mysql")) {
            config.set("mysql.use", false);
            config.set("mysql.host", "localhost");
            config.set("mysql.port", 3306);
            config.set("mysql.database", "minecraft");
            config.set("mysql.username", "minecraft");
            config.set("mysql.password", "aA1234Aa");
        }
        saveConfig();
        prefix = translateAllCodes(config.getString("prefix")) + "§r";
        String server = config.getString("server");
        boolean usemysql = config.getBoolean("mysql.use");
        if(usemysql) {
            mysql = new MySQL(config.getString("mysql.host"), config.getInt("mysql.port"), config.getString("mysql.database"), config.getString("mysql.username"), config.getString("mysql.password"), server);
        } else {
            File dbfile = new File(getDataFolder(), "storage.db");
            mysql = new MySQL(dbfile.getAbsolutePath(), server);
        }
        try {
            mysql.init();
        } catch(SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                try {
                    mysql.keepAlive();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 20*60*60L, 20*60*60L);
        Bukkit.getServicesManager().register(Economy.class, new CustomEconomy(mysql), Bukkit.getPluginManager().getPlugin("Vault"), ServicePriority.Highest);
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("setmoney").setExecutor(new SetmoneyCommand(this));
        getCommand("getmoney").setExecutor(new GetmoneyCommand(this));
        getCommand("addmoney").setExecutor(new AddmoneyCommand(this));
        getCommand("removemoney").setExecutor(new RemovemoneyCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("baltop").setExecutor(new BaltopCommand(this));
        getCommand("banktop").setExecutor(new BanktopCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            mysql.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQL getMysql() {
        return mysql;
    }

    public String getPrefix() {
        return prefix;
    }

    private String translateHexCodes (String text) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(text.substring(matcher.start()+1, matcher.end()));
            text = text.replace(text.substring(matcher.start(), matcher.end()), color.toString());
            matcher = pattern.matcher(text);
        }

        return text;
    }

    private String translateAllCodes (String text) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', translateHexCodes(text));
    }
}
