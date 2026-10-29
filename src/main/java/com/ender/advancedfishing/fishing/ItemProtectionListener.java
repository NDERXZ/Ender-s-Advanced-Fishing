package com.ender.advancedfishing.fishing;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.util.TextUtil;

public class ItemProtectionListener implements Listener {
    private final EndersAdvancedFishing plugin;

    private static final NamespacedKey KEY_FISH_ID = NamespacedKey.fromString("endersfishing:fish_id");
    private static final NamespacedKey KEY_FISH_ID_DOTTED = NamespacedKey.fromString("endersfishing:endersfishing.fish_id");
    private static final NamespacedKey KEY_FISH_ID_LEGACY = NamespacedKey.fromString("nusafishing:fish_id");

    public ItemProtectionListener(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
    }

    public static boolean isCustomFish(ItemStack item) {
        if (item == null) return false;
        try {
            if (!item.hasItemMeta()) return false;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return false;
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            if (KEY_FISH_ID != null && pdc.has(KEY_FISH_ID, PersistentDataType.STRING)) return true;
            if (KEY_FISH_ID_DOTTED != null && pdc.has(KEY_FISH_ID_DOTTED, PersistentDataType.STRING)) return true;
            if (KEY_FISH_ID_LEGACY != null && pdc.has(KEY_FISH_ID_LEGACY, PersistentDataType.STRING)) return true;
        } catch (Throwable ignored) {}
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (isCustomFish(item)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                    TextUtil.color("&c« ɪᴛᴇᴍ ᴛᴇʀʟɪɴᴅᴜɴɢɪ: ɪᴋᴀɴ ɪɴɪ ᴛᴇʀʟᴀʟᴜ ʙᴇʀʜᴀʀɢᴀ ᴜɴᴛᴜᴋ ᴅɪᴍᴀᴋᴀɴ! »")
                )
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (isCustomFish(item)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                    TextUtil.color("&c« ɪᴛᴇᴍ ᴛᴇʀʟɪɴᴅᴜɴɢɪ: ɪᴋᴀɴ ᴛɪᴅᴀᴋ ᴅᴀᴘᴀᴛ ᴅɪʟᴇᴛᴀᴋᴋᴀɴ sᴇʙᴀɢᴀɪ ʙʟᴏᴋ! »")
                )
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getInventory() == null) return;
        ItemStack[] matrix = event.getInventory().getMatrix();
        for (ItemStack slot : matrix) {
            if (isCustomFish(slot)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (event.getInventory() == null) return;
        ItemStack[] matrix = event.getInventory().getMatrix();
        for (ItemStack slot : matrix) {
            if (isCustomFish(slot)) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player player) {
                    player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                            TextUtil.color("&c« ɪᴛᴇᴍ ᴛᴇʀʟɪɴᴅᴜɴɢɪ: ɪᴋᴀɴ ᴛɪᴅᴀᴋ ᴅᴀᴘᴀᴛ ᴅɪᴊᴀᴅɪᴋᴀɴ ʙᴀʜᴀɴ ᴄʀᴀғᴛɪɴɢ! »")
                        )
                    );
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (isCustomFish(event.getSource())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (isCustomFish(event.getFuel())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        InventoryType type = event.getInventory().getType();

        boolean isCookingInventory = (type == InventoryType.FURNACE || 
                                     type == InventoryType.BLAST_FURNACE || 
                                     type == InventoryType.SMOKER || 
                                     type == InventoryType.BREWING);

        if (!isCookingInventory) return;

        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (event.getRawSlot() < event.getInventory().getSize()) {
            if (isCustomFish(cursor)) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player player) {
                    player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                            TextUtil.color("&c« ɪᴛᴇᴍ ᴛᴇʀʟɪɴᴅᴜɴɢɪ: ɪᴋᴀɴ ᴛɪᴅᴀᴋ ᴅᴀᴘᴀᴛ ᴅɪᴍᴀsᴀᴋ ᴀᴛᴀᴜ ᴅɪʙᴀᴋᴀʀ! »")
                        )
                    );
                }
            }
        } else if (event.isShiftClick() && isCustomFish(clicked)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                        TextUtil.color("&c« ɪᴛᴇᴍ ᴛᴇʀʟɪɴᴅᴜɴɢɪ: ɪᴋᴀɴ ᴛɪᴅᴀᴋ ᴅᴀᴘᴀᴛ ᴅɪᴍᴀsᴀᴋ ᴀᴛᴀᴜ ᴅɪʙᴀᴋᴀʀ! »")
                    )
                );
            }
        }
    }
}
