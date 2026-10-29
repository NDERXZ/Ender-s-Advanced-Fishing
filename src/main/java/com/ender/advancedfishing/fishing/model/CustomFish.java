package com.ender.advancedfishing.fishing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import com.ender.advancedfishing.util.ItemBuilder;
import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.compat.CustomModelDataHelper;
import com.ender.advancedfishing.util.TextUtil;

public class CustomFish {
    // Definisikan tier ikan
    public enum FishTier {
        COMMON("§7", 1.0, "Biasa"),
        UNCOMMON("§a", 1.5, "Tidak Biasa"),
        RARE("§9", 2.0, "Langka"),
        EPIC("§5", 3.0, "Epik"),
        LEGENDARY("§6", 5.0, "Legendaris"),
        MYTHIC("§c", 8.0, "Mistik"),
        SECRET("§4", 15.0, "Rahasia");
        
        private final String defaultColor;
        private final double priceMultiplier;
        private final String displayName;
        
        FishTier(String defaultColor, double priceMultiplier, String displayName) {
            this.defaultColor = defaultColor;
            this.priceMultiplier = priceMultiplier;
            this.displayName = displayName;
        }
        
        public String getDefaultColor() { return defaultColor; }

        public String getColor() {
            if (EndersAdvancedFishing.getInstance() != null && EndersAdvancedFishing.getInstance().getConfigManager() != null) {
                return EndersAdvancedFishing.getInstance().getConfigManager().getFishTierColor(this);
            }
            return defaultColor;
        }

        public double getPriceMultiplier() { return priceMultiplier; }
        public String getDisplayName() { return displayName; }
    }
    
    private static final Random random = new Random();
    
    private String id;
    private String name;
    private String description;
    private Material material;
    private int customModelData;
    private String textureProvider; // "vanilla", "nexo", "oraxen", "itemsadder"
    private String textureId;       // Item ID in custom provider
    private double minWeight;
    private double maxWeight;
    private int minQuality;
    private int maxQuality;
    private double basePrice;
    private FishTier baseTier; // Tier dasar ikan
    
    // Environmental conditions
    private String requiredWeather = "ANY";
    private String requiredTime = "ANY";
    private int minY = -64;
    private int maxY = 320;
    private List<String> requiredBiomes = new ArrayList<>();

    // State for caught fish
    private double weight;
    private int quality;
    private FishTier actualTier;
    
    public CustomFish(String id, String name, String description, Material material,
                     int customModelData, double minWeight, double maxWeight,
                     int minQuality, int maxQuality, double basePrice,
                     FishTier baseTier) {
        this(id, name, description, material, customModelData, "vanilla", null,
             minWeight, maxWeight, minQuality, maxQuality, basePrice, baseTier);
    }

    public CustomFish(String id, String name, String description, Material material,
                     int customModelData, String textureProvider, String textureId,
                     double minWeight, double maxWeight,
                     int minQuality, int maxQuality, double basePrice,
                     FishTier baseTier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.material = material != null ? material : Material.COD;
        this.customModelData = customModelData;
        this.textureProvider = (textureProvider == null || textureProvider.isBlank()) ? "vanilla" : textureProvider.trim().toLowerCase();
        this.textureId = textureId;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
        this.basePrice = basePrice;
        this.baseTier = baseTier == null ? FishTier.COMMON : baseTier;
    }
    
    // Create a new caught fish instance with random weight and quality
    public CustomFish createCaughtInstance(int tierBoost, double qualityBoost, double weightBoost, double maxWeight) {
        CustomFish fish = new CustomFish(id, name, description, material, customModelData,
                textureProvider, textureId, minWeight, maxWeight, minQuality, maxQuality, basePrice, baseTier);
        fish.setEnvironment(requiredWeather, requiredTime, minY, maxY, requiredBiomes);
        
        // Generate random weight with boost applied
        double weightRange = Math.min(this.maxWeight, maxWeight) - minWeight;
        double boostedMaxWeight = minWeight + (weightRange * weightBoost);
        
        double randomValue = Math.pow(random.nextDouble(), 0.7);
        fish.weight = minWeight + (randomValue * (boostedMaxWeight - minWeight));
        
        // Apply quality boost
        int boostedMaxQuality = Math.min(100, (int)(maxQuality * qualityBoost));
        fish.quality = minQuality + random.nextInt(boostedMaxQuality - minQuality + 1);
        
        FishTier calculatedTier = calculateActualTier(fish.quality, fish.weight, tierBoost);
        fish.actualTier = (calculatedTier != null) ? calculatedTier : baseTier;
        
        return fish;
    }

