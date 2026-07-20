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

public class KitManager {

    private final KitsPlugin plugin;
    private final File file;
    private final YamlConfiguration config;

    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(KitsPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "kits.yml");

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create kits.yml", e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        loadAllKits();
    }

    public Kit createKitFromPlayer(String kitName, Player player) {
        Kit kit = new Kit(kitName);
        PlayerInventory inv = player.getInventory();

        ItemStack[] storage = inv.getStorageContents();
        for (int slot = 0; slot < storage.length; slot++) {
            ItemStack item = storage[slot];
            if (isPresent(item)) {
                kit.setStorageSlot(slot, item.clone());
            }
        }

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

    public boolean deleteKit(String name) {
        String key = name.toLowerCase();
        if (!kits.containsKey(key)) {
            return false;
        }

        kits.remove(key);
        config.set("kits." + key, null);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save kits.yml after deleting a kit", e);
        }
        return true;
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public Kit getRandomKit() {
        if (kits.isEmpty()) {
            return null;
        }
        int index = new java.util.Random().nextInt(kits.size());
        int i = 0;
        for (Kit kit : kits.values()) {
            if (i == index) return kit;
            i++;
        }
        return null;
    }

    public boolean kitExists(String name) {
        return kits.containsKey(name.toLowerCase());
    }

    public Set<String> getKitNames() {
        return kits.keySet();
    }

    private void saveKit(Kit kit) {
        String base = "kits." + kit.getName().toLowerCase() + ".";

        config.set("kits." + kit.getName().toLowerCase(), null);

        for (Map.Entry<Integer, ItemStack> entry : kit.getStorageItems().entrySet()) {
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
