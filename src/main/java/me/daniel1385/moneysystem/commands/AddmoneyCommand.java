package me.daniel1385.moneysystem.commands;

import com.google.gson.JsonParser;
import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class AddmoneyCommand
extends CommandBase
{
	private MoneySystem plugin;

	public AddmoneyCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), 2, "/addmoney <Spieler> <Betrag>");
		this.plugin = plugin;
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		String uuid = getUUID(args[0]);
		if (uuid == null) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
			return false;
		}
		try {
			double betrag = Double.parseDouble(args[1].replace(".", "").replace(",", "."));
			betrag = round(betrag);
			if (betrag < 0) {
				sender.sendMessage(plugin.getPrefix() + "§cEs sind keine Minuszahlen erlaubt!");
				return false;
			}
			String reason;
			if(p != null) {
				reason = "Hinzugefügt von " + p.getName();
			} else {
				reason = "Hinzugefügt per Konsole";
			}
			MoneyAPI.addMoney(UUID.fromString(uuid), betrag, reason);
			sender.sendMessage(plugin.getPrefix() + "§aDer Kontostand von §6" + args[0] + " §awurde auf §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(UUID.fromString(uuid))) + "$ §agesetzt.");
			return true;
		} catch (NumberFormatException ex) {
			sender.sendMessage(plugin.getPrefix() + "§cDer Betrag keine gültige Zahl!");
			return false;
		} 
	}

	private String getUUID(String name) {
		Player p = Bukkit.getPlayerExact(name);
		if (p != null) {
			return p.getUniqueId().toString();
		}
		String uuid;
		if(!name.startsWith(FloodgateApi.getInstance().getPlayerPrefix())) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader((new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).openStream()));
				uuid = JsonParser.parseReader(in).getAsJsonObject().get("id").toString().replaceAll("\"", "");
				uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
				in.close();
			} catch(Exception e) {
				return null;
			}
		} else {
			try {
				uuid = FloodgateApi.getInstance().createJavaPlayerId(FloodgateApi.getInstance().getXuidFor(name.substring(1)).get()).toString();
			} catch(Exception ex) {
				return null;
			}
		}
		return uuid;
	}

	public double round(double value) {
		BigDecimal result = BigDecimal.valueOf(value);
		result = result.setScale(2, RoundingMode.DOWN);
		value = result.doubleValue();
		return value;
	}
}
