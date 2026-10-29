package org.minecraft.spawn;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

import static org.bukkit.Bukkit.getWorld;

public class SpawnCommand implements TabExecutor {
    private Spawn spawn;
    private static int teleportId;

    // Players currently in a teleport countdown
    public static final Map<UUID, CooldownInfo> startLoc = new HashMap<>();


    public SpawnCommand(Spawn spawn) {
        this.spawn = spawn;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Reusable Methods.
    public static String getWorldType(World world) {
        String worldType = null;


            if (world.getEnvironment() == World.Environment.NORMAL) {
                worldType = "Overworld";
            }
            else if (world.getEnvironment() == World.Environment.NETHER) {
                worldType = "Nether";
            }
            else if (world.getEnvironment() == World.Environment.THE_END) {
                worldType = "End";
            }


        return worldType;
    }



    public static twoItems getWorldAndCoordinates(String worldType, Spawn spawn) {
        World world = null;
        String coordinates = null;

        if (worldType.equals("Overworld")) {
            world = getWorld("world");
            coordinates = spawn.getConfig().getString("spawn.worlds.overworld");
        }

        else if (worldType.equals("Nether")) {
            world = getWorld("world_nether");
            coordinates = spawn.getConfig().getString("spawn.worlds.nether");
        }

        else if (worldType.equals("End")) {
            world = getWorld("world_the_end");
            coordinates = spawn.getConfig().getString("spawn.worlds.end");
        }

        return new twoItems(world, coordinates);

    }
    public static String checkCoordinates(World world, String coordinates, String worldType, Spawn spawn) {
        String newCoordinates = coordinates;
        if (coordinates == (null) || coordinates.isBlank()) {
            Location location = world.getSpawnLocation();
            Double x = location.getX();
            Double y = location.getY();
            Double z = location.getZ();

            newCoordinates = (x + " " + y + " " + z);

            if (worldType.equals("Overworld")) {
                spawn.getConfig().set("spawn.worlds.overworld", newCoordinates);
            }

            else if (worldType.equals("Nether")) {
                spawn.getConfig().set("spawn.worlds.nether", newCoordinates);
            }

            else if (worldType.equals("End")) {
                spawn.getConfig().set("spawn.worlds.end", newCoordinates);
            }

            spawn.saveConfig();

        }
        return newCoordinates;
    }


    public static threeItems parseCoordinates(String coordinates) {
        String[] split = coordinates.split(" ");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);

        return new threeItems(x, y, z);
    }

    public static Location getTeleportLocation(World world, Double x, Double y, Double z) {
        Location target = new Location(world, x, y, z);

        return target;
    }


