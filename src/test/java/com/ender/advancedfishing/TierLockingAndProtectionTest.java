package com.ender.advancedfishing;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import static org.junit.Assert.*;

import com.ender.advancedfishing.fishing.ItemProtectionListener;
import com.ender.advancedfishing.fishing.model.CustomFish;
import com.ender.advancedfishing.fishing.model.FishingRod;
import com.ender.advancedfishing.util.TextUtil;

public class TierLockingAndProtectionTest {

    @Test
    public void testSecretTierRegistration() {
        assertNotNull(CustomFish.FishTier.SECRET);
        assertEquals("§4", CustomFish.FishTier.SECRET.getDefaultColor());
        assertEquals(15.0, CustomFish.FishTier.SECRET.getPriceMultiplier(), 0.01);
        assertEquals("Rahasia", CustomFish.FishTier.SECRET.getDisplayName());

        assertNotNull(FishingRod.RodTier.SECRET);
        assertEquals("§4", FishingRod.RodTier.SECRET.getDefaultColor());
        assertEquals(5.0, FishingRod.RodTier.SECRET.getCatchRateMultiplier(), 0.01);
        assertEquals(6, FishingRod.RodTier.SECRET.getTierBoost());
    }

    @Test
    public void testRodTierLocking() {
        FishingRod starterRod = new FishingRod(
                "starter_rod", "Starter Rod", "Beginner rod",
                1001, 200, 1.0, FishingRod.RodTier.BASIC,
                false, "", 1500.0, 1, 50.0
        );
        starterRod.setAllowedTiers(List.of(CustomFish.FishTier.COMMON, CustomFish.FishTier.UNCOMMON));

        assertTrue(starterRod.isTierAllowed(CustomFish.FishTier.COMMON));
        assertTrue(starterRod.isTierAllowed(CustomFish.FishTier.UNCOMMON));
        assertFalse(starterRod.isTierAllowed(CustomFish.FishTier.RARE));
        assertFalse(starterRod.isTierAllowed(CustomFish.FishTier.EPIC));
        assertFalse(starterRod.isTierAllowed(CustomFish.FishTier.LEGENDARY));
        assertFalse(starterRod.isTierAllowed(CustomFish.FishTier.MYTHIC));
        assertFalse(starterRod.isTierAllowed(CustomFish.FishTier.SECRET));

        // Master / Secret rod allowing ALL
        FishingRod diamondRod = new FishingRod(
                "diamond_rod", "Diamond Rod", "Ultimate rod",
                1020, 10000, 0.15, FishingRod.RodTier.SECRET,
                true, "All catch", 2000000.0, 7, 100000.0
        );
        // Empty allowedTiers = ALL allowed
        diamondRod.setAllowedTiers(List.of());
        assertTrue(diamondRod.isTierAllowed(CustomFish.FishTier.COMMON));
        assertTrue(diamondRod.isTierAllowed(CustomFish.FishTier.MYTHIC));
        assertTrue(diamondRod.isTierAllowed(CustomFish.FishTier.SECRET));
    }

    @Test
    public void testSecretTierGradientAndSmallCaps() {
        String formatted = TextUtil.tierGradient(CustomFish.FishTier.SECRET, "Ancient Megalodon");
        assertNotNull(formatted);
        assertFalse("Gradient output must not contain raw Cyrillic", formatted.contains("ғ"));
        assertFalse("Output must have no emojis", formatted.contains("🐟"));
    }

    @Test
    public void testItemProtectionWithNull() {
        assertFalse("Null item should not be flagged as custom fish", 
                ItemProtectionListener.isCustomFish(null));
    }

    @Test
    public void testRodSpecialAbilityProperties() {
        FishingRod rodWithAbility = new FishingRod(
                "astral_rod", "Astral Rod", "Cosmic rod",
                1020, 3500, 0.35, FishingRod.RodTier.MASTER,
                true, "+25% peluang ikan kualitas di atas 90%", 350000.0, 5, 5000.0
        );
        assertTrue(rodWithAbility.hasSpecialAbility());
        assertEquals("+25% peluang ikan kualitas di atas 90%", rodWithAbility.getSpecialAbilityDescription());
        assertEquals(5, rodWithAbility.getLureLevel());
    }
}
