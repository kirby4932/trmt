package milkucha.trmt.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import milkucha.trmt.TRMTBlocks;
import milkucha.trmt.erosion.ErosionMapManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Since 26.3 hoe tilling / shovel flattening is data-driven (BlockTransformer + the
 * turns_into_farmland / turns_into_dirt_path tags, which include the eroded blocks).
 * This only drops the erosion entry once an eroded block was transformed.
 */
@Mixin(BlockTransformer.class)
public class BlockTransformerMixin {

    @WrapMethod(method = "transformBlock")
    private InteractionResult trmt$clearErosionEntry(UseOnContext context, Operation<InteractionResult> original) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState before = world.getBlockState(pos);
        InteractionResult result = original.call(context);

        if (!world.isClientSide()
                && (before.is(TRMTBlocks.ERODED_GRASS_BLOCK) || before.is(TRMTBlocks.ERODED_DIRT))
                && world.getBlockState(pos) != before) {
            ErosionMapManager.getInstance().removeEntry(pos);
        }
        return result;
    }
}
