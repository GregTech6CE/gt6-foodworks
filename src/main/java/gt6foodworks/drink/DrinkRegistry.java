package gt6foodworks.drink;

import static gt6foodworks.GT6Foodworks.MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

public final class DrinkRegistry {
    private static final ResourceLocation STILL_TEXTURE = ResourceLocation.parse("minecraft:block/water_still");
    private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.parse("minecraft:block/water_flow");
    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);
    private static final List<RegistryObject<Item>> CREATIVE_ITEMS = new ArrayList<>();

    private DrinkRegistry() {}

    public static void register(IEventBus bus, DeferredRegister<Item> items) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);

        FruitDrinks.register(items);
        OtherDrinks.register(items);
        BeerDrinks.register(items);
        SpiritDrinks.register(items);
        LiqueurDrinks.register(items);
        BrewingAndCondiments.register(items);
        FruitDrinks.registerOrchardDrinks(items);
        SpecialConsumables.register(items);
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
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
            return new BottledFoodItem(food.build(), animation, curesEffects, extinguishes, explosive, glint);
        });
    }

    public static void registerBottle(DeferredRegister<Item> items, String id, int tint, Supplier<Item> bottle) {
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
