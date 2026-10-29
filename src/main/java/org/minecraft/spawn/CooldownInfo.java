package org.minecraft.spawn;


import org.bukkit.Location;

public class CooldownInfo {
    public Location loc;
    public int teleportId;


    public CooldownInfo(Location loc, int teleportId) {
        this.loc = loc;
        this.teleportId = teleportId;
    }

}
