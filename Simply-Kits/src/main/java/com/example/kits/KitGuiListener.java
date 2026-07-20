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

/**
 * Builds the "click a kit to receive it" menu, and listens for clicks
 * inside it.
 */
public class KitGuiListener implements Listener {

    private final KitsPlugin plugin;
    private final KitManager kitManager;

    // A NamespacedKey is a plugin-scoped tag name (like "kits:kit_name").
    // We stamp this onto each icon's ItemMeta so that, later, when a click
    // comes in, we can read back EXACTLY which kit that icon represents -
    // instead of guessing from the item's display name or slot position.
    private final NamespacedKey kitNameKey;

    // The special value we stamp on the "Random Kit" button instead of a
    // real kit name, so the click handler can recognise it as a special case.
    private static final String RANDOM_MARKER = "__random__";

    public KitGuiListener(KitsPlugin plugin, KitManager kitManager) {
        this.plugin = plugin;
        this.kitManager = kitManager;
        this.kitNameKey = new NamespacedKey(plugin, "kit_name");
    }

    /**
     * Creates a fresh menu inventory listing every saved kit as a clickable item,
     * plus a "Random Kit" button at the very end.
     */
    public Inventory buildGui() {
        int kitCount = kitManager.getKitNames().size();
        // +1 slot reserved for the Random Kit button.
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

    /**
     * The button that gives the clicker a random kit instead of a specific one.
     */
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


    /**
     * Turns a Kit into the ItemStack that represents it in the menu.
     * We reuse one of the kit's own items as the icon so kits are easy
     * to tell apart at a glance, falling back to a chest if the kit is
     * somehow empty.
     */
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

        // Stamp the kit's real name into the item's persistent data so we
        // can read it back reliably in the click handler below.
        meta.getPersistentDataContainer().set(kitNameKey, PersistentDataType.STRING, kit.getName());
        icon.setItemMeta(meta);

        return icon;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only care about clicks inside OUR gui, identified by the holder
        // we tagged it with when we created it.
        if (!(event.getInventory().getHolder() instanceof KitsGuiHolder)) {
            return;
        }

        // Cancel the click so nobody can drag the icon item into their
        // own inventory - clicking should GIVE a kit, not give the icon.
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
            return; // kit might have been deleted after the menu was opened
        }

        // event.getWhoClicked() is the player who clicked - so only THEY
        // receive the kit, never anyone else who might also have the menu open.
        if (event.getWhoClicked() instanceof Player player) {
            kitManager.giveKit(kit, player);
            player.sendMessage(Component.text("You received the '" + kit.getName() + "' kit!", NamedTextColor.GREEN));
            player.closeInventory();
        }
    }
}
