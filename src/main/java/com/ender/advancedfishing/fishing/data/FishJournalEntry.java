package com.ender.advancedfishing.fishing.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FishJournalEntry {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final String fishId;
    private final long firstCaughtTime;
    private double heaviestWeight;
    private int bestQuality;
    private int totalCaught;

    public FishJournalEntry(String fishId, long firstCaughtTime, double heaviestWeight, int bestQuality, int totalCaught) {
        this.fishId = fishId;
        this.firstCaughtTime = firstCaughtTime > 0 ? firstCaughtTime : System.currentTimeMillis();
        this.heaviestWeight = heaviestWeight;
        this.bestQuality = bestQuality;
        this.totalCaught = Math.max(1, totalCaught);
    }

    public FishJournalEntry(String fishId, double initialWeight, int initialQuality) {
        this(fishId, System.currentTimeMillis(), initialWeight, initialQuality, 1);
    }

    public void updateCatch(double weight, int quality) {
        this.totalCaught++;
        if (weight > this.heaviestWeight) {
            this.heaviestWeight = weight;
        }
        if (quality > this.bestQuality) {
            this.bestQuality = quality;
        }
    }

    public String getFishId() {
        return fishId;
    }

    public long getFirstCaughtTime() {
        return firstCaughtTime;
    }

    public String getFormattedFirstCaughtDate() {
        return DATE_FORMAT.format(new Date(firstCaughtTime));
    }

    public double getHeaviestWeight() {
        return heaviestWeight;
    }

    public int getBestQuality() {
        return bestQuality;
    }

    public int getTotalCaught() {
        return totalCaught;
    }
}
