package gregtechfood;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.IntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GregTechFood.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SandwichRenderer implements BlockEntityRenderer<PlaceableSandwichEntity> {
    private static BlockEntityWithoutLevelRenderer itemRenderer;

    private SandwichRenderer(BlockEntityRendererProvider.Context context) {}

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PlaceableSandwich.BLOCK_ENTITY.get(), SandwichRenderer::new);
    }

    static BlockEntityWithoutLevelRenderer itemRenderer() {
        if (itemRenderer == null) {
            var client = Minecraft.getInstance();
            itemRenderer = new BlockEntityWithoutLevelRenderer(client.getBlockEntityRenderDispatcher(),
                    client.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                                         MultiBufferSource buffers, int light, int overlay) {
                    ItemStack[] layers = PlaceableSandwichEntity.itemLayers(stack);
                    renderLayers(slot -> SandwichLayers.forItem(layers[slot]), pose.last(), buffers, light, overlay);
                }
            };
        }
        return itemRenderer;
    }

    @Override
    public void render(PlaceableSandwichEntity sandwich, float tickDelta, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        renderLayers(sandwich::visualAt, pose.last(), buffers, light, overlay);
    }

    private static void renderLayers(IntFunction<SandwichLayers.Layer> layers, PoseStack.Pose pose,
                                     MultiBufferSource buffers, int light, int overlay) {
        int previousFootprint = 14;
        for (int slot = 0; slot < PlaceableSandwichEntity.HEIGHT; slot++) {
            var layer = layers.apply(slot);
            if (layer == null) continue;
            var top = texture(layer, "_top");
            var side = texture(layer, "_sides");
            int tint = layer.tint();
            float bottom = slot / 16F;
            float height = (slot + layer.thickness()) / 16F;
            int footprint = layer.footprint();
            if (footprint == 4) {
                for (int x = 1; x <= 9; x += 8) {
                    for (int z = 1; z <= 9; z += 8) {
                        drawBox(pose, buffers, top, side, light, overlay, tint,
                                x / 16F, bottom, z / 16F, (x + 6) / 16F, height, (z + 6) / 16F);
                    }
                }
            } else {
                float inset = (16 - footprint) / 32F;
                if (footprint == 0) {
                    float offset = (16 - slot) * 0.0005F;
                    inset = (previousFootprint < 14 ? 2F : 1F) / 16F - offset;
                    bottom = (previousFootprint < 14 ? 0.5F : Math.max(0F, slot - 0.5F)) / 16F;
                }
                drawBox(pose, buffers, top, side, light, overlay, tint,
                        inset, bottom, inset, 1F - inset, height, 1F - inset);
            }
            previousFootprint = footprint;
        }
    }

    private static RenderType texture(SandwichLayers.Layer layer, String side) {
        var id = ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID,
                "textures/block/sandwiches/" + layer.texture() + side + ".png");
        return RenderType.entityCutout(id);
    }

    private static void drawBox(PoseStack.Pose pose, MultiBufferSource buffers, RenderType top, RenderType side,
                                int light, int overlay, int tint,
                                float x0, float y0, float z0, float x1, float y1, float z1) {
        var horizontal = buffers.getBuffer(top);
        face(pose, horizontal, Direction.DOWN, tint, light, overlay, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
        face(pose, horizontal, Direction.UP, tint, light, overlay, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
        var vertical = buffers.getBuffer(side);
        face(pose, vertical, Direction.NORTH, tint, light, overlay, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0);
        face(pose, vertical, Direction.SOUTH, tint, light, overlay, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
        face(pose, vertical, Direction.WEST, tint, light, overlay, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
        face(pose, vertical, Direction.EAST, tint, light, overlay, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1);
    }

    private static void face(PoseStack.Pose pose, VertexConsumer out, Direction direction, int tint, int light,
                             int overlay, float... vertices) {
        for (int index = 0; index < vertices.length; index += 3) {
            float x = vertices[index];
            float y = vertices[index + 1];
            float z = vertices[index + 2];
            float u = switch (direction) {
                case WEST -> z;
                case EAST -> 1F - z;
                case SOUTH -> 1F - x;
                default -> x;
            };
            float v = switch (direction) {
                case UP -> z;
                case DOWN -> 1F - z;
                default -> 1F - y;
            };
            out.vertex(pose.pose(), x, y, z).color((tint >> 16) & 255, (tint >> 8) & 255, tint & 255, 255)
                    .uv(u, v).overlayCoords(overlay).uv2(light).normal(pose.normal(),
                            direction.getStepX(), direction.getStepY(), direction.getStepZ()).endVertex();
        }
    }
}