    public static void countdown(Location targets, Spawn spawn, Player player) {

        Boolean use_delay = spawn.getConfig().getBoolean("spawn.delay.use-delay", true);

        if (use_delay == true) {
            //Get the number of seconds delay the user requested for.
            if (startLoc.containsKey(player.getUniqueId())) {
                player.sendMessage("[Spawn] You already have a teleport activated, wait until it finishes.");
                return;
            }



            int delay = spawn.getConfig().getInt("spawn.delay.delay-seconds", 3);


            // Add player to pending teleport set
            UUID id = player.getUniqueId();

            Location currentLocation = player.getLocation();

            //Add one to the teleport id.
            teleportId += 1;
            int localTeleportId = teleportId;

            //Create cooldown info object.

            CooldownInfo cooldown = new CooldownInfo(currentLocation, localTeleportId);



            startLoc.put(id, cooldown);

            //Determine what colour the text will be.
            String[] colors;

            if (delay == 3) {
                colors = new String[3];
                colors[0] = "§a";
                colors[1] = "§c";
                colors[2] = "§4";
            }

            else {
                colors = new String[delay];
                Arrays.fill(colors, "§a");
                //Red
                colors[delay-1] = "§4";

                if (delay>=2) {
                    //Orange
                    colors[delay-2] = "§c";
                }
                if (delay>3) {
                    //Yellow
                    colors[delay-3] = "§e";
                }

            }


            //Looped for the time the user wants to wait.
            for (int i = 0; i < delay; i++) {
                final int secondsLeft = delay - i;
                final int h = i;
                Bukkit.getScheduler().runTaskLater(spawn, () -> {

                    if (!player.isOnline()) {
                        startLoc.remove(id);
                        return;
                    }
                    if (!startLoc.containsKey(id)) return; // cancelled by movement

                    CooldownInfo cooldownTwo = startLoc.get(id);


                    if (cooldownTwo.teleportId != localTeleportId) {
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    player.sendMessage("[Spawn] " + colors[h] + "Teleporting in " + secondsLeft + "s");
                    player.sendTitle(colors[h] + "Teleporting in " + secondsLeft + "s", "", 0, 20, 0);
                }, i * 20L);
            }

            // teleport (after countdown.)
            Bukkit.getScheduler().runTaskLater(spawn, () -> {
                if (!player.isOnline()) {
                    startLoc.remove(id);
                    return;
                }

                if (!startLoc.containsKey(id)) {
                    startLoc.remove(id);
                    return;
                }

                CooldownInfo cooldownTwo = startLoc.get(id);

                if (cooldownTwo.teleportId != localTeleportId) {
                    return;
                }

                // Success: remove from pending and teleport
                startLoc.remove(id);

                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                player.teleport(targets);
                player.sendMessage("[Spawn] Teleported!");
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 120, 0.6, 1.0, 0.6, 0.2);
            }, delay * 20L);

            return;
        }


        if (use_delay == false) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.teleport(targets);
            player.sendMessage("[Spawn] Teleported!");
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 120, 0.6, 1.0, 0.6, 0.2);
        }

        return;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can not do that command on the console.");
            return true;
        }



        //GUI System

        if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            if (player.hasPermission("spawn.spawn.gui")) {
                Inventory gui = Bukkit.createInventory(player, 9 * 3, "§aSpawns") ;



                ItemStack getOverworldSpawn = new ItemStack(Material.GRASS_BLOCK);
                ItemMeta spawnMeta = getOverworldSpawn.getItemMeta();
                spawnMeta.setDisplayName("Overworld");
                getOverworldSpawn.setItemMeta(spawnMeta);


                ItemStack getNetherSpawn = new ItemStack(Material.NETHERRACK);
                ItemMeta netherSpawnMeta = getNetherSpawn.getItemMeta();
                netherSpawnMeta.setDisplayName("Nether");
                getNetherSpawn.setItemMeta(netherSpawnMeta);


                ItemStack getEndSpawn = new ItemStack(Material.END_STONE);
                ItemMeta endSpawnMeta = getEndSpawn.getItemMeta();
                endSpawnMeta.setDisplayName("End");
                getEndSpawn.setItemMeta(endSpawnMeta);


                gui.setItem(11, getOverworldSpawn);
                gui.setItem(13, getNetherSpawn);
                gui.setItem(15, getEndSpawn);

                player.openInventory(gui);
                player.setMetadata("guiopen", new FixedMetadataValue(spawn, true));

                return true;
            }

            else {
                player.sendMessage("You do not have permission to do that.");
            }

        }


        Location target = null;

        //Get player world and coordinates
        //If nothing has been entered after spawn.
        if (args.length == 0) {

           World world = player.getWorld();

           String getType = getWorldType(world);
           twoItems items = getWorldAndCoordinates(getType, spawn);

           String coords = items.coordinates;
           World worldTwo = items.world;

           //Ensure coordinates are not just empty.
           String newCoords = checkCoordinates(worldTwo, coords, getType, spawn);

           threeItems itemsThree = parseCoordinates(newCoords);

           target = getTeleportLocation(worldTwo, itemsThree.x, itemsThree.y, itemsThree.z);

            countdown(target, spawn, player);
        }

        //If player has entered what world they want to teleport to.
        if (args.length == 1) {
            String getType = null;


            //Find out the world the player wants to teleport to.
            if (args[0].equalsIgnoreCase("Overworld")) {
                if (player.hasPermission("spawn.spawn.overworld") == false) {
                    return true;
                }
                getType = "Overworld";
            } else if (args[0].equalsIgnoreCase("Nether")) {
                if (player.hasPermission("spawn.spawn.nether") == false) {
                    return true;
                }
                getType = "Nether";
            } else if (args[0].equalsIgnoreCase("End")) {
                if (player.hasPermission("spawn.spawn.end") == false) {
                    return true;
                }
                getType = "End";
            } else {
                player.sendMessage("You did not enter a valid world. Try again");
                return true;
            }


            twoItems items = getWorldAndCoordinates(getType, spawn);

            String coords = items.coordinates;
            World worldTwo = items.world;

            //Ensure coordinates are not just empty.
            String newCoords = checkCoordinates(worldTwo, coords, getType, spawn);

            threeItems itemsThree = parseCoordinates(newCoords);

            target = getTeleportLocation(worldTwo, itemsThree.x, itemsThree.y, itemsThree.z);

            countdown(target, spawn, player);
        }

        Location targets = target;


       ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






        return true;
    }




    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            Player player = (Player) commandSender;
            List<String> suggestions = new ArrayList<>();
            if (player.hasPermission("spawn.spawn.gui")) {
                suggestions.add("GUI");
            }

            if (player.hasPermission("spawn.spawn.overworld")) {
                suggestions.add("Overworld");
            }

            if (player.hasPermission("spawn.spawn.nether")) {
                suggestions.add("Nether");
            }

            if (player.hasPermission("spawn.spawn.end")) {
                suggestions.add("End");
            }


            return suggestions;
        }
        return List.of();
    }
}
