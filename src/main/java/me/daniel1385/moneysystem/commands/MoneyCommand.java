package me.daniel1385.moneysystem.commands;

import me.daniel1385.moneysystem.apis.CommandBase;
import me.daniel1385.moneysystem.apis.MoneyAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Locale;

public class MoneyCommand
extends CommandBase {

	public MoneyCommand() {
		super(true);
	}

	public boolean run(CommandSender sender, Player p, String[] args) {
		p.sendMessage("§aDein Kontostand: §6" + DecimalFormat.getNumberInstance(Locale.GERMAN).format(MoneyAPI.get(p.getUniqueId())) + "$");
		return true;
	}
}
