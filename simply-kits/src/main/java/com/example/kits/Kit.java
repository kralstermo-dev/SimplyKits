package com.example.kits;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Kit {

    private final String name;

    private final Map<Integer, ItemStack> storageItems = new HashMap<>();

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

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
