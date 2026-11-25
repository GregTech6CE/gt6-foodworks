package gt6foodworks.sandwich;

import static gt6foodworks.GT6Foodworks.MOD_ID;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SandwichBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    private static final String ID = "sandwich_block";
    private static final TagKey<Item> KNIVES = TagKey.create(ForgeRegistries.Keys.ITEMS,
            ResourceLocation.parse("forge:tools/knife"));
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    static final RegistryObject<Block> BLOCK = BLOCKS.register(ID, SandwichBlock::new);
    static final RegistryObject<BlockEntityType<SandwichBlockEntity>> BLOCK_ENTITY = BLOCK_ENTITIES.register(
            ID, () -> BlockEntityType.Builder.of(SandwichBlockEntity::new, BLOCK.get()).build(null));
    static RegistryObject<SandwichBlockItem> item;

    private SandwichBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.25F)
                .sound(SoundType.WOOL).noOcclusion().dynamicShape().pushReaction(PushReaction.DESTROY));
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public static void register(IEventBus bus, DeferredRegister<Item> items) {
        item = items.register(ID, () -> new SandwichBlockItem(BLOCK.get()));
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MinecraftForge.EVENT_BUS.addListener(SandwichBlock::onToastUse);
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        event.accept(item);
    }

    private static void onToastUse(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = placeFromToast(event.getLevel(), event.getEntity(), event.getHand(), event.getHitVec());
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static InteractionResult placeFromToast(Level level, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack toast = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || !player.isShiftKeyDown() || !SandwichLayers.isStarter(toast)
                || level.getBlockState(hit.getBlockPos()).is(BLOCK.get())) {
            return InteractionResult.PASS;
        }
        int servings = Math.min(SandwichBlockEntity.HEIGHT, toast.getCount());
        ItemStack batch = SandwichBlockEntity.starterItem(toast, servings);
        var context = new BlockPlaceContext(level, player, hand, batch, hit);
        BlockPos pos = context.getClickedPos();
        if (!player.mayBuild() || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, context.getClickedFace(), toast)) {
            return InteractionResult.PASS;
        }
        InteractionResult result = batch.useOn(new UseOnContext(level, player, hand, batch, hit));
        if (result.consumesAction() && !level.isClientSide && !player.getAbilities().instabuild) {
            toast.shrink(servings);
        }
        return result;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SandwichBlockEntity sandwich)
                || !player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return InteractionResult.PASS;
        if (held.is(KNIVES)) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (level.destroyBlock(pos, true, player)) {
                held.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
            }
            return InteractionResult.CONSUME;
        }
        if (!sandwich.canAdd(held)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            int consumed = sandwich.add(held);
            if (!player.getAbilities().instabuild) {
                ItemStack remainder = held.getCraftingRemainingItem();
                held.shrink(consumed);
                if (!remainder.isEmpty()) {
                    remainder.setCount(remainder.getCount() * consumed);
                    if (!player.getInventory().add(remainder)) player.drop(remainder, false);
                }
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SandwichBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int height = level.getBlockEntity(pos) instanceof SandwichBlockEntity sandwich ? sandwich.height() : 2;
        return Block.box(0.5, 0, 0.5, 15.5, height, 15.5);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, water);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level,
                                  BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (side == Direction.DOWN && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, side, neighborState, level, pos, neighborPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder loot) {
        return loot.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SandwichBlockEntity sandwich
                ? List.of(sandwich.harvest()) : List.of();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos) instanceof SandwichBlockEntity sandwich
                ? sandwich.toItem(1) : new ItemStack(item.get());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SandwichBlockEntity sandwich ? sandwich.height() - 1 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return level.getBlockEntity(pos) instanceof SandwichBlockEntity sandwich && sandwich.emitsRedstone()
                ? sandwich.height() - 1 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return getSignal(state, level, pos, side);
    }
}
