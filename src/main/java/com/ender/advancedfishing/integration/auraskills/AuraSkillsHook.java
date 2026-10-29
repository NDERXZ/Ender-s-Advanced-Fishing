package com.ender.advancedfishing.integration.auraskills;

import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.model.CustomFish;

public class AuraSkillsHook {
    private final EndersAdvancedFishing plugin;
    private boolean available = false;

    public AuraSkillsHook(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
        checkAvailability();
    }

    public void checkAvailability() {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("AuraSkills")) {
                Class<?> apiClass = Class.forName("dev.aurelium.auraskills.api.AuraSkillsApi");
                Method getMethod = apiClass.getMethod("get");
                Object apiInstance = getMethod.invoke(null);
                if (apiInstance != null) {
                    available = true;
                    plugin.getLogger().info("AuraSkills detected and hooked successfully!");
                } else {
                    available = false;
                }
            } else {
                available = false;
            }
        } catch (Throwable t) {
            available = false;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isEnabledInConfig() {
        return plugin.getConfig().getBoolean("auraskills.enabled", true);
    }

    /**
     * Add AuraSkills fishing experience to player upon catching a fish.
     *
     * @param player The player who caught the fish
     * @param fish   The fish that was caught
     */
    public void addFishingXp(Player player, CustomFish fish) {
        if (!available || !isEnabledInConfig() || player == null || fish == null) {
            return;
        }

        try {
            double baseXp = plugin.getConfig().getDouble("auraskills.base_xp", 15.0);
            
            // Tier multiplier
            String tierKey = fish.getActualTier() != null ? fish.getActualTier().name() : "COMMON";
            double tierMultiplier = plugin.getConfig().getDouble("auraskills.tier_xp_multipliers." + tierKey, 1.0);
            
            // Weight & quality scaling
            double qualityFactor = Math.max(0.5, fish.getQuality() / 50.0);
            double weightFactor = Math.min(3.0, 1.0 + Math.log10(Math.max(1.0, fish.getWeight())));
            
            double totalXp = (baseXp + (weightFactor * qualityFactor * 2.0)) * tierMultiplier;
            
            giveFishingXp(player.getUniqueId(), totalXp);
        } catch (Throwable t) {
            plugin.getLogger().warning("Failed to add AuraSkills XP: " + t.getMessage());
        }
    }

    /**
     * Add direct AuraSkills fishing experience (e.g. from tournaments).
     */
    public void addXp(Player player, double amount) {
        if (!available || !isEnabledInConfig() || player == null || amount <= 0) {
            return;
        }

        try {
            giveFishingXp(player.getUniqueId(), amount);
        } catch (Throwable ignored) {}
    }

    private void giveFishingXp(UUID uuid, double amount) {
        try {
            Class<?> apiClass = Class.forName("dev.aurelium.auraskills.api.AuraSkillsApi");
            Method getApiMethod = apiClass.getMethod("get");
            Object api = getApiMethod.invoke(null);
            if (api == null) return;

            Method getUserMethod = api.getClass().getMethod("getUser", UUID.class);
            Object user = getUserMethod.invoke(api, uuid);
            if (user == null) return;

            Method isLoadedMethod = user.getClass().getMethod("isLoaded");
            if (!(Boolean) isLoadedMethod.invoke(user)) return;

            Class<?> skillsClass = Class.forName("dev.aurelium.auraskills.api.skill.Skills");
            Object fishingSkill = skillsClass.getField("FISHING").get(null);

            Method isEnabledMethod = fishingSkill.getClass().getMethod("isEnabled");
            if (!(Boolean) isEnabledMethod.invoke(fishingSkill)) return;

            Class<?> skillInterface = Class.forName("dev.aurelium.auraskills.api.skill.Skill");
            Method addSkillXpMethod = user.getClass().getMethod("addSkillXp", skillInterface, double.class);
            addSkillXpMethod.invoke(user, fishingSkill, amount);
        } catch (Throwable ignored) {}
    }
}
