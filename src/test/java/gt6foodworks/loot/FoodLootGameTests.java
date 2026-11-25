package gt6foodworks.loot;

import gt6foodworks.GT6Foodworks;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GT6Foodworks.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoodLootGameTests {
    @GameTest(template = "test_empty")
    public static void migrateLegacyFoodLoot(GameTestHelper helper) {
        ItemStack old = stack("gt6ce:raw_ham");
        old.setCount(2);
        old.setHoverName(Component.literal("Smoked ham"));
        var converted = MigratedFoodLootModifier.migrate(old);
        helper.assertTrue(converted.is(stack("gt6food:raw_ham").getItem()) && converted.getCount() == 2
                && converted.getHoverName().equals(old.getHoverName()), "Preserve count and name when migrating loot");
        helper.assertTrue(MigratedFoodLootModifier.migrate(new ItemStack(Items.IRON_INGOT)).is(Items.IRON_INGOT),
                "Ordinary loot stays in the original registry");
        helper.succeed();
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id));
    }

    private FoodLootGameTests() {}
}
