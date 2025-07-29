package me.daniel1385.moneysystem.commands;

import com.google.gson.JsonParser;
import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import me.daniel1385.moneysystem.apis.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class GetmoneyCommand
extends CommandBase
{
	private MoneySystem plugin;

	public GetmoneyCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), 1, "/getmoney <Spieler>");
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		String uuid = getUUID(args[0]);
		if (uuid == null) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
			return false;
		}
		sender.sendMessage(plugin.getPrefix() + "§aKontostand von §6" + args[0] + "§a: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(UUID.fromString(uuid))) + "$");
		return true;
	}

	private String getUUID(String name) {
		Player p = Bukkit.getPlayerExact(name);
		if (p != null) {
			return p.getUniqueId().toString();
		}
		String uuid;
		if(!name.startsWith("!")) {
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
}
