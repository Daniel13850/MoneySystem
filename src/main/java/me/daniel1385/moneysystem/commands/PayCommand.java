package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayCommand
extends CommandBase
{

	public PayCommand() {
		super(true, 2, "/pay <Spieler> <Betrag>");
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		double betrag;
		try {
			betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
		} catch(NumberFormatException e) {
			p.sendMessage("§cDer Betrag keine gültige Zahl!");
			return false;
		}
		betrag = round(betrag);
		if (betrag <= 0) {
			p.sendMessage("§cBitte gebe einen positiven Betrag ein!");
			return false;
		}
		if(args[0].equals("*")) {
			List<Player> players = new ArrayList<>();
			for(Player op : Bukkit.getOnlinePlayers()) {
				if(op.getName().equals(p.getName())) {
					continue;
				}
				players.add(op);
			}
			if(players.isEmpty()) {
				sender.sendMessage("§cEs ist niemand online!");
				return false;
			}
			if (MoneyAPI.removeMoney(p.getUniqueId(), BigDecimal.valueOf(betrag).multiply(BigDecimal.valueOf(players.size())).doubleValue(), "Überweisung an " + players.size() + " Spieler")) {
				for(Player op : players) {
					MoneyAPI.addMoney(op.getUniqueId(), betrag, "Überweisung von " + p.getName());
					p.sendMessage("§aDu hast §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + "$ §aan §6" + op.getDisplayName() + " §aüberwiesen.");
					op.sendMessage("§6" + String.valueOf(p.getDisplayName()) + " §ahat dir §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + "$ §aüberwiesen.");
				}
			} else {
				p.sendMessage("§cDu hast nicht genug Geld!");
				return false;
			}
		}
		Player pp = Bukkit.getPlayerExact(args[0]);
		if (pp == null) {
			p.sendMessage("§cDieser Spieler ist nicht online!");
			return false;
		}
		if (MoneyAPI.removeMoney(p.getUniqueId(), betrag, "Überweisung an " + pp.getName())) {
			MoneyAPI.addMoney(pp.getUniqueId(), betrag, "Überweisung von " + p.getName());
			p.sendMessage("§aDu hast §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + "$ §aan §6" + pp.getDisplayName() + " §aüberwiesen.");
			pp.sendMessage("§6" + String.valueOf(p.getDisplayName()) + " §ahat dir §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(betrag) + "$ §aüberwiesen.");
			return true;
		}
		p.sendMessage("§cDu hast nicht genug Geld!");
		return false;
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}
}
