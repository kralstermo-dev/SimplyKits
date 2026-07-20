package com.example.kits;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Every Bukkit Inventory has an "owner" (InventoryHolder) - for a real
 * chest, that's the chest block. We don't have a physical block, so we
 * make our own empty marker class instead.
 *
 * Why bother? When a click happens ANYWHERE (survival inventory, another
 * plugin's GUI, a chest...) our listener fires. Checking
 * "is the clicked inventory's holder a KitsGuiHolder?" is a clean, reliable
 * way to say "yes, this click was in OUR menu" without accidentally
 * matching on the menu's title text (which is fragile - titles can clash).
 */
public class KitsGuiHolder implements InventoryHolder {

    private Inventory inventory;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
