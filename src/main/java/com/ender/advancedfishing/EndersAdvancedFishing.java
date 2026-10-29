package com.ender.advancedfishing;

import java.util.logging.Logger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.ender.advancedfishing.commands.EndersFishingCommand;
import com.ender.advancedfishing.config.ConfigManager;
import com.ender.advancedfishing.economy.EconomyManager;
import com.ender.advancedfishing.fishing.FishingListener;
import com.ender.advancedfishing.fishing.FishingManager;
import com.ender.advancedfishing.fishing.data.PlayerSkillManager;
import com.ender.advancedfishing.fishing.data.StaminaManager;
import com.ender.advancedfishing.util.ItemBuilder;
import com.ender.advancedfishing.gui.GUIListener;
import com.ender.advancedfishing.event.FishingEventManager;
import com.ender.advancedfishing.event.ConfigEventFish;
import com.ender.advancedfishing.placeholder.EAFPlaceholders;
import com.ender.advancedfishing.fishing.data.FishingStatisticsManager;
import com.ender.advancedfishing.tournament.TournamentManager;
import com.ender.advancedfishing.managers.RewardsManager;
import com.ender.advancedfishing.integration.item.ItemProviderManager;
import com.ender.advancedfishing.integration.auraskills.AuraSkillsHook;
import com.ender.advancedfishing.fishing.afk.AfkFishingManager;
import com.ender.advancedfishing.fishing.data.JournalManager;
import com.ender.advancedfishing.fishing.ChumBucketManager;
import com.ender.advancedfishing.fishing.SeaMonsterManager;
import com.ender.advancedfishing.fishing.CatchEventManager;
import com.ender.advancedfishing.fishing.ItemProtectionListener;

