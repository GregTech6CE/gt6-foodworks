package gregtechfood;

import static gregtechfood.GregTechFood.MOD_ID;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

final class PlaceableSandwichEntity extends BlockEntity {
    static final int HEIGHT = 16;
    private static final String INGREDIENTS = "Ingredients";
    private static final String SERVINGS = "Servings";
    private static final String REDSTONE = "Redstone";

    private ItemStack[] layers = sample();
    private int servings = 1;
    private boolean redstone;

    PlaceableSandwichEntity(BlockPos pos, BlockState state) {
        super(PlaceableSandwich.BLOCK_ENTITY.get(), pos, state);
    }

    private static ItemStack[] emptyLayers() {
        ItemStack[] result = new ItemStack[HEIGHT];
        Arrays.fill(result, ItemStack.EMPTY);
        return result;
    }

    private static ItemStack[] sample() {
        ItemStack[] result = emptyLayers();
        String[] ingredients = {"toast", "cooked_ham_slice", "cucumber_slice", "cheese_slice", "onion_slice",
                "tomato_ketchup", "tomato_slice", "mayo", "pickle_slice", "toast"};
        int height = 0;
        for (String name : ingredients) {
            var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
            if (item == null || item == Items.AIR) continue;
            ItemStack stack = new ItemStack(item);
            var layer = SandwichLayers.forItem(stack);
            result[height] = stack;
            height += layer.thickness();
        }
        return result;
    }

    ItemStack layerAt(int height) {
        return layers[height].copy();
    }

    SandwichLayers.Layer visualAt(int height) {
        return SandwichLayers.forItem(layers[height]);
    }

    int height() {
        for (int slot = layers.length - 1; slot >= 0; slot--) {
            var layer = SandwichLayers.forItem(layers[slot]);
            if (layer != null) return slot + layer.thickness();
        }
        return 1;
    }

    boolean emitsRedstone() {
        return redstone;
    }

    private boolean isSameAsTop(ItemStack stack) {
        for (int slot = layers.length - 1; slot >= 0; slot--) {
            if (!layers[slot].isEmpty()) return ItemStack.isSameItem(layers[slot], stack);
        }
        return false;
    }

    private int quantity(ItemStack ingredient) {
        var container = ingredient.getCraftingRemainingItem();
        boolean bottle = container.is(Items.GLASS_BOTTLE)
                || ForgeRegistries.ITEMS.getKey(container.getItem()).equals(ResourceLocation.parse("gt6ce:bottle_empty"));
        return bottle ? (servings + 3) / 4 : servings;
    }

    boolean canAdd(ItemStack ingredient) {
        if (ingredient.isEmpty() || ingredient.getCount() < quantity(ingredient)) return false;
        if (ingredient.is(Items.REDSTONE)) return !redstone;
        var layer = SandwichLayers.forItem(ingredient);
        return layer != null && height() + layer.thickness() <= HEIGHT && !isSameAsTop(ingredient);
    }

    int add(ItemStack ingredient) {
        if (!canAdd(ingredient)) return 0;
        if (ingredient.is(Items.REDSTONE)) {
            redstone = true;
        } else {
            layers[height()] = ingredient.copyWithCount(1);
        }
        setChanged();
        if (level != null && !level.isClientSide) {
            var state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
            level.updateNeighborsAt(worldPosition, state.getBlock());
            level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
        }
        return quantity(ingredient);
    }

    ItemStack harvest() {
        var base = layers[0];
        if (!redstone && SandwichLayers.isStarter(base) && height() == SandwichLayers.forItem(base).thickness()) {
            return base.copyWithCount(servings);
        }
        return toItem(servings);
    }

    ItemStack toItem(int count) {
        ItemStack stack = new ItemStack(PlaceableSandwich.item.get(), count);
        BlockItem.setBlockEntityData(stack, PlaceableSandwich.BLOCK_ENTITY.get(), write(layers, 1, redstone));
        return stack;
    }

    static ItemStack starterItem(ItemStack toast, int servings) {
        var layers = emptyLayers();
        layers[0] = toast.copyWithCount(1);
        var stack = new ItemStack(PlaceableSandwich.item.get());
        BlockItem.setBlockEntityData(stack, PlaceableSandwich.BLOCK_ENTITY.get(), write(layers, servings, false));
        return stack;
    }

    static ItemStack[] itemLayers(ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        return data != null && data.contains(INGREDIENTS, Tag.TAG_LIST) ? read(data) : sample();
    }

    private static ItemStack[] read(CompoundTag data) {
        var layers = emptyLayers();
        var rows = data.getList(INGREDIENTS, Tag.TAG_COMPOUND);
        for (int index = 0; index < rows.size(); index++) {
            CompoundTag row = rows.getCompound(index);
            int height = Byte.toUnsignedInt(row.getByte("Slot"));
            ItemStack stack = ItemStack.of(row.getCompound("Stack"));
            var layer = SandwichLayers.forItem(stack);
            if (layer != null && height + layer.thickness() <= HEIGHT) {
                layers[height] = stack.copyWithCount(1);
            }
        }
        return layers[0].isEmpty() ? sample() : layers;
    }

    private static CompoundTag write(ItemStack[] layers, int servings, boolean redstone) {
        var data = new CompoundTag();
        var rows = new ListTag();
        for (int height = 0; height < HEIGHT; height++) {
            if (layers[height].isEmpty()) continue;
            var row = new CompoundTag();
            row.putByte("Slot", (byte) height);
            row.put("Stack", layers[height].save(new CompoundTag()));
            rows.add(row);
        }
        data.put(INGREDIENTS, rows);
        data.putInt(SERVINGS, servings);
        data.putBoolean(REDSTONE, redstone);
        return data;
    }

    @Override
    public void load(CompoundTag data) {
        super.load(data);
        if (data.contains(INGREDIENTS, Tag.TAG_LIST)) layers = read(data);
        servings = Math.max(1, Math.min(HEIGHT, data.getInt(SERVINGS)));
        redstone = data.getBoolean(REDSTONE);
    }

    @Override
    protected void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.merge(write(layers, servings, redstone));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
