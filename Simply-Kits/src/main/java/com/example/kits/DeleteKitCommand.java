package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles: /kitdelete <name>
 *
 * Implements BOTH CommandExecutor (runs the command) and TabCompleter
 * (suggests values while you're still typing). They're two separate
 * interfaces on purpose - a command doesn't have to offer suggestions,
 * and Paper only calls onTabComplete() while you're pressing Tab, never
 * when you actually hit Enter.
 */
public class DeleteKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public DeleteKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /kitdelete <name>", NamedTextColor.RED));
            return true;
        }

        boolean deleted = kitManager.deleteKit(args[0]);
        if (deleted) {
            sender.sendMessage(Component.text("Kit '" + args[0] + "' deleted.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("No kit named '" + args[0] + "' exists.", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Only suggest kit names that start with what's already typed,
            // e.g. typing "wa" only suggests "warrior", not "archer".
            return kitManager.getKitNames().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return new ArrayList<>();
    }
}
