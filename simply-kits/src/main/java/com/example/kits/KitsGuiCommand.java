package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitsGuiCommand implements CommandExecutor {

    private final KitGuiListener guiListener;

    public KitsGuiCommand(KitGuiListener guiListener) {
        this.guiListener = guiListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kits.gui")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can open the kits menu.", NamedTextColor.RED));
            return true;
        }

        player.openInventory(guiListener.buildGui());
        return true;
    }
}
