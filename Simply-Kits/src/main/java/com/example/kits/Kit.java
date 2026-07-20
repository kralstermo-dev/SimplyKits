package com.example.kits;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A Kit is just DATA: it remembers what item was in every single slot
 * of a player's inventory at the moment the kit was created.
 *
 * It doesn't know how to save itself to a file, and it doesn't know how
 * to give itself to a player - that logic lives in KitManager.
 * Keeping "dumb data" classes separate from "logic that acts on the data"
 * classes is a very common pattern and makes code much easier to follow.
 */
public class Kit {

    private final String name;

    // Main inventory storage slots: 0-8 are the hotbar, 9-35 are the
    // three rows above it. Key = slot number, Value = item in that slot.
    // We use a Map (not a plain array) so that EMPTY slots simply have no
    // entry at all, instead of us having to store "AIR" placeholders.
    private final Map<Integer, ItemStack> storageItems = new HashMap<>();

    // Armor is stored separately because Paper exposes it separately too
    // (Inventory#getHelmet(), #getChestplate(), etc). Any of these can be
    // null, meaning "no item in that armor slot".
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    // The offhand (shield slot) is its own thing as well.
    private ItemStack offHand;

    public Kit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, ItemStack> getStorageItems() {
        return storageItems;
    }

    public void setStorageSlot(int slot, ItemStack item) {
        storageItems.put(slot, item);
    }

    public ItemStack getHelmet() { return helmet; }
    public void setHelmet(ItemStack helmet) { this.helmet = helmet; }

    public ItemStack getChestplate() { return chestplate; }
    public void setChestplate(ItemStack chestplate) { this.chestplate = chestplate; }

    public ItemStack getLeggings() { return leggings; }
    public void setLeggings(ItemStack leggings) { this.leggings = leggings; }

    public ItemStack getBoots() { return boots; }
    public void setBoots(ItemStack boots) { this.boots = boots; }

    public ItemStack getOffHand() { return offHand; }
    public void setOffHand(ItemStack offHand) { this.offHand = offHand; }
}
