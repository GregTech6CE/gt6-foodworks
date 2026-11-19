package gregtechfood.drink;

import static gregtechfood.drink.DrinkRegistry.drink;
import static gregtechfood.drink.DrinkRegistry.fluid;
import static gregtechfood.drink.DrinkRegistry.pieces;
import static gregtechfood.drink.DrinkRegistry.registerBottle;

import gregtechfood.drink.DrinkRegistry.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.DeferredRegister;

final class FruitDrinks {
    private FruitDrinks() {}

    static void register(DeferredRegister<Item> items) {
        pieces(items, "apple_pieces", 4, 0.3F);
        drink(items, "apple_juice", 0xFFE2A63B, 6, 0.4F);

        pieces(items, "melon_pieces", 2, 0.3F);
        drink(items, "melon_juice", 0xFFD96460, 2, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        pieces(items, "blackberry_pieces", 1, 0.6F);
        drink(items, "blackberry_juice", 0xFF4E3353, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "blueberry_pieces", 1, 0.6F);
        drink(items, "blueberry_juice", 0xFF4D4578, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "raspberry_pieces", 1, 0.6F);
        drink(items, "raspberry_juice", 0xFFB53761, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "cranberry_pieces", 1, 0.6F);
        drink(items, "cranberry_juice", 0xFFA1303E, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "gooseberry_pieces", 1, 0.6F);
        drink(items, "gooseberry_juice", 0xFF93AC57, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "strawberry_pieces", 1, 0.6F);
        drink(items, "strawberry_juice", 0xFFD84551, 6, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.1F));
        pieces(items, "lemon_pieces", 1, 0.6F);
        drink(items, "lemon_juice", 0xFFE8D25A, 3, 0.8F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        pieces(items, "pomegranate_pieces", 1, 0.6F);
        drink(items, "pomegranate_juice", 0xFFA2314B, 2, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        pieces(items, "banana_pieces", 1, 0.6F);
        drink(items, "banana_juice", 0xFFE8CC69, 6, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        pieces(items, "ananas_pieces", 4, 0.3F);
        drink(items, "ananas_juice", 0xFFE1AD3C, 3, 0.4F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        pieces(items, "beet_pieces", 1, 0.6F);
        drink(items, "beet_juice", 0xFF7A2349, 1, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        pieces(items, "carrot_pieces", 3, 0.6F);
        drink(items, "carrot_juice", 0xFFE2762B, 1, 0.2F,
                new DrinkEffect(MobEffects.NIGHT_VISION, 400, 0, 0.3F));
        pieces(items, "pumpkin_pieces", 0, 0.0F);
        drink(items, "pumpkin_juice", 0xFFD68A33, 1, 0.2F);
        pieces(items, "tomato_pieces", 1, 0.6F);
        drink(items, "tomato_juice", 0xFFB94832, 1, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.3F));
        drink(items, "tomato_ketchup", 0xFFA62920, 4, 0.3F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.1F),
                new DrinkEffect(MobEffects.MOVEMENT_SPEED, 200, 0, 0.2F));
        pieces(items, "cucumber_pieces", 1, 1.2F);
        fluid("cucumber_juice", 0xFF82AD64);
        pieces(items, "onion_pieces", 1, 1.2F);
        fluid("onion_juice", 0xFFCDBAA6);
        drink(items, "ananas_smoothie", 0xFFFFFF00, 2, 0.2F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        drink(items, "apple_smoothie", 0xFFD6B16A, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 200, 0, 0.2F));
        drink(items, "banana_smoothie", 0xFFFFE792, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        drink(items, "blackberry_smoothie", 0xFF745278, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "blueberry_smoothie", 0xFF75669E, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "cranberry_smoothie", 0xFFBE6170, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "gooseberry_smoothie", 0xFFFFFF30, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "lemon_smoothie", 0xFFFFD830, 2, 0.4F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        drink(items, "melon_smoothie", 0xFFE88581, 1, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        drink(items, "pomegranate_smoothie", 0xFFFF4646, 1, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F));
        drink(items, "raspberry_smoothie", 0xFFD96B8A, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "strawberry_smoothie", 0xFFEFA8A8, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.1F));
        drink(items, "cider", 0xFFC88C3F, 4, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 400, 1, 0.6F));
        drink(items, "apple_cider_vinegar", 0xFFA96A37, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 400, 2, 0.7F));
        registerBottle(items, "mayo", 0xFFEADF9F, () -> new BottledFoodItem(
                new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build(), UseAnim.EAT, false));
        fluid("agave_juice", 0xFFA8BE7C);
    }

    static void registerOrchardDrinks(DeferredRegister<Item> items) {
        var haste = new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F);
        var hunger = new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F);
        var berryStrength = new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F);
        var pearHunger = new DrinkEffect(MobEffects.HUNGER, 200, 0, 0.2F);
        var grapefruitHunger = new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F);

        drink(items, "apricot_juice", 0xFFEBA159, 3, 0.8F, haste);
        drink(items, "apricot_smoothie", 0xFFF6B87C, 2, 0.4F, haste);
        drink(items, "orange_juice", 0xFFE6A144, 3, 0.8F, haste);
        drink(items, "orange_smoothie", 0xFFFFBF61, 2, 0.4F, haste);
        drink(items, "elderberry_juice", 0xFF5A345F, 3, 0.4F, berryStrength);
        drink(items, "elderberry_smoothie", 0xFF925783, 2, 0.2F, berryStrength);
        drink(items, "peach_juice", 0xFFF0AE76, 2, 0.4F, hunger);
        drink(items, "peach_smoothie", 0xFFF5C6AC, 1, 0.2F, hunger);
        drink(items, "pear_juice", 0xFFDCC77A, 6, 0.4F, pearHunger);
        drink(items, "pear_smoothie", 0xFFE3D99D, 4, 0.2F, pearHunger);
        drink(items, "plum_juice", 0xFF87465F, 2, 0.4F, hunger);
        drink(items, "plum_smoothie", 0xFFB37296, 1, 0.2F, hunger);
        drink(items, "lime_juice", 0xFF90B957, 3, 0.8F, haste);
        drink(items, "lime_smoothie", 0xFFB6D573, 2, 0.4F, haste);
        drink(items, "grapefruit_juice", 0xFFE58575, 3, 0.8F, grapefruitHunger);
        drink(items, "grapefruit_smoothie", 0xFFF7B2A8, 2, 0.4F, grapefruitHunger);
        drink(items, "persimmon_juice", 0xFFDC7732, 3, 0.8F, haste);
        drink(items, "persimmon_smoothie", 0xFFEF9F54, 2, 0.4F, haste);
        drink(items, "starfruit_juice", 0xFFC8BF56, 3, 0.4F, hunger);
        drink(items, "starfruit_smoothie", 0xFFE1D972, 2, 0.2F, hunger);
        drink(items, "fig_juice", 0xFF916D8B, 2, 0.4F, hunger);
        drink(items, "fig_smoothie", 0xFFC194B6, 1, 0.2F, hunger);
        drink(items, "mango_juice", 0xFFF0B03D, 3, 0.4F, hunger);
        drink(items, "mango_smoothie", 0xFFF9CB67, 2, 0.2F, hunger);
        drink(items, "papaya_juice", 0xFFEE8A45, 3, 0.4F, hunger);
        drink(items, "papaya_smoothie", 0xFFF7B57E, 2, 0.2F, hunger);
        drink(items, "juice", 0xFFCB7652, 3, 0.4F, hunger);
        drink(items, "froot_smoothie", 0xFFE99E83, 2, 0.2F, hunger);
    }
}
