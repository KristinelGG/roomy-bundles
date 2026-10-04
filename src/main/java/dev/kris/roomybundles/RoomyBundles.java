package dev.kris.roomybundles;

import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemInstance;
import org.apache.commons.lang3.math.Fraction;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

/**
 * Roomy Bundles: lets bundles hold several unstackable items
 * (tools, weapons, potions, beds...) instead of just one.
 */
public final class RoomyBundles {
    public static final String MOD_ID = "roomy_bundles";
    public static final int DEFAULT_PER_BUNDLE = 8;
    public static final int PER_BUNDLE = loadConfig();

    private RoomyBundles() {}

    private static int loadConfig() {
        int value = DEFAULT_PER_BUNDLE;
        Path path = Paths.get("config", MOD_ID + ".properties");
        try {
            if (Files.exists(path)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                }
                value = Integer.parseInt(props.getProperty("unstackables_per_bundle",
                        String.valueOf(DEFAULT_PER_BUNDLE)).trim());
            } else {
                Files.createDirectories(path.getParent());
                Files.writeString(path, String.join("\n",
                        "# Roomy Bundles configuration",
                        "#",
                        "# How many unstackable items (tools, weapons, potions, beds...)",
                        "# fit in a single bundle. Vanilla behaviour is 1.",
                        "# Allowed range: 1 - 64. Restart the game after changing it.",
                        "unstackables_per_bundle=" + DEFAULT_PER_BUNDLE, ""));
            }
        } catch (Exception e) {
            System.out.println("[Roomy Bundles] Could not read config, using default (" + DEFAULT_PER_BUNDLE + "): " + e);
        }
        value = Math.max(1, Math.min(64, value));
        System.out.println("[Roomy Bundles] Loaded: " + value + " unstackable items per bundle");
        return value;
    }

    /**
     * Adjusts the bundle weight of an item. Unstackable items normally weigh 1
     * (a full bundle); they are reduced to 1/PER_BUNDLE. Bundles themselves are
     * never touched so nesting keeps vanilla rules.
     */
    public static DataResult<Fraction> adjustWeight(ItemInstance item, DataResult<Fraction> original) {
        try {
            if (original == null || PER_BUNDLE <= 1) return original;
            if (item.getMaxStackSize() != 1) return original;
            if (item.get(DataComponents.BUNDLE_CONTENTS) != null) return original;
            Optional<Fraction> weight = original.result();
            if (weight.isPresent() && Fraction.ONE.equals(weight.get())) {
                return DataResult.success(Fraction.getFraction(1, PER_BUNDLE));
            }
        } catch (Throwable t) {
            System.out.println("[Roomy Bundles] Unexpected error, falling back to vanilla weight: " + t);
        }
        return original;
    }
}
