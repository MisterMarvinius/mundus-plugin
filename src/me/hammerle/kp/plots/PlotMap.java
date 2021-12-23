package me.hammerle.kp.plots;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.hammerle.kp.KajetansPlugin;

public class PlotMap {
    public static final class Position {
        private final int x;
        private final int y;
        private final int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @Override
        public int hashCode() {
            return x + y * 3276671 + z * 1034147;
        }

        @Override
        public boolean equals(Object o) {
            if(o.getClass() != Position.class) {
                return false;
            }
            Position other = (Position) o;
            return x == other.x && y == other.y && z == other.z;
        }
    }

    public static class Plot {
        private Plot previous = null;
        private Plot next = null;

        private final int id;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;

        private final ArrayList<UUID> owners = new ArrayList<>(1);
        private int flags = 0;

        private String name = "";

        public Plot(int id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            minX = Math.min(32000, Math.max(-32000, minX));
            minY = Math.min(32000, Math.max(-32000, minY));
            minZ = Math.min(32000, Math.max(-32000, minZ));
            maxX = Math.min(32000, Math.max(-32000, maxX));
            maxY = Math.min(32000, Math.max(-32000, maxY));
            maxZ = Math.min(32000, Math.max(-32000, maxZ));

            this.id = id;
            if(minX < maxX) {
                this.minX = minX;
                this.maxX = maxX;
            } else {
                this.minX = maxX;
                this.maxX = minX;
            }

            if(minY < maxY) {
                this.minY = minY;
                this.maxY = maxY;
            } else {
                this.minY = maxY;
                this.maxY = minY;
            }

            if(minZ < maxZ) {
                this.minZ = minZ;
                this.maxZ = maxZ;
            } else {
                this.minZ = maxZ;
                this.maxZ = minZ;
            }
        }

        private boolean isInside(int x, int y, int z) {
            return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
        }

