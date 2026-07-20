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

public class RandomKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public RandomKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kits.give")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /kitrandom <player|selector>", NamedTextColor.RED));
            return true;
        }

        Kit kit = kitManager.getRandomKit();
        if (kit == null) {
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
                kitManager.giveKit(kit, player);
                player.sendMessage(Component.text("You received a random kit: '" + kit.getName() + "'!", NamedTextColor.GREEN));
                given++;
            }
        }

        sender.sendMessage(Component.text("Gave random kit '" + kit.getName() + "' to " + given + " player(s).", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("kits.give")) {
            return new ArrayList<>();
        }

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
