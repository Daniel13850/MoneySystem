package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import me.daniel1385.moneysystem.apis.PlayerNameAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

public class GetmoneyCommand
extends CommandBase
{
	private MoneySystem plugin;

	public GetmoneyCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), 1, "/getmoney <Spieler>");
		this.plugin = plugin;
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		UUID uuid = PlayerNameAPI.getUUID(args[0]);
		if (uuid == null) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler wurde nicht gefunden!");
			return false;
		}
		sender.sendMessage(plugin.getPrefix() + "§aKontostand von §6" + args[0] + "§a: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(uuid)) + "$");
		return true;
	}
}
