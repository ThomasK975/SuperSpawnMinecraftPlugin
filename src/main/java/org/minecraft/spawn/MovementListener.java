package org.minecraft.spawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

import static org.minecraft.spawn.SpawnCommand.*;

public class MovementListener implements Listener {
    Spawn spawn;

    //Constructor
    public MovementListener(Spawn spawn) {
        this.spawn = spawn;
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player player = e.getPlayer();
        UUID playerMoving = player.getUniqueId();
        if (SpawnCommand.startLoc.containsKey(playerMoving)) {
            if (SpawnCommand.startLoc.get(playerMoving).loc.getX() != player.getX() || SpawnCommand.startLoc.get(playerMoving).loc.getY() != player.getY() ||
                    SpawnCommand.startLoc.get(playerMoving).loc.getZ() != player.getZ()) {

                startLoc.remove(playerMoving);
                player.sendMessage("§4[Spawn] Teleporting to spawn canceled!");


            }


        }

    }

    @EventHandler
    public void firstTimeJoinEvent (PlayerJoinEvent h) {

        Player player = h.getPlayer();
        if (player.hasPlayedBefore() == false) {
            //Get coordinates
            String coordinates = spawn.getConfig().getString("spawn.worlds.overworld");
            if (coordinates == null) {
                return;
            }
            else {
                String[] coordinatessplit = coordinates.split(" ");
                String Xcoord = coordinatessplit[0];
                String Ycoord = coordinatessplit[1];
                String Zcoord = coordinatessplit[2];
                World world = player.getWorld();

                Double xcoord = Double.parseDouble(Xcoord);
                Double ycoord = Double.parseDouble(Ycoord);
                Double zcoord = Double.parseDouble(Zcoord);

                Location location = new Location(world, xcoord, ycoord, zcoord);
                player.teleport(location);
                String username = player.getName();
                h.setJoinMessage("§c" + username + " joined the server for the first time!");
            }
        }

    }

    @EventHandler
    public void death(PlayerRespawnEvent j) {
        Player player = j.getPlayer();
        Location bed = player.getBedSpawnLocation();
        if (bed == null) {
            String coordinates = spawn.getConfig().getString("spawn.worlds.overworld");
            String[] coordinatessplit = coordinates.split(" ");
            String Xcoord = coordinatessplit[0];
            String Ycoord = coordinatessplit[1];
            String Zcoord = coordinatessplit[2];

            World world = spawn.getServer().getWorld("world");

            Double xcoord = Double.parseDouble(Xcoord);
            Double ycoord = Double.parseDouble(Ycoord);
            Double zcoord = Double.parseDouble(Zcoord);

            Location location = new Location(world, xcoord, ycoord, zcoord);

            j.setRespawnLocation(location);

        }
    }

    @EventHandler
    public void clickEvent (InventoryClickEvent l) {
        if (l.getWhoClicked().hasMetadata("guiopen")) {
            l.setCancelled(true);

            if (l.getSlot() == 11) {

                String getType = "Overworld";

                twoItems items = getWorldAndCoordinates(getType, spawn);

                String coords = items.coordinates;
                World worldTwo = items.world;

                //Ensure coordinates are not just empty.
                String newCoords = checkCoordinates(worldTwo, coords, getType, spawn);

                threeItems itemsThree = parseCoordinates(newCoords);

                Location target = getTeleportLocation(worldTwo, itemsThree.x, itemsThree.y, itemsThree.z);

                Player player = (Player) l.getWhoClicked();

                player.closeInventory();
                countdown(target, spawn, player);
            }

            else if (l.getSlot() == 13) {

                String getType = "Nether";

                twoItems items = getWorldAndCoordinates(getType, spawn);

                String coords = items.coordinates;
                World worldTwo = items.world;

                //Ensure coordinates are not just empty.
                String newCoords = checkCoordinates(worldTwo, coords, getType, spawn);

                threeItems itemsThree = parseCoordinates(newCoords);

                Location target = getTeleportLocation(worldTwo, itemsThree.x, itemsThree.y, itemsThree.z);

                Player player = (Player) l.getWhoClicked();

                player.closeInventory();
                countdown(target, spawn, player);
            }

            else if (l.getSlot() == 15) {

                String getType = "End";

                twoItems items = getWorldAndCoordinates(getType, spawn);

                String coords = items.coordinates;
                World worldTwo = items.world;

                //Ensure coordinates are not just empty.
                String newCoords = checkCoordinates(worldTwo, coords, getType, spawn);

                threeItems itemsThree = parseCoordinates(newCoords);

                Location target = getTeleportLocation(worldTwo, itemsThree.x, itemsThree.y, itemsThree.z);

                Player player = (Player) l.getWhoClicked();

                player.closeInventory();
                countdown(target, spawn, player);
            }
        }
    }

    @EventHandler
    public void closeInventoryEvent (InventoryCloseEvent d) {
        HumanEntity firstPlayer = d.getPlayer();
        Player player = (Player) firstPlayer;
        if (player.hasMetadata("guiopen")) {
            player.removeMetadata("guiopen",spawn);
        }
     }


}
