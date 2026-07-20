package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DeleteKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public DeleteKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kits.delete")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

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
        if (!sender.hasPermission("kits.delete")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return kitManager.getKitNames().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return new ArrayList<>();
    }
}
