package org.minecraft.spawn;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Spawn extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("   _____ _____   __          ___   _ ");
        getLogger().info("  / ____|  __ \\ /\\ \\        / / \\ | |");
        getLogger().info(" | (___ | |__) /  \\ \\  /\\  / /|  \\| |");
        getLogger().info("  \\___ \\|  ___/ /\\ \\ \\/  \\/ / | . ` |");
        getLogger().info("  ____) | |  / ____ \\  /\\  /  | |\\  |");
        getLogger().info(" |_____/|_| /_/    \\_\\/  \\/   |_| \\_|");
        getLogger().info("");
        getLogger().info("Loaded");

        // create executors AFTER folder/file exist
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("delspawn").setExecutor(new DeleteSpawn(this));

        //Create a listener for player movement.
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);

        //Create a tab executor.


    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
