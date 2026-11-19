package gregtechfood;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = GregTechFood.MOD_ID)
public final class MigratedFoodLoot extends LootModifier {
    static final Codec<MigratedFoodLoot> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, MigratedFoodLoot::new));
    private static final String CORE = "gt6ce";

    private MigratedFoodLoot(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> drops, LootContext context) {
        drops.replaceAll(MigratedFoodLoot::migrate);
        return drops;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    static ItemStack migrate(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null || !id.getNamespace().equals(CORE)) {
            return stack;
        }
        Item item = addonItem(id.getPath());
        if (item == null || item == Items.AIR) {
            return stack;
        }
        return replace(stack, item, stack.getCount());
    }

    private static ItemStack replace(ItemStack stack, Item item, int count) {
        ItemStack replacement = new ItemStack(item, count);
        if (stack.hasTag()) {
            replacement.setTag(stack.getTag().copy());
        }
        return replacement;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void migrateAnimalDrops(LivingDropsEvent event) {
        var animal = event.getEntity();
        if (animal.level().isClientSide || animal instanceof Player) {
            return;
        }
        // GT6CE 1.0.8 modifies the collection at LOWEST. It loads before this addon.
        for (ItemEntity drop : event.getDrops()) {
            drop.setItem(migrate(drop.getItem()));
        }
        if (animal.isBaby()) {
            return;
        }
        String meat = animal instanceof Wolf ? "dogmeat"
                : animal instanceof Donkey ? "donkey_meat"
                : animal instanceof Mule ? "mule_meat"
                : animal instanceof Horse ? "horse_meat" : null;
        if (meat != null && !coreHas(meat)) {
            boolean wolf = animal instanceof Wolf;
            int looting = Math.max(0, event.getLootingLevel());
            int amount = (wolf ? 0 : 1) + animal.getRandom().nextInt(3);
            amount += looting > 0 ? animal.getRandom().nextInt(looting + 1) : 0;
            if (amount > 0) {
                Item item = addonItem(animal.isOnFire() ? "grilled_" + meat : meat);
                event.getDrops().add(new ItemEntity(animal.level(), animal.getX(), animal.getY(), animal.getZ(),
                        new ItemStack(item, Math.min(amount, item.getMaxStackSize()))));
            }
        }
        boolean ownsPork = !coreHas("raw_ham");
        boolean ownsBeef = !coreHas("raw_ribs");
        if (ownsPork || ownsBeef) {
            for (ItemEntity drop : event.getDrops()) {
                replaceVanillaCut(drop, ownsPork, ownsBeef, animal.getRandom());
            }
        }
    }

    private static void replaceVanillaCut(ItemEntity drop, boolean ownsPork, boolean ownsBeef, RandomSource random) {
        ItemStack stack = drop.getItem();
        boolean pork = ownsPork && (stack.is(Items.PORKCHOP) || stack.is(Items.COOKED_PORKCHOP));
        boolean beef = ownsBeef && (stack.is(Items.BEEF) || stack.is(Items.COOKED_BEEF));
        if (!pork && !beef) {
            return;
        }
        int cut = random.nextInt(3);
        if (cut == 2) {
            return;
        }
        String id;
        if (pork) {
            boolean cooked = stack.is(Items.COOKED_PORKCHOP);
            id = cut == 0 ? (cooked ? "cooked_ham" : "raw_ham") : (cooked ? "grilled_bacon" : "raw_bacon");
        } else {
            boolean cooked = stack.is(Items.COOKED_BEEF);
            id = cut == 0 ? (cooked ? "grilled_ribs" : "raw_ribs") : (cooked ? "grilled_rib_eye_steak" : "raw_rib_eye_steak");
        }
        Item item = addonItem(id);
        int count = stack.getCount();
        if (pork && cut == 1) {
            count = Math.min(item.getMaxStackSize(), count * (3 + random.nextInt(3)));
        }
        drop.setItem(replace(stack, item, count));
    }

    private static boolean coreHas(String item) {
        return ForgeRegistries.ITEMS.containsKey(ResourceLocation.fromNamespaceAndPath(CORE, item));
    }

    private static Item addonItem(String path) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID, path));
    }
}
