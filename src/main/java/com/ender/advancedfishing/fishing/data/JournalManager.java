package com.ender.advancedfishing.fishing.data;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.util.TextUtil;

public class JournalManager {
    private final EndersAdvancedFishing plugin;
    private final File journalFile;
    private FileConfiguration journalConfig;
    private BukkitTask autosaveTask;

    // Cache: Player UUID -> Map of Fish ID to Journal Entry
    private final Map<UUID, Map<String, FishJournalEntry>> journalCache = new ConcurrentHashMap<>();

    public JournalManager(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.journalFile = new File(dataDir, "fish_journal.yml");

        if (!journalFile.exists()) {
            try {
                journalFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Tidak bisa membuat fish_journal.yml: " + e.getMessage());
            }
        }

        this.journalConfig = YamlConfiguration.loadConfiguration(journalFile);
        loadCaches();

        // Autosave every 5 minutes
        this.autosaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllData, 20L * 60 * 5, 20L * 60 * 5);
    }

    private void loadCaches() {
        for (String uuidStr : journalConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(uuidStr);
                ConfigurationSection playerSec = journalConfig.getConfigurationSection(uuidStr);
                if (playerSec == null) continue;

                Map<String, FishJournalEntry> entries = new ConcurrentHashMap<>();
                for (String fishId : playerSec.getKeys(false)) {
                    long firstCaught = playerSec.getLong(fishId + ".first_caught", System.currentTimeMillis());
                    double heaviest = playerSec.getDouble(fishId + ".heaviest_weight", 1.0);
                    int quality = playerSec.getInt(fishId + ".best_quality", 50);
                    int total = playerSec.getInt(fishId + ".total_caught", 1);

                    entries.put(fishId.toLowerCase(), new FishJournalEntry(fishId, firstCaught, heaviest, quality, total));
                }
                journalCache.put(playerId, entries);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public synchronized void recordCatch(Player player, CustomFish fish) {
        if (player == null || fish == null || fish.getId() == null) return;
        UUID playerId = player.getUniqueId();
        String fishId = fish.getId().toLowerCase();

        Map<String, FishJournalEntry> playerEntries = journalCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        boolean isNewDiscovery = !playerEntries.containsKey(fishId);

        FishJournalEntry entry = playerEntries.get(fishId);
        if (entry == null) {
            entry = new FishJournalEntry(fishId, fish.getWeight(), fish.getQuality());
            playerEntries.put(fishId, entry);
        } else {
            entry.updateCatch(fish.getWeight(), fish.getQuality());
        }

        if (isNewDiscovery) {
            String title = TextUtil.oceanic("sᴘᴇsɪᴇs ʙᴀʀᴜ ᴅɪᴛᴇᴍᴜᴋᴀɴ!");
            String subtitle = TextUtil.color("&f" + fish.getName() + " &8| &7ᴛᴇʀᴄᴀᴛᴀᴛ ᴅɪ ᴊᴏᴜʀɴᴀʟ");
            player.sendTitle(title, subtitle, 10, 50, 10);
            player.sendMessage(TextUtil.oceanic("[ ᴊᴏᴜʀɴᴀʟ ]") + TextUtil.color(" &7Spesies baru ditambahkan ke koleksi: &b" + fish.getName()));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
        }
    }

    public Map<String, FishJournalEntry> getJournal(UUID playerId) {
        return journalCache.getOrDefault(playerId, Collections.emptyMap());
    }

    public boolean isDiscovered(UUID playerId, String fishId) {
        if (fishId == null) return false;
        Map<String, FishJournalEntry> entries = journalCache.get(playerId);
        return entries != null && entries.containsKey(fishId.toLowerCase());
    }

    public int getDiscoveredCount(UUID playerId) {
        Map<String, FishJournalEntry> entries = journalCache.get(playerId);
        return entries != null ? entries.size() : 0;
    }

    public int getDiscoveredCountForTier(UUID playerId, CustomFish.FishTier tier) {
        if (tier == null || plugin.getConfigManager() == null) return 0;
        Map<String, CustomFish> allFishes = plugin.getConfigManager().getCustomFishes();
        Map<String, FishJournalEntry> entries = journalCache.get(playerId);
        if (entries == null || entries.isEmpty()) return 0;

        int count = 0;
        for (CustomFish fish : allFishes.values()) {
            if (fish.getTier() == tier && entries.containsKey(fish.getId().toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCountForTier(CustomFish.FishTier tier) {
        if (tier == null || plugin.getConfigManager() == null) return 0;
        int count = 0;
        for (CustomFish fish : plugin.getConfigManager().getCustomFishes().values()) {
            if (fish.getTier() == tier) count++;
        }
        return count;
    }

    public synchronized void saveAllData() {
        YamlConfiguration configToSave = new YamlConfiguration();
        // Snapshot cache to prevent ConcurrentModificationException during async autosave
        Map<UUID, Map<String, FishJournalEntry>> snapshot = new HashMap<>();
        for (Map.Entry<UUID, Map<String, FishJournalEntry>> entry : journalCache.entrySet()) {
            snapshot.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        for (Map.Entry<UUID, Map<String, FishJournalEntry>> playerEntry : snapshot.entrySet()) {
            String uuidStr = playerEntry.getKey().toString();
            for (Map.Entry<String, FishJournalEntry> fishEntry : playerEntry.getValue().entrySet()) {
                String path = uuidStr + "." + fishEntry.getKey();
                FishJournalEntry data = fishEntry.getValue();
                configToSave.set(path + ".first_caught", data.getFirstCaughtTime());
                configToSave.set(path + ".heaviest_weight", data.getHeaviestWeight());
                configToSave.set(path + ".best_quality", data.getBestQuality());
                configToSave.set(path + ".total_caught", data.getTotalCaught());
            }
        }

        try {
            configToSave.save(journalFile);
            this.journalConfig = configToSave;
        } catch (IOException e) {
            plugin.getLogger().severe("Gagal menyimpan fish_journal.yml: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (autosaveTask != null) {
            autosaveTask.cancel();
        }
        saveAllData();
    }
}
