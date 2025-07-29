package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import me.daniel1385.moneysystem.apis.PlayerNameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class RemovemoneyCommand
extends CommandBase
{
	private MoneySystem plugin;

	public RemovemoneyCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), 2, "/removemoney <Spieler> <Betrag>");
		this.plugin = plugin;
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		UUID uuid = PlayerNameAPI.getUUID(args[0]);
		if (uuid == null) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
			return false;
		}  try {
			double betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			betrag = round(betrag);
			if (betrag < 0) {
				sender.sendMessage(plugin.getPrefix() + "§cEs sind keine Minuszahlen erlaubt!");
				return false;
			}
			String reason;
			if (p != null) {
				reason = "Abgezogen von " + p.getName();
			} else {
				reason = "Abgezogen per Konsole";
			}
			if (MoneyAPI.removeMoney(uuid, betrag, reason)) {
				sender.sendMessage(plugin.getPrefix() + "§aDer Kontostand von §6" + args[0] + " §awurde auf §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(uuid)) + "$ §agesetzt.");
				return true;
			} else {
				sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler hat nicht genug Geld!");
				return false;
			}
		}
		catch (NumberFormatException ex) {
			sender.sendMessage(plugin.getPrefix() + "§cDer Betrag keine gültige Zahl!");
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
