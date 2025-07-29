package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.MoneySystem;
import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Locale;

public class MoneyCommand
extends CommandBase {
	private MoneySystem plugin;

	public MoneyCommand(MoneySystem plugin) {
		super(plugin.getPrefix(), true);
		this.plugin = plugin;
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		p.sendMessage(plugin.getPrefix() + "§aDein Kontostand: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(p.getUniqueId())) + "$");
		return true;
	}
}