public class EndersAdvancedFishing extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger("endersfishing");
    
    private static EndersAdvancedFishing instance;
    
    private ItemProviderManager itemProviderManager;
    private AuraSkillsHook auraSkillsHook;
    private AfkFishingManager afkFishingManager;
    private ConfigManager configManager;
    private StaminaManager staminaManager;
    private PlayerSkillManager skillManager;
    private FishingManager fishingManager;
    private EconomyManager economyManager;
    private FishingEventManager eventManager;
    private FishingStatisticsManager statisticsManager;
    private TournamentManager tournamentManager;
    private RewardsManager rewardsManager;
    private FishingListener fishingListener;
    private JournalManager journalManager;
    private ChumBucketManager chumBucketManager;
        private SeaMonsterManager seaMonsterManager;
    private CatchEventManager catchEventManager;
    private ItemProtectionListener itemProtectionListener;
    
    private BukkitTask priceUpdateTask;
    
    @Override
    public void onEnable() {
        // Set instance for static access
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize item provider manager for texture support (Nexo, Oraxen, ItemsAdder, Vanilla)
        itemProviderManager = new ItemProviderManager(this);
        
        // Initialize managers
        configManager = new ConfigManager(this);
        
        // Load event fish from config
        ConfigEventFish.loadFromConfig(this);
        
        // Initialize statistics manager
        statisticsManager = new FishingStatisticsManager(this);
        
        staminaManager = new StaminaManager(this);
        skillManager = new PlayerSkillManager(this);
        
        // Initialize fishing manager after config is loaded
        fishingManager = new FishingManager(this, configManager);
        
        // Initialize economy manager after config is loaded
        economyManager = new EconomyManager(this);
        
        // Initialize event manager after fishing manager
        eventManager = new FishingEventManager(this);
        
        // Initialize tournament manager
        tournamentManager = new TournamentManager(this);
        
        // Initialize Fish Journal
        this.journalManager = new JournalManager(this);

        // Initialize Chum Bucket / Bait Vortex
        this.chumBucketManager = new ChumBucketManager(this);
        getServer().getPluginManager().registerEvents(chumBucketManager, this);

        // Initialize Sea Monster encounter
        this.seaMonsterManager = new SeaMonsterManager(this);
                getServer().getPluginManager().registerEvents(seaMonsterManager, this);
        
        // Initialize Catch Event & Item Protection
        this.catchEventManager = new CatchEventManager(this);
        this.itemProtectionListener = new ItemProtectionListener(this);
        getServer().getPluginManager().registerEvents(itemProtectionListener, this);
        
        // Initialize rewards manager
        this.rewardsManager = new RewardsManager(this);
        
        // Initialize AuraSkills integration
        try {
            this.auraSkillsHook = new AuraSkillsHook(this);
        } catch (Throwable t) {
            getLogger().warning("Could not initialize AuraSkills hook: " + t.getMessage());
            this.auraSkillsHook = null;
        }

        // Initialize AFK fishing manager and register its listener
        this.afkFishingManager = new AfkFishingManager(this);
        getServer().getPluginManager().registerEvents(afkFishingManager, this);

        // Register listeners
        this.fishingListener = new FishingListener(this, fishingManager, staminaManager, skillManager);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        
        // Register GUI listener
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        
        // Register commands
        EndersFishingCommand commandExecutor = new EndersFishingCommand(this, fishingManager);
        getCommand("eaf").setExecutor(commandExecutor);
        getCommand("eaf").setTabCompleter(commandExecutor);
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EAFPlaceholders(this).register();
            getLogger().info("PlaceholderAPI ditemukan - placeholder EndersAdvancedFishing terdaftar!");
        }
        
        // Schedule price regeneration task if economy is enabled (runs every 15 minutes)
        if (economyManager.isEconomyEnabled()) {
            priceUpdateTask = getServer().getScheduler().runTaskTimer(this, 
                () -> economyManager.regeneratePrices(), 
                20 * 60 * 15, // Initial delay: 15 minutes
                20 * 60 * 15  // Run every 15 minutes
            );
        }
        
        LOGGER.info("EndersAdvancedFishing v" + getDescription().getVersion() + " enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save player data
        if (skillManager != null) {
            skillManager.saveAllData();
        }
        
        // Save statistics and cancel tasks before shutdown
        if (statisticsManager != null) {
            statisticsManager.shutdown();
        }
        
        // Clean up resources
        if (staminaManager != null) {
            staminaManager.shutdown();
        }
        
        // Cancel scheduled tasks
        if (priceUpdateTask != null) {
            priceUpdateTask.cancel();
        }
        
        // Shutdown event manager
        if (eventManager != null) {
            eventManager.shutdown();
        }
        
        // Shutdown tournament manager
        if (tournamentManager != null) {
            tournamentManager.shutdown();
        }

        // Cleanup AFK fishing manager
        if (afkFishingManager != null) {
            afkFishingManager.cleanup();
        }

        // Cleanup Journal and Chum Bucket
        if (journalManager != null) {
            journalManager.shutdown();
        }
        if (chumBucketManager != null) {
            chumBucketManager.cleanup();
        }
        
        LOGGER.info("EndersAdvancedFishing disabled");
    }
    
    // Static getter for plugin instance
    public static EndersAdvancedFishing getInstance() {
        return instance;
    }
    
    public ItemProviderManager getItemProviderManager() {
        return itemProviderManager;
    }
    
    // Getter methods for managers
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public StaminaManager getStaminaManager() {
        return staminaManager;
    }
    
    public PlayerSkillManager getSkillManager() {
        return skillManager;
    }
    
    public FishingManager getFishingManager() {
        return fishingManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public FishingEventManager getEventManager() {
        return eventManager;
    }
    
    public FishingStatisticsManager getStatisticsManager() {
        return statisticsManager;
    }
    
    public TournamentManager getTournamentManager() {
        return tournamentManager;
    }
    
    public RewardsManager getRewardsManager() {
        return rewardsManager;
    }

    public AuraSkillsHook getAuraSkillsHook() {
        return auraSkillsHook;
    }

    public AfkFishingManager getAfkFishingManager() {
        return afkFishingManager;
    }

    public FishingListener getFishingListener() {
        return fishingListener;
    }

    public JournalManager getJournalManager() {
        return journalManager;
    }

    public ChumBucketManager getChumBucketManager() {
        return chumBucketManager;
    }

        public CatchEventManager getCatchEventManager() {
        return catchEventManager;
    }

    public ItemProtectionListener getItemProtectionListener() {
        return itemProtectionListener;
    }

    public SeaMonsterManager getSeaMonsterManager() {
        return seaMonsterManager;
    }
    
    public void reloadAllConfigurations() {
        // Reload main config
        reloadConfig();
        
        // Reload fishing config
        configManager.reloadConfigs();
        
        // Reload event fish from config
        ConfigEventFish.loadFromConfig(this);

        // Reload stamina manager config
        if (staminaManager != null) {
            staminaManager.loadConfig();
        }

        // Recheck AuraSkills availability
        if (auraSkillsHook != null) {
            auraSkillsHook.checkAvailability();
        }
        
        // Reload dependent systems
        fishingManager.reloadConfig();
        
        // Reschedule price update task if needed
        if (priceUpdateTask != null) {
            priceUpdateTask.cancel();
            priceUpdateTask = null;
        }
        if (economyManager != null && economyManager.isEconomyEnabled()) {
            priceUpdateTask = getServer().getScheduler().runTaskTimer(this, 
                () -> economyManager.regeneratePrices(), 
                20 * 60 * 15,
                20 * 60 * 15
            );
        }

        getLogger().info("All configurations reloaded successfully!");
    }
}
