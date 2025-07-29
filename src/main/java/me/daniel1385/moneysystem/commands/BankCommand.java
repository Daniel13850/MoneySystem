package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import me.daniel1385.moneysystem.apis.PlayerNameAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class BankCommand extends CommandBase {
	private MoneySystem plugin;
	
	public BankCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), true);
		this.plugin = plugin;
	}

	@Override
	public boolean run(CommandSender sender, Player p, String[] args) {
		if(args.length > 0) {
			if(args[0].toLowerCase().equals("einzahlen")) {
				if(args.length == 1) {
					p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/bank einzahlen <Betrag>");
					return false;
				}
				double input;
				try {
					input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));;
				} catch(NumberFormatException ex) {
					p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
					return false;
				}
				input = round(input);
				if(input <= 0) {
					p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
					return false;
				}
				if(!MoneyAPI.removeMoney(p.getUniqueId(), input, "Bankeinzahlung")) {
					p.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
					return false;
				}
				try {
					plugin.getMysql().setBank(p.getUniqueId(), plugin.getMysql().getBank(p.getUniqueId()) + input, p.getDisplayName());
					p.sendMessage(plugin.getPrefix() + "§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden eingezahlt.");
					return true;
				} catch(SQLException e) {
					sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
					e.printStackTrace();
					MoneyAPI.addMoney(p.getUniqueId(), input, "Bankauszahlung");
					return false;
				}
			}
			if(args[0].toLowerCase().equals("abheben")) {
				if(args.length == 1) {
					p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/bank abheben <Betrag>");
					return false;
				}
				double input;
				try {
					input = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
				} catch(NumberFormatException ex) {
					p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
					return false;
				}
				input = round(input);
				if(input <= 0) {
					p.sendMessage(plugin.getPrefix() + "§cBitte gebe einen positiven Betrag ein!");
					return false;
				}
				try {
					if(plugin.getMysql().getBank(p.getUniqueId()) < input) {
						p.sendMessage(plugin.getPrefix() + "§cDu hast nicht genug Geld!");
						return false;
					}
					plugin.getMysql().setBank(p.getUniqueId(), plugin.getMysql().getBank(p.getUniqueId()) - input, p.getDisplayName());
					MoneyAPI.addMoney(p.getUniqueId(), input, "Bankauszahlung");
					p.sendMessage(plugin.getPrefix() + "§6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §awurden ausgezahlt.");
					return true;
				} catch (SQLException e) {
					sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
					e.printStackTrace();
					return false;
				}
			}
			if(args[0].toLowerCase().equals("get") && p.hasPermission("moneysystem.admin")) {
				if(args.length < 2) {
					p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/bank get <Name>");
					return false;
				}
				UUID uuid = PlayerNameAPI.getUUID(args[0]);
				if (uuid == null) {
					sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
					return false;
				}
				try {
					p.sendMessage(plugin.getPrefix() + "§aBankguthaben von §6" + args[1] + "§a: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(plugin.getMysql().getBank(uuid)) + "$");
				} catch(SQLException e) {
					sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
					e.printStackTrace();
					return false;
				}
				return true;
			}
			if(args[0].toLowerCase().equals("set") && p.hasPermission("moneysystem.admin")) {
				if(args.length < 2) {
					p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/bank get <Name>");
					return false;
				}
				UUID uuid = PlayerNameAPI.getUUID(args[1]);
				if (uuid == null) {
					sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
					return false;
				}
				double input;
				try {
					input = Double.parseDouble(args[2].replace(".", "").replace(",", "."));
				} catch(NumberFormatException ex) {
					p.sendMessage(plugin.getPrefix() + "§cKein gültiger Betrag eingegeben!");
					return false;
				}
				input = round(input);
				if(input < 0) {
					p.sendMessage(plugin.getPrefix() + "§cEs sind keine Minuszahlen erlaubt!");
					return false;
				}
				try {
					String display = null;
					Player t = Bukkit.getPlayer(uuid);
					if(t != null) {
						display = t.getDisplayName();
					}
					plugin.getMysql().setBank(uuid, input, display);
					sender.sendMessage(plugin.getPrefix() + "§aDas Bankguthaben von §6" + args[1] + " §awurde auf §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(input) + "$ §agesetzt.");
				} catch(SQLException e) {
					sender.sendMessage(plugin.getPrefix() + "§4Ein Fehler ist aufgetreten!");
					e.printStackTrace();
					return false;
				}
				return true;
			}
		}
		try {
			p.sendMessage(plugin.getPrefix() + "§aDein Bankguthaben: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(plugin.getMysql().getBank(p.getUniqueId())) + "$");
			p.sendMessage(plugin.getPrefix() + "§cEinzahlen: §6/bank einzahlen <Betrag>");
			p.sendMessage(plugin.getPrefix() + "§cAuszahlen: §6/bank abheben <Betrag>");
			if(p.hasPermission("moneysystem.admin")) {
				p.sendMessage(plugin.getPrefix() + "§cGuthaben von Spieler abfragen (Admin): §6/bank get <Name>");
				p.sendMessage(plugin.getPrefix() + "§cGuthaben von Spieler setzen (Admin): §6/bank set <Name> <Betrag>");
			}
			return true;
		} catch (SQLException e) {
			sender.sendMessage("§4Ein Fehler ist aufgetreten!");
			e.printStackTrace();
			return false;
		}
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}

}
