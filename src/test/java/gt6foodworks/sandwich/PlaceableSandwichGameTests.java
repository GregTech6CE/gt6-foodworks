package gt6foodworks.sandwich;

import gt6foodworks.GT6Foodworks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GT6Foodworks.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PlaceableSandwichGameTests {
    @GameTest(template = "test_empty")
    public static void layerCutPlaceAndEatSandwich(GameTestHelper helper) {
        for (String id : List.of("canned_ham", "canned_salmon", "canned_emerald_green", "canned_chum")) {
            helper.assertTrue(SandwichLayers.forItem(addon(id, 1)) != null, "Missing sandwich layer for " + id);
        }

        var base = new BlockPos(2, 3, 2);
        helper.setBlock(base, Blocks.STONE);
        var support = helper.absolutePos(base);
        var pos = support.above();
        var level = helper.getLevel();
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayBuild = true;
        player.setShiftKeyDown(true);

        ItemStack toast = addon("toast", 5);
        player.setItemInHand(InteractionHand.MAIN_HAND, toast);
        var placement = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, support, top(support));
        MinecraftForge.EVENT_BUS.post(placement);
        helper.assertTrue(placement.isCanceled() && toast.isEmpty(), "Sneak-use places a batch from five toast slices");
        var state = level.getBlockState(pos);
        var sandwich = (SandwichBlockEntity) level.getBlockEntity(pos);
        helper.assertTrue(sandwich != null && sandwich.height() == 2 && sandwich.harvest().getCount() == 5,
                "The initial batch contains five servings of one layer");

        var fish = addon("tiny_food_can_fish", 5);
        player.setShiftKeyDown(false);
        use(player, pos, fish);
        helper.assertTrue(fish.isEmpty() && player.getInventory().countItem(addon("empty_food_can", 1).getItem()) == 5,
                "Adding a layer from five servings of canned fish gives back five cans");
        var ketchup = addon("tomato_ketchup", 3);
        use(player, pos, ketchup);
        helper.assertTrue(ketchup.getCount() == 1 && player.getInventory().countItem(Items.GLASS_BOTTLE) == 2,
                "Five servings need two ketchup bottles, both returned");
        var duplicate = addon("tomato_ketchup", 2);
        use(player, pos, duplicate);
        helper.assertTrue(duplicate.getCount() == 2, "Identical adjacent layers are rejected");
        use(player, pos, addon("cheese_slice", 5));
        use(player, pos, addon("toasted_toast", 5));
        use(player, pos, new ItemStack(Items.REDSTONE, 5));
        helper.assertTrue(sandwich.height() == 8 && state.getAnalogOutputSignal(level, pos) == 7
                        && state.getSignal(level, pos, Direction.NORTH) == 7,
                "Layer thickness and redstone update both signal outputs");

        var restored = new SandwichBlockEntity(pos, state);
        restored.load(sandwich.saveWithoutMetadata());
        helper.assertTrue(restored.harvest().getCount() == 5 && restored.height() == 8
                        && restored.emitsRedstone() && restored.layerAt(4).is(ketchup.getItem()),
                "Block NBT retains the batch quantity, redstone and spread");
        var clientCopy = new SandwichBlockEntity(pos, state);
        clientCopy.load(sandwich.getUpdateTag());
        helper.assertTrue(clientCopy.visualAt(4).texture().equals("condiment") && clientCopy.height() == 8,
                "Clients receive the layers and their height");

        var knife = ForgeRegistries.ITEMS.tags()
                .getTag(ItemTags.create(ResourceLocation.parse("forge:tools/knife")))
                .stream().findFirst().orElseThrow();
        use(player, pos, new ItemStack(knife));
        var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1), entity ->
                entity.getItem().is(SandwichBlock.item.get()));
        helper.assertTrue(level.isEmptyBlock(pos) && drops.size() == 1 && drops.get(0).getItem().getCount() == 5,
                "Using a knife cuts the batch into five identical edible portions");
        var servings = drops.get(0).getItem().copy();
        drops.get(0).discard();
        player.setItemInHand(InteractionHand.MAIN_HAND, servings);
        player.setShiftKeyDown(true);
        helper.assertTrue(servings.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, top(support))).consumesAction(),
                "A cut portion can be placed again");
        var placedAgain = (SandwichBlockEntity) level.getBlockEntity(pos);
        helper.assertTrue(servings.getCount() == 4 && placedAgain != null
                        && placedAgain.harvest().getCount() == 1 && placedAgain.height() == 8,
                "Re-placing a cut portion preserves its layers and creates one serving");

        player.setShiftKeyDown(false);
        player.getFoodData().setFoodLevel(0);
        helper.assertTrue(servings.getItem().use(level, player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                "The player can eat a portion from their hand");
        servings.finishUsingItem(level, player);
        player.stopUsingItem();
        helper.assertTrue(servings.getCount() == 3 && player.getFoodData().getFoodLevel() == 9,
                "Eating one portion grants the total food from its five layers");
        helper.succeed();
    }

    private static void use(Player player, BlockPos pos, ItemStack stack) {
        var level = player.level();
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        level.getBlockState(pos).use(level, player, InteractionHand.MAIN_HAND, top(pos));
    }

    private static BlockHitResult top(BlockPos pos) {
        return new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
    }

    private static ItemStack addon(String id, int amount) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(GT6Foodworks.MOD_ID, id)), amount);
    }
}
