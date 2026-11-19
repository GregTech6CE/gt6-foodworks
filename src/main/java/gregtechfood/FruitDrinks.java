package gregtechfood;

import static gregtechfood.GregTechFood.MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

final class FruitDrinks {
    private static final ResourceLocation STILL_TEXTURE = ResourceLocation.parse("minecraft:block/water_still");
    private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.parse("minecraft:block/water_flow");
    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);
    private static final List<RegistryObject<Item>> CREATIVE_ITEMS = new ArrayList<>();

    private FruitDrinks() {}

    static void register(IEventBus bus, DeferredRegister<Item> items) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);

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
        registerBottle(items, "mayo", 0xFFEADF9F, () -> new BottledDrinkItem(
                new FoodProperties.Builder().nutrition(3).saturationMod(0.5F).build(), UseAnim.EAT, false));
        fluid("agave_juice", 0xFFA8BE7C);
        OtherDrinks.register(items);
        BeerDrinks.register(items);
        SpiritDrinks.register(items);
        LiqueurDrinks.register(items);
        BrewingAndCondiments.register(items);
        RemainingFruitDrinks.register(items);
        SpecialFoods.register(items);
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_ITEMS.forEach(event::accept);
    }

    static void pieces(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        CREATIVE_ITEMS.add(items.register(id, () -> {
            var properties = new Item.Properties();
            if (nutrition > 0) {
                properties.food(new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build());
            }
            return new Item(properties);
        }));
    }

    static void drink(DeferredRegister<Item> items, String id, int tint, int nutrition,
                      float saturation, DrinkEffect... effects) {
        drink(items, id, tint, nutrition, saturation, UseAnim.DRINK, false, false, false, false, effects);
    }

    static void drink(DeferredRegister<Item> items, String id, int tint, int nutrition, float saturation,
                      UseAnim animation, boolean curesEffects, boolean extinguishes, boolean explosive,
                      boolean glint, DrinkEffect... effects) {
        registerBottle(items, id, tint, () -> {
            var food = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
            for (DrinkEffect effect : effects) {
                food.effect(() -> new MobEffectInstance(effect.type(), effect.duration(), effect.amplifier(),
                                false, effect.visible()),
                        effect.chance());
            }
            return new BottledDrinkItem(food.build(), animation, curesEffects, extinguishes, explosive, glint);
        });
    }

    static void registerBottle(DeferredRegister<Item> items, String id, int tint, Supplier<Item> bottle) {
        fluid(id, tint);
        CREATIVE_ITEMS.add(items.register(id, bottle));
    }

    static void fluid(String id, int tint) {
        var type = FLUID_TYPES.register(id, () -> new FluidType(FluidType.Properties.create()) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return STILL_TEXTURE;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return FLOWING_TEXTURE;
                    }

                    @Override
                    public int getTintColor() {
                        return tint;
                    }
                });
            }
        });
        var source = ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
        var flowing = ResourceLocation.fromNamespaceAndPath(MOD_ID, "flowing_" + id);
        var properties = new ForgeFlowingFluid.Properties(type,
                () -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(source), source.toString()),
                () -> Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(flowing), flowing.toString()));
        FLUIDS.register(id, () -> new ForgeFlowingFluid.Source(properties));
        FLUIDS.register("flowing_" + id, () -> new ForgeFlowingFluid.Flowing(properties));
    }

    record DrinkEffect(MobEffect type, int duration, int amplifier, float chance, boolean visible) {
        DrinkEffect(MobEffect type, int duration, int amplifier, float chance) {
            this(type, duration, amplifier, chance, true);
        }
    }
}
