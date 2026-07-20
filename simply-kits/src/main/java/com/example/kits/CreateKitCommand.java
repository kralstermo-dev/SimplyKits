package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateKitCommand implements CommandExecutor {

    private final KitManager kitManager;

    public CreateKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kits.create")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can create kits.", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /kitcreate <name>", NamedTextColor.RED));
            return true;
        }

        String kitName = args[0];
        kitManager.createKitFromPlayer(kitName, player);

        sender.sendMessage(Component.text("Kit '" + kitName + "' created from your inventory!", NamedTextColor.GREEN));
        return true;
    }
}
