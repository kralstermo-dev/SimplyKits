package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles: /kitrng <selector>
 *
 * The "each player gets their OWN random roll" version. The only real
 * difference from RandomKitCommand is WHERE the kitManager.getRandomKit()
 * call happens: inside the loop instead of before it, so it re-rolls once
 * per target instead of once for the whole command.
 *
 * This command is deliberately left out of plugin.yml's "permissions"
 * defaults (see kits.rng below) with default: false, which means only
 * ops / people explicitly granted the permission can even see or run it -
 * everyone else's tab-complete just won't show it, like a hidden easter egg.
 */
public class RngKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public RngKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /kitrng <player|selector>", NamedTextColor.RED));
            return true;
        }

        if (kitManager.getKitNames().isEmpty()) {
            sender.sendMessage(Component.text("There are no kits to choose from yet.", NamedTextColor.RED));
            return true;
        }

        List<Entity> targets;
        try {
            targets = Bukkit.selectEntities(sender, args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid selector: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (targets.isEmpty()) {
            sender.sendMessage(Component.text("No matching players found.", NamedTextColor.RED));
            return true;
        }

        int given = 0;
        for (Entity entity : targets) {
            if (entity instanceof Player player) {
                // Rolled fresh for EACH player, unlike /kitrandom.
                Kit kit = kitManager.getRandomKit();
                kitManager.giveKit(kit, player);
                player.sendMessage(Component.text("You received a random kit: '" + kit.getName() + "'!", NamedTextColor.GREEN));
                given++;
            }
        }

        sender.sendMessage(Component.text("Gave independently-rolled random kits to " + given + " player(s).", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String typed = args[0].toLowerCase();
            List<String> options = new ArrayList<>(List.of("@a", "@p", "@r", "@s"));
            for (Player online : Bukkit.getOnlinePlayers()) {
                options.add(online.getName());
            }
            return options.stream()
                    .filter(option -> option.toLowerCase().startsWith(typed))
                    .toList();
        }
        return new ArrayList<>();
    }
}
