package org.minecraft.spawn;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteSpawn implements CommandExecutor {
    Spawn spawn;

    public DeleteSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       Player player = (Player) sender;
       if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
           spawn.getConfig().set("spawn.worlds.overworld","");
       }

       if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
           spawn.getConfig().set("spawn.worlds.nether","");
       }

       if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
           spawn.getConfig().set("spawn.worlds.end","");
       }

       spawn.saveConfig();

        sender.sendMessage("[Spawn] " + ChatColor.RED + "The current spawn location has been deleted.");
        return true;
    }
}
