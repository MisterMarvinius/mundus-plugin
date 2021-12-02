package me.hammerle.kp.utils;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationIterator implements Iterator<Location> {
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private final Location current;
    private int x;
    private int y;
    private int z;

    public LocationIterator(World world, int minX, int minY, int minZ, int maxX, int maxY,
            int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        current = new Location(world, minX, minY, minZ, 0, 0);
        x = minX;
        y = minY;
        z = minZ;
    }

    @Override
    public boolean hasNext() {
        return z <= maxZ;
    }

    @Override
    public Location next() {
        current.set(x, y, z);
        x++;
        if(x > maxX) {
            x = minX;
            y++;
            if(y > maxY) {
                y = minY;
                z++;
            }
        }
        return current;
    }
}
