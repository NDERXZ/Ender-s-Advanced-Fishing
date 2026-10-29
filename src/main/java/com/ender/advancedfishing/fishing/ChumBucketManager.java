package com.ender.advancedfishing.fishing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import com.ender.advancedfishing.EndersAdvancedFishing;
import com.ender.advancedfishing.util.ItemBuilder;
import com.ender.advancedfishing.util.TextUtil;

public class ChumBucketManager implements Listener {
    private final EndersAdvancedFishing plugin;
    private final Map<UUID, ActiveVortex> activeVortexes = new ConcurrentHashMap<>();
    private BukkitTask particleTask;

    private static final String CHUM_PDC_KEY = "endersfishing:chum_bucket";
    private static final long DURATION_MILLIS = 3 * 60 * 1000L; // 3 menit
    private static final double VORTEX_RADIUS = 6.0;

    public static class ActiveVortex {
        private final UUID id;
        private final Location center;
        private final long expireTime;

        public ActiveVortex(Location center, long expireTime) {
            this.id = UUID.randomUUID();
            this.center = center.clone();
            this.expireTime = expireTime;
        }

        public UUID getId() { return id; }
        public Location getCenter() { return center; }
        public boolean isExpired() { return System.currentTimeMillis() >= expireTime; }
    }

    public ChumBucketManager(EndersAdvancedFishing plugin) {
        this.plugin = plugin;
        startParticleTask();
    }

    private void startParticleTask() {
        this.particleTask = org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<UUID, ActiveVortex>> iterator = activeVortexes.entrySet().iterator();

            while (iterator.hasNext()) {
                ActiveVortex vortex = iterator.next().getValue();
                if (vortex.isExpired()) {
                    iterator.remove();
                    continue;
                }

                Location center = vortex.getCenter();
                if (center.getWorld() == null) continue;

                // Spawn circular vortex particles
                double angleOffset = (now / 150.0) % (2 * Math.PI);
                for (int i = 0; i < 6; i++) {
                    double angle = angleOffset + (i * (2 * Math.PI / 6));
                    double radius = 1.5 + (i * 0.4);
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    double y = center.getY() + 0.1;

                    Location particleLoc = new Location(center.getWorld(), x, y, z);
                    Particle splash = com.ender.advancedfishing.compat.CompatibilityHelper.SPLASH;
                    Particle bubble = com.ender.advancedfishing.compat.CompatibilityHelper.BUBBLE;
                    if (splash != null) center.getWorld().spawnParticle(splash, particleLoc, 2, 0.05, 0.05, 0.05, 0.02);
                    if (bubble != null) center.getWorld().spawnParticle(bubble, particleLoc, 1, 0.02, 0.02, 0.02, 0.01);
                }
            }
        }, 10L, 10L);
    }

    public static ItemStack createChumBucket(int amount) {
        ItemStack item = new ItemBuilder(Material.WATER_BUCKET)
                .name(TextUtil.oceanic("ᴇᴍʙᴇʀ ᴜᴍᴘᴀɴ ᴛᴀʙᴜʀ"))
                .lore(
                    TextUtil.color("&8────────────────────"),
                    TextUtil.color("&7ᴛɪᴘᴇ: &bᴠᴏʀᴛᴇx ᴜᴍᴘᴀɴ"),
                    TextUtil.color("&7ᴅᴜʀᴀsɪ: &f3 ᴍᴇɴɪᴛ"),
                    TextUtil.color("&7ʀᴀᴅɪᴜs: &f6 ʙʟᴏᴋ"),
                    TextUtil.color("&7ᴇꜰᴇᴋ: &a+25% ʟᴜᴄᴋ &7& &e-50% ᴡᴀɪᴛ ᴛɪᴍᴇ"),
                    TextUtil.color("&8────────────────────"),
                    TextUtil.color("&eᴋʟɪᴋ ᴋᴀɴᴀɴ ᴋᴇ ᴀɪʀ ᴜɴᴛᴜᴋ ᴍᴇɴɢᴀᴋᴛɪғᴋᴀɴ")
                )
                .amount(Math.max(1, amount))
                .build();

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(
                NamespacedKey.fromString(CHUM_PDC_KEY),
                PersistentDataType.INTEGER,
                1
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isChumBucket(ItemStack item) {
        if (item == null || item.getType() != Material.WATER_BUCKET) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString(CHUM_PDC_KEY);
        return key != null && pdc.has(key, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = event.getItem();
        if (!isChumBucket(item)) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Location targetLoc = null;

        if (clickedBlock != null && clickedBlock.getType() == Material.WATER) {
            targetLoc = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
        } else if (clickedBlock != null && clickedBlock.getRelative(event.getBlockFace()).getType() == Material.WATER) {
            targetLoc = clickedBlock.getRelative(event.getBlockFace()).getLocation().add(0.5, 0.5, 0.5);
        } else {
            // Check targeted block within 5 blocks
            Block target = player.getTargetBlockExact(5);
            if (target != null && target.getType() == Material.WATER) {
                targetLoc = target.getLocation().add(0.5, 0.5, 0.5);
            }
        }

        if (targetLoc == null) {
            player.sendMessage(TextUtil.oceanic("[ ᴠᴏʀᴛᴇx ᴜᴍᴘᴀɴ ]") + TextUtil.color(" &cKamu harus mengklik blok air untuk menaburkan umpan!"));
            return;
        }

        event.setCancelled(true);

        // Deduct 1 item and return empty bucket
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
            ItemStack emptyBucket = new ItemStack(Material.BUCKET);
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(emptyBucket);
            if (!leftover.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), emptyBucket);
            }
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
        }

        ActiveVortex vortex = new ActiveVortex(targetLoc, System.currentTimeMillis() + DURATION_MILLIS);
        activeVortexes.put(vortex.getId(), vortex);

        player.playSound(targetLoc, Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.0f);
        player.playSound(targetLoc, Sound.ENTITY_DOLPHIN_SPLASH, 1.0f, 1.0f);

        player.sendTitle(
            TextUtil.oceanic("ᴠᴏʀᴛᴇx ᴜᴍᴘᴀɴ ᴀᴋᴛɪғ!"),
            TextUtil.color("&7ᴅᴜʀᴀsɪ: &b3 ᴍᴇɴɪᴛ &8| &7ʀᴀᴅɪᴜs: &f6 ʙʟᴏᴋ"),
            10, 40, 10
        );

        player.sendMessage(TextUtil.oceanic("[ ᴠᴏʀᴛᴇx ᴜᴍᴘᴀɴ ]") + TextUtil.color(" &aVortex umpan berhasil aktif di area ini! Memancing di sekitar lingkaran akan mendapat bonus kelangkaan dan tarikan lebih cepat."));
    }

    public boolean isNearVortex(Location location) {
        if (location == null || location.getWorld() == null) return false;
        for (ActiveVortex vortex : activeVortexes.values()) {
            if (vortex.isExpired()) continue;
            if (vortex.getCenter().getWorld().equals(location.getWorld())) {
                if (vortex.getCenter().distanceSquared(location) <= (VORTEX_RADIUS * VORTEX_RADIUS)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cleanup() {
        if (particleTask != null) {
            particleTask.cancel();
        }
        activeVortexes.clear();
    }
}