        public int getMinX() {
            return minX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMinZ() {
            return minZ;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMaxY() {
            return maxY;
        }

        public int getMaxZ() {
            return maxZ;
        }

        public int getFlags() {
            return flags;
        }

        public boolean hasFlags(int flags) {
            return (flags & this.flags) == flags;
        }

        public void setFlag(int flags, boolean b) {
            if(b) {
                this.flags |= flags;
            } else {
                this.flags &= ~flags;
            }
        }

        public ArrayList<UUID> getOwners() {
            return owners;
        }

        public void setName(String s) {
            name = s;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.format("Plot(%d, %d, %d, %d, %d, %d)", minX, minY, minZ, maxX, maxY,
                    maxZ);
        }
    }

    private static int idCounter = 0;

    private static final int SIZE_FACTOR = 64;
    private final static int[] PRIMES = {17, 37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911,
            43853, 87719, 175447, 350899, 701819, 1403641, 2807303, 5614657, 11229331, 22458671,
            44917381, 89834777, 179669557, 359339171, 718678369};

    private int primeIndex = 0;
    private int size = 0;
    @SuppressWarnings("unchecked")
    private ArrayList<Plot>[] plots = new ArrayList[PRIMES[primeIndex]];
    private Plot last = null;
    private HashSet<Position> interactBlocks = new HashSet<>();
    private volatile boolean shouldSavePlots = false;
    private volatile boolean shouldSaveBlocks = false;

    public PlotMap() {}

    private int hash(int x, int z, int arrayLength) {
        int h = (x + z * 12195263) % arrayLength;
        if(h < 0) {
            return h + arrayLength;
        }
        return h;
    }

    private void rehash() {
        // load factor 0.75
        if(size < (plots.length * 3 / 4) || primeIndex + 1 >= PRIMES.length) {
            return;
        }
        primeIndex++;
        @SuppressWarnings("unchecked")
        ArrayList<Plot>[] newPlots = new ArrayList[PRIMES[primeIndex]];
        Plot p = last;
        while(p != null) {
            addIntern(newPlots, p, newPlots.length);
            p = p.previous;
        }
        plots = newPlots;
    }

    private void addIntern(ArrayList<Plot>[] data, Plot p, int arrayLength) {
        int startX = Math.floorDiv(p.minX, SIZE_FACTOR);
        int startZ = Math.floorDiv(p.minZ, SIZE_FACTOR);
        int endX = Math.floorDiv(p.maxX, SIZE_FACTOR);
        int endZ = Math.floorDiv(p.maxZ, SIZE_FACTOR);

        for(int x = startX; x <= endX; x++) {
            for(int z = startZ; z <= endZ; z++) {
                int hash = hash(x, z, arrayLength);
                if(data[hash] == null) {
                    data[hash] = new ArrayList<>();
                }
                if(!data[hash].contains(p)) {
                    data[hash].add(p);
                }
            }
        }
    }

    public Plot add(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int id) {
        if(id >= idCounter) {
            idCounter = id + 1;
        }

        Plot p = new Plot(id, minX, minY, minZ, maxX, maxY, maxZ);

        if(last == null) {
            last = p;
        } else {
            last.next = p;
            p.previous = last;
            last = p;
        }

        size++;
        rehash();

        addIntern(plots, p, plots.length);
        return p;
    }

    public Plot add(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return add(minX, minY, minZ, maxX, maxY, maxZ, idCounter);
    }

    public List<Plot> getPlotAt(int x, int y, int z) {
        ArrayList<Plot> list = plots[hash(Math.floorDiv(x, SIZE_FACTOR),
                Math.floorDiv(z, SIZE_FACTOR), plots.length)];
        if(list == null) {
            return new ArrayList<>();
        }
        return list.stream().filter(p -> p.isInside(x, y, z)).collect(Collectors.toList());
    }

    public boolean hasPlotAt(int x, int y, int z) {
        ArrayList<Plot> list = plots[hash(Math.floorDiv(x, SIZE_FACTOR),
                Math.floorDiv(z, SIZE_FACTOR), plots.length)];
        if(list == null) {
            return false;
        }
        for(Plot p : list) {
            if(p.isInside(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    public boolean anyPlotMatches(int x, int y, int z, boolean empty, Predicate<Plot> pred) {
        ArrayList<Plot> list = plots[hash(Math.floorDiv(x, SIZE_FACTOR),
                Math.floorDiv(z, SIZE_FACTOR), plots.length)];
        if(list == null) {
            return empty;
        }
        boolean r = empty;
        for(Plot p : list) {
            if(p.isInside(x, y, z)) {
                if(pred.test(p)) {
                    return true;
                }
                r = false;
            }
        }
        return r;
    }

    public void remove(Plot p) {
        if(last == p) {
            last = last.previous;
            if(last != null) {
                last.next = null;
            }
        } else {
            if(p.previous != null) {
                p.previous.next = p.next;
            }
            if(p.next != null) {
                p.next.previous = p.previous;
            }
        }

        int startX = Math.floorDiv(p.minX, SIZE_FACTOR);
        int startZ = Math.floorDiv(p.minZ, SIZE_FACTOR);
        int endX = Math.floorDiv(p.maxX, SIZE_FACTOR);
        int endZ = Math.floorDiv(p.maxZ, SIZE_FACTOR);

        for(int x = startX; x <= endX; x++) {
            for(int z = startZ; z <= endZ; z++) {
                int hash = hash(x, z, plots.length);
                if(plots[hash] == null) {
                    throw new NullPointerException("plot without list at location");
                }
                plots[hash].remove(p);
            }
        }
    }

    public Iterator<Position> getBlockIterator() {
        return interactBlocks.iterator();
    }

    public Iterator<Plot> getIterator() {
        return new Iterator<PlotMap.Plot>() {
            private Plot current = last;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Plot next() {
                Plot p = current;
                current = current.previous;
                return p;
            }
        };
    }

    public Iterator<Plot> getIterator(UUID uuid) {
        return new Iterator<PlotMap.Plot>() {
            private Plot current = last;

            private boolean gotoNext() {
                if(current == null) {
                    return false;
                }
                if(current.getOwners().contains(uuid)) {
                    return true;
                }
                while(current.previous != null) {
                    current = current.previous;
                    if(current.getOwners().contains(uuid)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean hasNext() {
                return gotoNext();
            }

            @Override
            public Plot next() {
                if(!gotoNext()) {
                    throw new IllegalStateException("next without a next element");
                }
                Plot p = current;
                current = current.previous;
                return p;
            }
        };
    }

    public void scheduleSavePlots(String worldName) {
        if(shouldSavePlots) {
            return;
        }
        shouldSavePlots = true;
        KajetansPlugin.scheduleTask(() -> {
            KajetansPlugin.log("Saving plots in '" + worldName + "'");
            savePlots(worldName);
            shouldSavePlots = false;
        }, 20 * 60 * 5);
    }

    private void savePlots(String worldName) {
        File f = new File("plot_storage");
        f.mkdir();
        f = new File("plot_storage/" + worldName);
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
            Iterator<Plot> iter = getIterator();
            while(iter.hasNext()) {
                Plot p = iter.next();

                out.writeInt(p.id);
                out.writeShort(p.minX);
                out.writeShort(p.minY);
                out.writeShort(p.minZ);
                out.writeShort(p.maxX);
                out.writeShort(p.maxY);
                out.writeShort(p.maxZ);
                int owners = Math.min(127, p.owners.size());
                out.writeByte(owners);
                for(int i = 0; i < owners; i++) {
                    out.writeLong(p.owners.get(i).getLeastSignificantBits());
                    out.writeLong(p.owners.get(i).getMostSignificantBits());
                }
                out.writeInt(p.flags);
                out.writeUTF(p.name);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void scheduleSaveBlocks(String worldName) {
        if(shouldSaveBlocks) {
            return;
        }
        shouldSaveBlocks = true;
        KajetansPlugin.scheduleTask(() -> {
            KajetansPlugin.log("Saving blocks in '" + worldName + "'");
            saveBlocks(worldName);
            shouldSaveBlocks = false;
        }, 20 * 60 * 5);
    }

    private void saveBlocks(String worldName) {
        File f = new File("plot_storage");
        f.mkdir();
        f = new File("plot_storage/" + worldName + "_blocks");
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
            for(Position pos : interactBlocks) {
                out.writeInt(pos.x);
                out.writeInt(pos.y);
                out.writeInt(pos.z);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void readInteractionBlocks(File f) {
        if(!f.exists()) {
            return;
        }
        try(DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            while(true) {
                int x = in.readInt();
                int y = in.readInt();
                int z = in.readInt();
                addInteractBlock(x, y, z);
            }
        } catch(EOFException ex) {
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void read(File f) {
        if(!f.exists()) {
            return;
        }
        try(DataInputStream in = new DataInputStream(new FileInputStream(f))) {
            while(true) {
                int id = in.readInt();
                int minX = in.readShort();
                int minY = in.readShort();
                int minZ = in.readShort();
                int maxX = in.readShort();
                int maxY = in.readShort();
                int maxZ = in.readShort();

                Plot p = add(minX, minY, minZ, maxX, maxY, maxZ, id);
                int owners = in.readByte();
                for(int i = 0; i < owners; i++) {
                    long lsb = in.readLong();
                    long msb = in.readLong();
                    p.owners.add(new UUID(msb, lsb));
                }
                p.flags = in.readInt();
                p.name = in.readUTF();
            }
        } catch(EOFException ex) {
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Plot> getIntersectingPlots(int minX, int minY, int minZ, int maxX, int maxY,
            int maxZ) {
        if(minX > maxX) {
            int tmp = minX;
            minX = maxX;
            maxX = tmp;
        }

        if(minY > maxY) {
            int tmp = minY;
            minY = maxY;
            maxY = tmp;
        }

        if(minZ > maxZ) {
            int tmp = minZ;
            minZ = maxZ;
            maxZ = tmp;
        }

        ArrayList<Plot> list = new ArrayList<>();

        Plot p = last;
        while(p != null) {
            if(maxX > p.minX && p.maxX > minX && maxY > p.minY && p.maxY > minY && maxZ > p.minZ
                    && p.maxZ > minZ) {
                list.add(p);
            }
            p = p.previous;
        }

        return list;
    }

    public void addInteractBlock(int x, int y, int z) {
        interactBlocks.add(new Position(x, y, z));
    }

    public void removeInteractBlock(int x, int y, int z) {
        interactBlocks.remove(new Position(x, y, z));
    }

    public boolean hasInteractBlock(int x, int y, int z) {
        return interactBlocks.contains(new Position(x, y, z));
    }
}
