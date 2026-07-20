package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles: /kits
 * Opens the clickable kit menu for the player who ran the command.
 */
public class KitsGuiCommand implements CommandExecutor {

    private final KitGuiListener guiListener;

    public KitsGuiCommand(KitGuiListener guiListener) {
        this.guiListener = guiListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can open the kits menu.", NamedTextColor.RED));
            return true;
        }

        player.openInventory(guiListener.buildGui());
        return true;
    }
}
