package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import me.hammerle.mp.MundusPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;

public class LuckPermsCommands {
    private static LuckPerms getLuckPerms() {
        return LuckPermsProvider.get();
    }

    private static Group loadGroup(String groupName) {
        LuckPerms luckPerms = getLuckPerms();
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group != null) {
            return group;
        }
        return luckPerms.getGroupManager().loadGroup(groupName).join();
    }

    private static User loadUser(Object player) {
        UUID uuid = CommandUtils.getUUID(player);
        return getLuckPerms().getUserManager().loadUser(uuid).join();
    }

    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("luckPerms.createGroup", (sc, in) -> {
            getLuckPerms().getGroupManager().createAndLoadGroup(in[0].getString(sc)).join();
        });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.deleteGroup", (sc, in) -> {
            Group group = loadGroup(in[0].getString(sc));
            if(group == null) {
                return;
            }
            getLuckPerms().getGroupManager().deleteGroup(group).join();
        });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.deleteAllGroups", (sc, in) -> {
            LuckPerms luckPerms = getLuckPerms();
            for(Group group : new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups())) {
                luckPerms.getGroupManager().deleteGroup(group).join();
            }
        });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.addPermissionToGroup",
                (sc, in) -> {
                    Group group = loadGroup(in[0].getString(sc));
                    if(group == null) {
                        throw new IllegalArgumentException("Unknown group");
                    }
                    group.data().add(PermissionNode.builder(in[1].getString(sc)).value(true).build());
                    getLuckPerms().getGroupManager().saveGroup(group);
                });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.removePermissionFromGroup",
                (sc, in) -> {
                    Group group = loadGroup(in[0].getString(sc));
                    if(group == null) {
                        throw new IllegalArgumentException("Unknown group");
                    }
                    group.data().remove(PermissionNode.builder(in[1].getString(sc)).value(true).build());
                    getLuckPerms().getGroupManager().saveGroup(group);
                });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.clearGroupPermissions",
                (sc, in) -> {
                    Group group = loadGroup(in[0].getString(sc));
                    if(group == null) {
                        throw new IllegalArgumentException("Unknown group");
                    }
                    for(Node node : new ArrayList<>(group.getNodes())) {
                        if(node instanceof PermissionNode) {
                            group.data().remove(node);
                        }
                    }
                    getLuckPerms().getGroupManager().saveGroup(group);
                });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.addPlayerToGroup", (sc, in) -> {
            User user = loadUser(in[0].get(sc));
            user.data().add(InheritanceNode.builder(in[1].getString(sc)).build());
            getLuckPerms().getUserManager().saveUser(user);
        });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.removePlayerFromGroup",
                (sc, in) -> {
                    User user = loadUser(in[0].get(sc));
                    user.data().remove(InheritanceNode.builder(in[1].getString(sc)).build());
                    getLuckPerms().getUserManager().saveUser(user);
                });
        MundusPlugin.scriptManager.registerConsumer("luckPerms.clearPlayerGroups", (sc, in) -> {
            User user = loadUser(in[0].get(sc));
            for(Node node : new ArrayList<>(user.getNodes())) {
                if(node instanceof InheritanceNode) {
                    user.data().remove(node);
                }
            }
            getLuckPerms().getUserManager().saveUser(user);
        });
        MundusPlugin.scriptManager.registerFunction("luckPerms.groupExists",
                (sc, in) -> loadGroup(in[0].getString(sc)) != null);
        MundusPlugin.scriptManager.registerFunction("luckPerms.getGroups", (sc, in) -> {
            LuckPerms luckPerms = getLuckPerms();
            return luckPerms.getGroupManager().getLoadedGroups().stream().map(Group::getName)
                    .collect(Collectors.toCollection(ArrayList::new));
        });
        MundusPlugin.scriptManager.registerFunction("luckPerms.getGroupPermissions",
                (sc, in) -> {
                    Group group = loadGroup(in[0].getString(sc));
                    if(group == null) {
                        throw new IllegalArgumentException("Unknown group");
                    }
                    return group.getNodes().stream().filter(node -> node instanceof PermissionNode)
                            .map(node -> ((PermissionNode) node).getPermission())
                            .collect(Collectors.toCollection(ArrayList::new));
                });
        MundusPlugin.scriptManager.registerFunction("luckPerms.groupHasPermission",
                (sc, in) -> {
                    Group group = loadGroup(in[0].getString(sc));
                    if(group == null) {
                        throw new IllegalArgumentException("Unknown group");
                    }
                    String permission = in[1].getString(sc);
                    return group.getNodes().stream().filter(node -> node instanceof PermissionNode)
                            .map(node -> (PermissionNode) node)
                            .anyMatch(node -> node.getPermission().equals(permission)
                                    && node.getValue());
                });
        MundusPlugin.scriptManager.registerFunction("luckPerms.getPlayerGroups", (sc, in) -> {
            User user = loadUser(in[0].get(sc));
            return user.getNodes().stream().filter(node -> node instanceof InheritanceNode)
                    .map(node -> ((InheritanceNode) node).getGroupName())
                    .collect(Collectors.toCollection(ArrayList::new));
        });
        MundusPlugin.scriptManager.registerFunction("luckPerms.isPlayerInGroup", (sc, in) -> {
            User user = loadUser(in[0].get(sc));
            String group = in[1].getString(sc);
            return user.getNodes().stream().filter(node -> node instanceof InheritanceNode)
                    .map(node -> (InheritanceNode) node)
                    .anyMatch(node -> node.getGroupName().equalsIgnoreCase(group));
        });
        MundusPlugin.scriptManager.registerFunction("luckPerms.getPlayerPermissions",
                (sc, in) -> {
                    User user = loadUser(in[0].get(sc));
                    return user.getNodes().stream().filter(node -> node instanceof PermissionNode)
                            .map(node -> ((PermissionNode) node).getPermission())
                            .collect(Collectors.toCollection(ArrayList::new));
                });
        MundusPlugin.scriptManager.registerFunction("luckPerms.playerHasPermission",
                (sc, in) -> {
                    User user = loadUser(in[0].get(sc));
                    return user.getCachedData().getPermissionData()
                            .checkPermission(in[1].getString(sc)).asBoolean();
                });
    }
}
