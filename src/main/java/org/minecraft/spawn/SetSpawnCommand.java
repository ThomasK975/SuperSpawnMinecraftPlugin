package org.minecraft.spawn;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SetSpawnCommand implements CommandExecutor {
    Spawn spawn;

    public SetSpawnCommand(Spawn spawn) {
        this.spawn = spawn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can not do that command on the console.");
            return true;
        }


        //Logic for if the player decides to enter coordinates x, y and z manually.
        if (args.length == 3) {
            Double xCoordinate = Double.parseDouble(args[0]);
            Double yCoordinate = Double.parseDouble(args[1]);
            Double zCoordinate = Double.parseDouble(args[2]);

            if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                spawn.getConfig().set("spawn.worlds.overworld", xCoordinate + " " + yCoordinate + " " + zCoordinate);
            }

            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                spawn.getConfig().set("spawn.worlds.nether", xCoordinate + " " + yCoordinate + " " + zCoordinate);
            }

            if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
                spawn.getConfig().set("spawn.worlds.end", xCoordinate + " " + yCoordinate + " " + zCoordinate);
            }

            player.sendMessage("[Spawn] §aNew spawn set using coordinates!");
            spawn.saveConfig();
            return true;
        }



        //Logic if the player wants to set their current location to be the spawn.
        Location loc = player.getLocation();


        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
            spawn.getConfig().set("spawn.worlds.overworld", loc.getX() + " " + loc.getY() + " " + loc.getZ());
        }

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            spawn.getConfig().set("spawn.worlds.nether", loc.getX() + " " + loc.getY() + " " + loc.getZ());
        }

        if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            spawn.getConfig().set("spawn.worlds.end", loc.getX() + " " + loc.getY() + " " + loc.getZ());
        }


        player.sendMessage("[Spawn] " + ChatColor.AQUA + "New spawn set!");
        spawn.saveConfig();
        return true;
    }
}
