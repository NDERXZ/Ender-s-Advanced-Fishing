package com.ender.advancedfishing.fishing;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.util.TextUtil;

public class SeaMonsterManager implements Listener {
    private final EndersAdvancedFishing plugin;
    private final Random random = new Random();

    public static final String METADATA_KEY = "EAF_SeaMonster";
    private static final double DEFAULT_SPAWN_CHANCE = 0.25; // 25% on Mythic fish

    public SeaMonsterManager(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks whether a sea monster should awaken upon catching a mythic fish.
     */
    public boolean checkAndSpawnBoss(Player player, Location location, CustomFish fish) {
        if (player == null || location == null || fish == null) return false;
        if (fish.getActualTier() != CustomFish.FishTier.MYTHIC) return false;

        if (random.nextDouble() > DEFAULT_SPAWN_CHANCE) {
            return false;
        }

        Location spawnLoc = location.clone().add(0, 0.5, 0);
        if (spawnLoc.getWorld() == null) return false;

        // Check if mob spawning is allowed in region (WorldGuard / RedProtect)
        if (!isMobSpawningAllowed(spawnLoc)) {
            return false;
        }
        // Visual lightning effect (does not ignite fire)
        spawnLoc.getWorld().strikeLightningEffect(spawnLoc);
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.2f, 0.8f);

        LivingEntity boss;
        // Spawn Elder Guardian or Drowned Boss
        if (random.nextBoolean()) {
            boss = spawnLoc.getWorld().spawn(spawnLoc, ElderGuardian.class);
        } else {
            Drowned drowned = spawnLoc.getWorld().spawn(spawnLoc, Drowned.class);
            drowned.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            drowned.getEquipment().setItemInMainHandDropChance(0.5f);
            boss = drowned;
        }

        String bossName = TextUtil.color("&c&l[ ᴋʀᴀᴋᴇɴ sᴘᴀᴡɴ ]");
        boss.setCustomName(bossName);
        boss.setCustomNameVisible(true);
        boss.setGlowing(true);
        boss.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        try {
            Attribute maxHealthAttr = Attribute.valueOf("MAX_HEALTH");
            if (boss.getAttribute(maxHealthAttr) != null) {
                boss.getAttribute(maxHealthAttr).setBaseValue(100.0);
            }
        } catch (Throwable t) {
            try {
                Attribute maxHealthAttr = Attribute.valueOf("GENERIC_MAX_HEALTH");
                if (boss.getAttribute(maxHealthAttr) != null) {
                    boss.getAttribute(maxHealthAttr).setBaseValue(100.0);
                }
            } catch (Throwable ignored) {}
        }
        boss.setHealth(100.0);

        // Send alert to player
        player.sendTitle(
            TextUtil.gradient("ʟᴀᴜᴛᴀɴ ʙᴇʀɢᴏɴᴄᴀɴɢ!", "#EB3349", "#F45C43"),
            TextUtil.color("&cᴍᴏɴsᴛᴇʀ ʟᴀᴜᴛ ᴍᴜɴᴄᴜʟ ᴅᴀʀɪ ᴋᴇᴅᴀʟᴀᴍᴀɴ!"),
            10, 60, 15
        );
        player.sendMessage(TextUtil.gradient("[ ᴍᴏɴsᴛᴇʀ ʟᴀᴜᴛ ]", "#EB3349", "#F45C43") + TextUtil.color(" &7Seekor monster laut purba terbangun dari kedalaman air! Kalahkan untuk memperoleh harta karun samudra."));

        return true;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasMetadata(METADATA_KEY)) return;

        Player killer = entity.getKiller();
        if (killer == null) {
            event.getDrops().clear();
            return;
        }

        // Guaranteed sea treasures on player kill
        event.getDrops().add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
        event.getDrops().add(new ItemStack(Material.PRISMARINE_CRYSTALS, 4));
        event.getDrops().add(new ItemStack(Material.NAUTILUS_SHELL, 2));

        if (killer != null) {
            killer.playSound(killer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            killer.sendMessage(TextUtil.oceanic("[ ᴍᴏɴsᴛᴇʀ ʟᴀᴜᴛ ]") + TextUtil.color(" &aMonster laut berhasil ditaklukkan! Harta karun samudra telah dijatuhkan."));

            // Award economy reward if Vault is enabled
            if (plugin.getEconomyManager() != null && plugin.getEconomyManager().isEconomyEnabled()) {
                plugin.getEconomyManager().depositMoney(killer, 15000.0);
                killer.sendMessage(TextUtil.oceanic("[ ᴍᴏɴsᴛᴇʀ ʟᴀᴜᴛ ]") + TextUtil.color(" &aHadiah uang sebesar &e$15,000 &atelah ditambahkan ke akunmu!"));
            }

            // Award skill XP
            if (plugin.getSkillManager() != null) {
                plugin.getSkillManager().addFishingExp(killer, 500);
            }
        }
    }

    private boolean isMobSpawningAllowed(Location loc) {
        try {
            if (org.bukkit.Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
                Object wgInstance = wgClass.getMethod("getInstance").invoke(null);
                Object platform = wgClass.getMethod("getPlatform").invoke(wgInstance);
                Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
                Object query = container.getClass().getMethod("createQuery").invoke(container);
                Class<?> adapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                Object weLoc = adapterClass.getMethod("adapt", Location.class).invoke(null, loc);
                Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
                Object mobSpawnFlag = flagsClass.getField("MOB_SPAWNING").get(null);
                Object res = query.getClass().getMethod("queryValue", weLoc.getClass(), Class.forName("com.sk89q.worldguard.protection.association.RegionAssociable"), Class.forName("com.sk89q.worldguard.protection.flags.Flag")).invoke(query, weLoc, null, mobSpawnFlag);
                if (Boolean.FALSE.equals(res)) return false;
            }
        } catch (Throwable ignored) {}
        return true;
    }
}
