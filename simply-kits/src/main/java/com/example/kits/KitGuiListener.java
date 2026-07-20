package com.example.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class KitGuiListener implements Listener {

    private final KitsPlugin plugin;
    private final KitManager kitManager;

    private final NamespacedKey kitNameKey;

    private static final String RANDOM_MARKER = "__random__";

    public KitGuiListener(KitsPlugin plugin, KitManager kitManager) {
        this.plugin = plugin;
        this.kitManager = kitManager;
        this.kitNameKey = new NamespacedKey(plugin, "kit_name");
    }

    public Inventory buildGui() {
        int kitCount = kitManager.getKitNames().size();
        int rows = Math.max(1, Math.min(6, (int) Math.ceil((kitCount + 1) / 9.0)));
        int size = rows * 9;

        KitsGuiHolder holder = new KitsGuiHolder();
        Inventory gui = plugin.getServer().createInventory(
                holder, size, Component.text("Available Kits", NamedTextColor.DARK_AQUA));
        holder.setInventory(gui);

        for (String kitName : kitManager.getKitNames()) {
            Kit kit = kitManager.getKit(kitName);
            gui.addItem(buildIcon(kit));
        }

        if (kitCount > 0) {
            gui.setItem(size - 1, buildRandomIcon());
        }

        return gui;
    }

    private ItemStack buildRandomIcon() {
        ItemStack icon = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text("Random Kit", NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(java.util.List.of(
                Component.text("Click for a surprise!", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
        meta.getPersistentDataContainer().set(kitNameKey, PersistentDataType.STRING, RANDOM_MARKER);
        icon.setItemMeta(meta);
        return icon;
    }

    private ItemStack buildIcon(Kit kit) {
        ItemStack icon = kit.getStorageItems().values().stream().findFirst()
                .or(() -> java.util.Optional.ofNullable(kit.getHelmet()))
                .map(ItemStack::clone)
                .orElse(new ItemStack(Material.CHEST));
        icon.setAmount(1);

        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text(kit.getName(), NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(java.util.List.of(
                Component.text("Click to receive this kit!", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));

        meta.getPersistentDataContainer().set(kitNameKey, PersistentDataType.STRING, kit.getName());
        icon.setItemMeta(meta);

        return icon;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof KitsGuiHolder)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        String kitName = clicked.getItemMeta().getPersistentDataContainer()
                .get(kitNameKey, PersistentDataType.STRING);
        if (kitName == null) {
            return;
        }

        Kit kit = kitName.equals(RANDOM_MARKER) ? kitManager.getRandomKit() : kitManager.getKit(kitName);
        if (kit == null) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            kitManager.giveKit(kit, player);
            player.sendMessage(Component.text("You received the '" + kit.getName() + "' kit!", NamedTextColor.GREEN));
            player.closeInventory();
        }
    }
}