    public CustomFish createCaughtInstance(int tierBoost, double qualityBoost, double weightBoost) {
        return createCaughtInstance(tierBoost, qualityBoost, weightBoost, maxWeight);
    }

    public CustomFish createCaughtInstance(int tierBoost, double qualityBoost) {
        return createCaughtInstance(tierBoost, qualityBoost, 1.0, maxWeight);
    }
    
    private FishTier calculateActualTier(int quality, double weight, int tierBoost) {
        try {
            int tierIndex = baseTier.ordinal();
            
            if (quality >= 90) {
                tierIndex++;
            } else if (quality >= 75) {
                tierIndex += (random.nextDouble() < 0.5 ? 1 : 0);
            }
            
            double weightRatio = (weight - minWeight) / Math.max(0.1, (maxWeight - minWeight));
            if (weightRatio >= 0.9) {
                tierIndex++;
            } else if (weightRatio >= 0.75) {
                tierIndex += (random.nextDouble() < 0.5 ? 1 : 0);
            }
            
            tierIndex += tierBoost;
            tierIndex = Math.max(0, Math.min(tierIndex, FishTier.values().length - 1));
            
            return FishTier.values()[tierIndex];
        } catch (Exception e) {
            return baseTier;
        }
    }
    
    public void setEnvironment(String weather, String time, int minY, int maxY, List<String> biomes) {
        this.requiredWeather = weather != null ? weather : "ANY";
        this.requiredTime = time != null ? time : "ANY";
        this.minY = minY;
        this.maxY = maxY;
        this.requiredBiomes = biomes != null ? new ArrayList<>(biomes) : new ArrayList<>();
    }

    public boolean matchesEnvironment(World world, Location location) {
        if (world == null || location == null) return true;

        if (requiredWeather != null && !requiredWeather.equalsIgnoreCase("ANY")) {
            if (requiredWeather.equalsIgnoreCase("THUNDER") && !world.isThundering()) return false;
            if (requiredWeather.equalsIgnoreCase("RAIN") && !world.hasStorm()) return false;
            if (requiredWeather.equalsIgnoreCase("CLEAR") && (world.hasStorm() || world.isThundering())) return false;
        }

        if (requiredTime != null && !requiredTime.equalsIgnoreCase("ANY")) {
            long time = world.getTime();
            boolean isNight = (time >= 13000 && time <= 23000);
            if (requiredTime.equalsIgnoreCase("NIGHT") && !isNight) return false;
            if (requiredTime.equalsIgnoreCase("DAY") && isNight) return false;
            if (requiredTime.equalsIgnoreCase("FULL_MOON")) {
                if (!isNight) return false;
                long day = world.getFullTime() / 24000L;
                if (day % 8L != 0L) return false;
            }
        }

        int y = location.getBlockY();
        if (y < minY || y > maxY) return false;

        if (requiredBiomes != null && !requiredBiomes.isEmpty()) {
            String currentBiome = location.getBlock().getBiome().name();
            boolean match = false;
            for (String b : requiredBiomes) {
                if (b.equalsIgnoreCase(currentBiome)) {
                    match = true;
                    break;
                }
            }
            if (!match) return false;
        }

        return true;
    }

    public double getPrice() {
        double rawPrice;
        if (weight <= 0 && quality <= 0) {
            rawPrice = basePrice * getActualTier().getPriceMultiplier();
        } else {
            double weightRatio = (weight - minWeight) / Math.max(0.1, (maxWeight - minWeight));
            double weightMultiplier;
            if (weight < 10.0) {
                weightMultiplier = 1.0 + weightRatio;
            } else if (weight < 100.0) {
                weightMultiplier = 1.0 + Math.log10(weight);
            } else {
                weightMultiplier = 2.0 + Math.sqrt(weight) / 10.0;
            }
            
            double qualityMultiplier = 0.5 + (quality / 100.0);
            FishTier currentActualTier = getActualTier();
            double tierMultiplier = currentActualTier.getPriceMultiplier();
            rawPrice = basePrice * weightMultiplier * qualityMultiplier * tierMultiplier;
            
            if (currentActualTier == FishTier.MYTHIC) {
                rawPrice = Math.max(rawPrice, basePrice * 3.0);
            }
        }
        
        if (rawPrice > 2000000.0) {
            rawPrice = 2000000.0;
        }
        return rawPrice;
    }
    
