package com.example.kits;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Handles everything to do with kits: turning a player's inventory into a
 * Kit object, saving kits to disk, loading them back, and applying a kit
 * to a player.
 */
public class KitManager {

    private final KitsPlugin plugin;
    private final File file;
    private final YamlConfiguration config;

    // In-memory cache so we don't hit the disk every time someone runs /kitgive.
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(KitsPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "kits.yml");

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs(); // make sure the plugin's folder exists
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create kits.yml", e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        loadAllKits();
    }

    // ---------------------------------------------------------------
    // Creating a kit from a player's current inventory
    // ---------------------------------------------------------------

    /**
     * Captures the player's EXACT inventory, slot by slot, into a new Kit.
     */
    public Kit createKitFromPlayer(String kitName, Player player) {
        Kit kit = new Kit(kitName);
        PlayerInventory inv = player.getInventory();

        // getStorageContents() = the 36 main slots (hotbar + 3 rows),
        // in the SAME order as the slot numbers you'd use in an /item
        // command or a chest GUI: index 0-8 = hotbar, 9-35 = the rest.
        ItemStack[] storage = inv.getStorageContents();
        for (int slot = 0; slot < storage.length; slot++) {
            ItemStack item = storage[slot];
            if (isPresent(item)) {
                // .clone() is important! Without it we'd be storing a
                // reference to the actual item in the player's inventory,
                // and changes to their inventory later could corrupt the kit.
                kit.setStorageSlot(slot, item.clone());
            }
        }

        // Armor and offhand each have their own dedicated getters.
        if (isPresent(inv.getHelmet()))     kit.setHelmet(inv.getHelmet().clone());
        if (isPresent(inv.getChestplate())) kit.setChestplate(inv.getChestplate().clone());
        if (isPresent(inv.getLeggings()))   kit.setLeggings(inv.getLeggings().clone());
        if (isPresent(inv.getBoots()))      kit.setBoots(inv.getBoots().clone());
        if (isPresent(inv.getItemInOffHand())) kit.setOffHand(inv.getItemInOffHand().clone());

        kits.put(kitName.toLowerCase(), kit);
        saveKit(kit);
        return kit;
    }

    private boolean isPresent(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    // ---------------------------------------------------------------
    // Giving a kit to a player
    // ---------------------------------------------------------------

    /**
     * Places every item from the kit into the EXACT same slot on the
     * target player. Slots that the kit didn't have anything saved in
     * are left completely untouched.
     */
    public void giveKit(Kit kit, Player target) {
        PlayerInventory inv = target.getInventory();

        for (Map.Entry<Integer, ItemStack> entry : kit.getStorageItems().entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().clone());
        }

        if (kit.getHelmet() != null)     inv.setHelmet(kit.getHelmet().clone());
        if (kit.getChestplate() != null) inv.setChestplate(kit.getChestplate().clone());
        if (kit.getLeggings() != null)   inv.setLeggings(kit.getLeggings().clone());
        if (kit.getBoots() != null)      inv.setBoots(kit.getBoots().clone());
        if (kit.getOffHand() != null)    inv.setItemInOffHand(kit.getOffHand().clone());
    }

    // ---------------------------------------------------------------
    // Deleting a kit
    // ---------------------------------------------------------------

    /**
     * Removes a kit from memory AND from kits.yml. Returns true if a kit
     * with that name actually existed.
     */
    public boolean deleteKit(String name) {
        String key = name.toLowerCase();
        if (!kits.containsKey(key)) {
            return false;
        }

        kits.remove(key);
        config.set("kits." + key, null); // setting a path to null erases it
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save kits.yml after deleting a kit", e);
        }
        return true;
    }

    // ---------------------------------------------------------------
    // Lookup helpers
    // ---------------------------------------------------------------

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    /**
     * Returns a random kit, or null if no kits have been created yet.
     */
    public Kit getRandomKit() {
        if (kits.isEmpty()) {
            return null;
        }
        // kits.values() has no index access (it's a Map), so we pick a
        // random position and walk to it. Fine for the handful of kits
        // a server will realistically have.
        int index = new java.util.Random().nextInt(kits.size());
        int i = 0;
        for (Kit kit : kits.values()) {
            if (i == index) return kit;
            i++;
        }
        return null; // unreachable, but the compiler wants a return here
    }

    public boolean kitExists(String name) {
        return kits.containsKey(name.toLowerCase());
    }

    public Set<String> getKitNames() {
        return kits.keySet();
    }

    // ---------------------------------------------------------------
    // Persistence (kits.yml)
    // ---------------------------------------------------------------

    private void saveKit(Kit kit) {
        String base = "kits." + kit.getName().toLowerCase() + ".";

        // Clear out anything previously saved under this kit name first,
        // in case this is an overwrite and the new kit has fewer items.
        config.set("kits." + kit.getName().toLowerCase(), null);

        for (Map.Entry<Integer, ItemStack> entry : kit.getStorageItems().entrySet()) {
            // YamlConfiguration knows how to turn an ItemStack into YAML
            // automatically, because ItemStack implements ConfigurationSerializable.
            config.set(base + "storage." + entry.getKey(), entry.getValue());
        }
        if (kit.getHelmet() != null)     config.set(base + "helmet", kit.getHelmet());
        if (kit.getChestplate() != null) config.set(base + "chestplate", kit.getChestplate());
        if (kit.getLeggings() != null)   config.set(base + "leggings", kit.getLeggings());
        if (kit.getBoots() != null)      config.set(base + "boots", kit.getBoots());
        if (kit.getOffHand() != null)    config.set(base + "offhand", kit.getOffHand());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save kits.yml", e);
        }
    }

    private void loadAllKits() {
        if (!config.contains("kits")) return;

        for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
            Kit kit = new Kit(kitName);
            String base = "kits." + kitName + ".";

            if (config.contains(base + "storage")) {
                for (String slotKey : config.getConfigurationSection(base + "storage").getKeys(false)) {
                    ItemStack item = config.getItemStack(base + "storage." + slotKey);
                    kit.setStorageSlot(Integer.parseInt(slotKey), item);
                }
            }
            kit.setHelmet(config.getItemStack(base + "helmet"));
            kit.setChestplate(config.getItemStack(base + "chestplate"));
            kit.setLeggings(config.getItemStack(base + "leggings"));
            kit.setBoots(config.getItemStack(base + "boots"));
            kit.setOffHand(config.getItemStack(base + "offhand"));

            kits.put(kitName.toLowerCase(), kit);
        }
    }
}
