package com.example.kits;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The entry point of the plugin. Paper looks for this class (as declared
 * in plugin.yml's "main" field) and calls onEnable() when the server starts
 * or when the plugin is loaded/reloaded.
 */
public class KitsPlugin extends JavaPlugin {

    private KitManager kitManager;

    @Override
    public void onEnable() {
        this.kitManager = new KitManager(this);

        // /kitgive needs both an executor (runs it) and a tab completer
        // (suggests kit names, then players/selectors). One object can be
        // both, since GiveKitCommand implements both interfaces.
        GiveKitCommand giveKitCommand = new GiveKitCommand(kitManager);
        getCommand("kitgive").setExecutor(giveKitCommand);
        getCommand("kitgive").setTabCompleter(giveKitCommand);

        DeleteKitCommand deleteKitCommand = new DeleteKitCommand(kitManager);
        getCommand("kitdelete").setExecutor(deleteKitCommand);
        getCommand("kitdelete").setTabCompleter(deleteKitCommand);

        RandomKitCommand randomKitCommand = new RandomKitCommand(kitManager);
        getCommand("kitrandom").setExecutor(randomKitCommand);
        getCommand("kitrandom").setTabCompleter(randomKitCommand);

        RngKitCommand rngKitCommand = new RngKitCommand(kitManager);
        getCommand("kitrng").setExecutor(rngKitCommand);
        getCommand("kitrng").setTabCompleter(rngKitCommand);

        getCommand("kitcreate").setExecutor(new CreateKitCommand(kitManager));
        getCommand("kitlist").setExecutor(new ListKitsCommand(kitManager));

        KitGuiListener guiListener = new KitGuiListener(this, kitManager);
        getCommand("kits").setExecutor(new KitsGuiCommand(guiListener));

        // Listeners are registered separately from commands - this is what
        // lets KitGuiListener react to InventoryClickEvent whenever it
        // happens, not just when a specific command runs.
        getServer().getPluginManager().registerEvents(guiListener, this);

        getLogger().info("Kits plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kits plugin disabled.");
    }
}
