package com.ender.advancedfishing.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.util.TextUtil;

public class CatchEventManager {
    private final EndersAdvancedFishing plugin;

    public CatchEventManager(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
    }

    public void handleCatchEvent(Player player, CustomFish fish, Location location) {
        if (player == null || fish == null) return;
        CustomFish.FishTier tier = fish.getActualTier();
        if (tier == null) tier = fish.getBaseTier();
        if (tier == null) return;

        Location loc = location != null ? location.clone() : player.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        switch (tier) {
            case EPIC -> handleEpicCatch(player, fish, loc, world);
            case LEGENDARY -> handleLegendaryCatch(player, fish, loc, world);
            case MYTHIC -> handleMythicCatch(player, fish, loc, world);
            case SECRET -> handleSecretCatch(player, fish, loc, world);
            default -> {}
        }
    }

    private void handleEpicCatch(Player player, CustomFish fish, Location loc, World world) {
        spawnSafeParticle(world, "FLASH", loc.clone().add(0, 1.2, 0), 1, 0, 0, 0, 0);
        spawnSafeParticle(world, "WATER_SPLASH", loc.clone().add(0, 0.5, 0), 25, 0.5, 0.5, 0.5, 0.1);
        player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.3f);
    }

    private void handleLegendaryCatch(Player player, CustomFish fish, Location loc, World world) {
        spawnSafeParticle(world, "FLASH", loc.clone().add(0, 1.5, 0), 2, 0.1, 0.1, 0.1, 0);
        spawnSafeParticle(world, "FIREWORK", loc.clone().add(0, 1.5, 0), 30, 0.5, 0.8, 0.5, 0.1);
        spawnSafeParticle(world, "DRAGON_BREATH", loc.clone().add(0, 1.0, 0), 25, 0.6, 0.6, 0.6, 0.05);
        player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        String broadcastMsg = TextUtil.oceanic("[ ᴇɴᴅᴇʀ's ғɪsʜɪɴɢ ] ") 
                + TextUtil.color("&f" + player.getName() + " &7ᴛᴇʟᴀʜ ᴍᴇɴᴀɴɢᴋᴀᴘ ɪᴋᴀɴ ʟᴇɢᴇɴᴅᴀʀɪs ") 
                + TextUtil.tierGradient(CustomFish.FishTier.LEGENDARY, fish.getName()) 
                + TextUtil.color(" &8(" + fish.getFormattedWeight() + ")");
        Bukkit.broadcastMessage(broadcastMsg);
    }

    private void handleMythicCatch(Player player, CustomFish fish, Location loc, World world) {
        world.strikeLightningEffect(loc);
        spawnSafeParticle(world, "SONIC_BOOM", loc.clone().add(0, 1.2, 0), 1, 0, 0, 0, 0);
        spawnSafeParticle(world, "SOUL_FIRE_FLAME", loc.clone().add(0, 1.2, 0), 40, 0.7, 1.0, 0.7, 0.05);
        spawnSafeParticle(world, "TOTEM_OF_UNDYING", loc.clone().add(0, 1.5, 0), 50, 0.8, 1.2, 0.8, 0.3);

        player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f, 0.8f);
        player.playSound(loc, Sound.ENTITY_WARDEN_ROAR, 1.0f, 1.1f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 0, false, false));

        String broadcastMsg = TextUtil.oceanic("[ ᴇɴᴅᴇʀ's ғɪsʜɪɴɢ ] ") 
                + TextUtil.color("&f" + player.getName() + " &7ᴛᴇʟᴀʜ ᴍᴇɴᴀɴɢᴋᴀᴘ ɪᴋᴀɴ ᴍɪsᴛɪᴋ ") 
                + TextUtil.tierGradient(CustomFish.FishTier.MYTHIC, fish.getName()) 
                + TextUtil.color(" &8(" + fish.getFormattedWeight() + ")");
        Bukkit.broadcastMessage(broadcastMsg);
    }

    private void handleSecretCatch(Player player, CustomFish fish, Location loc, World world) {
        world.strikeLightningEffect(loc);
        spawnSafeParticle(world, "FLASH", loc.clone().add(0, 1.5, 0), 3, 0.2, 0.2, 0.2, 0);
        spawnSafeParticle(world, "SONIC_BOOM", loc.clone().add(0, 1.2, 0), 2, 0, 0, 0, 0);
        spawnSafeParticle(world, "DRAGON_BREATH", loc.clone().add(0, 1.2, 0), 50, 0.8, 1.0, 0.8, 0.08);
        spawnSafeParticle(world, "TOTEM_OF_UNDYING", loc.clone().add(0, 1.5, 0), 80, 0.9, 1.5, 0.9, 0.4);
        spawnSafeParticle(world, "SCULK_SOUL", loc.clone().add(0, 1.2, 0), 60, 0.8, 1.2, 0.8, 0.08);
        spawnSafeParticle(world, "END_ROD", loc.clone().add(0, 1.8, 0), 40, 0.6, 1.2, 0.6, 0.1);

        player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f, 1.0f);
        player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.85f);
        try {
            player.playSound(loc, Sound.ITEM_TOTEM_USE, 0.9f, 1.0f);
        } catch (Throwable ignored) {}

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 15, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 0, false, false));

        String broadcastMsg = TextUtil.oceanic("[ ᴇɴᴅᴇʀ's ғɪsʜɪɴɢ ] ") 
                + TextUtil.color("&f" + player.getName() + " &7ᴛᴇʟᴀʜ ᴍᴇɴɢᴜᴀsᴀɪ ᴋᴇᴀᴊᴀɪʙᴀɴ sᴀᴍᴜᴅʀᴀ ʀᴀʜᴀsɪᴀ ") 
                + TextUtil.tierGradient(CustomFish.FishTier.SECRET, fish.getName()) 
                + TextUtil.color(" &8(" + fish.getFormattedWeight() + ")");
        Bukkit.broadcastMessage(broadcastMsg);
    }

    private void spawnSafeParticle(World world, String particleName, Location loc, int count, double ox, double oy, double oz, double speed) {
        try {
            Particle particle = Particle.valueOf(particleName);
            world.spawnParticle(particle, loc, count, ox, oy, oz, speed);
        } catch (Throwable ignored) {}
    }
}
