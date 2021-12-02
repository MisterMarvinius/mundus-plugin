/*package me.km.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class Permissions {
    private final static UUID MARVINIUS = UUID.fromString("e41b5335-3c74-46e9-a6c5-dafc6334a477");
    private final static UUID KAJETANJOHANNES = UUID.fromString("51e240f9-ab10-4ea6-8a5d-779319f51257");

    private final HashMap<UUID, ArrayList<Integer>> playerGroups = new HashMap<>();
    private final ArrayList<HashSet<String>> stringGroupPerms = new ArrayList<>();
    private final HashSet<String> otherGroup = new HashSet<>();
    private final boolean debug;

    public Permissions(boolean debug) {
        this.debug = debug;
        addGroups();
    }

    public void clear() {
        playerGroups.clear();
        stringGroupPerms.clear();
        addGroups();
    }

    private void addGroups() {
        stringGroupPerms.add(new HashSet<>());
        stringGroupPerms.add(otherGroup);
    }

    public void addOtherGroupPermission(String perm) {
        otherGroup.add(perm);
    }

    public boolean has(Entity ent, String perm) {
        return has(ent.getUniqueID(), perm);
    }

    public boolean has(UUID uuid, String perm) {
        if(debug) {
            return true;
        }
        if(perm.equals("script") && (uuid.equals(MARVINIUS) || uuid.equals(KAJETANJOHANNES))) {
            return true;
        }
        ArrayList<Integer> groups = playerGroups.get(uuid);
        if(groups == null) {
            if(!stringGroupPerms.isEmpty()) {
                return stringGroupPerms.get(0).contains(perm);
            } else {
                return false;
            }
        }
        return groups.stream().anyMatch(i -> stringGroupPerms.get(i).contains(perm));
    }

    public boolean has(CommandSource cs, String perm) {
        Entity ent = cs.getEntity();
        if(ent != null) {
            return has(ent.getUniqueID(), perm);
        }
        return true;
    }

    public void register(UUID uuid, int groupId) {
        if(groupId < 0 || groupId >= stringGroupPerms.size()) {
            throw new IllegalArgumentException(String.format("%d' is no valid group id", groupId));
        }
        ArrayList<Integer> groups = playerGroups.get(uuid);
        if(groups == null) {
            groups = new ArrayList<>();
            // adding default group
            groups.add(0);
            playerGroups.put(uuid, groups);
        }
        groups.add(groupId);
    }

    public boolean unregister(UUID uuid, int groupId) {
        if(groupId < 0 || groupId >= stringGroupPerms.size()) {
            throw new IllegalArgumentException(String.format("%d' is no valid group id", groupId));
        }
        ArrayList<Integer> groups = playerGroups.get(uuid);
        if(groups == null) {
            return false;
        }
        return groups.remove((Integer) groupId);
    }

    public void register(int groupId, String perm) {
        if(perm.isEmpty()) {
            throw new IllegalArgumentException("empty permission string");
        }
        if(groupId == 1) {
            throw new IllegalArgumentException("id 1 is reserved for worldedit");
        }
        if(groupId >= stringGroupPerms.size()) {
            HashSet<String> set = new HashSet<>();
            set.add(perm);
            stringGroupPerms.add(set);
            return;
        }
        stringGroupPerms.get(groupId).add(perm);
    }
}
*/
