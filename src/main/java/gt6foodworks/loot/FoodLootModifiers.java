package gt6foodworks.loot;

import com.mojang.serialization.Codec;
import gt6foodworks.GT6Foodworks;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FoodLootModifiers {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, GT6Foodworks.MOD_ID);

    private FoodLootModifiers() {}

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register("extra_loot", () -> ExtraLootModifier.CODEC);
        LOOT_MODIFIERS.register("migrated_food", () -> MigratedFoodLootModifier.CODEC);
        LOOT_MODIFIERS.register(bus);
    }
}
