package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.apis.MoneyAPI;
import me.daniel1385.moneysystem.apis.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;

public class BankCommand implements CommandExecutor {
	private MySQL mysql;
	
	public BankCommand(MySQL mysql) {
		this.mysql = mysql;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return false;
		}
		Player p = (Player) sender;
		if(args.length == 0) {
			p.sendMessage("§c/bank guthaben");
			p.sendMessage("§c/bank einzahlen <Betrag>");
			p.sendMessage("§c/bank abheben <Betrag>");
			return false;
		}
		if(args[0].toLowerCase().equals("guthaben")) {
			try {
				p.sendMessage("§aDein Bankguthaben: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(mysql.getBank(p.getUniqueId())) + "$");
			} catch (SQLException e) {
				sender.sendMessage("§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				return false;
			}
			return true;
		}
		if(args[0].toLowerCase().equals("einzahlen")) {
			if(args.length == 1) {
				p.sendMessage("§cSyntax: §6/bank einzahlen <Betrag>");
				return false;
			}
			double input;
			try {
				input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));;
			} catch(NumberFormatException ex) {
				p.sendMessage("§cKein gültiger Betrag eingegeben!");
				return false;
			}
			input = round(input);
			if(input <= 0) {
				p.sendMessage("§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			if(!MoneyAPI.removeMoney(p.getUniqueId(), input, "Bankeinzahlung")) {
				p.sendMessage("§cDu hast nicht genug Geld!");
				return false;
			}
			try {
				mysql.setBank(p.getUniqueId(), mysql.getBank(p.getUniqueId()) + input);
				p.sendMessage("§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden eingezahlt.");
				return true;
			} catch(SQLException e) {
				sender.sendMessage("§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				MoneyAPI.addMoney(p.getUniqueId(), input, "Bankauszahlung");
				return false;
			}
		}
		if(args[0].toLowerCase().equals("abheben")) {
			if(args.length == 1) {
				p.sendMessage("§cSyntax: §6/bank abheben <Betrag>");
				return false;
			}
			double input;
			try {
				input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			} catch(NumberFormatException ex) {
				p.sendMessage("§cKein gültiger Betrag eingegeben!");
				return false;
			}
			input = round(input);
			if(input <= 0) {
				p.sendMessage("§cBitte gebe einen positiven Betrag ein!");
				return false;
			}
			try {
				if(mysql.getBank(p.getUniqueId()) < input) {
					p.sendMessage("§cDu hast nicht genug Geld!");
					return false;
				}
				mysql.setBank(p.getUniqueId(), mysql.getBank(p.getUniqueId()) - input);
				MoneyAPI.addMoney(p.getUniqueId(), input, "Bankauszahlung");
				p.sendMessage("§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden ausgezahlt.");
				return true;
			} catch (SQLException e) {
				sender.sendMessage("§4Ein Fehler ist aufgetreten!");
				e.printStackTrace();
				return false;
			}
		}
		p.sendMessage("§cUngültiger Befehl! §6/bank guthaben§c, §6/bank einzahlen§c, §6/bank abheben§c.");
		return false;
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}

}
