package com.ender.advancedfishing;

import org.junit.Test;
import static org.junit.Assert.*;

import com.ender.advancedfishing.fishing.data.FishJournalEntry;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.util.TextUtil;
import org.bukkit.Material;

public class JournalAndGimmicksTest {

    @Test
    public void testSmallCapsConversion() {
        String result = TextUtil.toSmallCaps("Fish Journal");
        assertEquals("ꜰɪsʜ ᴊᴏᴜʀɴᴀʟ", result);
        assertEquals("ᴄᴏᴍᴍᴏɴ", TextUtil.toSmallCaps("COMMON"));
        assertEquals("ᴛᴇʀsɪsᴀ: 4 ᴋʟɪᴋ", TextUtil.toSmallCaps("Tersisa: 4 Klik"));
    }

    @Test
    public void testEmojiStripping() {
        String withEmoji = "Ikan Gurame 🐟 Tangkapan 🎣 Super 🌟";
        String stripped = TextUtil.stripEmojis(withEmoji).trim();
        assertEquals("Ikan Gurame  Tangkapan  Super", stripped);
        assertFalse(stripped.contains("🐟"));
        assertFalse(stripped.contains("🎣"));
        assertFalse(stripped.contains("🌟"));
    }

    @Test
    public void testOceanicGradient() {
        String sample = "Ender";
        String gradient = TextUtil.oceanic(sample);
        assertNotNull(gradient);
        assertTrue(gradient.length() > sample.length());
    }

    @Test
    public void testFishJournalEntryProgression() {
        FishJournalEntry entry = new FishJournalEntry("bass", 10.5, 70);
        assertEquals("bass", entry.getFishId());
        assertEquals(1, entry.getTotalCaught());
        assertEquals(10.5, entry.getHeaviestWeight(), 0.001);
        assertEquals(70, entry.getBestQuality());

        // Update with larger weight and better quality
        entry.updateCatch(18.2, 95);
        assertEquals(2, entry.getTotalCaught());
        assertEquals(18.2, entry.getHeaviestWeight(), 0.001);
        assertEquals(95, entry.getBestQuality());

        // Update with smaller weight and lower quality
        entry.updateCatch(5.0, 40);
        assertEquals(3, entry.getTotalCaught());
        assertEquals(18.2, entry.getHeaviestWeight(), 0.001); // remains highest
        assertEquals(95, entry.getBestQuality()); // remains best
    }

    @Test
    public void testEnvironmentalConditions() {
        CustomFish fish = new CustomFish(
                "abyssal_eel", "Abyssal Eel", "Belut jurang", Material.COD,
                0, 1.0, 5.0, 10, 50, 10.0, CustomFish.FishTier.RARE
        );
        fish.setEnvironment("ANY", "NIGHT", -64, 0, java.util.List.of());

        assertEquals("NIGHT", fish.getRequiredTime());
        assertEquals(-64, fish.getMinY());
        assertEquals(0, fish.getMaxY());
    }
}
