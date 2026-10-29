package com.ender.advancedfishing.gui;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.fishing.data.FishJournalEntry;
import com.ender.advancedfishing.fishing.data.JournalManager;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.util.ItemBuilder;
import com.ender.advancedfishing.util.TextUtil;

public class JournalGUI extends GUI {
    private static final int ITEMS_PER_PAGE = 36; // slots 9 to 44

    private final EndersAdvancedFishing plugin;
    private final JournalManager journalManager;
    private CustomFish.FishTier currentTier;
    private int currentPage;

    public JournalGUI(Player player, EndersAdvancedFishing plugin) {
        this(player, plugin, CustomFish.FishTier.COMMON, 0);
    }

    public JournalGUI(Player player, EndersAdvancedFishing plugin, CustomFish.FishTier tier, int page) {
        super(player, TextUtil.oceanic("ᴇɴᴅᴇʀ's ғɪsʜɪɴɢ ᴊᴏᴜʀɴᴀʟ"), 6);
        this.plugin = plugin;
        this.journalManager = plugin.getJournalManager();
        this.currentTier = tier != null ? tier : CustomFish.FishTier.COMMON;
        this.currentPage = Math.max(0, page);

        render();
    }

    public void render() {
        inventory.clear();
        clickHandlers.clear();

        renderTopTabBar();
        renderFishContent();
        renderBottomBar();
    }

    private void renderTopTabBar() {
        CustomFish.FishTier[] tiers = {
            CustomFish.FishTier.COMMON,
            CustomFish.FishTier.UNCOMMON,
            CustomFish.FishTier.RARE,
            CustomFish.FishTier.EPIC,
            CustomFish.FishTier.LEGENDARY,
            CustomFish.FishTier.MYTHIC,
            CustomFish.FishTier.SECRET
        };

        // Filler for slot 0 and 8
        ItemStack frame = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        setItem(0, frame);
        setItem(8, frame);

        for (int i = 0; i < tiers.length; i++) {
            CustomFish.FishTier tier = tiers[i];
            int slot = i + 1;
            boolean isSelected = (tier == currentTier);

            Material mat;
            switch (tier) {
                case COMMON: mat = Material.GRAY_DYE; break;
                case UNCOMMON: mat = Material.LIME_DYE; break;
                case RARE: mat = Material.LIGHT_BLUE_DYE; break;
                case EPIC: mat = Material.PURPLE_DYE; break;
                case LEGENDARY: mat = Material.ORANGE_DYE; break;
                case MYTHIC: mat = Material.RED_DYE; break;
                case SECRET: mat = Material.BLACK_DYE; break;
                default: mat = Material.CYAN_DYE; break;
            }

            int discovered = journalManager != null ? journalManager.getDiscoveredCountForTier(player.getUniqueId(), tier) : 0;
            int total = journalManager != null ? journalManager.getTotalCountForTier(tier) : 0;

            String tabName = isSelected
                    ? TextUtil.tierGradient(tier, "[ " + tier.name() + " ]")
                    : TextUtil.color("&7" + TextUtil.toSmallCaps(tier.name()));

            ItemBuilder builder = new ItemBuilder(mat)
                    .name(tabName)
                    .lore(
                        TextUtil.color("&8────────────────────"),
                        TextUtil.color("&7ᴘʀᴏɢʀᴇs: &b" + discovered + " &7/ &f" + total),
                        TextUtil.color(isSelected ? "&a» ᴛᴀʙ ᴀᴋᴛɪғ" : "&e» ᴋʟɪᴋ ᴜɴᴛᴜᴋ ᴍᴇʟɪʜᴀᴛ"),
                        TextUtil.color("&8────────────────────")
                    );

            if (isSelected) {
                builder.enchant(Enchantment.UNBREAKING, 1).flag(ItemFlag.HIDE_ENCHANTS);
            }

            setItem(slot, builder.build(), () -> {
                this.currentTier = tier;
                this.currentPage = 0;
                render();
            });
        }
    }

    private void renderFishContent() {
        if (plugin.getConfigManager() == null) return;

        List<CustomFish> fishesInTier = new ArrayList<>();
        for (CustomFish fish : plugin.getConfigManager().getCustomFishes().values()) {
            if (fish.getTier() == currentTier) {
                fishesInTier.add(fish);
            }
        }

        // Sort alphabetically by ID
        fishesInTier.sort(Comparator.comparing(CustomFish::getId));

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, fishesInTier.size());

