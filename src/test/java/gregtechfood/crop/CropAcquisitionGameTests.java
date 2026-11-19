package gregtechfood.crop;

import gregtechfood.GregTechFood;
import gregtech.content.data.MachineRecipeMaps;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GregTechFood.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CropAcquisitionGameTests {
    @GameTest(template = "test_empty")
    public static void plantGrowAndProcessTomato(GameTestHelper helper) {
        var farmland = new BlockPos(2, 3, 2);
        helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
        helper.setBlock(farmland.above(), Blocks.AIR);
        helper.setBlock(farmland.above().east(), Blocks.GLOWSTONE);
        helper.runAfterDelay(5, () -> checkTomatoHarvest(helper, farmland));
    }

    private static void checkTomatoHarvest(GameTestHelper helper, BlockPos farmland) {
        var farmlandPos = helper.absolutePos(farmland);
        var cropPos = farmlandPos.above();
        var level = helper.getLevel();
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getAbilities().mayBuild = true;
        var hit = new BlockHitResult(Vec3.atCenterOf(farmlandPos).add(0, 7.0 / 16, 0), Direction.UP, farmlandPos, false);

        helper.assertTrue(level.isEmptyBlock(cropPos) && Blocks.WHEAT.defaultBlockState().canSurvive(level, cropPos),
                "The test crop position needs empty, lit farmland. Brightness: " + level.getRawBrightness(cropPos, 0));
        var wheat = new ItemStack(Items.WHEAT_SEEDS);
        player.setItemInHand(InteractionHand.MAIN_HAND, wheat);
        helper.assertTrue(wheat.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)).consumesAction()
                        && level.getBlockState(cropPos).is(Blocks.WHEAT),
                "The test player can plant a vanilla wheat seed");
        level.removeBlock(cropPos, false);

        var tomatoSeed = item("tomato_seeds");
        var seeds = new ItemStack(tomatoSeed);
        player.setItemInHand(InteractionHand.MAIN_HAND, seeds);
        var crop = (CropBlock) Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(id("tomato_crop")));
        var planting = seeds.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        helper.assertTrue(planting.consumesAction(), "A tomato seed can be used on farmland. Result: " + planting);
        var planted = level.getBlockState(cropPos);
        helper.assertTrue(planted.is(crop) && crop.getAge(planted) == 0 && seeds.isEmpty(),
                "Planting consumes one seed and places the crop at age zero");

        var tomatoItem = item("tomato");
        var immature = Block.getDrops(planted, level, cropPos, null, player, ItemStack.EMPTY);
        helper.assertTrue(count(immature, tomatoSeed) == 1 && count(immature, tomatoItem) == 0,
                "An immature crop gives back its seed and yields no produce");

        var bonemeal = new ItemStack(Items.BONE_MEAL, 8);
        for (int attempt = 0; attempt < 8 && !crop.isMaxAge(level.getBlockState(cropPos)); attempt++) {
            helper.assertTrue(BoneMealItem.applyBonemeal(bonemeal, level, cropPos, player),
                    "An immature tomato crop accepts bonemeal");
        }
        var ripe = level.getBlockState(cropPos);
        helper.assertTrue(crop.isMaxAge(ripe), "The tomato crop reaches maturity");

        var harvest = Block.getDrops(ripe, level, cropPos, null, player, ItemStack.EMPTY);
        helper.assertTrue(count(harvest, tomatoItem) == 2 && count(harvest, tomatoSeed) == 2,
                "A mature tomato crop drops two tomatoes and two seeds");
        var tomato = harvest.stream().filter(stack -> stack.is(tomatoItem)).findFirst().orElseThrow();
        helper.assertTrue(tomato.is(ItemTags.create(ResourceLocation.parse("forge:crops/tomato"))),
                "The homegrown tomato is available through the shared Forge crop tag");

        var knife = ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(ResourceLocation.parse("forge:tools/knife")))
                .stream().findFirst().orElseThrow();
        var crafting = new TransientCraftingContainer(new CraftingMenu(0, player.getInventory()), 2, 2);
        crafting.setItem(0, new ItemStack(knife));
        crafting.setItem(2, tomato.copyWithCount(1));
        var slicing = (CraftingRecipe) level.getRecipeManager().byKey(id("produce/tomato_pieces")).orElseThrow();
        helper.assertTrue(slicing.matches(crafting, level), "The slicing recipe accepts a homegrown tomato");
        var pieces = slicing.assemble(crafting, level.registryAccess());
        helper.assertTrue(pieces.is(item("tomato_pieces")), "Slicing a homegrown tomato yields tomato pieces");

        var pressed = MachineRecipeMaps.Juicer.findRecipe(List.of(pieces), List.of(), true, 1, 3);
        var juice = ForgeRegistries.FLUIDS.getValue(id("tomato_juice"));
        helper.assertTrue(pressed != null && pressed.fluidOutputs.length == 1
                        && pressed.fluidOutputs[0].getFluid() == juice && pressed.fluidOutputs[0].getAmount() == 75,
                "A homegrown tomato can be pressed into 75 mB of addon tomato juice");

        crafting.clearContent();
        crafting.setItem(0, tomato.copyWithCount(1));
        crafting.setItem(1, new ItemStack(knife));
        var slicingThin = (CraftingRecipe) level.getRecipeManager().byKey(id("produce/tomato_slice")).orElseThrow();
        helper.assertTrue(slicingThin.matches(crafting, level), "The recipe for thin slices accepts homegrown tomatoes");
        var slices = slicingThin.assemble(crafting, level.registryAccess());
        helper.assertTrue(slices.is(item("tomato_slice")) && slices.getCount() == 4,
                "Thinly slicing a homegrown tomato yields four tomato slices");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void bottleKetchupAndPrepareVegetablePizza(GameTestHelper helper) {
        var tomatoJuice = fluid("tomato_juice", 750);
        var ketchup = fluid("tomato_ketchup", 750);
        var mixed = MachineRecipeMaps.Mixer.findRecipe(List.of(new ItemStack(Items.SUGAR)), List.of(tomatoJuice), true, 1, 3);
        helper.assertTrue(mixed != null && mixed.fluidOutputs.length == 1
                        && mixed.fluidOutputs[0].isFluidEqual(ketchup) && mixed.fluidOutputs[0].getAmount() == 750,
                "The mixer makes 750 mB of ketchup from addon tomato juice and sugar");

        var filled = MachineRecipeMaps.Canner.findRecipe(List.of(new ItemStack(Items.GLASS_BOTTLE)),
                List.of(mixed.fluidOutputs[0]), true, 2, 2);
        helper.assertTrue(filled != null && filled.itemOutputs[0].is(item("tomato_ketchup"))
                        && filled.fluidInputs[0].getAmount() == 250,
                "A glass bottle takes 250 mB of ketchup in the canner");

        var level = helper.getLevel();
        var inventory = helper.makeMockPlayer().getInventory();
        var crafting = new TransientCraftingContainer(new CraftingMenu(0, inventory), 2, 2);
        crafting.setItem(0, filled.itemOutputs[0].copy());
        crafting.setItem(1, new ItemStack(item("flattened_dough")));
        var topping = (CraftingRecipe) level.getRecipeManager().byKey(id("bakery/flat_dough_with_ketchup_1")).orElseThrow();
        helper.assertTrue(topping.matches(crafting, level), "The pizza dough accepts addon bottled ketchup");
        var toppedDough = topping.assemble(crafting, level.registryAccess());
        helper.assertTrue(toppedDough.is(item("flat_dough_with_ketchup"))
                        && topping.getRemainingItems(crafting).get(0).is(Items.GLASS_BOTTLE),
                "Topping dough with bottled ketchup leaves an empty glass bottle");

        crafting.clearContent();
        crafting.setItem(0, toppedDough);
        crafting.setItem(1, new ItemStack(item("cucumber_slice")));
        crafting.setItem(2, new ItemStack(item("tomato_slice")));
        crafting.setItem(3, new ItemStack(item("onion_slice")));
        var pizza = (CraftingRecipe) level.getRecipeManager().byKey(id("bakery/raw_veggie_pizza_1")).orElseThrow();
        helper.assertTrue(pizza.matches(crafting, level) && pizza.assemble(crafting, level.registryAccess()).is(item("raw_veggie_pizza")),
                "The vegetable pizza recipe accepts all three addon slices");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void discoverGrassSeeds(GameTestHelper helper) {
        var expected = seedIds("tomato", "onion", "cucumber", "blackberry", "blueberry", "raspberry",
                "cranberry", "gooseberry", "strawberry", "ananas", "green_grapes", "white_grapes",
                "red_grapes", "purple_grapes", "black_currants", "white_currants", "red_currants", "chili_pepper",
                "mint_leaves", "peanut", "candleberry", "anise", "hops", "barley", "corn", "rye",
                "soybean", "rice", "spice_leaf");
        for (Block grass : List.of(Blocks.GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN)) {
            var drops = sampleDrops(helper, grass, ItemStack.EMPTY, 1024);
            assertSeeds(helper, grass, drops, expected);
            if (grass == Blocks.GRASS) {
                helper.assertTrue(drops.contains(Items.WHEAT_SEEDS), "Grass still drops vanilla wheat seeds");
            }
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void discoverTreeSeedsWithoutSilkTouch(GameTestHelper helper) {
        var silkTouch = new ItemStack(Items.DIAMOND_AXE);
        silkTouch.enchant(Enchantments.SILK_TOUCH, 1);
        var shears = new ItemStack(Items.SHEARS);
        for (Block leaves : List.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES)) {
            var expected = leaves == Blocks.JUNGLE_LEAVES
                    ? seedIds("banana", "black_pepper", "coconut", "kiwi", "coffee_beans", "vanilla_bean", "orange",
                            "lime", "grapefruit", "persimmon", "starfruit", "fig", "mango", "papaya")
                    : seedIds("lemon", "pomegranate", "hazelnut", "pistachio", "cherry", "tea_leaves",
                            "apple", "apple_2", "apple_3", "apricot", "elderberry", "peach",
                            "pear", "plum", "almond", "juniper_berries");
            var drops = sampleDrops(helper, leaves, ItemStack.EMPTY, 1024);
            assertSeeds(helper, leaves, drops, expected);
            assertSeeds(helper, leaves, sampleDrops(helper, leaves, silkTouch, 256), Set.of());
            var sheared = sampleDrops(helper, leaves, shears, 256);
            assertSeeds(helper, leaves, sheared, Set.of());
            helper.assertTrue(sheared.contains(leaves.asItem()), "Shears still collect the vanilla leaf block");
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void discoverAgaveAndPressBrewingJuices(GameTestHelper helper) {
        var drops = sampleDrops(helper, Blocks.CACTUS, ItemStack.EMPTY, 128);
        assertSeeds(helper, Blocks.CACTUS, drops, seedIds("agave"));
        helper.assertTrue(drops.contains(Items.CACTUS), "Cacti still drop their block item");

        for (String crop : List.of("apricot", "orange", "elderberry", "peach", "pear", "plum", "agave")) {
            var harvested = sampleDrops(helper, ForgeRegistries.BLOCKS.getValue(id(crop + "_crop")),
                    ItemStack.EMPTY, 1, true);
            helper.assertTrue(harvested.contains(item(crop)), "Mature crop yields " + crop);
            int squeezedAmount = crop.equals("elderberry") ? 125 : 150;
            var input = List.of(new ItemStack(item(crop)));
            var juiced = MachineRecipeMaps.Juicer.findRecipe(input, List.of(), true, 1, 4);
            var pressed = MachineRecipeMaps.Squeezer.findRecipe(input, List.of(), true, 1, 4);
            helper.assertTrue(juiced != null && juiced.fluidOutputs.length == 1
                    && juiced.fluidOutputs[0].isFluidStackIdentical(fluid(crop + "_juice", squeezedAmount - 25)),
                    "Juicer accepts addon " + crop);
            helper.assertTrue(pressed != null && pressed.fluidOutputs.length == 1
                    && pressed.fluidOutputs[0].isFluidStackIdentical(fluid(crop + "_juice", squeezedAmount)),
                    "Squeezer accepts addon " + crop);
        }
        helper.succeed();
    }

    private static Set<Item> sampleDrops(GameTestHelper helper, Block block, ItemStack tool, int samples) {
        return sampleDrops(helper, block, tool, samples, false);
    }

    private static Set<Item> sampleDrops(GameTestHelper helper, Block block, ItemStack tool, int samples, boolean mature) {
        var level = helper.getLevel();
        var params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(new BlockPos(2, 1, 2))))
                .withParameter(LootContextParams.BLOCK_STATE, mature
                        ? block.defaultBlockState().setValue(CropBlock.AGE, 7) : block.defaultBlockState())
                .withParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.BLOCK);
        var table = level.getServer().getLootData().getLootTable(block.getLootTable());
        var random = RandomSource.create(0x583E15FA0D9C2B47L);
        Set<Item> drops = new HashSet<>();
        for (int i = 0; i < samples; i++) {
            for (ItemStack stack : table.getRandomItems(params, random.nextLong() | 1L)) {
                drops.add(stack.getItem());
            }
        }
        return drops;
    }

    private static void assertSeeds(GameTestHelper helper, Block block, Set<Item> drops, Set<ResourceLocation> expected) {
        var actual = drops.stream().map(ForgeRegistries.ITEMS::getKey).filter(Objects::nonNull)
                .filter(key -> key.getNamespace().equals(GregTechFood.MOD_ID) && key.getPath().endsWith("_seeds"))
                .collect(Collectors.toSet());
        helper.assertTrue(actual.equals(expected),
                block.getDescriptionId() + " seed drops were " + actual + ", expected " + expected);
    }

    private static Set<ResourceLocation> seedIds(String... crops) {
        var ids = new HashSet<ResourceLocation>();
        for (String crop : crops) ids.add(id(crop + "_seeds"));
        return ids;
    }

    private static int count(List<ItemStack> drops, Item item) {
        return drops.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }

    private static Item item(String path) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id(path)), path);
    }

    private static FluidStack fluid(String path, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(id(path)), path), amount);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID, path);
    }

    private CropAcquisitionGameTests() {}
}
