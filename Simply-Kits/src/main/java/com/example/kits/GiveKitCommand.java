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
 * Handles: /kitgive <name> <selector>
 * <selector> can be an exact player name OR a vanilla target selector
 * like @a, @p, @r, @a[distance=..10] etc, because we hand the string
 * straight to Bukkit's own selector parser.
 */
public class GiveKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public GiveKitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /kitgive <name> <player|selector>", NamedTextColor.RED));
            return true;
        }

        String kitName = args[0];
        String selector = args[1];

        Kit kit = kitManager.getKit(kitName);
        if (kit == null) {
            sender.sendMessage(Component.text("No kit named '" + kitName + "' exists.", NamedTextColor.RED));
            return true;
        }

        // Bukkit.selectEntities() understands @a, @p, @r, @e, @s and all
        // their [brackets=...] filters, exactly like a /give command would.
        // It also just returns a single-entry list if you pass a plain
        // player name instead of a selector, so this one method covers both.
        List<Entity> targets;
        try {
            targets = Bukkit.selectEntities(sender, selector);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid selector: " + selector, NamedTextColor.RED));
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
                player.sendMessage(Component.text("You received the '" + kitName + "' kit!", NamedTextColor.GREEN));
                given++;
            }
        }

        sender.sendMessage(Component.text("Gave kit '" + kitName + "' to " + given + " player(s).", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // args[0] is the kit name being typed - suggest matching kit names.
        if (args.length == 1) {
            String typed = args[0].toLowerCase();
            return kitManager.getKitNames().stream()
                    .filter(name -> name.startsWith(typed))
                    .toList();
        }

        // args[1] is the target - suggest the classic selectors plus every
        // online player's name, same as vanilla commands do.
        if (args.length == 2) {
            String typed = args[1].toLowerCase();
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
