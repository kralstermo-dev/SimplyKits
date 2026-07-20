package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListKitsCommand implements CommandExecutor {

    private final KitManager kitManager;

    public ListKitsCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (kitManager.getKitNames().isEmpty()) {
            sender.sendMessage(Component.text("No kits have been created yet.", NamedTextColor.YELLOW));
            return true;
        }

        String joined = String.join(", ", kitManager.getKitNames());
        sender.sendMessage(Component.text("Available kits: " + joined, NamedTextColor.AQUA));
        return true;
    }
}
