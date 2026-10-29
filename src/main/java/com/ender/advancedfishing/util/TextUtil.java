package com.ender.advancedfishing.util;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import com.ender.advancedfishing.fishing.model.CustomFish;

public final class TextUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{1F700}-\\x{1F77F}\\x{1F780}-\\x{1F7FF}\\x{1F800}-\\x{1F8FF}\\x{1F900}-\\x{1F9FF}\\x{1FA00}-\\x{1FA6F}\\x{1FA70}-\\x{1FAFF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]"
    );

    // Primary Oceanic Gradient Palette
    public static final String OCEANIC_START = "#0052D4";
    public static final String OCEANIC_MID = "#4364F7";
    public static final String OCEANIC_END = "#6FB1FC";

    private TextUtil() {}

    /**
     * Converts standard ASCII characters (a-z, A-Z) into elegant small caps unicode characters.
     */
    public static String toSmallCaps(String input) {
        if (input == null || input.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case 'a': sb.append('ᴀ'); break;
                case 'b': sb.append('ʙ'); break;
                case 'c': sb.append('ᴄ'); break;
                case 'd': sb.append('ᴅ'); break;
                case 'e': sb.append('ᴇ'); break;
                case 'f': sb.append('ꜰ'); break;
                case 'g': sb.append('ɢ'); break;
                case 'h': sb.append('ʜ'); break;
                case 'i': sb.append('ɪ'); break;
                case 'j': sb.append('ᴊ'); break;
                case 'k': sb.append('ᴋ'); break;
                case 'l': sb.append('ʟ'); break;
                case 'm': sb.append('ᴍ'); break;
                case 'n': sb.append('ɴ'); break;
                case 'o': sb.append('ᴏ'); break;
                case 'p': sb.append('ᴘ'); break;
                case 'q': sb.append('ǫ'); break;
                case 'r': sb.append('ʀ'); break;
                case 's': sb.append('s'); break;
                case 't': sb.append('ᴛ'); break;
                case 'u': sb.append('ᴜ'); break;
                case 'v': sb.append('ᴠ'); break;
                case 'w': sb.append('ᴡ'); break;
                case 'x': sb.append('x'); break;
                case 'y': sb.append('ʏ'); break;
                case 'z': sb.append('ᴢ'); break;
                default:  sb.append(c); break;
            }
        }
        return sb.toString();
    }

    /**
     * Strips emojis from text strictly.
     */
    public static String stripEmojis(String text) {
        if (text == null) return "";
        return EMOJI_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Translates standard ampersand color codes and hex codes (&#RRGGBB).
     */
    public static String color(String text) {
        if (text == null || text.isEmpty()) return "";
        text = stripEmojis(text);

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Creates a smooth multi-stop RGB gradient across text.
     */
    public static String gradient(String text, String... hexColors) {
        if (text == null || text.isEmpty()) return "";
        text = stripEmojis(text);
        if (hexColors == null || hexColors.length == 0) return text;
        if (hexColors.length == 1 || text.length() <= 1) {
            return ChatColor.of(hexColors[0]) + text;
        }

        Color[] colors = new Color[hexColors.length];
        for (int i = 0; i < hexColors.length; i++) {
            colors[i] = Color.decode(hexColors[i].startsWith("#") ? hexColors[i] : "#" + hexColors[i]);
        }

        StringBuilder result = new StringBuilder();
        int length = text.length();
        int segments = colors.length - 1;

        for (int i = 0; i < length; i++) {
            float globalProgress = (float) i / (float) Math.max(1, length - 1);
            float scaledProgress = globalProgress * segments;
            int segmentIndex = Math.min((int) scaledProgress, segments - 1);
            float localProgress = scaledProgress - segmentIndex;

            Color c1 = colors[segmentIndex];
            Color c2 = colors[segmentIndex + 1];

            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * localProgress);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * localProgress);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * localProgress);

            result.append(ChatColor.of(new Color(r, g, b))).append(text.charAt(i));
        }

        return result.toString();
    }

    /**
     * Applies the oceanic gradient palette (#0052D4 -> #4364F7 -> #6FB1FC).
     */
    public static String oceanic(String text) {
        return gradient(text, OCEANIC_START, OCEANIC_MID, OCEANIC_END);
    }

    /**
     * Converts to small caps and applies the oceanic gradient.
     */
    public static String oceanicSmallCaps(String text) {
        return oceanic(toSmallCaps(text));
    }

    /**
     * Applies distinctive, sleek gradients based on fish tier in small caps.
     */
    public static String tierGradient(CustomFish.FishTier tier, String text) {
        String sc = toSmallCaps(text);
        if (tier == null) return oceanic(sc);

        switch (tier) {
            case COMMON:
                return gradient(sc, "#757F9A", "#B0B7C6", "#D7DDE8");
            case UNCOMMON:
                return gradient(sc, "#11998E", "#28C76F", "#38EF7D");
            case RARE:
                return gradient(sc, "#0052D4", "#4364F7", "#6FB1FC");
            case EPIC:
                return gradient(sc, "#8E2DE2", "#C0392B", "#F000FF");
            case LEGENDARY:
                return gradient(sc, "#F7971E", "#FFB347", "#FFD200");
            case MYTHIC:
                return gradient(sc, "#FF0844", "#FF4E50", "#F9D423");
            case SECRET:
                return gradient(sc, "#2B2B2B", "#6D6D6D", "#EAEAEA");
            default:
                return oceanic(sc);
        }
    }
}
