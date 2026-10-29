package com.ender.advancedfishing.fishing.minigame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.FishingListener;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.fishing.model.FishingRod;
import com.ender.advancedfishing.util.TextUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class StrikeMinigame {
    private final EndersAdvancedFishing plugin;
    private final Map<UUID, BukkitTask> activeTasks;
    private final Map<UUID, CustomFish> pendingFish;
    private final Map<UUID, ItemStack> pendingRods;
    private final Map<UUID, Integer> pendingDurabilityLoss;
    
    // Minigame state data
    private final Map<UUID, Integer> requiredClicks;
    private final Map<UUID, Integer> currentClicks;
    private final Map<UUID, Long> lastClickTimes;
    
    private final Random random = new Random();
    
    private static final int MAX_TIME_SECONDS = 10;
    
    public StrikeMinigame(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
        this.activeTasks = new HashMap<>();
        this.pendingFish = new HashMap<>();
        this.pendingRods = new HashMap<>();
        this.pendingDurabilityLoss = new HashMap<>();
        this.requiredClicks = new HashMap<>();
        this.currentClicks = new HashMap<>();
        this.lastClickTimes = new HashMap<>();
    }
    
    /**
     * Start a new clicking minigame for player
     */
    public void startStrikeGame(Player player, CustomFish fish, ItemStack fishingRod, int durabilityLoss) {
        UUID playerId = player.getUniqueId();
        
        // Cancel existing game if any
        cancelActiveGame(player);
        
        // Store the fish and rod that will be used
        pendingFish.put(playerId, fish);
        pendingRods.put(playerId, fishingRod);
        pendingDurabilityLoss.put(playerId, durabilityLoss);
        
        // Calculate number of clicks based on fish weight
        int baseClicks = 5;
        double weightFactor = Math.sqrt(fish.getWeight()) / 2.5;
        int clicks = baseClicks + (int) Math.ceil(weightFactor);
        
        // Clicks between 5 and 30
        clicks = Math.max(5, Math.min(clicks, 30));
        
        requiredClicks.put(playerId, clicks);
        currentClicks.put(playerId, 0);
        
        // Sound and alert
        player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1.0f, 1.0f);
        
        player.sendTitle(
            TextUtil.oceanic("ғɪsʜ ʙɪᴛɪɴɢ!"), 
            TextUtil.color("&7ʀᴇᴇʟ ʟɪɴᴇ: &8[ &b0 &7/ &e" + clicks + " &8] &8| &f" + clicks + " ᴄʟɪᴄᴋs ʟᴇғᴛ"), 
            5, 30, 5
        );
        sendActionBarMessage(player, TextUtil.oceanic("« ʀᴇᴇʟ ʟɪɴᴇ (ᴄʟɪᴄᴋ): " + clicks + " ʟᴇғᴛ »"));
        
        updateProgressDisplay(player, MAX_TIME_SECONDS);
        
        // Start countdown timer
        BukkitTask task = new BukkitRunnable() {
            private int secondsLeft = MAX_TIME_SECONDS;
            
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    endMinigame(player, false);
                    return;
                }
                
                updateProgressDisplay(player, secondsLeft);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
        
        activeTasks.put(playerId, task);
    }
    
    /**
     * Handle player click (left-click or Bedrock reel/tap) to progress the minigame
     */
    public boolean handleClick(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!isPlayerInMinigame(playerId)) {
            return false;
        }

        long now = System.currentTimeMillis();
        long lastClick = lastClickTimes.getOrDefault(playerId, 0L);
        if (now - lastClick < 80L) {
            return true; // Debounce duplicate events in same tick
        }
        lastClickTimes.put(playerId, now);
        
        int currentClickCount = currentClicks.getOrDefault(playerId, 0) + 1;
        int requiredClicksCount = requiredClicks.getOrDefault(playerId, 10);
        
        if (currentClickCount >= requiredClicksCount) {
            currentClicks.put(playerId, currentClickCount);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            endMinigame(player, true);
            return true;
        }

        currentClicks.put(playerId, currentClickCount);
        int remaining = Math.max(0, requiredClicksCount - currentClickCount);
        
        player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_STEP, 0.6f, 1.2f);
        sendActionBarMessage(player, TextUtil.oceanic("« ʀᴇᴇʟ ʟɪɴᴇ (ᴄʟɪᴄᴋ): " + remaining + " ʟᴇғᴛ »"));
        updateProgressDisplay(player, 10);
        return true;
    }
    
    /**
     * End the minigame with result
     */
    private void endMinigame(Player player, boolean success) {
        UUID playerId = player.getUniqueId();
        
        cancelActiveGame(player);

        CustomFish fish = pendingFish.remove(playerId);
        ItemStack fishingRod = pendingRods.remove(playerId);
        Integer durabilityLoss = pendingDurabilityLoss.remove(playerId);
        
        requiredClicks.remove(playerId);
        currentClicks.remove(playerId);
        
        player.removeMetadata("EAF_InMinigame", plugin);
        removePlayerFishingHook(player);
        
        if (success && fish != null) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(fish.createItemStack());
            if (!leftover.isEmpty()) {
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
                player.sendMessage(TextUtil.color("&e[ ɪɴᴠᴇɴᴛᴏʀʏ ] &fInventory penuh, ikan dijatuhkan ke tanah."));
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            
            // Record statistics
            if (plugin.getStatisticsManager() != null) {
                plugin.getStatisticsManager().recordFishCaught(player.getUniqueId(), fish);
            }

            // Record in Fish Journal
            if (plugin.getJournalManager() != null) {
                plugin.getJournalManager().recordCatch(player, fish);
            }
            
            // Event fish catch
            if (fish.getId().startsWith("event_") && plugin.getEventManager() != null) {
                plugin.getEventManager().handleEventFishCaught(player, fish);
            }

            // Tournament catch
            if (plugin.getTournamentManager() != null && plugin.getTournamentManager().isTournamentActive()) {
                plugin.getTournamentManager().recordCatch(player, fish);
            }

            // Trigger Sea Monster Boss encounter if eligible
            if (plugin.getSeaMonsterManager() != null) {
                plugin.getSeaMonsterManager().checkAndSpawnBoss(player, player.getLocation(), fish);
            }

            // Trigger Catch Event Visuals & SFX
            if (plugin.getCatchEventManager() != null) {
                plugin.getCatchEventManager().handleCatchEvent(player, fish, player.getLocation());
            }
            
            // Display victory title in sleek tier gradient
            player.sendTitle(
                TextUtil.tierGradient(fish.getActualTier(), "ʙᴇʀʜᴀsɪʟ ᴅɪᴛᴀɴɢᴋᴀᴘ!"),
                TextUtil.color("&f" + fish.getName() + " &8| &b" + fish.getFormattedWeight()), 
                10, 50, 10
            );
            
            // Fishing XP
            int xpGain = (int)(5 + (fish.getWeight() * fish.getQuality() / 20));
            if (plugin.getSkillManager() != null) {
                plugin.getSkillManager().addFishingExp(player, xpGain);
            }

            if (plugin.getAuraSkillsHook() != null) {
                plugin.getAuraSkillsHook().addFishingXp(player, fish);
            }
            
            // Rod durability
            FishingRod customRod = null;
            if (fishingRod != null) {
                ItemMeta meta = fishingRod.getItemMeta();
                if (meta != null) {
                    NamespacedKey cleanRodKey = NamespacedKey.fromString("endersfishing:rod_id");
                    NamespacedKey dottedRodKey = NamespacedKey.fromString("endersfishing:endersfishing.rod_id");
                    String rodId = null;
                    if (cleanRodKey != null && meta.getPersistentDataContainer().has(cleanRodKey, PersistentDataType.STRING)) {
                        rodId = meta.getPersistentDataContainer().get(cleanRodKey, PersistentDataType.STRING);
                    } else if (dottedRodKey != null && meta.getPersistentDataContainer().has(dottedRodKey, PersistentDataType.STRING)) {
                        rodId = meta.getPersistentDataContainer().get(dottedRodKey, PersistentDataType.STRING);
                    }
                    if (rodId != null && plugin.getFishingManager() != null) {
                        customRod = plugin.getFishingManager().getRodById(rodId);
                    }
                }
                if (durabilityLoss != null) {
                    FishingListener.reduceDurability(player, fishingRod, durabilityLoss);
                }
            }

            if (plugin.getStaminaManager() != null) {
                plugin.getStaminaManager().consumeStaminaForFish(player, fish, customRod);
            }
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            player.sendTitle(
                TextUtil.color("&c[ ғɪsʜ ᴇsᴄᴀᴘᴇᴅ ]"), 
                TextUtil.color("&7ʟɪɴᴇ sɴᴀᴘᴘᴇᴅ ᴏʀ ᴛɪᴍᴇ ᴇxᴘɪʀᴇᴅ"), 
                10, 40, 10
            );
            
            if (fishingRod != null) {
                FishingListener.reduceDurability(player, fishingRod, 1);
            }
        }
    }
    
    private void removePlayerFishingHook(Player player) {
        if (player == null) return;
        try {
            org.bukkit.entity.FishHook hook = player.getFishHook();
            if (hook != null && hook.isValid()) {
                hook.remove();
                return;
            }
        } catch (Throwable ignored) {}
        
        player.getWorld().getEntitiesByClass(org.bukkit.entity.FishHook.class).stream()
            .filter(hook -> hook.getShooter() instanceof Player)
            .filter(hook -> ((Player) hook.getShooter()).getUniqueId().equals(player.getUniqueId()))
            .forEach(org.bukkit.entity.Entity::remove);
    }
    
    public void endMinigameAbort(Player player) {
        endMinigame(player, false);
        player.playSound(player.getLocation(), Sound.ENTITY_SALMON_FLOP, 1.0f, 0.5f);
        player.sendTitle(
            TextUtil.color("&c[ ғɪsʜ ᴇsᴄᴀᴘᴇᴅ ]"), 
            TextUtil.color("&7ᴋᴀᴍᴜ ᴍᴇᴍʙᴜᴋᴀ ɪɴᴠᴇɴᴛᴏʀʏ sᴀᴀᴛ ᴍᴇɴᴀʀɪᴋ sᴇɴᴀʀ!"), 
            10, 50, 10
        );
        removePlayerFishingHook(player);
    }
    
    private void updateProgressDisplay(Player player, int secondsLeft) {
        UUID playerId = player.getUniqueId();
        int current = currentClicks.getOrDefault(playerId, 0);
        int required = requiredClicks.getOrDefault(playerId, 10);
        int remaining = Math.max(0, required - current);
        
        String progressTitle = TextUtil.gradient("[ " + current + " / " + required + " ]", "#0052D4", "#6FB1FC");
        String progressSubtitle = TextUtil.color("&7ᴛᴇʀsɪsᴀ &f" + remaining + " ᴋʟɪᴋ &8| " + buildProgressBar(current, required) + " &8| &b" + secondsLeft + "s");
        
        player.sendTitle(progressTitle, progressSubtitle, 0, 15, 5);
    }
    
    private String buildProgressBar(int current, int required) {
        StringBuilder progressBar = new StringBuilder();
        int progressLength = 12;
        int filledChars = (int) Math.floor((double) current / Math.max(1, required) * progressLength);
        
        for (int i = 0; i < progressLength; i++) {
            if (i < filledChars) {
                progressBar.append("§b|");
            } else {
                progressBar.append("§8|");
            }
        }
        return progressBar.toString();
    }
    
    private void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    
    private void cancelActiveGame(Player player) {
        UUID playerId = player.getUniqueId();
        lastClickTimes.remove(playerId);
        BukkitTask task = activeTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }
    
    private boolean isPlayerInMinigame(UUID playerId) {
        return activeTasks.containsKey(playerId);
    }
    
    public void cleanupPlayer(Player player) {
        cancelActiveGame(player);
        UUID playerId = player.getUniqueId();
        lastClickTimes.remove(playerId);
        pendingFish.remove(playerId);
        pendingRods.remove(playerId);
        pendingDurabilityLoss.remove(playerId);
        requiredClicks.remove(playerId);
        currentClicks.remove(playerId);
        player.removeMetadata("EAF_InMinigame", plugin);
    }
    
    public boolean handleStrike(Player player) {
        return false;
    }
}