    public String getFormattedWeight() {
        if (weight < 1.0) {
            return String.format(java.util.Locale.US, "%.2f g", weight * 1000);
        } else if (weight < 1000.0) {
            return String.format(java.util.Locale.US, "%.2f kg", weight);
        } else {
            return String.format(java.util.Locale.US, "%.2f ton", weight / 1000);
        }
    }

    public ItemStack createItemStack() {
        ItemStack fishItem;
        if (EndersAdvancedFishing.getInstance() != null && EndersAdvancedFishing.getInstance().getItemProviderManager() != null) {
            fishItem = EndersAdvancedFishing.getInstance().getItemProviderManager()
                    .resolveBaseItem(textureProvider, textureId, material, customModelData);
        } else {
            fishItem = new ItemStack(material != null ? material : Material.COD);
            ItemMeta meta = fishItem.getItemMeta();
            if (meta != null && customModelData > 0) {
                CustomModelDataHelper.applyCustomModelData(meta, customModelData);
                fishItem.setItemMeta(meta);
            }
        }

        ItemMeta meta = fishItem.getItemMeta();
        if (meta == null) {
            meta = org.bukkit.Bukkit.getItemFactory().getItemMeta(fishItem.getType());
        }

        FishTier tier = getActualTier();
        meta.setDisplayName(TextUtil.tierGradient(tier, name));
        
        List<String> lore = new ArrayList<>();
        lore.add(TextUtil.color("&8────────────────────"));
        lore.add(TextUtil.color("&7ʙᴇʀᴀᴛ: &f" + getFormattedWeight()));
        lore.add(TextUtil.color("&7ᴋᴜᴀʟɪᴛᴀs: &f" + quality + " / 100"));
        lore.add(TextUtil.color("&7ᴛɪᴇʀ: ") + TextUtil.tierGradient(tier, tier.name()));
        lore.add(TextUtil.color("&7ɴɪʟᴀɪ: &b" + String.format(java.util.Locale.US, "%.2f", getPrice())));
        lore.add(TextUtil.color("&8────────────────────"));
        if (description != null && !description.isEmpty()) {
            lore.add(TextUtil.color("&8" + description));
        }
        meta.setLore(lore);
                
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(NamespacedKey.fromString("endersfishing:fish_id"), PersistentDataType.STRING, id);
        container.set(NamespacedKey.fromString("endersfishing:fish_weight"), PersistentDataType.DOUBLE, weight);
        container.set(NamespacedKey.fromString("endersfishing:fish_quality"), PersistentDataType.INTEGER, quality);
        container.set(NamespacedKey.fromString("endersfishing:fish_tier"), PersistentDataType.INTEGER, tier.ordinal());
        fishItem.setItemMeta(meta);
        
        return fishItem;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public String getTextureProvider() { return textureProvider; }
    public String getTextureId() { return textureId; }
    public double getMinWeight() { return minWeight; }
    public double getMaxWeight() { return maxWeight; }
    public int getMinQuality() { return minQuality; }
    public int getMaxQuality() { return maxQuality; }
    public double getBasePrice() { return basePrice; }
    public FishTier getBaseTier() { return baseTier; }
    public double getWeight() { return weight; }
    public int getQuality() { return quality; }
    public FishTier getActualTier() {
        return actualTier != null ? actualTier : baseTier;
    }
    public FishTier getTier() {
        return getActualTier();
    }
    public String getRequiredWeather() { return requiredWeather; }
    public String getRequiredTime() { return requiredTime; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public List<String> getRequiredBiomes() { return requiredBiomes; }

    public void applyCaughtData(double weight, int quality, FishTier actualTier) {
        this.weight = weight;
        this.quality = quality;
        this.actualTier = actualTier != null ? actualTier : this.baseTier;
    }
}
