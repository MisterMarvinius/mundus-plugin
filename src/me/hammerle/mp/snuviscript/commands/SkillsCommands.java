package me.hammerle.mp.snuviscript.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class SkillsCommands {
    private static final String[] API_CLASS_NAMES = {
            "com.archyx.auraskills.api.AuraSkills",
            "com.archyx.auraskills.api.AuraSkillsApi",
            "dev.aurelium.auraskills.api.AuraSkillsApi",
            "dev.aurelium.auraskills.api.AuraSkills",
            "com.auraskills.api.AuraSkillsApi",
            "com.auraskills.api.AuraSkills"
    };

    private static final String[] CUSTOM_SKILL_CLASS_NAMES = {
            "com.archyx.auraskills.api.skill.CustomSkill",
            "dev.aurelium.auraskills.api.skill.CustomSkill",
            "com.auraskills.api.skill.CustomSkill"
    };

    private static final String[] SKILL_CLASS_NAMES = {
            "com.archyx.auraskills.api.skill.Skill",
            "dev.aurelium.auraskills.api.skill.Skill",
            "com.auraskills.api.skill.Skill"
    };

    private static final String[] REGISTRY_METHOD_NAMES = {
            "getSkillRegistry",
            "getSkills",
            "getSkillManager"
    };

    private static final String[] REGISTER_METHOD_NAMES = {
            "registerCustomSkill",
            "registerSkill",
            "register",
            "addCustomSkill",
            "addSkill"
    };

    private static final String[] GET_SKILL_METHOD_NAMES = {
            "getSkill",
            "getSkillByName",
            "getSkillByKey"
    };

    private static final String[] GET_USER_MANAGER_METHOD_NAMES = {
            "getUserManager",
            "getPlayerManager",
            "getUserRegistry"
    };

    private static final String[] GET_USER_METHOD_NAMES = {
            "getUser",
            "getPlayer",
            "getUserData"
    };

    private static final String[] GET_LEVEL_METHOD_NAMES = {
            "getSkillLevel",
            "getLevel"
    };

    private static final String[] SET_LEVEL_METHOD_NAMES = {
            "setSkillLevel",
            "setLevel"
    };

    private static final String[] GET_XP_METHOD_NAMES = {
            "getSkillXp",
            "getXp",
            "getExperience"
    };

    private static final String[] SET_XP_METHOD_NAMES = {
            "setSkillXp",
            "setXp",
            "setExperience"
    };

    private static final String[] ADD_XP_METHOD_NAMES = {
            "addSkillXp",
            "addXp",
            "addExperience"
    };

    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("skills.custom.add", (sc, in) -> {
            String key = in[0].getString(sc);
            Object nameValue = in.length > 1 ? in[1].get(sc) : key;
            String displayName = nameValue instanceof Component
                    ? componentToString((Component) nameValue)
                    : Objects.toString(nameValue, key);
            Component displayComponent = nameValue instanceof Component
                    ? (Component) nameValue
                    : Component.text(displayName);
            String iconName = in.length > 2 ? in[2].getString(sc) : null;
            List<String> description = in.length > 3 ? toStringList(in[3].get(sc)) : null;
            List<Component> descriptionComponents = toComponentList(description);
            Integer maxLevel = in.length > 4 ? in[4].getInt(sc) : null;
            registerCustomSkill(key, displayName, displayComponent, iconName, description,
                    descriptionComponents, maxLevel);
        });
        MundusPlugin.scriptManager.registerFunction("skills.exists", (sc, in) -> {
            return resolveSkill(getApi(), in[0].getString(sc)) != null;
        });
        MundusPlugin.scriptManager.registerFunction("skills.getlevel", (sc, in) -> {
            Object api = getApi();
            Object player = in[0].get(sc);
            String skill = in[1].getString(sc);
            Object result = invokeFirstMatching(api, GET_LEVEL_METHOD_NAMES, player, skill);
            if(result != null) {
                return toDouble(result);
            }
            UUID uuid = CommandUtils.getUUID(player);
            result = invokeFirstMatching(api, GET_LEVEL_METHOD_NAMES, uuid, skill);
            if(result != null) {
                return toDouble(result);
            }
            Object user = resolveUser(api, player);
            Object skillObj = resolveSkill(api, skill);
            if(user == null || skillObj == null) {
                return 0d;
            }
            result = invokeFirstMatching(user, GET_LEVEL_METHOD_NAMES, skillObj);
            if(result == null) {
                result = invokeFirstMatching(user, GET_LEVEL_METHOD_NAMES, skill);
            }
            return toDouble(result);
        });
        MundusPlugin.scriptManager.registerConsumer("skills.setlevel", (sc, in) -> {
            Object api = getApi();
            Object player = in[0].get(sc);
            String skill = in[1].getString(sc);
            int level = in[2].getInt(sc);
            if(invokeFirstMatching(api, SET_LEVEL_METHOD_NAMES, player, skill, level) != null) {
                return;
            }
            UUID uuid = CommandUtils.getUUID(player);
            if(invokeFirstMatching(api, SET_LEVEL_METHOD_NAMES, uuid, skill, level) != null) {
                return;
            }
            Object user = resolveUser(api, player);
            Object skillObj = resolveSkill(api, skill);
            if(user == null || skillObj == null) {
                throw new IllegalStateException("AuraSkills user or skill not found");
            }
            if(invokeFirstMatching(user, SET_LEVEL_METHOD_NAMES, skillObj, level) != null) {
                return;
            }
            if(invokeFirstMatching(user, SET_LEVEL_METHOD_NAMES, skill, level) != null) {
                return;
            }
            throw new IllegalStateException("AuraSkills could not set skill level");
        });
        MundusPlugin.scriptManager.registerFunction("skills.getxp", (sc, in) -> {
            Object api = getApi();
            Object player = in[0].get(sc);
            String skill = in[1].getString(sc);
            Object result = invokeFirstMatching(api, GET_XP_METHOD_NAMES, player, skill);
            if(result != null) {
                return toDouble(result);
            }
            UUID uuid = CommandUtils.getUUID(player);
            result = invokeFirstMatching(api, GET_XP_METHOD_NAMES, uuid, skill);
            if(result != null) {
                return toDouble(result);
            }
            Object user = resolveUser(api, player);
            Object skillObj = resolveSkill(api, skill);
            if(user == null || skillObj == null) {
                return 0d;
            }
            result = invokeFirstMatching(user, GET_XP_METHOD_NAMES, skillObj);
            if(result == null) {
                result = invokeFirstMatching(user, GET_XP_METHOD_NAMES, skill);
            }
            return toDouble(result);
        });
        MundusPlugin.scriptManager.registerConsumer("skills.setxp", (sc, in) -> {
            Object api = getApi();
            Object player = in[0].get(sc);
            String skill = in[1].getString(sc);
            double amount = in[2].getDouble(sc);
            if(invokeFirstMatching(api, SET_XP_METHOD_NAMES, player, skill, amount) != null) {
                return;
            }
            UUID uuid = CommandUtils.getUUID(player);
            if(invokeFirstMatching(api, SET_XP_METHOD_NAMES, uuid, skill, amount) != null) {
                return;
            }
            Object user = resolveUser(api, player);
            Object skillObj = resolveSkill(api, skill);
            if(user == null || skillObj == null) {
                throw new IllegalStateException("AuraSkills user or skill not found");
            }
            if(invokeFirstMatching(user, SET_XP_METHOD_NAMES, skillObj, amount) != null) {
                return;
            }
            if(invokeFirstMatching(user, SET_XP_METHOD_NAMES, skill, amount) != null) {
                return;
            }
            throw new IllegalStateException("AuraSkills could not set skill xp");
        });
        MundusPlugin.scriptManager.registerConsumer("skills.addxp", (sc, in) -> {
            Object api = getApi();
            Object player = in[0].get(sc);
            String skill = in[1].getString(sc);
            double amount = in[2].getDouble(sc);
            if(invokeFirstMatching(api, ADD_XP_METHOD_NAMES, player, skill, amount) != null) {
                return;
            }
            UUID uuid = CommandUtils.getUUID(player);
            if(invokeFirstMatching(api, ADD_XP_METHOD_NAMES, uuid, skill, amount) != null) {
                return;
            }
            Object user = resolveUser(api, player);
            Object skillObj = resolveSkill(api, skill);
            if(user == null || skillObj == null) {
                throw new IllegalStateException("AuraSkills user or skill not found");
            }
            if(invokeFirstMatching(user, ADD_XP_METHOD_NAMES, skillObj, amount) != null) {
                return;
            }
            if(invokeFirstMatching(user, ADD_XP_METHOD_NAMES, skill, amount) != null) {
                return;
            }
            throw new IllegalStateException("AuraSkills could not add skill xp");
        });
    }

    private static Object getApi() {
        for(String className : API_CLASS_NAMES) {
            try {
                Class<?> apiClass = Class.forName(className);
                Object api = invokeStatic(apiClass, "get", "getInstance", "getApi", "getAPI");
                if(api != null) {
                    return api;
                }
                RegisteredServiceProvider<?> provider =
                        Bukkit.getServicesManager().getRegistration(apiClass);
                if(provider != null && provider.getProvider() != null) {
                    return provider.getProvider();
                }
            } catch(ClassNotFoundException ex) {
                // ignore
            }
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AuraSkills");
        if(plugin != null) {
            Object api = invokeFirstMatching(plugin, new String[] {"getApi", "getAPI", "getAuraSkillsApi",
                    "getAuraSkillsAPI"});
            if(api != null) {
                return api;
            }
        }
        throw new IllegalStateException("AuraSkills API not available");
    }

    private static void registerCustomSkill(String key, String displayName, Component displayComponent,
            String iconName, List<String> description, List<Component> descriptionComponents,
            Integer maxLevel) {
        Object api = getApi();
        Object registry = resolveRegistry(api);
        Object icon = iconName != null ? Material.matchMaterial(iconName) : null;
        Object skill = createCustomSkill(key, displayName, displayComponent, icon, description,
                descriptionComponents, maxLevel);
        if(skill != null && invokeFirstMatching(registry, REGISTER_METHOD_NAMES, skill) != null) {
            return;
        }
        List<Object[]> candidates = new ArrayList<>();
        candidates.add(new Object[] {key, displayName});
        candidates.add(new Object[] {key, displayComponent});
        if(icon != null) {
            candidates.add(new Object[] {key, displayName, icon});
            candidates.add(new Object[] {key, displayComponent, icon});
        }
        if(description != null) {
            candidates.add(new Object[] {key, displayName, description});
            candidates.add(new Object[] {key, displayComponent, description});
        }
        if(descriptionComponents != null) {
            candidates.add(new Object[] {key, displayName, descriptionComponents});
            candidates.add(new Object[] {key, displayComponent, descriptionComponents});
        }
        if(icon != null && description != null) {
            candidates.add(new Object[] {key, displayName, icon, description});
            candidates.add(new Object[] {key, displayComponent, icon, description});
        }
        if(icon != null && descriptionComponents != null) {
            candidates.add(new Object[] {key, displayName, icon, descriptionComponents});
            candidates.add(new Object[] {key, displayComponent, icon, descriptionComponents});
        }
        if(maxLevel != null) {
            candidates.add(new Object[] {key, displayName, maxLevel});
            candidates.add(new Object[] {key, displayComponent, maxLevel});
            if(icon != null) {
                candidates.add(new Object[] {key, displayName, icon, maxLevel});
                candidates.add(new Object[] {key, displayComponent, icon, maxLevel});
            }
            if(icon != null && description != null) {
                candidates.add(new Object[] {key, displayName, icon, description, maxLevel});
                candidates.add(new Object[] {key, displayComponent, icon, description, maxLevel});
            }
            if(icon != null && descriptionComponents != null) {
                candidates.add(
                        new Object[] {key, displayName, icon, descriptionComponents, maxLevel});
                candidates.add(
                        new Object[] {key, displayComponent, icon, descriptionComponents, maxLevel});
            }
        }
        for(Object[] args : candidates) {
            if(invokeFirstMatching(registry, REGISTER_METHOD_NAMES, args) != null) {
                return;
            }
        }
        throw new IllegalStateException("AuraSkills custom skill registration failed");
    }

    private static Object resolveRegistry(Object api) {
        Object registry = invokeFirstMatching(api, REGISTRY_METHOD_NAMES);
        return registry != null ? registry : api;
    }

    private static Object createCustomSkill(String key, String displayName, Component displayComponent,
            Object icon, List<String> description, List<Component> descriptionComponents,
            Integer maxLevel) {
        Class<?> customSkillClass = loadClass(CUSTOM_SKILL_CLASS_NAMES);
        if(customSkillClass == null) {
            return null;
        }
        for(Constructor<?> ctor : customSkillClass.getConstructors()) {
            Object[] stringArgs = buildArgsForConstructor(ctor.getParameterTypes(), key, displayName,
                    displayComponent, icon, description, maxLevel);
            if(stringArgs != null) {
                try {
                    return ctor.newInstance(stringArgs);
                } catch(Exception ex) {
                    // ignore and try next
                }
            }
            Object[] componentArgs =
                    buildArgsForConstructor(ctor.getParameterTypes(), key, displayName,
                            displayComponent, icon, descriptionComponents, maxLevel);
            if(componentArgs != null) {
                try {
                    return ctor.newInstance(componentArgs);
                } catch(Exception ex) {
                    // ignore and try next
                }
            }
        }
        return null;
    }

    private static Object resolveUser(Object api, Object player) {
        Object user = invokeFirstMatching(api, GET_USER_METHOD_NAMES, player);
        if(user != null) {
            return user;
        }
        UUID uuid = CommandUtils.getUUID(player);
        user = invokeFirstMatching(api, GET_USER_METHOD_NAMES, uuid);
        if(user != null) {
            return user;
        }
        Object manager = invokeFirstMatching(api, GET_USER_MANAGER_METHOD_NAMES);
        if(manager == null) {
            return null;
        }
        user = invokeFirstMatching(manager, GET_USER_METHOD_NAMES, player);
        if(user != null) {
            return user;
        }
        return invokeFirstMatching(manager, GET_USER_METHOD_NAMES, uuid);
    }

    private static Object resolveSkill(Object api, String skillName) {
        Object registry = resolveRegistry(api);
        Object skill = invokeFirstMatching(registry, GET_SKILL_METHOD_NAMES, skillName);
        if(skill != null) {
            return skill;
        }
        Object userSkill = invokeFirstMatching(api, GET_SKILL_METHOD_NAMES, skillName);
        if(userSkill != null) {
            return userSkill;
        }
        Class<?> skillClass = loadClass(SKILL_CLASS_NAMES);
        if(skillClass != null && skillClass.isEnum()) {
            Object[] constants = skillClass.getEnumConstants();
            if(constants != null) {
                for(Object constant : constants) {
                    if(constant != null && constant.toString().equalsIgnoreCase(skillName)) {
                        return constant;
                    }
                }
            }
        }
        return null;
    }

    private static Object invokeStatic(Class<?> clazz, String... names) {
        for(String name : names) {
            try {
                Method method = clazz.getMethod(name);
                return method.invoke(null);
            } catch(Exception ex) {
                // ignore
            }
        }
        return null;
    }

    private static Object invokeFirstMatching(Object target, String[] names, Object... args) {
        if(target == null) {
            return null;
        }
        List<String> nameList = Arrays.asList(names);
        for(Method method : target.getClass().getMethods()) {
            if(!nameList.contains(method.getName())) {
                continue;
            }
            Object[] resolved = resolveArguments(method.getParameterTypes(), args);
            if(resolved == null) {
                continue;
            }
            try {
                return method.invoke(target, resolved);
            } catch(Exception ex) {
                // ignore and try another
            }
        }
        return null;
    }

    private static Object[] resolveArguments(Class<?>[] parameterTypes, Object[] args) {
        if(parameterTypes.length != args.length) {
            return null;
        }
        Object[] resolved = new Object[args.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            Object value = coerceValue(parameterTypes[i], args[i]);
            if(value == null && parameterTypes[i].isPrimitive()) {
                return null;
            }
            if(value == null && args[i] != null) {
                return null;
            }
            resolved[i] = value;
        }
        return resolved;
    }

    private static Object coerceValue(Class<?> type, Object value) {
        if(value == null) {
            return null;
        }
        if(type.isInstance(value)) {
            return value;
        }
        if(type == double.class || type == Double.class) {
            return value instanceof Number ? ((Number) value).doubleValue() : null;
        }
        if(type == int.class || type == Integer.class) {
            return value instanceof Number ? ((Number) value).intValue() : null;
        }
        if(type == float.class || type == Float.class) {
            return value instanceof Number ? ((Number) value).floatValue() : null;
        }
        if(type == long.class || type == Long.class) {
            return value instanceof Number ? ((Number) value).longValue() : null;
        }
        if(type == String.class) {
            return value.toString();
        }
        if(type == NamespacedKey.class && value instanceof String) {
            return toNamespacedKey((String) value);
        }
        if(type == Key.class && value instanceof String) {
            return toAdventureKey((String) value);
        }
        if(type == Component.class && value instanceof String) {
            return Component.text((String) value);
        }
        if(type == UUID.class && value instanceof Player) {
            return ((Player) value).getUniqueId();
        }
        if(type == Material.class && value instanceof String) {
            return Material.matchMaterial((String) value);
        }
        return null;
    }

    private static Object[] buildArgsForConstructor(Class<?>[] parameterTypes, String key,
            String displayName, Component displayComponent, Object icon, List<?> description,
            Integer maxLevel) {
        Object[] args = new Object[parameterTypes.length];
        int stringCount = 0;
        for(int i = 0; i < parameterTypes.length; i++) {
            Class<?> param = parameterTypes[i];
            Object value = null;
            if(param == String.class) {
                value = stringCount == 0 ? key : displayName;
                stringCount++;
            } else if(param == NamespacedKey.class) {
                value = toNamespacedKey(key);
            } else if(param == Key.class) {
                value = toAdventureKey(key);
            } else if(param == Component.class) {
                value = displayComponent;
            } else if(param.isInstance(icon)) {
                value = icon;
            } else if(description != null && param.isAssignableFrom(description.getClass())) {
                value = description;
            } else if(description != null && param.isAssignableFrom(List.class)) {
                value = description;
            } else if(maxLevel != null && (param == int.class || param == Integer.class)) {
                value = maxLevel;
            }
            if(value == null && param.isPrimitive()) {
                return null;
            }
            if(value == null && !param.isPrimitive()) {
                return null;
            }
            args[i] = value;
        }
        return args;
    }

    private static Class<?> loadClass(String[] classNames) {
        for(String name : classNames) {
            try {
                return Class.forName(name);
            } catch(ClassNotFoundException ex) {
                // ignore
            }
        }
        return null;
    }

    private static List<String> toStringList(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof List) {
            List<?> list = (List<?>) value;
            List<String> result = new ArrayList<>();
            for(Object item : list) {
                result.add(Objects.toString(item));
            }
            return result;
        }
        if(value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            List<String> result = new ArrayList<>();
            for(Object item : collection) {
                result.add(Objects.toString(item));
            }
            return result;
        }
        if(value instanceof Object[]) {
            Object[] array = (Object[]) value;
            List<String> result = new ArrayList<>();
            for(Object item : array) {
                result.add(Objects.toString(item));
            }
            return result;
        }
        return new ArrayList<>(List.of(Objects.toString(value)));
    }

    private static List<Component> toComponentList(List<String> values) {
        if(values == null) {
            return null;
        }
        List<Component> components = new ArrayList<>();
        for(String value : values) {
            components.add(Component.text(value));
        }
        return components;
    }

    private static double toDouble(Object value) {
        if(value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if(value == null) {
            return 0d;
        }
        return Double.parseDouble(value.toString());
    }

    private static String componentToString(Component component) {
        try {
            Class<?> serializerClass = Class.forName(
                    "net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer");
            Method plainTextMethod = serializerClass.getMethod("plainText");
            Object serializer = plainTextMethod.invoke(null);
            Method serializeMethod = serializerClass.getMethod("serialize", Component.class);
            Object result = serializeMethod.invoke(serializer, component);
            if(result != null) {
                return result.toString();
            }
        } catch(Exception ex) {
            // ignore and fallback
        }
        return component.toString();
    }

    private static NamespacedKey toNamespacedKey(String value) {
        if(value == null) {
            return null;
        }
        NamespacedKey key = NamespacedKey.fromString(value);
        if(key != null) {
            return key;
        }
        if(MundusPlugin.instance != null) {
            return new NamespacedKey(MundusPlugin.instance, value.toLowerCase());
        }
        return NamespacedKey.fromString("mundus:" + value.toLowerCase());
    }

    private static Key toAdventureKey(String value) {
        if(value == null) {
            return null;
        }
        try {
            return Key.key(value);
        } catch(IllegalArgumentException ex) {
            String namespace = MundusPlugin.instance != null
                    ? MundusPlugin.instance.getName().toLowerCase()
                    : "mundus";
            return Key.key(namespace, value.toLowerCase());
        }
    }
}