        int currentSlot = 9;
        for (int i = startIndex; i < endIndex; i++) {
            CustomFish fish = fishesInTier.get(i);
            boolean discovered = journalManager != null && journalManager.isDiscovered(player.getUniqueId(), fish.getId());

            if (discovered) {
                FishJournalEntry entry = journalManager.getJournal(player.getUniqueId()).get(fish.getId().toLowerCase());
                double weight = entry != null ? entry.getHeaviestWeight() : fish.getMinWeight();
                int quality = entry != null ? entry.getBestQuality() : fish.getMinQuality();
                int totalCaught = entry != null ? entry.getTotalCaught() : 1;
                String firstDate = entry != null ? entry.getFormattedFirstCaughtDate() : "Baru saja";

                ItemStack fishItem = fish.createItemStack();
                ItemBuilder builder = new ItemBuilder(fishItem)
                        .name(TextUtil.tierGradient(currentTier, fish.getName()))
                        .lore(
                            TextUtil.color("&8────────────────────"),
                            TextUtil.color("&7sᴛᴀᴛᴜs: &aᴅɪᴛᴇᴍᴜᴋᴀɴ"),
                            TextUtil.color("&7ᴛᴏᴛᴀʟ ᴄᴀᴜɢʜᴛ: &f" + totalCaught),
                            TextUtil.color("&7ʙᴏʙᴏᴛ ᴛᴇʀʙᴇsᴀʀ: &b" + String.format(Locale.US, "%.2f", weight) + " ᴋɢ"),
                            TextUtil.color("&7ᴋᴜᴀʟɪᴛᴀs ᴛᴇʀʙᴀɪᴋ: &e" + quality + " / 100"),
                            TextUtil.color("&7ᴘᴇʀᴛᴀᴍᴀ ᴋᴀʟɪ: &7" + firstDate),
                            TextUtil.color("&8────────────────────"),
                            TextUtil.color("&8" + fish.getDescription())
                        );

                setItem(currentSlot, builder.build());
            } else {
                ItemStack undiscovered = new ItemBuilder(Material.GRAY_DYE)
                        .name(TextUtil.color("&8[ &7??? &8]"))
                        .lore(
                            TextUtil.color("&8────────────────────"),
                            TextUtil.color("&7sᴛᴀᴛᴜs: &cᴜɴᴅɪsᴄᴏᴠᴇʀᴇᴅ"),
                            TextUtil.color("&7ᴛɪᴇʀ: &7" + TextUtil.toSmallCaps(currentTier.name())),
                            TextUtil.color("&7ᴘᴇᴛᴜɴᴊᴜᴋ: &fPancing di perairan untuk menemukan spesies ini."),
                            TextUtil.color("&8────────────────────")
                        )
                        .build();

                setItem(currentSlot, undiscovered);
            }
            currentSlot++;
        }

        // Fill remaining middle slots with empty spaces
        while (currentSlot < 45) {
            setItem(currentSlot, null);
            currentSlot++;
        }
    }

    private void renderBottomBar() {
        ItemStack frame = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 45; i < 54; i++) {
            setItem(i, frame);
        }

        List<CustomFish> fishesInTier = new ArrayList<>();
        if (plugin.getConfigManager() != null) {
            for (CustomFish fish : plugin.getConfigManager().getCustomFishes().values()) {
                if (fish.getTier() == currentTier) fishesInTier.add(fish);
            }
        }
        int maxPages = (int) Math.ceil((double) fishesInTier.size() / ITEMS_PER_PAGE);

        // Previous Page
        if (currentPage > 0) {
            ItemStack prev = new ItemBuilder(Material.ARROW)
                    .name(TextUtil.oceanic("« ʜᴀʟᴀᴍᴀɴ sᴇʙᴇʟᴜᴍɴʏᴀ"))
                    .build();
            setItem(45, prev, () -> {
                this.currentPage--;
                render();
            });
        }

        // Collection Statistics Book
        int totalDiscovered = journalManager != null ? journalManager.getDiscoveredCount(player.getUniqueId()) : 0;
        int totalAvailable = plugin.getConfigManager() != null ? plugin.getConfigManager().getCustomFishes().size() : 0;
        int percent = totalAvailable > 0 ? (int) Math.round(((double) totalDiscovered / totalAvailable) * 100) : 0;

        int tierDiscovered = journalManager != null ? journalManager.getDiscoveredCountForTier(player.getUniqueId(), currentTier) : 0;
        int tierTotal = journalManager != null ? journalManager.getTotalCountForTier(currentTier) : 0;

        ItemStack summary = new ItemBuilder(Material.BOOK)
                .name(TextUtil.oceanic("sᴛᴀᴛɪsᴛɪᴋ ᴋᴏʟᴇᴋsɪ"))
                .lore(
                    TextUtil.color("&8────────────────────"),
                    TextUtil.color("&7ᴛᴏᴛᴀʟ ᴅɪᴛᴇᴍᴜᴋᴀɴ: &b" + totalDiscovered + " &7/ &f" + totalAvailable + " &8(&e" + percent + "%&8)"),
                    TextUtil.color("&7ᴛɪᴇʀ " + TextUtil.toSmallCaps(currentTier.name()) + ": &a" + tierDiscovered + " &7/ &f" + tierTotal),
                    TextUtil.color("&8────────────────────")
                )
                .build();
        setItem(49, summary);

        // Close Button
        ItemStack close = new ItemBuilder(Material.BARRIER)
                .name(TextUtil.color("&cᴛᴜᴛᴜᴘ"))
                .build();
        setItem(50, close, this::close);

        // Next Page
        if (currentPage < maxPages - 1) {
            ItemStack next = new ItemBuilder(Material.ARROW)
                    .name(TextUtil.oceanic("ʜᴀʟᴀᴍᴀɴ sᴇʟᴀɴᴊᴜᴛɴʏᴀ »"))
                    .build();
            setItem(53, next, () -> {
                this.currentPage++;
                render();
            });
        }
    }
}
