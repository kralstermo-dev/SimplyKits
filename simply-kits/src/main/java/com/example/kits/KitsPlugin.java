package com.example.kits;

import org.bukkit.plugin.java.JavaPlugin;

public class KitsPlugin extends JavaPlugin {

    private KitManager kitManager;

    @Override
    public void onEnable() {
        this.kitManager = new KitManager(this);

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

        getServer().getPluginManager().registerEvents(guiListener, this);

        getLogger().info("Kits plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kits plugin disabled.");
    }
}
