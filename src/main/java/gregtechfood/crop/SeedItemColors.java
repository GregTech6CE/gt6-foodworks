package gregtechfood.crop;

import static java.util.Map.entry;

import gregtechfood.GregTechFood;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GregTechFood.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SeedItemColors {
    private static final SeedTint WHEAT = new SeedTint(0xB7BB3F, 0x00E210);

    // Match GT6 Crops for shared crops and use produce colors for the rest.
    private static final Map<String, SeedTint> COLORS = Map.ofEntries(
            entry("agave_seeds", new SeedTint(0x286D59, 0x78C6A7)),
            entry("almond_seeds", new SeedTint(0x8A592F, 0xDDB483)),
            entry("ananas_seeds", new SeedTint(0x7A8A18, 0xE0C040)),
            entry("anise_seeds", new SeedTint(0x54422B, 0xB49563)),
            entry("apple_seeds", new SeedTint(0x3D6B16, 0x8FCB3A)),
            entry("apple_2_seeds", new SeedTint(0xC6A20E, 0xF6DE62)),
            entry("apple_3_seeds", new SeedTint(0x4E1018, 0x8A2A38)),
            entry("apricot_seeds", new SeedTint(0xA84E22, 0xFFB85D)),
            entry("banana_seeds", new SeedTint(0xB28F15, 0xFFE572)),
            entry("barley_seeds", new SeedTint(0x285221, 0x84C63D)),
            entry("black_currants_seeds", new SeedTint(0x1A1028, 0x4A3068)),
            entry("black_pepper_seeds", new SeedTint(0x39352E, 0x827B69)),
            entry("blackberry_seeds", new SeedTint(0x141C22, 0x4D5A66)),
            entry("blueberry_seeds", new SeedTint(0x354085, 0x5262B5)),
            entry("candleberry_seeds", new SeedTint(0xC47A20, 0xF0C060)),
            entry("cherry_seeds", new SeedTint(0x7E1331, 0xF23C57)),
            entry("chili_pepper_seeds", new SeedTint(0x83090B, 0xC8462A)),
            entry("coconut_seeds", new SeedTint(0x75482C, 0xE9D7BD)),
            entry("coffee_beans_seeds", new SeedTint(0x593021, 0xA66A47)),
            entry("corn_seeds", new SeedTint(0xB87810, 0xFFD347)),
            entry("cranberry_seeds", new SeedTint(0x4A1028, 0xC45A78)),
            entry("cucumber_seeds", new SeedTint(0x13260D, 0x259038)),
            entry("elderberry_seeds", new SeedTint(0x302043, 0x76568C)),
            entry("fig_seeds", new SeedTint(0x573A53, 0xC4839C)),
            entry("gooseberry_seeds", new SeedTint(0x6A7A20, 0xC6D060)),
            entry("grapefruit_seeds", new SeedTint(0x944760, 0xFFABAD)),
            entry("green_grapes_seeds", new SeedTint(0x3A6224, 0x86B84A)),
            entry("hazelnut_seeds", new SeedTint(0x7C4A1F, 0xC68B53)),
            entry("hops_seeds", new SeedTint(0x42612B, 0xB6C65E)),
            entry("juniper_berries_seeds", new SeedTint(0x233C63, 0x7394B8)),
            entry("kiwi_seeds", new SeedTint(0x75502F, 0xA6CA55)),
            entry("lemon_seeds", new SeedTint(0xD8950C, 0xFFEF83)),
            entry("lime_seeds", new SeedTint(0x4C7C1B, 0xABDE57)),
            entry("mango_seeds", new SeedTint(0xD9790B, 0xFFCC4E)),
            entry("mint_leaves_seeds", new SeedTint(0x1A6B48, 0x5CDBA0)),
            entry("onion_seeds", new SeedTint(0x4E1B3F, 0x753C64)),
            entry("orange_seeds", new SeedTint(0xB95813, 0xFF991E)),
            entry("papaya_seeds", new SeedTint(0xA34921, 0xFA9067)),
            entry("peach_seeds", new SeedTint(0xA6553B, 0xFFAB85)),
            entry("peanut_seeds", new SeedTint(0x8A6438, 0xD4B07A)),
            entry("pear_seeds", new SeedTint(0x6F7D26, 0xC8D47D)),
            entry("persimmon_seeds", new SeedTint(0xB44813, 0xEF852B)),
            entry("pistachio_seeds", new SeedTint(0x7C8950, 0xC1CF83)),
            entry("plum_seeds", new SeedTint(0x4B2B68, 0x9970BE)),
            entry("pomegranate_seeds", new SeedTint(0x821C42, 0xD85375)),
            entry("purple_grapes_seeds", new SeedTint(0x4A1041, 0x5D1451)),
            entry("raspberry_seeds", new SeedTint(0xA23131, 0xC54F4F)),
            entry("red_currants_seeds", new SeedTint(0xA02838, 0xE06068)),
            entry("red_grapes_seeds", new SeedTint(0x7A1830, 0xD24A58)),
            entry("rice_seeds", new SeedTint(0xA8A48C, 0xF2EDE0)),
            entry("rye_seeds", new SeedTint(0x6E4E28, 0xC49555)),
            entry("soybean_seeds", new SeedTint(0x8A7532, 0xE1CD89)),
            entry("spice_leaf_seeds", new SeedTint(0x38754A, 0x72A64B)),
            entry("starfruit_seeds", new SeedTint(0xB58916, 0xF9DE6D)),
            entry("strawberry_seeds", new SeedTint(0x8F0205, 0xCE0609)),
            entry("tea_leaves_seeds", new SeedTint(0x7C8C58, 0xD8C97D)),
            entry("tomato_seeds", new SeedTint(0x91101F, 0xE3321F)),
            entry("vanilla_bean_seeds", new SeedTint(0x50332C, 0xAD8266)),
            entry("white_currants_seeds", new SeedTint(0xA89A88, 0xF0E8D8)),
            entry("white_grapes_seeds", new SeedTint(0x9AA078, 0xE8E6C4)));

    private SeedItemColors() {}

    @SubscribeEvent
    public static void register(RegisterColorHandlersEvent.Item event) {
        FoodCrops.seeds().forEach(seed -> {
            var colors = COLORS.getOrDefault(seed.getId().getPath(), WHEAT);
            event.register((stack, layer) -> switch (layer) {
                case 0 -> colors.body();
                case 1 -> colors.highlight();
                default -> -1;
            }, seed.get());
        });
    }

    private record SeedTint(int body, int highlight) {}
}
