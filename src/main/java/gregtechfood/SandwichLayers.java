package gregtechfood;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/** Layer dimensions and ingredient IDs match the original GT6 sandwich catalog. */
final class SandwichLayers {
    record Layer(String texture, int footprint, int thickness, int tint) {}

    private static final int WHITE = 0xFFFFFF;
    private static final int RED = 0xFF0000;
    private static final int BROWN = 0x604000;
    private static final int YELLOW = 0xFFFF00;
    private static final int PALE_GREEN = 0x80FF80;
    private static final Layer TOAST = new Layer("toast", 15, 2, WHITE);
    private static final Layer TOASTED = new Layer("toasted", 15, 2, WHITE);
    private static final Map<String, Layer> LAYERS = new HashMap<>();

    static {
        register(TOAST, "toast");
        register(TOASTED, "toasted_toast");
        solid("ananas", 12, 1, "ananas_slice");
        solid("banana", 4, 1, "banana_slice");
        solid("carrot", 4, 1, "carrot_slice");
        solid("cheese", 14, 1, "cheese_slice");
        solid("chili", 12, 2, "chili_pepper");
        solid("chocolate", 10, 2, "chocolate_bar");
        solid("comb", 14, 1, "honey_comb", "water_comb", "jungle_comb", "shroomy_comb", "royal_comb");
        solid("cucumber", 4, 1, "cucumber_slice");
        solid("fish_raw", 14, 2, "raw_fish_bar", "minecraft:cod", "minecraft:salmon");
        var fish = solid("fish", 14, 2, "cooked_fish_bar", "minecraft:cooked_cod", "minecraft:cooked_salmon");
        solid("ham_raw", 12, 1, "raw_ham_slice");
        solid("ham", 12, 1, "cooked_ham_slice");
        solid("lemon", 4, 1, "lemon_slice");
        var raw = solid("meat_raw", 14, 2, "raw_meat_bar", "scrap_meat", "raw_ribs", "dogmeat", "horse_meat",
                "mule_meat", "donkey_meat", "minecraft:beef", "minecraft:chicken", "minecraft:porkchop",
                "minecraft:mutton", "minecraft:rotten_flesh");
        solid("meat", 14, 2, "cooked_meat_bar", "grilled_ribs", "barbecue_ribs", "grilled_dogmeat",
                "grilled_horse_meat", "grilled_mule_meat", "grilled_donkey_meat", "minecraft:cooked_beef",
                "minecraft:cooked_chicken", "minecraft:cooked_porkchop", "minecraft:cooked_mutton");
        solid("onion", 4, 1, "onion_slice");
        solid("ripeye_raw", 14, 3, "raw_rib_eye_steak");
        solid("ripeye", 14, 3, "grilled_rib_eye_steak");
        solid("soylent", 14, 2, "emerald_green_bar", "canned_emerald_green");
        solid("tomato", 4, 1, "tomato_slice");
        solid("bacon_raw", 14, 1, "raw_bacon");
        solid("bacon", 14, 1, "grilled_bacon");
        var chum = solid("chum", 14, 2, "chum");
        solid("pickle", 4, 1, "pickle_slice");
        solid("pill", 4, 1, "empty_wax_pill", "radaway", "peppermint", "blue_pill", "red_pill", "antidote", "cure_all");
        solid("egg_fried", 12, 1, "fried_egg");
        solid("egg_sliced", 4, 1, "sliced_egg");

        register(new Layer("spice", 0, 1, YELLOW), "egg_yolk", "scrambled_egg");
        register(new Layer("spice", 0, 1, WHITE), "egg_white");
        register(new Layer("rainbow", 0, 1, WHITE), "rainbow_sap");
        spread(WHITE, "mayo", "heavy_cream", "coconut_cream");
        spread(RED, "tomato_ketchup", "chili_sauce", "hot_sauce", "diabolo_sauce", "diablo_sauce", "there_is_no_cow_sauce");
        spread(BROWN, "barbecue_sauce", "chocolate_cream", "nutella", "maple_syrup");
        spread(PALE_GREEN, "grape_vinegar", "apple_cider_vinegar", "dressing", "olive_oil", "seed_oil", "hemp_oil",
                "lin_oil", "green_slime_bottle");
        spread(YELLOW, "cave_johnsons_grenade_juice", "sunflower_oil", "nut_oil", "fish_oil",
                "royal_jelly", "minecraft:honey_bottle");
        spread(0x800080, "ambrosia");

        for (String size : List.of("tiny", "small", "tall", "wide", "large", "huge")) {
            String prefix = size + "_food_can_";
            register(raw, prefix + "rotten", prefix + "meat");
            register(fish, prefix + "fish");
            register(chum, prefix + "chum");
        }
    }

    private SandwichLayers() {}

    static Layer forItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return null;
        Layer layer = switch (id.getNamespace()) {
            case GregTechFood.MOD_ID, "gt6ce" -> LAYERS.get(id.getPath());
            default -> LAYERS.get(id.toString());
        };
        if (layer != null) return layer;

        var contents = CannedFoods.contentsOf(stack);
        if (contents == null) return null;
        return switch (contents) {
            case MEAT, ROTTEN -> LAYERS.get("tiny_food_can_meat");
            case FISH -> LAYERS.get("tiny_food_can_fish");
            case CHUM -> LAYERS.get("tiny_food_can_chum");
            default -> null;
        };
    }

    static boolean isStarter(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getNamespace().equals(GregTechFood.MOD_ID)
                && (id.getPath().equals("toast") || id.getPath().equals("toasted_toast"));
    }

    private static void register(Layer layer, String... items) {
        for (String item : items) LAYERS.put(item, layer);
    }

    private static Layer solid(String texture, int footprint, int height, String... items) {
        Layer layer = new Layer(texture, footprint, height, WHITE);
        register(layer, items);
        return layer;
    }

    private static void spread(int tint, String... items) {
        register(new Layer("condiment", 0, 1, tint), items);
    }
}
