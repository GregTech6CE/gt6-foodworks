package gt6foodworks.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

final class ExtraLootModifier extends LootModifier {
    static final Codec<ExtraLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance)
                    .and(instance.group(
                            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(modifier -> modifier.lootTable),
                            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC)
                                    .optionalFieldOf("skip_drops_if_item_exists", Map.of())
                                    .forGetter(modifier -> modifier.skipDropsIfItemExists)))
                    .apply(instance, ExtraLootModifier::new));

    private final ResourceLocation lootTable;
    private final Map<ResourceLocation, ResourceLocation> skipDropsIfItemExists;

    private ExtraLootModifier(LootItemCondition[] conditions, ResourceLocation lootTable,
                              Map<ResourceLocation, ResourceLocation> skipDropsIfItemExists) {
        super(conditions);
        this.lootTable = lootTable;
        this.skipDropsIfItemExists = skipDropsIfItemExists;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        context.getResolver().getLootTable(lootTable).getRandomItemsRaw(context, stack -> {
            var itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            var substitute = itemId == null ? null : skipDropsIfItemExists.get(itemId);
            if (substitute == null || !ForgeRegistries.ITEMS.containsKey(substitute)) {
                generatedLoot.add(stack);
            }
        });
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
