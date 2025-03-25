package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

public class BanktopCommand
extends CommandBase
{
	private MySQL mysql;

	public BanktopCommand(MySQL mysql) {
		this.mysql = mysql;
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
        try {
            Map<String, Double> top = mysql.getTop10Bank();
			sender.sendMessage("§7---------- §a§lTop 10 Bankkonten §7----------");
			int i = 1;
			for(Map.Entry<String, Double> entry : top.entrySet()) {
				sender.sendMessage("§7§l" + i + ". §f" + entry.getKey().substring(36) + "§8: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(entry.getValue()) + "$");
				i++;
			}
			return true;
        } catch (SQLException e) {
			sender.sendMessage("§4Ein Fehler ist aufgetreten!");
			e.printStackTrace();
			return false;
        }
	}
}
